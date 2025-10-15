package lsp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LSP refactoring tests")
class LspRefactoTest {

    @Test
    @DisplayName("Sparrow.fly should keep the same behavior")
    void fly_should_keep_same_behavior_for_sparrow() {
        Sparrow sparrow = new Sparrow();
        assertEquals("flap", sparrow.fly());
    }

    @Test
    @DisplayName("Bird should no longer expose non-LSP method fly()")
    void after_refactor_bird_should_not_have_non_lsp_methods_anymore() {
        Method[] declared = Bird.class.getDeclaredMethods();
        boolean hasFly = Arrays.stream(declared)
                .anyMatch(m -> m.getName().equals("fly") && m.getParameterCount() == 0);

        assertFalse(hasFly);
    }

    @Test
    @DisplayName("Non-flying birds should not define a fly() method (no UnsupportedOperationException hacks)")
    void non_flying_birds_should_not_define_fly_method() throws Exception {
        Method[] declared = Ostrich.class.getDeclaredMethods();
        boolean hasFly = Arrays.stream(declared)
                .anyMatch(m -> m.getName().equals("fly") && m.getParameterCount() == 0);

        assertFalse(hasFly);
    }

    @Test
    @DisplayName("Flying capability is extracted behind a Flyable contract with a fly() method")
    void flying_capability_should_be_exposed_via_contract() throws Exception {
        Path srcPath = Paths.get("src/main/java/lsp");
        List<Class<?>> classes = new ArrayList<>();

        if (Files.exists(srcPath)) {
            try (Stream<Path> files = Files.walk(srcPath)) {
                files.filter(path -> path.toString().endsWith(".java"))
                        .map(path -> path.getFileName().toString().replace(".java", ""))
                        .forEach(className -> {
                            try {
                                classes.add(Class.forName("lsp." + className));
                            } catch (ClassNotFoundException e) {
                            }
                        });
            }
        }

        boolean hasFlyable = classes.stream().anyMatch(clazz -> clazz.isInterface() && clazz.getSimpleName().equals("Flyable"));
        assertTrue(hasFlyable);

        Optional<Class<?>> flyableOpt = classes.stream().filter(c -> c.isInterface() && c.getSimpleName().equals("Flyable")).findFirst();
        assertTrue(flyableOpt.isPresent());

        Method[] methods = flyableOpt.get().getDeclaredMethods();
        boolean hasFlyMethod = Arrays.stream(methods)
                .anyMatch(m -> m.getName().equals("fly") && m.getParameterCount() == 0 && m.getReturnType() == String.class);
        assertTrue(hasFlyMethod);
    }
}
