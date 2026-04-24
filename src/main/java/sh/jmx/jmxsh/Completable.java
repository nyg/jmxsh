package sh.jmx.jmxsh;

import java.util.List;

/**
 * Interface for objects that support tab completion of arguments and option values. Replaces the
 * {@code org.cyclopsgroup.jcli.AutoCompletable} interface from the JCLI library.
 */
public interface Completable {

  /**
   * Suggest possible arguments for auto completion.
   *
   * @param partialArg Partial argument text or {@code null}
   * @return List of possible arguments or {@code null}
   */
  List<String> suggestArgument(String partialArg);

  /**
   * Suggest possible option values for auto completion.
   *
   * @param name Option name
   * @param partialValue Partial value text or {@code null}
   * @return List of possible values or {@code null}
   */
  List<String> suggestOption(String name, String partialValue);
}
