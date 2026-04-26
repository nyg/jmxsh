package sh.jmx.jmxsh.cmd;

import java.util.List;

import javax.management.JMException;

import sh.jmx.jmxsh.Command;

/** Abstract command that provides domain/bean tab-completion for {@code -d} and {@code -b} options. */
abstract class DomainBeanAwareCommand extends Command {

  @Override
  protected List<String> doSuggestOption(String optionName) throws JMException {
    if ("d".equals(optionName)) {
      return DomainsCommand.getCandidateDomains(getSession());
    } else if ("b".equals(optionName)) {
      return BeanCommand.getCandidateBeanNames(getSession());
    }
    return List.of();
  }
}
