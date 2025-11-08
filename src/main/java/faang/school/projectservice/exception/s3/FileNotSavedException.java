package faang.school.projectservice.exception.s3;

public class FileNotSavedException extends RuntimeException {
    public FileNotSavedException(String message) {
        super(message);
    }
}
