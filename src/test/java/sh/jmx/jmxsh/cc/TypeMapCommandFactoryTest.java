package sh.jmx.jmxsh.cc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import sh.jmx.jmxsh.Command;
import sh.jmx.jmxsh.SelfRecordingCommand;
import org.junit.jupiter.api.Test;

class TypeMapCommandFactoryTest {

  @Test
  void constructorThrowsWhenSuppliersNull() {
    assertThatThrownBy(() -> new TypeMapCommandFactory(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void createCommandThrowsWhenNameNull() {
    TypeMapCommandFactory factory = new TypeMapCommandFactory(new HashMap<>());
    assertThatThrownBy(() -> factory.createCommand(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void createCommandReturnsCommand() {
    Map<String, Supplier<Command>> suppliers = new HashMap<>();
    suppliers.put("test", () -> new SelfRecordingCommand(new ArrayList<>()));
    TypeMapCommandFactory factory = new TypeMapCommandFactory(suppliers);
    assertThat(factory.createCommand("test")).isInstanceOf(SelfRecordingCommand.class);
  }
}
