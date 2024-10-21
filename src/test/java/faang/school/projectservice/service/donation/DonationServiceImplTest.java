package faang.school.projectservice.service.donation;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.dto.client.Currency;
import faang.school.projectservice.model.dto.donation.DonationDto;
import faang.school.projectservice.model.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.entity.Campaign;
import faang.school.projectservice.model.entity.Donation;
import faang.school.projectservice.model.filter.donation.MinAmountFilter;
import faang.school.projectservice.model.mapper.donation.DonationMapperImpl;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.impl.donation.DonationServiceImpl;
import faang.school.projectservice.validator.donation.DonationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyLong;

@ExtendWith(MockitoExtension.class)
public class DonationServiceImplTest {

    @Mock
    private DonationRepository donationRepository;
    @Mock
    private DonationValidator donationValidator;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Spy
    private DonationMapperImpl donationMapper;
    @Mock
    private UserContext userContext;

    private MinAmountFilter minAmountFilter;
    private DonationServiceImpl donationService;
    private DonationDto donationDto;
    private Donation donation;

    @BeforeEach
    void setup() {
        minAmountFilter = new MinAmountFilter();
        donationService = new DonationServiceImpl(donationRepository, donationValidator, paymentServiceClient,
                donationMapper, userContext, List.of(minAmountFilter));

        donationDto = DonationDto.builder()
                .userId(1L)
                .amount(BigDecimal.valueOf(300))
                .currency(Currency.USD)
                .campaignId(1L)
                .paymentNumber(10L)
                .build();

        donation = Donation.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(300L))
                .campaign(Campaign.builder().build())
                .currency(Currency.USD)
                .donationTime(LocalDateTime.now())
                .userId(1L)
                .build();
    }

    @Test
    void testSendDonation_OK() {
        when(donationRepository.save(any())).thenReturn(donation);

        DonationDto savedDto = donationService.sendDonation(donationDto);

        assertEquals(donationDto.amount(), savedDto.amount());
        assertEquals(donationDto.currency(), savedDto.currency());
        assertEquals(donationDto.userId(), savedDto.userId());
    }

    @Test
    void testGetDonationByIdOk() {
        when(userContext.getUserId()).thenReturn(1L);
        when(donationRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(donation));
        DonationDto savedDonation = donationService.getDonationById(1L);

        verify(donationRepository).findByIdAndUserId(1L, 1L);
        assertEquals(BigDecimal.valueOf(300L), savedDonation.amount());
        assertEquals(Currency.USD, savedDonation.currency());
    }

    @Test
    void testGetAllDonationsByUserId() {
        Donation secondDonation = Donation.builder()
                .userId(1L)
                .amount(BigDecimal.valueOf(200))
                .donationTime(LocalDateTime.now())
                .build();

        DonationFilterDto filterDto = DonationFilterDto.builder()
                .minAmount(BigDecimal.valueOf(250)).build();
        when(donationRepository.findAllByUserId(anyLong())).thenReturn(List.of(donation, secondDonation));

        List<DonationDto> allDonationsByUserId = donationService.getAllDonationsByUserId(1L, filterDto);

        assertEquals(1, allDonationsByUserId.size());
    }
}
