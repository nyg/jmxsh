package sh.jmx.jmxsh.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Named aliases for JMX connection targets, persisted in
 * {@code $XDG_CONFIG_HOME/jmxsh/aliases.properties}. An alias maps a short name to anything the
 * {@code open} command accepts: a PID, {@code host:port}, {@code jmxmp://host:port} or a full JMX
 * service URL. The file may also be edited by hand; every mutation rewrites it.
 */
@Slf4j
public class AliasStore {

  /**
   * Valid alias names can never be mistaken for a connection target: they must not be all digits
   * (PID) and must not contain {@code :} or {@code /} (host:port, URL).
   */
  static final Pattern VALID_NAME = Pattern.compile("^[A-Za-z_][A-Za-z0-9_.-]*$");

  private static final String FILE_HEADER =
      "# jmxsh connection aliases; managed by the \"alias\" command, hand-editing is OK";

  private final Path file;
  private final SortedMap<String, String> aliases = new TreeMap<>();

  public AliasStore(@NonNull Path file) {
    this.file = file;
    load();
  }

  /** Loads the alias store from the XDG aliases file for the given directories. */
  public static AliasStore load(XdgDirectories xdg) {
    return new AliasStore(xdg.getAliasesFile());
  }

  /** Returns whether the given string is a valid alias name. */
  public static boolean isValidName(String name) {
    return name != null && VALID_NAME.matcher(name).matches();
  }

  /**
   * Resolves a connection target: if it is a defined alias name, returns the aliased target,
   * otherwise returns the input unchanged.
   */
  public String resolve(String target) {
    if (target == null) {
      return null;
    }
    return aliases.getOrDefault(target, target);
  }

  /** Defines or redefines an alias and persists the change. */
  public void put(@NonNull String name, @NonNull String value) throws IOException {
    if (!isValidName(name)) {
      throw new IllegalArgumentException(
          "Invalid alias name \""
              + name
              + "\": names must start with a letter or underscore and contain only letters, digits, \"_\", \".\" or \"-\"");
    }
    aliases.put(name, value);
    save();
  }

  /**
   * Removes an alias and persists the change. Returns false (without touching the file) if the
   * alias is not defined.
   */
  public boolean remove(@NonNull String name) throws IOException {
    if (aliases.remove(name) == null) {
      return false;
    }
    save();
    return true;
  }

  /** Returns an unmodifiable view of all aliases, sorted by name. */
  public SortedMap<String, String> asMap() {
    return Collections.unmodifiableSortedMap(aliases);
  }

  /** Returns all alias names, sorted. */
  public Set<String> names() {
    return asMap().keySet();
  }

  private void load() {
    if (!Files.isRegularFile(file)) {
      return;
    }
    Properties props = new Properties();
    try (InputStream in = Files.newInputStream(file)) {
      props.load(in);
    } catch (IOException e) {
      log.warn("failed to read aliases from {}, starting with none", file, e);
      return;
    }
    for (Map.Entry<Object, Object> entry : props.entrySet()) {
      String name = (String) entry.getKey();
      if (isValidName(name)) {
        aliases.put(name, (String) entry.getValue());
      } else {
        log.debug("skipping invalid alias name \"{}\" in {}", name, file);
      }
    }
  }

  private void save() throws IOException {
    Files.createDirectories(file.getParent());
    StringBuilder content = new StringBuilder(FILE_HEADER).append('\n');
    for (Map.Entry<String, String> entry : aliases.entrySet()) {
      content
          .append(entry.getKey())
          .append('=')
          .append(entry.getValue().replace("\\", "\\\\"))
          .append('\n');
    }
    Path temp = Files.createTempFile(file.getParent(), file.getFileName().toString(), ".tmp");
    try {
      Files.writeString(temp, content.toString(), StandardCharsets.UTF_8);
      try {
        Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (AtomicMoveNotSupportedException _) {
        Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
      }
    } finally {
      Files.deleteIfExists(temp);
    }
  }
}
