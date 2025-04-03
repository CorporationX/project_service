package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.client.PaymentStatus;
import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.dto.donation.DonationViewDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.donation.filters.TestDonationCurrencyFilter;
import faang.school.projectservice.filter.donation.filters.TestDonationTimeFilter;
import faang.school.projectservice.mapper.DonationMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.validation.DonationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long DONATION_ID = 1L;

    @Mock
    private DonationValidator validator;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private PaymentServiceClient paymentService;

    @Mock
    private CampaignRepository campaignRepository;

    @Spy
    private DonationMapperImpl donationMapper;

    @Spy
    private TestDonationCurrencyFilter currencyFilter;

    @Spy
    private TestDonationTimeFilter timeFilter;

    @InjectMocks
    private DonationService donationService;

    private final Donation donation = new Donation();
    private final DonationCreateDto donationCreateDto = new DonationCreateDto();
    private final DonationViewDto donationViewDto = new DonationViewDto();
    private final DonationFilterDto donationFilterDto = new DonationFilterDto();
    private final Campaign campaign = new Campaign();
    private PaymentRequest paymentRequest;
    private PaymentResponse paymentResponse;


    @BeforeEach
    void setUp() {
        donationService = new DonationService(
                validator,
                donationMapper,
                donationRepository,
                List.of(currencyFilter,
                        timeFilter),
                paymentService,
                campaignRepository
        );

        donation.setCampaign(campaign);

        donationFilterDto.setCurrency(Currency.USD);
        donationFilterDto.setDonationTime(LocalDateTime.parse("2025-01-01T10:20:37"));

        paymentRequest = new PaymentRequest(
                donation.getPaymentNumber(),
                donation.getAmount(),
                donation.getCurrency(),
                donation.getCampaign().getCurrency());

        paymentResponse = new PaymentResponse(
                PaymentStatus.SUCCESS,
                12,
                123,
                null,
                null,
                null,
                null
        );
    }

    @Test
    @DisplayName("Ошибка при отправке платежа")
    void testSendDonationPaymentError() {
        Mockito.when(campaignRepository.findById(donationCreateDto.getCampaignId()))
                .thenReturn(Optional.of(campaign));
        Mockito.when(paymentService.sendPayment(paymentRequest))
                .thenThrow(new RuntimeException("Payment failed"));

        Exception exception = assertThrows(RuntimeException.class,
                () -> donationService.sendDonation(donationCreateDto, USER_ID));

        assertEquals("Payment failed", exception.getMessage());
    }

    @Test
    @DisplayName("Успешная отправка платежа")
    void testSendDonationPaymentSuccess() {
        Mockito.when(campaignRepository.findById(donationCreateDto.getCampaignId()))
                .thenReturn(Optional.of(campaign));
        Mockito.when(donationMapper.toEntity(donationCreateDto)).thenReturn(donation);
        Mockito.when(paymentService.sendPayment(paymentRequest))
                .thenReturn(paymentResponse);
        Mockito.when(donationMapper.toDto(donation)).thenReturn(donationViewDto);

        DonationViewDto result = donationService.sendDonation(donationCreateDto, USER_ID);

        assertEquals(donationViewDto, result);
    }

    @Test
    @DisplayName("Поиск несуществующего доната пользователя")
    void testGetDonationByIdForUserNotFound() {
        Mockito.when(donationRepository.findByIdAndUserId(DONATION_ID, USER_ID))
                .thenThrow(new EntityNotFoundException("donation with id " + DONATION_ID +
                        "not found for user " + USER_ID));

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> donationService.getDonationByIdForUser(DONATION_ID, USER_ID));

        assertEquals("donation with id " + DONATION_ID + "not found for user " + USER_ID,
                exception.getMessage());
    }

    @Test
    @DisplayName("Успешный поиск доната пользователя")
    void testGetDonationByIdForUserSuccess() {
        Mockito.when(donationRepository.findByIdAndUserId(DONATION_ID, USER_ID))
                .thenReturn(Optional.of(donation));
        Mockito.when(donationMapper.toDto(donation)).thenReturn(donationViewDto);

        DonationViewDto result = donationService.getDonationByIdForUser(DONATION_ID, USER_ID);

        assertEquals(donationViewDto, result);
    }

    @Test
    @DisplayName("Успешный поиск донатов пользователя с фильтром")
    void testGetUserDonationsSuccess() {
        List<Donation> userDonations = List.of(
                Donation.builder()
                        .currency(Currency.USD)
                        .donationTime(LocalDateTime.parse("2025-01-01T10:20:37"))
                        .build(),
                Donation.builder()
                        .currency(Currency.USD)
                        .donationTime(LocalDateTime.parse("2026-01-01T10:20:37"))
                        .build(),
                Donation.builder()
                        .currency(Currency.EUR)
                        .donationTime(LocalDateTime.parse("2025-01-01T10:20:37"))
                        .build()
        );
        Mockito.when(donationRepository.findAllByUserId(USER_ID)).thenReturn(userDonations);

        List<DonationViewDto> result = donationService.getUserDonations(USER_ID, donationFilterDto);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Поиск при отсутствии донатов у пользователя")
    void testGetUserDonationsEmpty() {
        List<Donation> userDonations = List.of();
        Mockito.when(donationRepository.findAllByUserId(USER_ID)).thenReturn(userDonations);

        List<DonationViewDto> result = donationService.getUserDonations(USER_ID, donationFilterDto);

        assertEquals(0, result.size());
    }
}