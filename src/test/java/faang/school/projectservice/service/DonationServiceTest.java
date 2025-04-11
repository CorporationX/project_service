package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.dto.event.FundRaisedEvent;
import faang.school.projectservice.exception.CampaignNotActiveException;
import faang.school.projectservice.exception.DifferentCurrencyException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.mapper.donation.DonationMapperImpl;
import faang.school.projectservice.mapper.donation.PaymentMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.publisher.FundRaisedEventPublisher;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DonationServiceTest {

    private final Long firstId = 1L;
    private final Long secondId = 2L;
    private final BigDecimal firstAmount = new BigDecimal("10000.00");
    private final BigDecimal thirdAmount = new BigDecimal("30000.00");
    private final Currency firstCurrency = Currency.USD;
    private final List<DonationDto> donationDtoList = List.of(
            createDonationDto(firstAmount, firstCurrency, firstId),
            createDonationDto(thirdAmount, firstCurrency, firstId)
    );
    private final List<Donation> listDonations = List.of(
            createDonation(firstId, firstCurrency, firstAmount),
            createDonation(secondId, firstCurrency, thirdAmount)
    );

    @InjectMocks
    private DonationService donationService;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private CampaignRepository campaignRepository;

    @Spy
    private DonationMapperImpl donationMapper;

    @Spy
    private PaymentMapperImpl paymentMapper;

    @Mock
    private PaymentServiceClient paymentClient;

    @Mock
    private DonationFilter createdFromDateFilter;

    @Mock
    private DonationFilter createdToDateFilter;

    @Mock
    private DonationFilter currencyFilter;

    @Mock
    private DonationFilter maxAmountFilter;

    @Mock
    private DonationFilter minAmountFilter;

    @Mock
    private UserContext userContext;

    @Mock
    private FundRaisedEventPublisher eventPublisher;

    @Mock
    private UserServiceClient userClient;

    @BeforeEach
    public void setUp() {
        donationService = new DonationService(donationRepository, campaignRepository, donationMapper, paymentMapper,
                paymentClient, List.of(createdFromDateFilter, createdToDateFilter, currencyFilter, maxAmountFilter,
                minAmountFilter), userContext, userClient, eventPublisher);
    }

    @Test
    public void testNegativeSendDonationWhenUserNotFound() {
        DonationDto donation = donationDtoList.get(0);

        assertThrows(NullPointerException.class, () -> donationService.sendDonation(donation));
    }

    @Test
    public void testNegativeSendDonationWhenCampaignNotFound() {
        DonationDto donation = donationDtoList.get(0);
        includeCheckUserId();

        assertThrows(EntityNotFoundException.class, () -> donationService.sendDonation(donation));
    }

    @Test
    public void testNegativeSendDonationWhenCampaignStatusNull() {
        DonationDto donation = donationDtoList.get(0);
        includeCheckUserId();
        Campaign campaign = createCampaign(null, null);
        when(campaignRepository.findById(donation.campaignId())).thenReturn(Optional.of(campaign));

        assertThrows(NullPointerException.class, () -> donationService.sendDonation(donation));
    }

    @Test
    public void testNegativeSendDonationWhenCampaignStatusNotActive() {
        DonationDto donation = donationDtoList.get(0);
        includeCheckUserId();
        Campaign campaign = createCampaign(CampaignStatus.COMPLETED, null);
        when(campaignRepository.findById(donation.campaignId())).thenReturn(Optional.of(campaign));

        assertThrows(CampaignNotActiveException.class, () -> donationService.sendDonation(donation));
    }

    @Test
    public void testNegativeSendDonationWhenCampaignCurrencyNull() {
        DonationDto donation = donationDtoList.get(0);
        includeCheckUserId();
        Campaign campaign = createCampaign(CampaignStatus.ACTIVE, null);
        when(campaignRepository.findById(donation.campaignId())).thenReturn(Optional.of(campaign));

        assertThrows(NullPointerException.class, () -> donationService.sendDonation(donation));
    }

    @Test
    public void testNegativeSendDonationWhenCurrencyDifferent() {
        DonationDto donation = donationDtoList.get(0);
        includeCheckUserId();
        Campaign campaign = createCampaign(CampaignStatus.ACTIVE, Currency.EUR);
        when(campaignRepository.findById(donation.campaignId())).thenReturn(Optional.of(campaign));

        assertThrows(DifferentCurrencyException.class, () -> donationService.sendDonation(donation));
    }

    @Test
    public void testPositiveSendDonation() {
        try (MockedStatic<UUID> mockedUuid = mockStatic(UUID.class)) {
            UUID uuidMock = mock(UUID.class);
            when(uuidMock.getMostSignificantBits()).thenReturn(firstId);
            mockedUuid.when(UUID::randomUUID).thenReturn(uuidMock);

            DonationDto donation = donationDtoList.get(0);
            includeCheckUserId();
            Campaign campaign = createCampaign(CampaignStatus.ACTIVE, Currency.USD);
            PaymentRequest request = createDonationRequest(firstAmount, firstCurrency);
            PaymentResponse response = createDonationResponse(firstAmount, firstCurrency);
            when(campaignRepository.findById(donation.campaignId())).thenReturn(Optional.of(campaign));
            when(paymentClient.sendPayment(request)).thenReturn(response);

            PaymentResponse result = donationService.sendDonation(donation);

            assertEquals(result.amount(), donation.amount());
            assertEquals(result.currency(), donation.currency());
        }
    }

    @Test
    public void testNegativeFindDonationWhenDonationNotFound() {
        assertThrows(EntityNotFoundException.class, () -> donationService.findDonationById(firstId));
    }

    @Test
    public void testPositiveFindDonation() {
        Donation donation = listDonations.get(0);
        when(donationRepository.findByIdAndUserId(firstId, firstId)).thenReturn(Optional.of(donation));
        when(userContext.getUserId()).thenReturn(firstId);

        DonationDto result = donationService.findDonationById(firstId);

        assertEquals(result.amount(), donation.getAmount());
        assertEquals(result.currency(), donation.getCurrency());
        assertEquals(firstId, donation.getId());
    }

    @Test
    public void testPositiveFindDonationsByFilters() {
        DonationFilterDto filterDto = createFilterDto();

        when(userContext.getUserId()).thenReturn(firstId);
        when(donationRepository.findAll()).thenReturn(listDonations);

        List<DonationDto> result = donationService.findDonationsByFilters(filterDto);

        assertEquals(2, result.size());
    }

    private DonationDto createDonationDto(BigDecimal amount, Currency currency, Long campaignId) {
        return DonationDto.builder()
                .amount(amount)
                .currency(currency)
                .campaignId(campaignId)
                .paymentNumber(firstId)
                .build();
    }

    private Campaign createCampaign(CampaignStatus status, Currency currency) {
        return Campaign.builder()
                .status(status)
                .project(Project.builder()
                        .id(1L)
                        .build())
                .currency(currency)
                .build();
    }

    private PaymentResponse createDonationResponse(BigDecimal amount,
                                                   Currency currency) {
        return PaymentResponse.builder()
                .amount(amount)
                .currency(currency)
                .paymentNumber(firstId)
                .build();
    }

    private PaymentRequest createDonationRequest(BigDecimal amount,
                                                 Currency currency) {
        return PaymentRequest.builder()
                .amount(amount)
                .currency(currency)
                .paymentNumber(firstId)
                .build();
    }

    private DonationFilterDto createFilterDto() {
        return DonationFilterDto.builder()
                .currency(null)
                .maxAmount(null)
                .minAmount(null)
                .fromDonationTime(null)
                .toDonationTime(null)
                .build();
    }

    private Donation createDonation(Long id, Currency currency, BigDecimal amount) {
        return Donation.builder()
                .id(id)
                .userId(firstId)
                .currency(currency)
                .amount(amount)
                .paymentNumber(firstId)
                .donationTime(LocalDateTime.now())
                .build();
    }

    private UserDto createUser(Long id) {
        return UserDto.builder()
                .id(id)
                .build();
    }

    private void includeCheckUserId() {
        when(userContext.getUserId()).thenReturn(firstId);
        when(userClient.getUser(firstId)).thenReturn(createUser(firstId));
    }
}
