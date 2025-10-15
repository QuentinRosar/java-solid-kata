package isp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ISP refactoring tests")
class IspRefactoTest {

    @Test
    @DisplayName("Printer.print should keep the same behavior")
    void print_should_keep_same_behavior_for_printer() {
        OldPrinter printer = new OldPrinter();
        assertEquals("printing: doc", printer.print("doc"));
    }

    @Test
    @DisplayName("Fat interface should no longer exist after refactor (no MultiFunctionDevice)")
    void after_refactor_fat_interface_should_not_exist_anymore() throws Exception {
        Path srcPath = Paths.get("src/main/java/isp");
        boolean hasMfd = false;
        if (Files.exists(srcPath)) {
            try (Stream<Path> files = Files.walk(srcPath)) {
                hasMfd = files.filter(path -> path.toString().endsWith(".java"))
                        .map(path -> path.getFileName().toString().replace(".java", ""))
                        .anyMatch(className -> className.equals("MultiFunctionDevice"));
            }
        }
        assertFalse(hasMfd);
    }

    @Test
    @DisplayName("Non-capable devices should not declare unrelated methods (no UnsupportedOperation hacks)")
    void non_capable_devices_should_not_declare_unrelated_methods() {
        Method[] declared = OldPrinter.class.getDeclaredMethods();
        boolean hasScan = Arrays.stream(declared).anyMatch(m -> m.getName().equals("scan"));
        boolean hasFax = Arrays.stream(declared).anyMatch(m -> m.getName().equals("fax"));

        assertFalse(hasScan);
        assertFalse(hasFax);
    }


    @Test
    @DisplayName("Capabilities are exposed via segregated contracts (Printable, Scannable, Faxable)")
    void capabilities_should_be_exposed_via_segregated_contracts() throws Exception {
        Path srcPath = Paths.get("src/main/java/isp");
        List<Class<?>> classes = new ArrayList<>();

        if (Files.exists(srcPath)) {
            try (Stream<Path> files = Files.walk(srcPath)) {
                files.filter(path -> path.toString().endsWith(".java"))
                      .map(path -> path.getFileName().toString().replace(".java", ""))
                      .forEach(className -> {
                          try {
                              classes.add(Class.forName("isp." + className));
                          } catch (ClassNotFoundException e) {
                          }
                      });
            }
        }

        Optional<Class<?>> printInterfaceOpt = classes.stream()
              .filter(Class::isInterface)
              .filter(c -> hasMethod(c, "print", String.class))
              .findFirst();

        Optional<Class<?>> scanInterfaceOpt = classes.stream()
              .filter(Class::isInterface)
              .filter(c -> hasMethod(c, "scan", String.class))
              .findFirst();

        assertTrue(printInterfaceOpt.isPresent());
        assertTrue(scanInterfaceOpt.isPresent());
        assertTrue(hasMethod(printInterfaceOpt.get(), "print", String.class));
        assertTrue(hasMethod(scanInterfaceOpt.get(), "scan", String.class));
    }

    private boolean hasMethod(Class<?> iface, String name, Class<?> param) {
        return Arrays.stream(iface.getDeclaredMethods())
              .anyMatch(m -> m.getName().equals(name)
                             && Arrays.equals(m.getParameterTypes(), new Class<?>[]{param})
                             && m.getReturnType() == String.class);
    }
}
