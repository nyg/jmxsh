package sh.jmx.jmxsh.boot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ListIterator;

import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.impl.ReaderUtils;
import org.jline.reader.impl.history.DefaultHistory;

/**
 * A history implementation that deduplicates entries both in-memory and on disk.
 *
 * <p>When a command is added that already exists in the history, the old entry is removed
 * and the new entry is appended — ensuring each command appears at most once (most recent
 * position wins). This applies both during an active session (Up/Down arrow browsing) and
 * in the history file persisted to disk.
 */
public class DeduplicatingHistory extends DefaultHistory {

  private LineReader lineReader;

  @Override
  public void attach(LineReader reader) {
    this.lineReader = reader;
    super.attach(reader);
  }

  /**
   * Adds an entry and removes any previous occurrence of the same line.
   *
   * <p>All normal JLine filters (HISTORY_IGNORE_SPACE, HISTORY_IGNORE_DUPS, HISTORY_IGNORE
   * patterns, etc.) are applied by {@code super.add()} first. Only if an entry was actually
   * added do we scan and remove earlier occurrences.
   *
   * <p>{@link #moveToEnd()} is called after removal to keep the navigation cursor consistent
   * with the new list size.
   */
  @Override
  public void add(Instant time, String line) {
    int sizeBefore = size();
    super.add(time, line);
    if (size() > sizeBefore) {
      int addedIndex = last();
      String addedLine = get(addedIndex);
      ListIterator<History.Entry> it = iterator(first());
      while (it.hasNext()) {
        History.Entry e = it.next();
        if (e.index() != addedIndex && e.line().equals(addedLine)) {
          it.remove();
        }
      }
      moveToEnd();
    }
  }

  /**
   * Saves history with a full file rewrite to avoid the {@code lastLoaded} issue.
   *
   * <p>When {@link #add} removes a loaded entry from the in-memory list, the superclass
   * {@code save()} would compute an empty append range and silently drop the newly-added
   * command. We bypass this by calling {@link #write write(null, false)} which rewrites
   * all entries from scratch, then {@link #trimHistory} to deduplicate any pre-existing
   * duplicates and reset internal file metadata.
   */
  @Override
  public void save() throws IOException {
    if (lineReader == null) {
      super.save();
      return;
    }
    Path path = resolvePath(lineReader.getVariable(LineReader.HISTORY_FILE));
    if (path == null) {
      super.save();
      return;
    }
    write(null, false);
    if (Files.exists(path) && Files.size(path) > 0) {
      int max = ReaderUtils.getInt(lineReader, LineReader.HISTORY_FILE_SIZE, DEFAULT_HISTORY_FILE_SIZE);
      trimHistory(path, max);
    }
  }

  private static Path resolvePath(Object obj) {
    if (obj instanceof Path p) {
      return p;
    } else if (obj instanceof File f) {
      return f.toPath();
    } else if (obj != null) {
      return Path.of(obj.toString());
    }
    return null;
  }
}
