package sh.jmx.jmxsh;

import java.util.List;

/**
 * Interface for objects that support tab completion of arguments and option values.
 */
public interface Completable {

  /**
   * Suggest possible arguments for auto completion.
   */
  List<String> suggestArgument(String partialArg);

  /**
   * Suggest possible option values for auto completion.
   */
  List<String> suggestOption(String name, String partialValue);
}
