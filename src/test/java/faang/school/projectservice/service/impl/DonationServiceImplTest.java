package faang.school.projectservice.service.impl;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.DonationDto;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceImplTest {

    @InjectMocks
    private DonationServiceImpl donationService;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private UserContext userContext;

    @Mock
    private DonationMapper donationMapper;

    private Donation donation;
    private DonationDto donationDto;

    @BeforeEach
    void setUp() {
        donation = new Donation();
        donation.setId(1L);
        donation.setAmount(BigDecimal.TEN);
        donation.setCurrency(Currency.EUR);
        donation.setDonationTime(LocalDateTime.now());

        donationDto = new DonationDto(1L, 2L, 3L, BigDecimal.TEN, Currency.EUR, 123L);
    }

    @Test
    void sendDonation_Success() {
        when(userContext.getUserId()).thenReturn(2L);
        when(donationMapper.toEntity(donationDto)).thenReturn(donation);
        when(donationRepository.save(donation)).thenReturn(donation);
        when(donationMapper.toDto(donation)).thenReturn(donationDto);

        DonationDto result = donationService.sendDonation(donationDto);

        assertNotNull(result);
        assertEquals(BigDecimal.TEN, result.amount());
        verify(paymentServiceClient, times(1)).sendPayment(any(PaymentRequest.class));
    }

    @Test
    void getDonation_Success() {
        when(donationRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.of(donation));
        when(donationMapper.toDto(donation)).thenReturn(donationDto);

        DonationDto result = donationService.getDonation(1L, 2L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }
}
