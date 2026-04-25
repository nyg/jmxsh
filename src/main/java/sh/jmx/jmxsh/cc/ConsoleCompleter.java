package sh.jmx.jmxsh.cc;

import java.util.List;
import java.util.Objects;

import sh.jmx.jmxsh.Command;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import picocli.CommandLine;
import picocli.CommandLine.Model.OptionSpec;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleCompleter implements Completer {

  private final CommandCenter commandCenter;

  private final List<Candidate> commandNames;

  public ConsoleCompleter(CommandCenter commandCenter) {
    Objects.requireNonNull(commandCenter, "Command center can't be NULL");
    this.commandCenter = commandCenter;
    this.commandNames = commandCenter.getCommandNames().stream()
        .sorted()
        .map(Candidate::new)
        .toList();
  }

  @Override
  public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
    try {
      List<String> words = line.words();
      int wordIndex = line.wordIndex();

      if (wordIndex == 0) {
        completeCommandName(line.word(), candidates);
        return;
      }

      String commandName = words.getFirst();
      Command cmd = commandCenter.commandFactory.createCommand(commandName);
      cmd.setSession(commandCenter.session);
      CommandLine cl = new CommandLine(cmd);
      CommandLine.Model.CommandSpec spec = cl.getCommandSpec();

      String currentWord = line.word();
      String previousWord = wordIndex > 1 ? words.get(wordIndex - 1) : "";

      if (currentWord.startsWith("-")) {
        for (OptionSpec option : spec.options()) {
          for (String name : option.names()) {
            if (name.startsWith(currentWord)) {
              candidates.add(new Candidate(name));
            }
          }
        }
        return;
      }

      // Complete option values (previous word was a non-boolean option)
      if (previousWord.startsWith("-")) {
        OptionSpec matchedOption = findOption(spec, previousWord);
        if (matchedOption != null && !matchedOption.type().equals(boolean.class) && !matchedOption.type().equals(Boolean.class)) {
          String shortName = extractShortName(matchedOption);
          if (shortName != null) {
            List<String> suggestions = cmd.suggestOption(shortName, null);
            addFilteredSuggestions(suggestions, currentWord, candidates);
          }
          return;
        }
      }

      List<String> suggestions = cmd.suggestArgument(null);
      addFilteredSuggestions(suggestions, currentWord, candidates);
    } catch (RuntimeException e) {
      if (log.isDebugEnabled()) {
        log.debug("Couldn't complete input", e);
      }
    }
  }

  private OptionSpec findOption(CommandLine.Model.CommandSpec spec, String name) {
    for (OptionSpec option : spec.options()) {
      for (String optName : option.names()) {
        if (optName.equals(name)) {
          return option;
        }
      }
    }
    return null;
  }

  private String extractShortName(OptionSpec option) {
    for (String name : option.names()) {
      if (name.startsWith("-") && !name.startsWith("--") && name.length() == 2) {
        return name.substring(1);
      }
    }
    // Fall back to first name without dashes
    String first = option.names()[0];
    return first.replaceFirst("^-+", "");
  }

  private void addFilteredSuggestions(List<String> suggestions, String prefix,
      List<Candidate> candidates) {
    if (suggestions == null) {
      return;
    }
    for (String suggestion : suggestions) {
      if (prefix == null || prefix.isEmpty() || suggestion.startsWith(prefix)) {
        candidates.add(new Candidate(suggestion));
      }
    }
  }

  private void completeCommandName(String buf, List<Candidate> candidates) {
    if (buf == null || buf.isEmpty()) {
      candidates.addAll(commandNames);
    } else {
      for (Candidate commandName : commandNames) {
        if (commandName.value().startsWith(buf)) {
          candidates.add(commandName);
        }
      }
    }
  }
}
