package dip;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DIP refactoring tests")
class DipRefactoTest {

    @Test
    @DisplayName("EmailSender.send should keep the same behavior")
    void send_should_keep_same_behavior_for_email_sender() {
        EmailSender emailSender = new EmailSender();
        assertEquals("email: hello", emailSender.send("hello"));
    }

    @Test
    @DisplayName("A MessageSender contract exists with send(String) method")
    void message_sender_contract_should_exist() throws Exception {
        Path srcPath = Paths.get("src/main/java/dip");
        List<Class<?>> classes = new ArrayList<>();

        if (Files.exists(srcPath)) {
            try (Stream<Path> files = Files.walk(srcPath)) {
                files.filter(path -> path.toString().endsWith(".java"))
                        .map(path -> path.getFileName().toString().replace(".java", ""))
                        .forEach(className -> {
                            try {
                                classes.add(Class.forName("dip." + className));
                            } catch (ClassNotFoundException e) {
                            }
                        });
            }
        }

        Optional<Class<?>> messageSenderOpt = classes.stream().filter(c -> c.isInterface() && c.getSimpleName().equals("MessageSender")).findFirst();
        assertTrue(messageSenderOpt.isPresent());

        Method[] methods = messageSenderOpt.get().getDeclaredMethods();
        boolean hasSendMethod = Arrays.stream(methods)
                .anyMatch(m -> m.getName().equals("send")
                        && Arrays.equals(m.getParameterTypes(), new Class<?>[]{String.class})
                        && m.getReturnType() == String.class);
        assertTrue(hasSendMethod);
    }

    @Test
    @DisplayName("NotificationService depends on abstraction via constructor injection")
    void notification_service_should_use_constructor_injection() {
        Class<?> clazz = NotificationService.class;

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        boolean hasCtorWithInterface = Arrays.stream(constructors)
                .anyMatch(c -> Arrays.equals(c.getParameterTypes(), new Class<?>[]{getMessageSenderInterface()}));
        assertTrue(hasCtorWithInterface);

        boolean hasNoArgCtor = Arrays.stream(constructors)
                .anyMatch(c -> c.getParameterCount() == 0);
        assertFalse(hasNoArgCtor);

        Field[] fields = clazz.getDeclaredFields();
        boolean hasConcreteField = Arrays.stream(fields)
                .anyMatch(f -> f.getType().getSimpleName().equals("EmailSender") || f.getType().getSimpleName().equals("SmsSender"));
        assertFalse(hasConcreteField);
    }

    @Test
    @DisplayName("High-level notify method delegates to injected abstraction (single String parameter)")
    void notification_service_should_expose_simple_notify_api() {
        Method[] methods = NotificationService.class.getDeclaredMethods();

        boolean hasSimpleNotify = Arrays.stream(methods)
                .anyMatch(m -> m.getReturnType() == String.class && Arrays.equals(m.getParameterTypes(), new Class<?>[]{String.class}));

        assertTrue(hasSimpleNotify);

        boolean hasChannelBasedApi = Arrays.stream(methods)
                .anyMatch(m -> m.getName().toLowerCase().contains("notify") && m.getParameterCount() == 2 && Arrays.stream(m.getParameterTypes()).allMatch(t -> t == String.class));
        assertFalse(hasChannelBasedApi);
    }

    private Class<?> getMessageSenderInterface() {
        try {
            return Class.forName("dip.MessageSender");
        } catch (ClassNotFoundException e) {
            fail("MessageSender interface not found. Refactor should introduce dip.MessageSender");
            return null;
        }
    }
}
