package sh.jmx.jmxsh.attach;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class JavaProcessManager {

  private static final String LOCAL_CONNECTOR_ADDRESS_PROP = "com.sun.management.jmxremote.localConnectorAddress";

  public JavaProcess get(int pid) {
    return list().stream()
        .filter(process -> process.getProcessId() == pid)
        .findAny()
        .orElse(null);
  }

  public List<JavaProcess> list() {
    List<VirtualMachineDescriptor> vmDescriptors = VirtualMachine.list();
    List<JavaProcess> javaProcesses = new ArrayList<>(vmDescriptors.size());

    for (VirtualMachineDescriptor vmd : vmDescriptors) {
      VirtualMachine vm = null;
      try {
        vm = VirtualMachine.attach(vmd);
        Properties agentProps = vm.getAgentProperties();
        String address = agentProps.getProperty(LOCAL_CONNECTOR_ADDRESS_PROP);
        javaProcesses.add(new JavaProcess(vmd, address));
      } catch (AttachNotSupportedException | IOException _) {
        javaProcesses.add(new JavaProcess(vmd, null));
      } finally {
        if (vm != null) {
          try {
            vm.detach();
          } catch (IOException _) {
            // Could not detach from the VM, ignoring as we cannot do anything about it
          }
        }
      }
    }

    return javaProcesses;
  }
}
