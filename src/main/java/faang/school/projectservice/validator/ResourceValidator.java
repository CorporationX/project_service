package faang.school.projectservice.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.math.BigInteger;

@Component
@Slf4j
public class ResourceValidator {
    public void checkStorageSizeExceeded(BigInteger newStorageSize, BigInteger projectMaxStorageSize) {
        if (newStorageSize.compareTo(projectMaxStorageSize) >= 0) {
            log.info("{} is less than {}", projectMaxStorageSize.toString(), newStorageSize.toString());
            throw new MaxUploadSizeExceededException(projectMaxStorageSize.longValue());
        }
    }
}
