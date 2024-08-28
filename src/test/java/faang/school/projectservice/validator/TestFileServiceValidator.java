package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exception.StorageLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

//@ExtendWith(MockitoExtension.class)
//public class TestFileServiceValidator {
//    private FileServiceValidator fileServiceValidator;
//    private UserDto userDto;
//
//    @BeforeEach
//    public void setUp() {
//        userDto = new UserDto();
//        fileServiceValidator = new FileServiceValidator();
//    }
//
//    @DisplayName("Если пользователь не премиум и память свободна")
//    @Test
//    public void testCheckMemoryAvailabilityUserClassicMemoryFree() {
//        userDto.setPremiumStatus("Classic");
//        BigInteger size = new BigInteger("12345");
//
//        assertDoesNotThrow(() -> fileServiceValidator.checkMemoryAvailability(size, userDto));
//    }
//
//    @DisplayName("Если пользователь не премиум и память заполнена")
//    @Test
//    public void testCheckMemoryAvailabilityUserClassicMemoryFull() {
//        userDto.setPremiumStatus("Classic");
//        BigInteger size = new BigInteger("2147483650");
//
//        assertThrows(StorageLimitExceededException.class, () -> fileServiceValidator.checkMemoryAvailability(size, userDto));
//    }
//
//    @DisplayName("Если пользователь премиум и память свободна")
//    @Test
//    public void testCheckMemoryAvailabilityUserPremiumMemoryFree() {
//        userDto.setPremiumStatus("Premium");
//        BigInteger size = new BigInteger("4294967280");
//
//        assertDoesNotThrow(() -> fileServiceValidator.checkMemoryAvailability(size, userDto));
//    }
//
//    @DisplayName("Если пользователь премиум и память заполнена")
//    @Test
//    public void testCheckMemoryAvailabilityUserPremiumMemoryFull() {
//        userDto.setPremiumStatus("Premium");
//        BigInteger size = new BigInteger("4294967300");
//
//        assertThrows(StorageLimitExceededException.class, () -> fileServiceValidator.checkMemoryAvailability(size, userDto));
//    }
//}
