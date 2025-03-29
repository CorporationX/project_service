package faang.school.projectservice.validation;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class DonationValidatorTest {

    private static final long USER_ID = 1L;
    private static final long CAMPAIGN_ID = 1L;

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private DonationValidator validator;

    private DonationCreateDto donation;

    private UserDto user;

    @BeforeEach
    void setUp() {
        donation = new DonationCreateDto();
        donation.setCampaignId(CAMPAIGN_ID);
        donation.setAmount(BigDecimal.valueOf(100));
        user = new UserDto(USER_ID, "razrab", "123@gmail.com");
    }

    @Test
    @DisplayName("Проверка при сумме доната меньше нуля")
    void testValidateAmountLessThanZero() {
        donation.setAmount(BigDecimal.valueOf(-1));

        Exception exception = assertThrows(DataValidationException.class,
                () -> validator.validateDonation(donation, USER_ID));

        assertEquals("amount must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка при сумме доната равной нулю")
    void testValidateAmountEqualZero() {
        donation.setAmount(BigDecimal.valueOf(0));

        Exception exception = assertThrows(DataValidationException.class,
                () -> validator.validateDonation(donation, USER_ID));

        assertEquals("amount must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка отправления доната несуществующей компании")
    void testValidateCampaignNotFound() {
        Mockito.when(campaignRepository.existsById(CAMPAIGN_ID)).thenReturn(false);

        Exception exception = assertThrows(DataValidationException.class,
                () -> validator.validateDonation(donation, USER_ID));

        assertEquals("campaign with id " + CAMPAIGN_ID + " not found", exception.getMessage());
    }

    @Test
    @DisplayName("Проверка отправления доната несуществующим пользователем")
    void testValidateUserNotFound() {
        Mockito.when(campaignRepository.existsById(CAMPAIGN_ID)).thenReturn(true);
        Mockito.when(userServiceClient.getUser(USER_ID)).thenReturn(null);

        Exception exception = assertThrows(DataValidationException.class,
                () -> validator.validateDonation(donation, USER_ID));

        assertEquals("user with id " + USER_ID + " not found", exception.getMessage());
    }

    @Test
    @DisplayName("Успешная проверка валидного доната")
    void testValidateSuccess() {
        Mockito.when(campaignRepository.existsById(CAMPAIGN_ID)).thenReturn(true);
        Mockito.when(userServiceClient.getUser(USER_ID)).thenReturn(user);

        assertDoesNotThrow(() -> validator.validateDonation(donation, USER_ID));
    }
}