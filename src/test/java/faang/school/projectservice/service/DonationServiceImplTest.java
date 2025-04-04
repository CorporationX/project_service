package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationCreateRequest;
import faang.school.projectservice.dto.donation.DonationResponse;
import faang.school.projectservice.dto.donation.SearchDonationDto;
import faang.school.projectservice.exception.campaign.CampaignCanceledException;
import faang.school.projectservice.exception.campaign.CampaignCompletedException;
import faang.school.projectservice.exception.campaign.CampaignExceptionMessage;
import faang.school.projectservice.exception.campaign.CampaignNotFoundException;
import faang.school.projectservice.exception.donation.DonationExceptionMessage;
import faang.school.projectservice.exception.donation.DonationNotFoundException;
import faang.school.projectservice.exception.donation.ExceedDonationAmountException;
import faang.school.projectservice.exception.payment.PaymentExceptionMessage;
import faang.school.projectservice.exception.payment.PaymentFailedException;
import faang.school.projectservice.exception.user.UserExceptionMessage;
import faang.school.projectservice.exception.user.UserNotFoundException;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.filter.donation.TestDonationCreationDateFilter;
import faang.school.projectservice.filter.donation.TestDonationMinAmountFilter;
import faang.school.projectservice.mapper.campaign.CampaignMapperImpl;
import faang.school.projectservice.mapper.donation.DonationMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.campaign.CampaignService;
import faang.school.projectservice.service.donation.DonationServiceImpl;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test cases of DonationServiceImplTest")
public class DonationServiceImplTest {

    private static final long USER_ID = 1L;
    private static final long CAMPAIGN_ID = 1L;
    private static final long DONATION_ID = 1L;
    private static final long NON_EXIST_ID = 100L;
    private static final long PAYMENT_NUMBER = 1000_0000_0000_0000L;
    private static final LocalDateTime DONATION_TIME = LocalDateTime.now();
    private static final String PAYMENT_SUCCESSFUL_MESSAGE = "Dear friend! Thank you for your purchase! " +
            "Your payment on 1,00 USD was accepted.";
    private static final String PAYMENT_FAILS_MESSAGE = "payment is failed...";

    private final DonationFilter donationCreationDateFilter = new TestDonationCreationDateFilter();
    private final DonationFilter donationMinAmountFilter = new TestDonationMinAmountFilter();

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private CampaignService campaignService;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private CampaignMapperImpl campaignMapper;

    @Spy
    private DonationMapperImpl donationMapper;

    @Captor
    private ArgumentCaptor<Donation> captor;

    private DonationServiceImpl donationService;

    private DonationCreateRequest donationCreateRequest;
    private Campaign campaign;

    @BeforeEach
    public void setUp() {
        setUpDonationRequest();
        setUpCampaign();

        ReflectionTestUtils.setField(donationMapper, "campaignMapper", campaignMapper);

        setUpDonationService();
    }

    @Test
    @DisplayName("createDonation - non-exist user ID")
    public void testCreateDonationWithNonExistUser() {
        donationCreateRequest.setUserId(NON_EXIST_ID);
        when(userServiceClient.getUser(NON_EXIST_ID))
                .thenThrow(FeignException.class);

        Exception exception = assertThrows(UserNotFoundException.class,
                () -> donationService.createDonation(donationCreateRequest));

        assertEquals(UserExceptionMessage.getNotFound(NON_EXIST_ID), exception.getMessage());
    }

    @Test
    @DisplayName("createDonation - non-exist campaign ID")
    public void testCreateDonationWithNonExistCampaign() {
        donationCreateRequest.setCampaignId(NON_EXIST_ID);
        when(campaignService.findById(NON_EXIST_ID))
                .thenThrow(new CampaignNotFoundException(CampaignExceptionMessage.getNotFound(NON_EXIST_ID)));

        Exception exception = assertThrows(CampaignNotFoundException.class,
                () -> donationService.createDonation(donationCreateRequest));

        assertEquals(CampaignExceptionMessage.getNotFound(NON_EXIST_ID), exception.getMessage());
    }

    @Test
    @DisplayName("createDonation - campaign is completed")
    public void testCreateDonationWithCompletedCampaign() {
        campaign.setStatus(CampaignStatus.COMPLETED);
        when(campaignService.findById(CAMPAIGN_ID))
                .thenReturn(campaign);

        Exception exception = assertThrows(CampaignCompletedException.class,
                () -> donationService.createDonation(donationCreateRequest));

        assertEquals(CampaignExceptionMessage.getCompleted(CAMPAIGN_ID), exception.getMessage());
    }

    @Test
    @DisplayName("createDonation - campaign is canceled")
    public void testCreateDonationWithCanceledCampaign() {
        campaign.setStatus(CampaignStatus.CANCELED);
        when(campaignService.findById(CAMPAIGN_ID))
                .thenReturn(campaign);

        Exception exception = assertThrows(CampaignCanceledException.class,
                () -> donationService.createDonation(donationCreateRequest));

        assertEquals(CampaignExceptionMessage.getCanceled(CAMPAIGN_ID), exception.getMessage());
    }

    @Test
    @DisplayName("createDonation - exceeded donation amount")
    public void testCreateDonationWithExceededAmount() {
        donationCreateRequest.setAmount(BigDecimal.valueOf(100L));
        when(campaignService.findById(CAMPAIGN_ID))
                .thenReturn(campaign);

        Exception exception = assertThrows(ExceedDonationAmountException.class,
                () -> donationService.createDonation(donationCreateRequest));

        assertEquals(DonationExceptionMessage.EXCEED_AMOUNT, exception.getMessage());
    }

    @Test
    @DisplayName("createDonation - unsuccessfully payment status")
    public void testCreateDonationWithFailedPayment() {
        PaymentResponse invalidPayment = PaymentResponse.builder()
                .status("FAILED")
                .message(PAYMENT_FAILS_MESSAGE)
                .build();
        when(campaignService.findById(CAMPAIGN_ID))
                .thenReturn(campaign);
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class)))
                .thenReturn(invalidPayment);

        Exception exception = assertThrows(PaymentFailedException.class,
                () -> donationService.createDonation(donationCreateRequest));

        assertEquals(PaymentExceptionMessage.getFailed(PAYMENT_FAILS_MESSAGE), exception.getMessage());
    }

    @Test
    @DisplayName("createDonation - success")
    public void testCreateDonationSuccess() {
        when(campaignService.findById(CAMPAIGN_ID))
                .thenReturn(campaign);
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class)))
                .thenReturn(createPaymentResponse());

        donationService.createDonation(donationCreateRequest);

        assertEquals(BigDecimal.ONE, campaign.getAmountRaised());
        verify(donationRepository, times(1)).save(captor.capture());
        Donation donation = captor.getValue();
        assertEquals(donationCreateRequest.getAmount(), donation.getAmount());
        assertEquals(donationCreateRequest.getCurrency(), donation.getCurrency());
        assertEquals(donationCreateRequest.getUserId(), donation.getUserId());
        assertEquals(donationCreateRequest.getCampaignId(),donation.getCampaign().getId());
    }

    @Test
    @DisplayName("getDonation - non-exist user ID")
    public void testGetDonationWithNonExistUser() {
        when(userServiceClient.getUser(NON_EXIST_ID))
                .thenThrow(FeignException.class);

        Exception exception = assertThrows(UserNotFoundException.class,
                () -> donationService.getDonation(DONATION_ID, NON_EXIST_ID));

        assertEquals(UserExceptionMessage.getNotFound(NON_EXIST_ID), exception.getMessage());
    }

    @Test
    @DisplayName("getDonation - non-exist donation ID")
    public void testGetDonationWithNonExistDonation() {
        when(donationRepository.findByIdAndUserId(NON_EXIST_ID, USER_ID))
                .thenThrow(new DonationNotFoundException(DonationExceptionMessage.NOT_FOUND));

        Exception exception = assertThrows(DonationNotFoundException.class,
                () -> donationService.getDonation(NON_EXIST_ID, USER_ID));

        assertEquals(DonationExceptionMessage.NOT_FOUND, exception.getMessage());
    }

    @Test
    @DisplayName("getDonation - success")
    public void testGetDonationSuccess() {
        when(donationRepository.findByIdAndUserId(USER_ID, DONATION_ID))
                .thenReturn(Optional.of(createDonation()));

        DonationResponse donationResponse = donationService.getDonation(USER_ID, DONATION_ID);

        assertEquals(DONATION_ID, donationResponse.getId());
    }

    @Test
    @DisplayName("getDonations - without suitable donation")
    public void testGetDonationsNoSuitableDonation() {
        Donation firstDonation = Donation.builder()
                .donationTime(LocalDateTime.now().minusDays(1L))
                .amount(BigDecimal.valueOf(25L))
                .build();
        Donation secondDonation = Donation.builder()
                .donationTime(LocalDateTime.now().minusDays(1L))
                .amount(BigDecimal.valueOf(45L))
                .build();
        when(donationRepository.findAllByUserId(USER_ID))
                .thenReturn(List.of(firstDonation, secondDonation));

        List<DonationResponse> result = donationService.getDonations(
                USER_ID,
                new SearchDonationDto(null, null, null, null)
        );

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getDonations - success")
    public void testGetDonationsSuccess() {
        Donation firstDonation = Donation.builder()
                .donationTime(LocalDateTime.now())
                .amount(BigDecimal.valueOf(50L))
                .build();
        Donation secondDonation = Donation.builder()
                .donationTime(LocalDateTime.now().minusDays(1L))
                .amount(BigDecimal.valueOf(45L))
                .build();
        when(donationRepository.findAllByUserId(USER_ID))
                .thenReturn(List.of(firstDonation, secondDonation));

        List<DonationResponse> result = donationService.getDonations(
                USER_ID,
                new SearchDonationDto(null, null, null, null)
        );

        assertEquals(1, result.size());
        assertEquals(LocalDate.now(), result.get(0).getDonationTime().toLocalDate());
        assertTrue(result.get(0).getAmount().compareTo(BigDecimal.valueOf(50L)) >= 0);
    }

    private void setUpDonationRequest() {
        donationCreateRequest = DonationCreateRequest.builder()
                .amount(BigDecimal.ONE)
                .campaignId(CAMPAIGN_ID)
                .currency(Currency.USD)
                .userId(USER_ID)
                .build();
    }

    private void setUpCampaign() {
        campaign = Campaign.builder()
                .id(CAMPAIGN_ID)
                .title("Java Bootcamp")
                .description("bootcamp")
                .goal(BigDecimal.TEN)
                .amountRaised(BigDecimal.ZERO)
                .status(CampaignStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .createdBy(USER_ID)
                .currency(Currency.USD)
                .build();
    }

    private void setUpDonationService() {
        donationService = new DonationServiceImpl(
                donationRepository,
                donationMapper,
                campaignService,
                paymentServiceClient,
                userServiceClient,
                List.of(donationCreationDateFilter, donationMinAmountFilter)
        );
    }

    private static PaymentResponse createPaymentResponse() {
        return PaymentResponse.builder()
                .status("SUCCESS")
                .verificationCode(1000)
                .paymentNumber(PAYMENT_NUMBER)
                .amount(BigDecimal.ONE)
                .paymentCurrency(Currency.USD)
                .message(PAYMENT_SUCCESSFUL_MESSAGE)
                .build();
    }

    private Donation createDonation() {
        return Donation.builder()
                .id(DONATION_ID)
                .paymentNumber(PAYMENT_NUMBER)
                .amount(BigDecimal.ONE)
                .donationTime(DONATION_TIME)
                .campaign(campaign)
                .currency(Currency.USD)
                .userId(USER_ID)
                .build();
    }
}
