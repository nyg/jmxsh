<p align="center">
  <img src="docs/favicon.png" alt="jmxsh" width="130">
  <br>
  <a href="https://github.com/nyg/homebrew-jmxsh"><img src="https://img.shields.io/badge/homebrew-nyg%2Fjmxsh-FBB040?logo=homebrew&logoColor=FBB040" alt="Homebrew"></a>
  <a href="https://jmx.sh/apt"><img src="https://img.shields.io/badge/apt-jmx.sh%2Fapt-A80030?logo=debian&logoColor=white" alt="APT Repository"></a>
  <br><br>
  <strong><a href="https://jmx.sh">jmx.sh</a></strong>
</p>

**jmx.sh** lets you connect to any JMX-enabled JVM, browse MBeans, read and write attributes, and
invoke operations — all from the comfort of your terminal.

> **Fork notice** — jmxsh is an actively maintained fork of
> [jiaqi/jmxterm](https://github.com/jiaqi/jmxterm), incorporating contributions from
> [LeMyst/jmxterm](https://github.com/LeMyst/jmxterm). The goal is to keep the project alive with
> regular updates and releases, dependency maintenance, and new features.

## Table of Contents

- [Installation](#installation)
- [Features](#features)
- [Usage](#usage)
  - [Key Commands](#key-commands)
  - [Operation Parameters](#operation-parameters)
  - [JMXMP Connections](#jmxmp-connections)
  - [Connection Aliases](#connection-aliases)
  - [Watching Attributes](#watching-attributes)
  - [Configuration](#configuration)
  - [Non-Interactive Mode](#non-interactive-mode)
- [Documentation](#documentation)
- [License](#license)

## Installation

### JAR (all platforms)

Download the latest [JAR from Releases](https://github.com/nyg/jmxsh/releases) and run:

```bash
java -jar jmxsh-<version>.jar
```

### Homebrew (macOS & Linux)

```bash
brew install nyg/jmxsh/jmxsh
```

### Debian/Ubuntu

```bash
# Import the GPG key
curl -fsSL https://jmx.sh/apt/gpg.asc | sudo gpg --dearmor -o /usr/share/keyrings/jmxsh.gpg

# Add the repository
sudo tee /etc/apt/sources.list.d/jmxsh.sources << 'EOF'
Types: deb
URIs: https://jmx.sh/apt
Suites: stable
Components: main
Signed-By: /usr/share/keyrings/jmxsh.gpg
EOF

# Install
sudo apt update && sudo apt install jmxsh
```

## Features

- **Interactive REPL** with tab completion and command history (JLine)
- **Remote & local connections** — connect via host:port, JMX URL, or local PID
- **JMXMP protocol support** — connect via `jmxmp://host:port` in addition to the default RMI protocol
- **Full MBean support** — browse domains, read/write attributes, invoke operations
- **Typed parameters** — pass operation parameters and attribute values as JSON, and let types like `Instant`, `UUID`, enums, arrays and maps be parsed for you
- **Live monitoring** — watch attribute values with `watch`, subscribe to MBean notifications with `subscribe`
- **Connection aliases** — name your frequent connection targets with `alias` and reuse them in `open` or `-l`
- **Configurable prompt** — show the connected server, selected domain and bean in the prompt via a simple template
- **Command chaining** — run multiple commands in one line with `&&`
- **Script mode** — automate JMX operations via files or piped input
- **Quiet mode** — suppress informational messages with `-q` for scripting-friendly output
- **Cross-platform** — runs anywhere Java runs (JAR, DEB, RPM)
- **XDG Base Directory compliance** — configuration in `$XDG_CONFIG_HOME/jmxsh/`, command history in `$XDG_STATE_HOME/jmxsh/`, keeping your home directory clean

## Usage

```
$ java -jar jmxsh-<version>.jar
Welcome to jmx.sh, type "help" for available commands.
> open localhost:9999
Connection to localhost:9999 is opened.
> domains
The following domains are available:
JMImplementation
java.lang
com.example
> bean com.example:type=AppStats
Bean is set to com.example:type=AppStats.
> get RequestCount
MBean = com.example:type=AppStats:
RequestCount = 42;
> run resetStats
Calling operation resetStats of mbean com.example:type=AppStats with params [].
Operation returns:
null
> close
Disconnected.
> quit
Bye.
```

### Key Commands

| Command | Description |
|---|---|
| `open <host:port>` | Connect to a remote JMX endpoint (RMI) |
| `open jmxmp://<host:port>` | Connect to a remote JMX endpoint (JMXMP) |
| `open <pid>` | Attach to a local JVM by process ID |
| `domains` | List all MBean domains |
| `domain [name]` | Show or set the current domain |
| `beans` | List all MBeans (optionally filter by domain with `-d`) |
| `bean <name>` | Select an MBean for subsequent operations |
| `info` | Show attributes and operations of the selected MBean |
| `get <attr>` | Read an MBean attribute |
| `set <attr> <value>` | Write an MBean attribute |
| `run <op> [args]` | Invoke an MBean operation |
| `run -j <json> <op>` | Invoke an MBean operation with JSON parameters |
| `watch <attr>` | Poll an MBean attribute and print its value continuously |
| `subscribe` | Subscribe to the notifications of an MBean |
| `unsubscribe` | Unsubscribe from the notifications of an MBean |
| `close` | Disconnect from the JMX endpoint |
| `alias <name> [target]` | Define, show or list connection aliases |
| `unalias <name>` | Remove a connection alias |
| `jvms` | List local Java processes |
| `help` | Show all available commands |
| `quit` | Exit jmxsh (also `exit` or `bye`) |

### Operation Parameters

Parameters are converted to the types the operation declares, which `info -o <op>` prints. Beyond primitives and strings, values are parsed with Jackson, so types such as `Instant`, `LocalDate`, `Duration`, `UUID`, `java.util.Date`, `URI` and enum constants are accepted as plain text:

```
> bean com.example:type=Scheduler
> info -o schedule
  %0   - void schedule(java.time.Instant p1), Operation exposed for management
> run schedule 2026-01-01T00:00:00Z
```

A value starting with `[` or `{` is read as a JSON document, which is how arrays, collections and maps are passed:

```
> run sum [1,2,3]
> run configure '{"retries": 3, "verbose": true}'
```

The same conversion applies to `set`, so `set Deadline 2026-01-01T00:00:00Z` and `set Tags '["a", "b"]'` work on attributes of those types.

#### JSON parameters with `-j`

`-j` (or `--json`) takes all of an operation's parameters as a single JSON value, and cannot be combined with positional parameters. A JSON array binds by position:

```
> run -j '[3, 5]' add
Calling operation add of mbean com.example:type=Calculator with params [3, 5].
Operation returns:
8
```

A JSON object binds by parameter name, which also picks the right overload:

```
> run -j '{"p1": 3, "p2": 5}' add
```

For a plain standard MBean the JVM does not retain the declared parameter names and synthesises `p1`, `p2`, … instead (`p0`, `p1`, … for MXBeans). Real names appear only when the MBean supplies them, as Model MBeans and Spring's `@ManagedOperationParameter` do. Either way `info -o <op>` prints the names that `-j` expects, and a mismatch reports the known signatures:

```
> run -j '{"a": 3, "b": 5}' add
Operation add with 2 parameters doesn't exist in bean com.example:type=Calculator, known signatures are add(int p1, int p2)
```

#### Quoting

The prompt tokenises a line before the command sees it, so JSON containing spaces must be quoted: `run sum [1,2,3]` works, `run sum [1, 2, 3]` is split into three arguments. One level of backslash escaping is consumed by the tokeniser too, so write `\\n` where JSON needs `\n`. A `#` starts a comment unless escaped as `\#`, and `&&` separates chained commands; neither can appear unescaped inside a JSON string. Because positional values are also unescaped before conversion, JSON documents containing escape sequences must be passed with `-j`.

#### Serial filters

A JVM started with `-Dcom.sun.management.jmxremote` applies a deserialisation filter to invocation arguments that allows little beyond `java.lang.*` and `java.util.*`, so passing a `java.time` value or an enum to such a target fails with `filter status: REJECTED`. This is a policy of the target JVM, not of jmxsh; widen it there with `-Dcom.sun.management.jmxremote.serial.filter.pattern`.

### JMXMP Connections

To connect using the JMXMP protocol instead of the default RMI:

```
$> open jmxmp://localhost:9999
Connection to jmxmp://localhost:9999 is opened.
```

Full service URLs are also supported: `open service:jmx:jmxmp://localhost:9999`

### Connection Aliases

Define short names for connection targets you use often. A target is anything `open`
accepts: a `host:port`, a PID, a `jmxmp://` address or a full JMX service URL.

```
$> alias my_server myserver:1234
Alias my_server is set to myserver:1234.
$> open my_server
Connection to my_server (myserver:1234) is opened.
```

Aliases also work with the `-l` command line option:

```bash
jmxsh -l my_server
```

They are stored in `$XDG_CONFIG_HOME/jmxsh/aliases.properties` (default:
`~/.config/jmxsh/aliases.properties`), which can also be edited by hand. Use `alias` to
list all aliases and `unalias <name>` to remove one.

### Watching Attributes

Poll one or more attributes of the selected MBean at a regular interval, with an optional
printf-style format, poll interval (`-i`, in seconds) and duration (`-s`, stop after N seconds):

```
> bean java.lang:type=Memory
Bean is set to java.lang:type=Memory.
> watch -i 5 HeapMemoryUsage
```

Use `subscribe` and `unsubscribe` to receive JMX notifications emitted by an MBean instead of
polling it.

### Configuration

jmxsh reads its configuration from `$XDG_CONFIG_HOME/jmxsh/config.properties` (default:
`~/.config/jmxsh/config.properties`). The file is created with commented-out defaults on first
run. Available settings:

| Setting | Default | Description |
|---|---|---|
| `prompt` | `> ` | REPL prompt template |
| `logging.file.enabled` | `false` | Write logs to a rotating file in `$XDG_STATE_HOME/jmxsh/logs/` |

The prompt template supports the variables `{server}` (connected server), `{domain}` (selected
domain) and `{bean}` (selected bean). Wrap sections in `{?...}` to hide them when the variables
inside are empty:

```properties
prompt={?[{server}] }{?{domain}}{?/{bean}}> 
```

renders as `> ` when disconnected, and as `[localhost:9999] java.lang/type=Memory> ` once a
server, domain and bean are selected.

### Non-Interactive Mode

Run commands from a script file:

```bash
jmxsh -l localhost:9999 --input commands.txt
```

Or pipe commands via stdin:

```bash
echo "open localhost:9999 && beans" | jmxsh -n
```

## Documentation

- [Architecture](docs/dev/architecture.md)
- [Build Process](docs/dev/build-process.md)
- [Integration Tests](docs/dev/integration-tests.md)
- [E2E Tests](docs/dev/e2e-tests.md)

## License

Apache License 2.0 — see [LICENSE](LICENSE) for details.
