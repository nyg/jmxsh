# Security Enhancements

This document describes security measures in place and recommended enhancements for the jmxsh
build process, release pipeline, repository configuration, and application code.

## Table of Contents

- [Build \& CI Security](#build--ci-security)
- [Release Security](#release-security)
- [Repository Configuration](#repository-configuration)
- [Application Security](#application-security)

---

## Build & CI Security

### ✅ Actions pinned by SHA

All GitHub Actions in CI and release workflows are pinned to full commit SHAs (not mutable tags).
This prevents supply-chain attacks where a compromised tag could inject malicious code into the
pipeline.

```yaml
# Good — pinned to SHA
- uses: actions/checkout@de0fac2e4500dabe0009e67214ff5f5447ce83dd # v6

# Bad — mutable tag
- uses: actions/checkout@v6
```

### ✅ Minimal workflow permissions

The CI workflow uses `permissions: contents: read`, following the principle of least privilege.
The release workflow uses `permissions: contents: write` — necessary for pushing tags and
creating GitHub Releases.

### CodeQL scanning (added)

A CodeQL workflow (`.github/workflows/codeql.yml`) has been added to perform static application
security testing (SAST) on every push to `master` and on every pull request. It also runs on a
weekly schedule to catch issues from newly discovered vulnerability patterns.

CodeQL for Java analyzes compiled bytecode. The workflow builds the project with Maven before
analysis to ensure accurate results.

### Maven enforcer rules (added)

The `maven-enforcer-plugin` is now configured with active rules in `pom.xml`:

| Rule | Purpose |
|---|---|
| `requireMavenVersion` (≥ 3.8.8) | Ensures a minimum Maven version with known security fixes |
| `requireJavaVersion` (≥ 25) | Prevents accidental builds with older JDKs |
| `banDuplicatePomDependencyVersions` | Catches duplicate dependency declarations |

These rules run during the `validate` phase, failing the build early if requirements aren't met.

### Recommended: OSSF Scorecard

Consider adding the [OSSF Scorecard](https://github.com/ossf/scorecard-action) GitHub Action to
continuously assess the project's security posture. Scorecard checks for pinned dependencies,
branch protection, signed releases, and other security best practices.

```yaml
# .github/workflows/scorecard.yml
- uses: ossf/scorecard-action@...
  with:
    results_file: results.sarif
    publish_results: true
```

---

## Release Security

### Artifact checksums (added)

The release workflow now generates SHA-256 checksums for all release artifacts (JAR, DEB, RPM)
and attaches them to the GitHub Release. Users can verify artifact integrity:

```bash
sha256sum -c jmxsh-1.2.3-checksums.txt
```

### Recommended: Signed commits

Configure Git to sign commits with GPG or SSH keys. This proves authorship and prevents tampering.

**Setup (SSH signing — recommended for simplicity):**

```bash
# Configure Git to use SSH signing
git config --global gpg.format ssh
git config --global user.signingkey ~/.ssh/id_ed25519.pub
git config --global commit.gpgsign true
git config --global tag.gpgsign true
```

**Setup (GPG signing):**

```bash
# List your GPG keys
gpg --list-secret-keys --keyid-format=long

# Configure Git
git config --global user.signingkey <KEY_ID>
git config --global commit.gpgsign true
git config --global tag.gpgsign true
```

Then enforce signed commits via GitHub branch protection (see [Repository
Configuration](#repository-configuration)).

### Recommended: Signed tags

Release tags created by `maven-release-plugin` are currently unsigned. To sign tags:

1. **In the release workflow** — import a GPG key into the GitHub Actions runner and configure
   Git to sign tags. The release workflow's `Configure Git user` step would need to set up
   `gpg.program` and import the signing key from a GitHub Secret.

2. **In Maven** — Configure `maven-release-plugin` with
   `<signTag>true</signTag>`. This requires GPG to be available on the runner.

> **Note:** Tag signing adds operational complexity (key management, secret rotation). Evaluate
> whether the threat model justifies it. For an open-source CLI tool, artifact checksums and
> GitHub's verified commit badges may provide sufficient assurance.

### Recommended: SBOM generation

Generate a Software Bill of Materials (SBOM) and attach it to releases. This helps users and
security scanners understand the dependency tree.

```xml
<!-- Add to pom.xml -->
<plugin>
  <groupId>org.cyclonedx</groupId>
  <artifactId>cyclonedx-maven-plugin</artifactId>
  <version>2.9.1</version>
  <executions>
    <execution>
      <goals>
        <goal>makeBom</goal>
      </goals>
      <phase>package</phase>
    </execution>
  </executions>
</plugin>
```

Then upload `target/bom.json` as a release artifact.

### Recommended: Build provenance attestation

GitHub's [artifact attestation](https://docs.github.com/en/actions/security-for-github-actions/using-artifact-attestations/using-artifact-attestations-to-establish-provenance-for-builds)
generates SLSA provenance metadata, proving that artifacts were built in a specific CI pipeline
from a specific commit.

```yaml
- uses: actions/attest-build-provenance@...
  with:
    subject-path: target/jmxsh-*.jar
```

Users can then verify provenance with `gh attestation verify`.

---

## Repository Configuration

The following settings are recommended for the GitHub repository. These are configured in the
repository's **Settings** page, not in code.

### Branch protection on `master`

| Setting | Recommended | Current |
|---|---|---|
| Require pull request reviews before merging | ✅ (≥ 1 reviewer) | Verify |
| Require status checks to pass (Java CI) | ✅ | Verify |
| Require signed commits | ✅ | ❌ (enable after setting up signing) |
| Require linear history | ✅ (prevents merge commits) | Verify |
| Disallow force pushes | ✅ | Verify |
| Restrict who can push to matching branches | ✅ (admin only) | Verify |

### Security features

| Feature | Purpose | How to enable |
|---|---|---|
| **Dependabot security alerts** | Alerts on known vulnerabilities in dependencies | Settings → Code security → Enable |
| **Dependabot security updates** | Auto-creates PRs to fix vulnerable dependencies | Settings → Code security → Enable |
| **Secret scanning** | Detects accidentally committed secrets (API keys, tokens) | Settings → Code security → Enable |
| **Push protection** | Blocks pushes containing detected secrets | Settings → Code security → Enable |
| **Private vulnerability reporting** | Allows security researchers to report issues privately | Settings → Code security → Enable |

> **Note:** Renovate is already configured (`renovate.json`) for dependency updates. Dependabot
> security updates complement Renovate by specifically targeting known CVEs, even between
> Renovate's scheduled runs.

### Recommended: CODEOWNERS

Add a `CODEOWNERS` file to require specific reviewers for sensitive paths:

```
# .github/CODEOWNERS
* @nyg
.github/ @nyg
pom.xml @nyg
```

---

## Application Security

### Low attack surface

jmxsh is a CLI tool with no network-facing server component. The primary attack surface is:

1. **JMX connections** — Users explicitly connect to JMX endpoints. Connection URLs are
   user-provided; no automatic discovery of remote services.
2. **Script execution** — The `-i` flag executes commands from a file. This is equivalent to
   shell script execution and carries the same trust model.
3. **Local JVM attach** — Uses `com.sun.tools.attach` to connect to local processes. Requires
   the same user permissions as `jcmd` or `jmap`.

### ✅ SSL support

`OpenCommand` supports `--sslrmi` for RMI connections with `SslRMIClientSocketFactory`. Users
connecting to production JMX endpoints over the network should use SSL.

### Credential handling considerations

The `--password` option accepts passwords on the command line. This has two implications:

1. **Shell history** — The password appears in the shell history file (`~/.bash_history`,
   `~/.zsh_history`). Users should be aware of this. Document the recommendation to use
   interactive mode (which prompts for the password without echoing) or to prefix the command
   with a space (which most shells exclude from history).

2. **Process listing** — The password is visible in `/proc/<pid>/cmdline` on Linux while the
   process runs. This is a known limitation of CLI password arguments.

**Recommendation:** Add a note to the `--password` option help text warning about shell history
exposure, and suggest using `-p` without a value to trigger interactive (masked) input.
