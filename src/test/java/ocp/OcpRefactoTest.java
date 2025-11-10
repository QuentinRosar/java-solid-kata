package ocp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OCP refactoring tests")
class OcpRefactoTest {

    @Test
    @DisplayName("Calculator should no longer expose non-OCP method computeDiscount(String, double)")
    void after_refactor_calculator_should_not_have_non_ocp_methods_anymore() {
        Method[] declared = DiscountCalculator.class.getDeclaredMethods();
        boolean hasBadMethod = Arrays.stream(declared)
                .anyMatch(m -> Arrays.equals(m.getParameterTypes(), new Class<?>[]{double.class}));

        assertFalse(hasBadMethod);
    }

    @Test
    @DisplayName("Responsibilities are extracted into dedicated classes with an apply(double) method")
    void responsibilities_should_be_extracted_to_dedicated_classes() throws Exception {
        Path srcPath = Paths.get("src/main/java/ocp");
        List<Class<?>> otherClasses = new ArrayList<>();

        if (Files.exists(srcPath)) {
            try (Stream<Path> files = Files.walk(srcPath)) {
                files.filter(path -> path.toString().endsWith(".java"))
                        .map(path -> path.getFileName().toString().replace(".java", ""))
                        .filter(className -> !className.equals("DiscountCalculator"))
                        .forEach(className -> {
                            try {
                                otherClasses.add(Class.forName("ocp." + className));
                            } catch (ClassNotFoundException e) {
                            }
                        });
            }
        }

        Class<?>[] expectedParams = new Class<?>[]{double.class};
        boolean foundApplyMethod = otherClasses.stream()
                .flatMap(clazz -> Arrays.stream(clazz.getDeclaredMethods()))
                .anyMatch(method -> Arrays.equals(method.getParameterTypes(), expectedParams));

        assertTrue(foundApplyMethod);
    }

    @Test
    @DisplayName("Calculator should not rely on String-based conditions in computeDiscount")
    void after_refactor_calculator_should_not_have_hardcoded_conditions() {
        Method[] methods = DiscountCalculator.class.getDeclaredMethods();

        boolean hasStringBasedMethod = Arrays.stream(methods)
              .anyMatch(m -> m.getName().equals("computeDiscount")
                             && Arrays.asList(m.getParameterTypes()).contains(String.class));

        assertFalse(hasStringBasedMethod);
    }

    @Test
    @DisplayName("Discount strategies share a common contract (interface or abstract class)")
    void discount_strategies_should_share_common_contract() throws Exception {
        Path srcPath = Paths.get("src/main/java/ocp");
        List<Class<?>> allClasses = new ArrayList<>();

        if (Files.exists(srcPath)) {
            try (Stream<Path> files = Files.walk(srcPath)) {
                files.filter(path -> path.toString().endsWith(".java"))
                      .map(path -> path.getFileName().toString().replace(".java", ""))
                      .forEach(className -> {
                          try {
                              allClasses.add(Class.forName("ocp." + className));
                          } catch (ClassNotFoundException e) {
                          }
                      });
            }
        }

        boolean hasCommonContract = allClasses.stream()
              .anyMatch(clazz -> clazz.isInterface() ||
                                 java.lang.reflect.Modifier.isAbstract(clazz.getModifiers()));

        assertTrue(hasCommonContract);
    }


}
