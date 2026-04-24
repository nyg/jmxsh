package org.cyclopsgroup.jmxterm.cmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeast;
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
 * Test case for {@link RunCommand}
 *
 */
class UnsubscribeCommandTest {
  private SubscribeCommand subscribeCommand;
  private UnsubscribeCommand unsubscribeCommand;

  private StringWriter output;

  /** Setup objects to test */
  @BeforeEach
  void setUp() {
    subscribeCommand = new SubscribeCommand();
    unsubscribeCommand = new UnsubscribeCommand();
    output = new StringWriter();
  }

  @AfterEach
  void tearDown() {
    SubscribeCommand.getListeners().clear();
  }

  /** @throws Exception */
  @Test
  void executeNormally() throws Exception {
    subscribeCommand.setBean("a:type=x");
    unsubscribeCommand.setBean("a:type=x");

    MBeanServerConnection con = mock(MBeanServerConnection.class);
    MBeanInfo beanInfo = mock(MBeanInfo.class);
    ObjectName objectName = new ObjectName("a:type=x");

    when(con.getMBeanInfo(objectName)).thenReturn(beanInfo);

    MockSession session = new MockSession(output, con);
    subscribeCommand.setSession(session);
    subscribeCommand.execute();
    assertThat(SubscribeCommand.getListeners()).hasSize(1);

    unsubscribeCommand.setSession(session);
    unsubscribeCommand.execute();
    assertThat(SubscribeCommand.getListeners()).isEmpty();

    verify(con, atLeast(2)).getMBeanInfo(objectName);
    verify(con)
        .addNotificationListener(
            eq(objectName),
            any(NotificationListener.class),
            isNull(),
            isNull());
    verify(con)
        .removeNotificationListener(eq(objectName), any(NotificationListener.class));
  }

  /** @throws Exception */
  @Test
  void executeTwoNotifications() throws Exception {
    subscribeCommand.setBean("a:type=x");

    MBeanServerConnection con = mock(MBeanServerConnection.class);
    MBeanInfo beanInfo = mock(MBeanInfo.class);
    Notification notification = mock(Notification.class);

    ObjectName objectName = new ObjectName("a:type=x");
    when(con.getMBeanInfo(objectName)).thenReturn(beanInfo);
    when(notification.getTimeStamp()).thenReturn(123L);
    when(notification.getSource()).thenReturn("xyz");
    when(notification.getType()).thenReturn("azerty");
    when(notification.getMessage()).thenReturn("qwerty");

    subscribeCommand.setSession(new MockSession(output, con));
    subscribeCommand.execute();
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
  }
}
