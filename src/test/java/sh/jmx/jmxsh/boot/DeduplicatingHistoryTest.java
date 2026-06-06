package sh.jmx.jmxsh.boot;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DeduplicatingHistoryTest {

  @TempDir
  Path tmpDir;

  private Terminal terminal;
  private DeduplicatingHistory history;
  private LineReader reader;

  @BeforeEach
  void setUp() throws IOException {
    terminal = TerminalBuilder.builder().dumb(true).build();
    history = new DeduplicatingHistory();
    reader = LineReaderBuilder.builder().terminal(terminal).history(history).build();
    reader.setVariable(LineReader.HISTORY_FILE, tmpDir.resolve("history"));
    reader.option(LineReader.Option.HISTORY_TIMESTAMPED, false);
    history.attach(reader);
  }

  @AfterEach
  void tearDown() throws IOException {
    terminal.close();
  }

  // -- in-session (add) deduplication -----------------------------------------

  @Test
  void addRemovesEarlierOccurrenceOfSameLine() {
    history.add("cmd1");
    history.add("cmd2");
    history.add("cmd1"); // duplicate of first entry

    assertThat(lines()).containsExactly("cmd2", "cmd1");
  }

  @Test
  void addKeepsMostRecentEntry() {
    history.add("cmd1");
    history.add("cmd2");
    history.add("cmd1");

    assertThat(lines().getLast()).isEqualTo("cmd1");
  }

  @Test
  void addDoesNotDeduplicateDistinctLines() {
    history.add("cmd1");
    history.add("cmd2");
    history.add("cmd3");

    assertThat(lines()).containsExactly("cmd1", "cmd2", "cmd3");
  }

  @Test
  void addSingleEntryLeavesHistorySizeOne() {
    history.add("cmd1");

    assertThat(history.size()).isEqualTo(1);
  }

  @Test
  void addConsecutiveDuplicateIsHandledCorrectly() {
    history.add("cmd1");
    history.add("cmd1");

    // HISTORY_IGNORE_DUPS prevents second add, so we end up with just one entry
    assertThat(lines()).containsExactly("cmd1");
  }

  @Test
  void addMultipleDuplicatesRemovesAllPrior() {
    history.add("a");
    history.add("b");
    history.add("a");
    history.add("c");
    history.add("a");

    // Only the most recent "a" should remain; earlier occurrences removed
    assertThat(lines()).containsExactly("b", "c", "a");
  }

  // -- file persistence (save) deduplication ----------------------------------

  @Test
  void saveDeduplicatesFileWithPreExistingDuplicates(@TempDir Path dir) throws IOException {
    Path file = dir.resolve("history");
    // Write a file with duplicates simulating a previous session
    Files.writeString(file, "cmd1\ncmd2\ncmd1\n");

    DeduplicatingHistory h = new DeduplicatingHistory();
    Terminal t = TerminalBuilder.builder().dumb(true).build();
    try {
      LineReader r = LineReaderBuilder.builder().terminal(t).history(h).build();
      r.setVariable(LineReader.HISTORY_FILE, file);
      r.option(LineReader.Option.HISTORY_TIMESTAMPED, false);
      h.attach(r); // load() is called by attach; items = [cmd1, cmd2, cmd1]
      h.save();

      List<String> saved = Files.readAllLines(file);
      assertThat(saved).containsExactly("cmd2", "cmd1");
    } finally {
      t.close();
    }
  }

  @Test
  void saveSafeWhenFileDoesNotExistYet() throws IOException {
    // No history file created yet — save() should not throw
    history.add("cmd1");
    history.save();

    Path file = (Path) reader.getVariable(LineReader.HISTORY_FILE);
    assertThat(Files.readAllLines(file)).containsExactly("cmd1");
  }

  @Test
  void saveWritesAllInSessionEntriesToFile() throws IOException {
    history.add("cmd1");
    history.add("cmd2");
    history.add("cmd3");
    history.save();

    Path file = (Path) reader.getVariable(LineReader.HISTORY_FILE);
    assertThat(Files.readAllLines(file)).containsExactly("cmd1", "cmd2", "cmd3");
  }

  @Test
  void saveDeduplicatesAfterLoadPlusInSessionAdd(@TempDir Path dir) throws IOException {
    Path file = dir.resolve("history");
    Files.writeString(file, "old\n");

    DeduplicatingHistory h = new DeduplicatingHistory();
    Terminal t = TerminalBuilder.builder().dumb(true).build();
    try {
      LineReader r = LineReaderBuilder.builder().terminal(t).history(h).build();
      r.setVariable(LineReader.HISTORY_FILE, file);
      r.option(LineReader.Option.HISTORY_TIMESTAMPED, false);
      h.attach(r); // loads "old" from file
      h.add("old"); // duplicate of loaded entry — our add() removes the loaded one
      h.save();

      List<String> saved = Files.readAllLines(file);
      // "old" should appear exactly once — in the most recent position
      assertThat(saved).containsExactly("old");
      assertThat(h.size()).isEqualTo(1);
    } finally {
      t.close();
    }
  }

  // -- save() fallback paths ---------------------------------------------------

  @Test
  void saveIsNoopWhenHistoryNotAttached() throws IOException {
    DeduplicatingHistory h = new DeduplicatingHistory();
    h.save(); // must not throw
    assertThat(h.size()).isEqualTo(0);
  }

  @Test
  void saveIsNoopWhenNoHistoryFileConfigured() throws IOException {
    DeduplicatingHistory h = new DeduplicatingHistory();
    Terminal t = TerminalBuilder.builder().dumb(true).build();
    try {
      LineReader r = LineReaderBuilder.builder().terminal(t).history(h).build();
      h.attach(r); // HISTORY_FILE not set → getVariable returns null
      h.add("cmd1");
      h.save(); // must not throw
      assertThat(h.size()).isEqualTo(1);
    } finally {
      t.close();
    }
  }

  @Test
  void saveAcceptsFileObjectAsHistoryPath(@TempDir Path dir) throws IOException {
    Path file = dir.resolve("history");
    DeduplicatingHistory h = new DeduplicatingHistory();
    Terminal t = TerminalBuilder.builder().dumb(true).build();
    try {
      LineReader r = LineReaderBuilder.builder().terminal(t).history(h).build();
      r.setVariable(LineReader.HISTORY_FILE, new File(file.toString())); // java.io.File, not Path
      r.option(LineReader.Option.HISTORY_TIMESTAMPED, false);
      h.attach(r);
      h.add("cmd1");
      h.save();

      assertThat(Files.readAllLines(file)).containsExactly("cmd1");
    } finally {
      t.close();
    }
  }

  @Test
  void saveAcceptsStringAsHistoryPath(@TempDir Path dir) throws IOException {
    Path file = dir.resolve("history");
    DeduplicatingHistory h = new DeduplicatingHistory();
    Terminal t = TerminalBuilder.builder().dumb(true).build();
    try {
      LineReader r = LineReaderBuilder.builder().terminal(t).history(h).build();
      r.setVariable(LineReader.HISTORY_FILE, file.toString()); // String, not Path
      r.option(LineReader.Option.HISTORY_TIMESTAMPED, false);
      h.attach(r);
      h.add("cmd1");
      h.save();

      assertThat(Files.readAllLines(file)).containsExactly("cmd1");
    } finally {
      t.close();
    }
  }

  // -- helpers -----------------------------------------------------------------

  private List<String> lines() {
    List<String> result = new ArrayList<>();
    history.iterator(history.first()).forEachRemaining(e -> result.add(e.line()));
    return result;
  }
}
