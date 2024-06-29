package faang.school.projectservice.exception.aws.s3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum S3ExceptionMessages {
    BUCKET_NOT_FOUND_EXCEPTION("S3 bucket Not Found");

    private final String message;
}
