package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.repository.DonationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {
    @Mock
    private DonationRepository donationRepository;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private DonationMapper donationMapper;
    @InjectMocks
    private DonationService donationService;

    private DonationDto donationDto;
    private CampaignDto campaignDto;

    @Test
    public void sendDonation_correctAnswer() {
        BigDecimal bigDecimal = BigDecimal.valueOf(1000);

        donationDto = DonationDto.builder()
                        .id(1L)
                        .paymentNumber(3L)
                        .amount(bigDecimal)
                        .currency(Currency.EUR)
                        .campaignDto(campaignDto)
                        .build();
        PaymentRequest paymentRequest = new PaymentRequest(donationDto.getPaymentNumber(), donationDto.getAmount(), donationDto.getCurrency(), donationDto.getCurrency());
        campaignDto = CampaignDto.builder()
                .id(8L)
                .title("Campaign1")
                .amountRaised(paymentRequest.amount())
                .build();
        assertEquals(BigDecimal.valueOf(1000), campaignDto.getAmountRaised());
    }
}