package srp;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DisplayName("SRP refactoring tests")
class SrpRefactoTest {

    @Test
    @DisplayName("Invoice.summary should keep the same behavior")
    void summary_should_keep_same_behavior() {
        Invoice invoice = new Invoice("Alice", 42.5);

        assertEquals("Alice owes 42.5", invoice.summary());
    }

    @Test
    @DisplayName("Invoice should no longer contain non-SRP methods (saveToFile, sendEmail)")
    void after_refactor_invoice_should_not_have_non_srp_methods_anymore() {
        Method[] declared = Invoice.class.getDeclaredMethods();
        List<String> names = Arrays.stream(declared).map(Method::getName).toList();

        assertFalse(names.contains("saveToFile"));
        assertFalse(names.contains("sendEmail"));
    }

    @Test
    @DisplayName("Responsibilities are extracted into dedicated classes with proper methods and signatures")
    void responsibilities_should_be_extracted_to_dedicated_classes() throws Exception {
        Map<String, Class<?>[]> expectedMethods = Map.of(
              "saveToFile", new Class<?>[]{Invoice.class, String.class},
              "send", new Class<?>[]{Invoice.class, String.class}
        );

        Path srcPath = Paths.get("src/main/java/srp");
        List<Class<?>> otherClasses = new ArrayList<>();

        if (Files.exists(srcPath)) {
            try (Stream<Path> files = Files.walk(srcPath)) {
                files.filter(path -> path.toString().endsWith(".java"))
                      .map(path -> path.getFileName().toString().replace(".java", ""))
                      .filter(className -> !className.equals("Invoice"))
                      .forEach(className -> {
                          try {
                              otherClasses.add(Class.forName("srp." + className));
                          } catch (ClassNotFoundException e) {
                          }
                      });
            }
        }

        for (Map.Entry<String, Class<?>[]> entry : expectedMethods.entrySet()) {
            String keyword = entry.getKey();
            Class<?>[] expectedParams = entry.getValue();

            boolean found = otherClasses.stream()
                  .flatMap(clazz -> Arrays.stream(clazz.getDeclaredMethods()))
                  .anyMatch(method ->
                        method.getName().toLowerCase().contains(keyword.toLowerCase()) &&
                        Arrays.equals(method.getParameterTypes(), expectedParams));

            assertTrue(found);
        }
    }
}
