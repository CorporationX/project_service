package faang.school.projectservice.utils;

public class FileUtils {
    public static String getFileExtension(String filename) {
        if (filename == null) {
            return "jpg";
        }
        String[] parts = filename.split("\\.");
        return parts.length > 1 ? parts[parts.length - 1].toLowerCase() : "jpg";
    }
}
