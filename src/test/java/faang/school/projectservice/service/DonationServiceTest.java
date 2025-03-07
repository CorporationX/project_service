package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.PaymentFailedException;
import faang.school.projectservice.mapper.DonationMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.publishers.FundRaisedEventPublisher;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.validator.CampaignValidator;
import feign.FeignException;
import feign.Request;
import feign.Response;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DonationServiceTest {
    @Mock
    private DonationRepository donationRepository;
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Mock
    private CampaignValidator campaignValidator;
    @Mock
    private CampaignService campaignService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private FundRaisedEventPublisher fundRaisedEventPublisher;
    @Spy
    private DonationMapperImpl donationMapper;
    private DonationService donationService;

    private DonationFilterDto donationFilterDto;
    private DonationCreateDto donationCreateDto;

    @BeforeEach
    void setUp() {
        donationCreateDto = DonationCreateDto.builder().campaignId(1L).build();

        donationFilterDto = new DonationFilterDto();

        donationService = new DonationService(donationRepository, campaignRepository,
                donationMapper, campaignService, paymentServiceClient,
                campaignValidator, userServiceClient, fundRaisedEventPublisher);
    }

    @Test
    void testSendDonation_ShouldThrowExceptionWhenUserNotFound() {
        when(userServiceClient.getUser(1L)).thenThrow(FeignException.errorStatus("exception",
                Response.builder()
                        .status(404)
                        .request(Request.create(Request.HttpMethod.GET, "/users", Map.of(), null, null, null))
                        .body("error message".getBytes())
                        .build()));

        assertThrows(DataValidationException.class,
                () -> donationService.sendDonation(donationCreateDto, 1L));
    }

    @Test
    void testSendDonation_ShouldThrowExceptionWhenPaymentStatusIsNotSuccess() {
        PaymentResponse response = new PaymentResponse("FAILED", 1, 1L, new BigDecimal(500), Currency.EUR, "message");

        when(paymentServiceClient.sendPayment(any(PaymentRequest.class)))
                .thenReturn(response);
        assertThrows(PaymentFailedException.class,
                () -> donationService.sendDonation(donationCreateDto, 1L));
    }

    @Test
    void testSendDonation_ShouldThrowExceptionWhenPaymentFailed() {
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class)))
                .thenThrow(FeignException.errorStatus("exception",
                        Response.builder()
                                .status(500)
                                .request(Request.create(Request.HttpMethod.POST,
                                        "/api/payment", Map.of(), null, null, null))
                                .body("error message".getBytes())
                                .build()));

        assertThrows(PaymentFailedException.class,
                () -> donationService.sendDonation(donationCreateDto, 1L));
    }

    @Test
    void testSendDonation_Success() {
        BigDecimal amount = new BigDecimal(500);
        LocalDateTime donationTime = LocalDateTime.now();
        Project project = Project.builder().id(1L).build();
        Campaign campaign = Campaign.builder()
                .id(1L)
                .status(CampaignStatus.ACTIVE)
                .goal(new BigDecimal(1000))
                .amountRaised(new BigDecimal(0))
                .project(project)
                .build();

        Donation donation = Donation.builder()
                .id(1L)
                .paymentNumber(111L)
                .amount(amount)
                .donationTime(donationTime)
                .campaign(campaign)
                .currency(Currency.EUR)
                .userId(1L)
                .build();

        PaymentResponse paymentResponse = new PaymentResponse("SUCCESS", 1, 1L,
                amount, Currency.EUR, "message");

        when(campaignService.findCampaignById(1L)).thenReturn(campaign);
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class)))
                .thenReturn(paymentResponse);
        when(donationRepository.save(any(Donation.class))).thenReturn(donation);

        DonationDto donationDto = DonationDto.builder()
                .id(1L)
                .paymentNumber(111L)
                .amount(amount)
                .donationTime(donationTime)
                .campaignId(1L)
                .currency(Currency.EUR)
                .userId(1L)
                .build();

        DonationCreateDto dto = DonationCreateDto.builder()
                .amount(amount)
                .campaignId(1L)
                .currency(Currency.EUR)
                .build();

        assertEquals(donationDto, donationService.sendDonation(dto, 1L));
    }

    @Test
    void testGetDonationByIdAndUserId_ShouldThrowExceptionWhenDonationNotFound() {
        when(donationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                donationService.getDonationByIdAndUserId(1L, 1L));
    }

    @Test
    void testGetDonationByIdAndUserId_Success() {
        Donation donation = new Donation();
        donation.setId(2L);
        donation.setAmount(new BigDecimal(500));

        when(donationRepository.findByIdAndUserId(2L, 1L)).thenReturn(Optional.of(donation));

        assertEquals(donationMapper.toDto(donation), donationService.getDonationByIdAndUserId(2L, 1L));
    }


    @Test
    void testGetAllDonationsByUser_ShouldReturnFilteredList() {
        Donation donation1 = Donation.builder().currency(Currency.EUR).userId(1L).build();

        List<Donation> input = List.of(donation1);

        donationFilterDto.setCurrency(Currency.EUR);

        when(donationRepository.findAllByUserIdFilteredAndThenSortedByDate(
                1L,
                donationFilterDto.getStartDate(),
                donationFilterDto.getEndDate(),
                donationFilterDto.getCurrency(),
                donationFilterDto.getMaxAmount(),
                donationFilterDto.getMinAmount()
        )).thenReturn(input);

        List<DonationDto> expected = List.of(donationMapper.toDto(donation1));
        List<DonationDto> actual = donationService.getAllDonationsByUser(1L, donationFilterDto);

        assertEquals(expected, actual);
    }
}
