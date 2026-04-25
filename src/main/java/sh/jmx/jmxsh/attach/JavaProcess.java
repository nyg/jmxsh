package sh.jmx.jmxsh.attach;

import java.io.IOException;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class JavaProcess {

  @NonNull
  private final VirtualMachineDescriptor vmd;
  private final String address;

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
    try {
      VirtualMachine.attach(vmd).startLocalManagementAgent();
    } catch (SecurityException | AttachNotSupportedException e) {
      throw new IllegalStateException("Cannot start management agent on VM with pid " + vmd.id(), e);
    }
  }

  public String toUrl() {
    return address;
  }
}
