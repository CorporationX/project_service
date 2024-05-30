package faang.school.projectservice.validation.donation;

import faang.school.projectservice.dto.donation.DonationToSendDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.repository.CampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DonationValidatorTest {
    @Mock
    private CampaignRepository campaignRepository;
    @InjectMocks
    private DonationValidator validator;

    private DonationToSendDto dto;

    @BeforeEach
    void init() {
        dto = DonationToSendDto.builder()
                .paymentNumber(1234L)
                .amount(BigDecimal.valueOf(567L))
                .campaignId(1L)
                .currency("USD")
                .build();
    }

    @Test
    void shouldThrowExceptionForInactiveCampaign() {
        when(campaignRepository.isCampaignActive(dto.getCampaignId())).thenReturn(false);

        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validateSendDonation(dto));
        assertEquals("Donation cannot be sent to inactive campaign", e.getMessage());

        verify(campaignRepository, times(1)).isCampaignActive(dto.getCampaignId());
    }
}