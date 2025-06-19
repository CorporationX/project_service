package faang.school.projectservice.exception.common;

public class FileCorruptedException extends RuntimeException {
    public FileCorruptedException(String message) {
        super(message);
    }
}