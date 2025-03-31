package faang.school.projectservice.exception.presentation;

public class UploadFileToMinioError extends RuntimeException {
    public UploadFileToMinioError(String message) {
        super(message);
    }
}
