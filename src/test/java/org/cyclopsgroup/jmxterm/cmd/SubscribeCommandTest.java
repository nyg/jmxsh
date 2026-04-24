package org.cyclopsgroup.jmxterm.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.StringWriter;

import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.cyclopsgroup.jmxterm.MockSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link org.cyclopsgroup.jmxterm.cmd.RunCommand}
 *
 */
class SubscribeCommandTest {
  private SubscribeCommand command;

  private StringWriter output;

  /** Setup objects to test */
  @BeforeEach
  void setUp() {
    command = new SubscribeCommand();
    output = new StringWriter();
  }

  @AfterEach
  void tearDown() {
    SubscribeCommand.getListeners().clear();
  }

  /** @throws Exception */
  @Test
  void executeOneNotification() throws Exception {
    command.setBean("a:type=x");

    MBeanServerConnection con = mock(MBeanServerConnection.class);
    MBeanInfo beanInfo = mock(MBeanInfo.class);
    Notification notification = mock(Notification.class);

    ObjectName objectName = new ObjectName("a:type=x");
    when(con.getMBeanInfo(objectName)).thenReturn(beanInfo);
    when(notification.getTimeStamp()).thenReturn(123L);
    when(notification.getSource()).thenReturn("xyz");
    when(notification.getType()).thenReturn("azerty");
    when(notification.getMessage()).thenReturn("qwerty");

    command.setSession(new MockSession(output, con));
    command.execute();
    assertThat(SubscribeCommand.getListeners()).hasSize(1);

    NotificationListener notificationListener = SubscribeCommand.getListeners().get(objectName);
    assertThat(notificationListener).isNotNull();

    notificationListener.handleNotification(notification, null);
    assertThat(output.toString().trim())
        .isEqualTo(
            "notification received: timestamp=123,class="
                + notification.getClass().getName()
                + ",source=xyz,type=azerty,message=qwerty");

    verify(con)
        .addNotificationListener(
            eq(objectName),
            any(NotificationListener.class),
            isNull(),
            isNull());
  }

  /** @throws Exception */
  @Test
  void executeTwoNotifications() throws Exception {
    command.setBean("a:type=x");

    MBeanServerConnection con = mock(MBeanServerConnection.class);
    MBeanInfo beanInfo = mock(MBeanInfo.class);
    Notification notification = mock(Notification.class);

    ObjectName objectName = new ObjectName("a:type=x");
    when(con.getMBeanInfo(objectName)).thenReturn(beanInfo);
    when(notification.getTimeStamp()).thenReturn(123L);
    when(notification.getSource()).thenReturn("xyz");
    when(notification.getType()).thenReturn("azerty");
    when(notification.getMessage()).thenReturn("qwerty");

    command.setSession(new MockSession(output, con));
    command.execute();
    assertThat(SubscribeCommand.getListeners()).hasSize(1);

    NotificationListener notificationListener = SubscribeCommand.getListeners().get(objectName);
    assertThat(notificationListener).isNotNull();

    notificationListener.handleNotification(notification, null);
    notificationListener.handleNotification(notification, null);

    String expected =
        "notification received: timestamp=123,class="
            + notification.getClass().getName()
            + ",source=xyz,type=azerty,message=qwerty"
            + System.lineSeparator()
            + "notification received: timestamp=123,class="
            + notification.getClass().getName()
            + ",source=xyz,type=azerty,message=qwerty";

    assertThat(output.toString().trim()).isEqualTo(expected);

    verify(con)
        .addNotificationListener(
            eq(objectName),
            any(NotificationListener.class),
            isNull(),
            isNull());
  }
}
