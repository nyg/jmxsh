package sh.jmx.jmxsh.attach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

@ExtendWith(MockitoExtension.class)
class JavaProcessTest {

  @Mock
  private VirtualMachineDescriptor vmd;

  @Mock
  private VirtualMachine vm;

  @Test
  void should_becomeManageable_when_managementAgentStarts() throws IOException {
    // Given
    JavaProcess unit = new JavaProcess(vmd, null);

    try (MockedStatic<VirtualMachine> attachApi = mockStatic(VirtualMachine.class)) {
      attachApi.when(() -> VirtualMachine.attach(vmd)).thenReturn(vm);
      when(vm.startLocalManagementAgent()).thenReturn("service:jmx:rmi://127.0.0.1/stub");

      // When
      unit.startManagementAgent();
    }

    // Then
    assertThat(unit.isManageable()).isTrue();
    assertThat(unit.toUrl()).isEqualTo("service:jmx:rmi://127.0.0.1/stub");
    verify(vm).detach();
  }

  @Test
  void should_remainUnmanageable_when_attachIsNotSupported() {
    // Given
    JavaProcess unit = new JavaProcess(vmd, null);
    when(vmd.id()).thenReturn("42");

    try (MockedStatic<VirtualMachine> attachApi = mockStatic(VirtualMachine.class)) {
      attachApi.when(() -> VirtualMachine.attach(vmd)).thenThrow(new AttachNotSupportedException("nope"));

      // When / Then
      assertThatThrownBy(unit::startManagementAgent)
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("42");
    }

    assertThat(unit.isManageable()).isFalse();
  }
}
