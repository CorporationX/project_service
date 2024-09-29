package faang.school.projectservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DuplicateClassesTest {

    private final static String FOLDER_PATH = "faang.school.projectservice";

    private final static int ZERO_DUPS = 0;

    @Test
    @DisplayName("Check for dups")
    void whenDuplicateClassesExistsThenThrowException() {
        assertEquals(ZERO_DUPS, checkForDuplicateClasses(FOLDER_PATH).size());
    }

    private static Map<String, Integer> checkForDuplicateClasses(String packageName) {
        String path = packageName.replace('.', '/');
        File directory = new File("src/main/java/" + path);
        Map<String, Integer> classMap = new HashMap<>();

        if (directory.exists()) {
            findClasses(directory, classMap);
        }

        classMap.entrySet().removeIf(entry -> entry.getValue() <= 1);

        for (Map.Entry<String, Integer> entry : classMap.entrySet()) {
            if (entry.getValue() > 1) {
                System.out.println("Duplicate class found: " + entry.getKey());
            }
        }

        return classMap;
    }

    private static void findClasses(File directory, Map<String, Integer> classMap) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findClasses(file, classMap);
                } else if (file.isFile() && file.getName().endsWith(".java")) {
                    String className = file.getName().replace(".java", "");
                    classMap.put(className, classMap.getOrDefault(className, 0) + 1);
                }
            }
        }
    }
}