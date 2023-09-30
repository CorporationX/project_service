package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.PaymentException;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.mapper.DonationMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.validator.DonationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DonationServiceTest {
    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private DonationValidator donationValidator;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private UserContext userContext;

    @Mock
    private DonationMapperImpl donationMapper;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private List<DonationFilter> donationFilters;

    @InjectMocks
    private DonationService donationService;

    private Campaign campaign;
    private DonationDto donationDto;
    private Donation donation;
    private DonationFilterDto donationFilter;

    @BeforeEach
    void setUp() {
        campaign = Campaign.builder().build();
        donationDto = DonationDto.builder()
                .currency(String.valueOf(Currency.USD))
                .amount(BigDecimal.TEN)
                .campaignId(1L)
                .build();
        donation = Donation.builder()
                .currency(Currency.valueOf("USD"))
                .amount(BigDecimal.TEN)
                .campaign(campaign)
                .build();
        donationFilter = DonationFilterDto.builder().build();
    }

    @Test
    public void testCreateDonation_WithValidCampaign_ShouldReturnPaymentResponse() {
        PaymentResponse paymentResponse = PaymentResponse.builder().build();
        Long userId = 123L;
        long paymentNumber = 1234567890L;

        when(campaignRepository.findById(donationDto.getCampaignId())).thenReturn(Optional.of(campaign));
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);
        when(userContext.getUserId()).thenReturn(userId);
        when(donationMapper.toEntity(donationDto)).thenReturn(new Donation());
        doNothing().when(donationValidator).validateCampaign(campaign);
        doNothing().when(donationValidator).validatePaymentResponse(paymentResponse, paymentNumber);

        PaymentResponse result = donationService.createDonation(donationDto);

        assertNotNull(result);
        assertEquals(paymentResponse, result);
        verify(campaignRepository, times(1)).findById(donationDto.getCampaignId());
        verify(donationValidator, times(1)).validateCampaign(campaign);
        verify(paymentServiceClient, times(1)).sendPayment(any(PaymentRequest.class));
        verify(userContext, times(1)).getUserId();
        verify(donationMapper, times(1)).toEntity(donationDto);
        verify(donationRepository, times(1)).save(any(Donation.class));
    }

    @Test
    public void testCreateDonation_WithInvalidCampaign_ShouldThrowEntityNotFoundException() {
        when(campaignRepository.findById(donationDto.getCampaignId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> donationService.createDonation(donationDto));
    }

    @Test
    public void testCreateDonation_WithCancelledCampaign_ShouldThrowPaymentException() {
        campaign.setStatus(CampaignStatus.CANCELED);
        when(campaignRepository.findById(donationDto.getCampaignId())).thenReturn(Optional.of(campaign));
        doThrow(PaymentException.class).when(donationValidator).validateCampaign(campaign);
        assertThrows(PaymentException.class, () -> donationService.createDonation(donationDto));
    }

    @Test
    public void testGetDonationById_WithValidId_ShouldReturnDonation() {
        when(userContext.getUserId()).thenReturn(1L);
        when(donationRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(donation));
        when(donationMapper.toDto(donation)).thenReturn(donationDto);
        DonationDto result = donationService.getDonationById(1L);
        assertNotNull(result);
    }

    @Test
    public void testGetDonationById_WithInvalidId_ShouldThrowEntityNotFoundException() {
        when(userContext.getUserId()).thenReturn(1L);
        when(donationRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> donationService.getDonationById(1L));
    }

    @Test
    public void testGetAllDonations_WithValidUserId_ShouldReturnAllDonations() {
        when(userContext.getUserId()).thenReturn(1L);
        when(donationRepository.findAllByUserId(anyLong(), eq(PageRequest.of(0, 100)))).thenReturn(List.of(donation));
        when(donationMapper.toDtoList(List.of(donation))).thenReturn(List.of(donationDto));
        List<DonationDto> result = donationService.getAllDonations();
        assertNotNull(result);
    }

    @Test
    public void testGetAllDonationsByFilters_WithValidUserId_ShouldReturnAllDonations() {
        when(userContext.getUserId()).thenReturn(1L);
        when(donationRepository.findAllByUserId(anyLong(), eq(PageRequest.of(0, 100)))).thenReturn(List.of(donation));
        when(donationMapper.toDtoList(List.of(donation))).thenReturn(List.of(donationDto));
        List<DonationDto> result = donationService.getAllDonationsByFilters(donationFilter);
        assertNotNull(result);
    }
}