package faang.school.projectservice.exception.aws.s3;

import lombok.Getter;

@Getter
public enum S3ExceptionMessages {
    BUCKET_NOT_FOUND_EXCEPTION("S3 bucket Not Found"),;
    
    S3ExceptionMessages(String message) {
        this.message = message;
    }
    
    private String message;
}
