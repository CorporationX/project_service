package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.StorageLimitExceededException;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class FileServiceValidator {
    private final BigInteger STANDARD_STORAGE_SIZE = new BigInteger("2147483648");
    private final BigInteger PREMIUM_STORAGE_SIZE = new BigInteger("4294967296");

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
}
