package faang.school.projectservice.exception.aws.s3;

public class NotFoundException extends S3Exception {
    public NotFoundException(String message) {
        super(message);
    }
}
