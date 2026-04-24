package org.cyclopsgroup.jmxterm.jdk9;

import java.io.IOException;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.cyclopsgroup.jmxterm.JavaProcess;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * JDK9 specific implementation of {@link JavaProcess}
 *
 * @author <a href="https://github.com/nyg">nyg</a>
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Jdk9JavaProcess implements JavaProcess {

  @NonNull
  private final VirtualMachineDescriptor vmd;
  private final String address;

  @Override
  public String getDisplayName() {
    return vmd.displayName();
  }

  @Override
  public int getProcessId() {
    return Integer.parseInt(vmd.id());
  }

  @Override
  public boolean isManageable() {
    return address != null;
  }

  @Override
  public void startManagementAgent() throws IOException {
    try {
      VirtualMachine.attach(vmd).startLocalManagementAgent();
    } catch (SecurityException | AttachNotSupportedException e) {
      throw new IllegalStateException("Cannot start management agent on VM with pid " + vmd.id(), e);
    }
  }

  @Override
  public String toUrl() {
    return address;
  }
}
