package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.donation.DonationCurrencyFilter;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.mapper.donation.DonationMapper;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private CampaignService campaignService;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    UserContext userContext;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private DonationMapper donationMapper;

    @Mock
    private DonationCurrencyFilter donationCurrencyFilter;

    @Mock
    private List<DonationFilter> donationFilters;

    @Mock
    Iterator<DonationFilter> iterator;

    @InjectMocks
    private DonationService donationService;

    private DonationDto donationDto;
    private Donation donation;
    private CampaignDto campaignDto;

    @BeforeEach
    void setUp() {
        donationDto = new DonationDto(
                1L,
                null,
                BigDecimal.valueOf(100L),
                LocalDateTime.now(),
                123L,
                Currency.USD
        );
        donation = new Donation();
        donation.setAmount(BigDecimal.valueOf(100));
        donation.setCurrency(Currency.USD);

        campaignDto = new CampaignDto(
                123L,
                "Campaign",
                "Description",
                "1000",
                "500",
                "ACTIVE",
                1L,
                "USD",
                "2025-03-29",
                1L,
                "2025-03-29",
                2L
        );

        donationFilters.add(donationCurrencyFilter);
    }

    @Test
    void testCreateDonationSuccess() {
        when(userContext.getUserId()).thenReturn(2L);
        when(campaignService.getCampaignById(123L)).thenReturn(campaignDto);
        when(userServiceClient.getUser(2L)).thenReturn(null);
        when(donationMapper.toEntity(donationDto)).thenReturn(donation);
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class)))
                .thenReturn(
                        new PaymentResponse(
                                "SUCCESS",
                                123,
                                987L,
                                BigDecimal.TEN,
                                Currency.USD,
                                "message"
                        )
                );

        donationService.createDonation(donationDto);

        assertEquals(987L, donation.getPaymentNumber());
        verify(donationRepository, times(1)).save(donation);
    }

    @Test
    void testCreateDonationFailedCampaignStatus() {
        campaignDto = new CampaignDto(123L, "Campaign", "Description", "1000", "500", "CLOSED", 1L, "USD", "2025-03-29", 1L, "2025-03-29", 2L);
        when(campaignService.getCampaignById(123L)).thenReturn(campaignDto);

        assertThrows(DataValidationException.class, () -> donationService.createDonation(donationDto));
        verify(donationRepository, never()).save(any());
    }

    @Test
    void testGetDonationByIdAndUserIdSuccess() {
        when(userContext.getUserId()).thenReturn(2L);
        when(userServiceClient.getUser(2L)).thenReturn(null);
        when(donationRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.of(donation));
        when(donationMapper.toDto(donation)).thenReturn(donationDto);

        DonationDto result = donationService.getDonationByIdAndUserId(1L);

        assertNotNull(result);
        assertEquals(donationDto, result);
    }

    @Test
    void testGetDonationByIdAndUserIdNotFound() {
        when(userContext.getUserId()).thenReturn(2L);
        when(userServiceClient.getUser(2L)).thenReturn(null);
        when(donationRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> donationService.getDonationByIdAndUserId(1L));
    }

    @Test
    void testGetAllDonationsByUserId() {
        DonationFilterDto filterDto = new DonationFilterDto(
                null,
                null,
                null,
                null
        );
        List<Donation> donations = List.of(donation);
        when(userContext.getUserId()).thenReturn(2L);
        when(userServiceClient.getUser(2L)).thenReturn(null);
        when(donationRepository.findAllByUserId(2L)).thenReturn(donations);
        when(donationFilters.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true).thenReturn(false);
        when(iterator.next()).thenReturn(donationCurrencyFilter);
        when(donationCurrencyFilter.isApplicable(any())).thenReturn(true);
        when(donationCurrencyFilter.apply(any(), any())).thenReturn(donations.stream());
        when(donationMapper.toDto(anyList())).thenReturn(List.of(donationDto));

        List<DonationDto> result = donationService.getAllDonationsByUserId(filterDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(donationDto, result.get(0));
    }
}
