package sh.jmx.jmxsh.boot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import sh.jmx.jmxsh.cc.CommandCenter;
import sh.jmx.jmxsh.io.CommandInput;
import sh.jmx.jmxsh.io.CommandOutput;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CliMainTest {

  @Mock
  private CommandInput input;
  @Mock
  private CommandOutput output;
  @Mock
  private CommandCenter commandCenter;

  @Test
  void should_print_bye_when_interactive_input_is_interrupted() throws IOException {
    // Given
    CliMainOptions options = new CliMainOptions();
    when(input.readLine()).thenThrow(new UserInterruptException(""));

    // When
    int exitCode = CliMain.runCommandLoop(input, output, commandCenter, options);

    // Then
    assertThat(exitCode).isZero();
    verify(output).printMessage("Welcome to jmx.sh, type \"help\" for available commands.");
    verify(output).printMessage("Bye.");
  }

  @Test
  void should_print_bye_when_interactive_input_reaches_end_of_file() throws IOException {
    // Given
    CliMainOptions options = new CliMainOptions();
    when(input.readLine()).thenThrow(new EndOfFileException());

    // When
    int exitCode = CliMain.runCommandLoop(input, output, commandCenter, options);

    // Then
    assertThat(exitCode).isZero();
    verify(output).printMessage("Bye.");
  }

  @Test
  void should_print_no_messages_when_non_interactive() throws IOException {
    // Given
    CliMainOptions options = new CliMainOptions();
    options.setNonInteractive(true);
    when(input.readLine()).thenThrow(new EndOfFileException());

    // When
    int exitCode = CliMain.runCommandLoop(input, output, commandCenter, options);

    // Then
    assertThat(exitCode).isZero();
    verify(output, never()).printMessage(anyString());
  }

  @Test
  void should_print_no_messages_when_quiet() throws IOException {
    // Given
    CliMainOptions options = new CliMainOptions();
    options.setQuiet(true);
    when(input.readLine()).thenReturn(null);

    // When
    int exitCode = CliMain.runCommandLoop(input, output, commandCenter, options);

    // Then
    assertThat(exitCode).isZero();
    verify(output, never()).printMessage(anyString());
  }

  @Test
  void should_not_print_bye_when_input_is_exhausted() throws IOException {
    // Given
    CliMainOptions options = new CliMainOptions();
    when(input.readLine()).thenReturn("open localhost:9999", (String) null);
    when(commandCenter.execute("open localhost:9999")).thenReturn(true);

    // When
    int exitCode = CliMain.runCommandLoop(input, output, commandCenter, options);

    // Then
    assertThat(exitCode).isZero();
    verify(output, never()).printMessage("Bye.");
  }

  @Test
  void should_return_negative_line_number_when_command_fails_with_exit_on_failure() throws IOException {
    // Given
    CliMainOptions options = new CliMainOptions();
    options.setExitOnFailure(true);
    when(input.readLine()).thenReturn("first", "second");
    when(commandCenter.execute("first")).thenReturn(true);
    when(commandCenter.execute("second")).thenReturn(false);

    // When
    int exitCode = CliMain.runCommandLoop(input, output, commandCenter, options);

    // Then
    assertThat(exitCode).isEqualTo(-2);
  }

  @Test
  void should_continue_when_command_fails_without_exit_on_failure() throws IOException {
    // Given
    CliMainOptions options = new CliMainOptions();
    when(input.readLine()).thenReturn("bad command", (String) null);
    when(commandCenter.execute("bad command")).thenReturn(false);

    // When
    int exitCode = CliMain.runCommandLoop(input, output, commandCenter, options);

    // Then
    assertThat(exitCode).isZero();
  }

  @Test
  void should_stop_reading_when_command_center_is_closed() throws IOException {
    // Given
    CliMainOptions options = new CliMainOptions();
    when(input.readLine()).thenReturn("quit");
    when(commandCenter.execute("quit")).thenReturn(true);
    when(commandCenter.isClosed()).thenReturn(true);

    // When
    int exitCode = CliMain.runCommandLoop(input, output, commandCenter, options);

    // Then
    assertThat(exitCode).isZero();
    verify(commandCenter).execute("quit");
  }

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
