package sh.jmx.jmxsh.boot;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CliMainTest {

  @Test
  void migrateHistoryCopiesLegacyFileWhenTargetMissing(@TempDir Path tmp) throws IOException {
    Path legacy = tmp.resolve("legacy_history");
    Files.writeString(legacy, "old-history-content");

    Path target = tmp.resolve("xdg/jmxsh/history");

    CliMain.migrateHistory(legacy, target);

    assertThat(target).exists().hasContent("old-history-content");
    assertThat(legacy).exists().hasContent("old-history-content");
  }

  @Test
  void migrateHistorySkipsWhenTargetAlreadyExists(@TempDir Path tmp) throws IOException {
    Path legacy = tmp.resolve("legacy_history");
    Files.writeString(legacy, "old-content");

    Path target = tmp.resolve("xdg/jmxsh/history");
    Files.createDirectories(target.getParent());
    Files.writeString(target, "new-content");

    CliMain.migrateHistory(legacy, target);

    assertThat(target).hasContent("new-content");
  }

  @Test
  void migrateHistorySkipsWhenLegacyFileMissing(@TempDir Path tmp) throws IOException {
    Path legacy = tmp.resolve("nonexistent");
    Path target = tmp.resolve("xdg/jmxsh/history");

    CliMain.migrateHistory(legacy, target);

    assertThat(target).doesNotExist();
  }
}
