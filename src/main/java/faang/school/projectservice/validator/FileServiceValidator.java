package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.StorageLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class FileServiceValidator {

    @Value("${services.s3.storage_size.standard}")
    private BigInteger STANDARD_STORAGE_SIZE;
    @Value("${services.s3.storage_size.premium}")
    private BigInteger PREMIUM_STORAGE_SIZE;

    public void checkMemoryAvailability(BigInteger size, UserDto userDto) {
        if (userDto.getPremiumStatus().equals("Premium")) {
            if (size.compareTo(PREMIUM_STORAGE_SIZE) > 0) {
                throw new StorageLimitExceededException("Premium storage limit exceeded");
            }
        } else {
            if (size.compareTo(STANDARD_STORAGE_SIZE) > 0) {
                throw new StorageLimitExceededException("Standard storage limit exceeded");
            }
        }
    }

    public void checkAccessRights(long ownerProjectId, long ownerResourceId, long userId) {
        if (ownerProjectId != userId || ownerResourceId != userId) {
            throw new SecurityException("No access rights to delete file");
        }
    }
}
