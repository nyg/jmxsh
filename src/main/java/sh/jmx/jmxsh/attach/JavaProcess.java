package sh.jmx.jmxsh.attach;

import java.io.IOException;

import lombok.NonNull;


import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class JavaProcess {

  @NonNull
  private final VirtualMachineDescriptor vmd;
  private String address;

  JavaProcess(@NonNull VirtualMachineDescriptor vmd, String address) {
    this.vmd = vmd;
    this.address = address;
  }

  public String getDisplayName() {
    return vmd.displayName();
  }

  public int getProcessId() {
    return Integer.parseInt(vmd.id());
  }

  public boolean isManageable() {
    return address != null;
  }

  public void startManagementAgent() throws IOException {
    VirtualMachine vm = null;
    try {
      vm = VirtualMachine.attach(vmd);
      address = vm.startLocalManagementAgent();
    } catch (SecurityException | AttachNotSupportedException e) {
      throw new IllegalStateException("Cannot start management agent on VM with pid " + vmd.id(), e);
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

  public String toUrl() {
    return address;
  }
}
