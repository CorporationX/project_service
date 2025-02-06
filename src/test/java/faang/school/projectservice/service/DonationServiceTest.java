package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.donate.DonationCreateDto;
import faang.school.projectservice.dto.donate.DonationDto;
import faang.school.projectservice.dto.donate.DonationFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.PaymentFailedException;
import faang.school.projectservice.exception.PaymentServiceConnectException;
import faang.school.projectservice.exception.UserServiceConnectionException;
import faang.school.projectservice.mapper.DonationMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.filter.donation.CurrencyFilter;
import faang.school.projectservice.service.filter.donation.DonationFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DonationServiceTest {

    private static List<UserDto> usersList;
    private static List<Donation> donationList;
    @Mock
    private UserServiceClient userServiceClient;
    @Spy
    private DonationMapperImpl donationMapper;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Mock
    private List<DonationFilter> donationFilters;
    @Mock
    private DonationRepository donationRepository;
    @Mock
    private CampaignService campaignService;
    @InjectMocks
    private DonationService donationService;

    @BeforeAll
    public static void setUp() {

        donationList = List.of(
                Donation.builder().id(1L).userId(1L).donationTime(LocalDateTime.now()).amount(new BigDecimal(1000)).currency(Currency.USD).build(),
                Donation.builder().id(2L).userId(1L).donationTime(LocalDateTime.now()).amount(new BigDecimal(6000)).currency(Currency.USD).build(),
                Donation.builder().id(3L).userId(2L).donationTime(LocalDateTime.now()).amount(new BigDecimal(2000)).currency(Currency.USD).build(),
                Donation.builder().id(4L).userId(4L).donationTime(LocalDateTime.now()).amount(new BigDecimal(3000)).currency(Currency.USD).build()
        );

        usersList = List.of(
                new UserDto(1L, "user1", "user1@gmail.com"),
                new UserDto(2L, "user2", "user2@gmail.com"),
                new UserDto(3L, "user3", "user3@gmail.com"),
                new UserDto(4L, "user4", "user4@gmail.com")
        );

    }

    @Test
    public void getUserById_Success() {
        when(userServiceClient.getUser(1L)).thenReturn(usersList.get(0));

        UserDto user = donationService.getUserById(1L);

        assertEquals(user.email(), usersList.get(0).email());
    }

    @Test
    public void getUserById_UserIsNull() {
        when(userServiceClient.getUser(6L)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                donationService.getUserById(6L)
        );

        assertEquals("User not found with id = 6", exception.getMessage());
    }

    @Test
    public void getUserById_UserServiceConnectionException() {

        when(userServiceClient.getUser(1L)).thenThrow(new UserServiceConnectionException("User service not working !!!"));

        UserServiceConnectionException exception = assertThrows(UserServiceConnectionException.class, () ->
                donationService.getUserById(1L)
        );

        assertEquals("User service not working !!!", exception.getMessage());
        verify(userServiceClient, times(1)).getUser(1L);
    }

    @Test
    public void getDonationById_Success() {
        when(donationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(donationList.get(0)));

        DonationDto donation = donationService.getDonationById(1L, 1L);

        verify(donationRepository, times(1)).findByIdAndUserId(1L, 1L);
        assertEquals(donation.amount(), donationList.get(0).getAmount());
    }

    @Test
    public void getDonationById_NotFound() {
        when(donationRepository.findByIdAndUserId(1L, 5L)).thenReturn(Optional.empty());


        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            donationService.getDonationById(1L, 5L);
        });

        assertEquals("Donation not found", exception.getMessage());
        verify(donationRepository, times(1)).findByIdAndUserId(1L, 5L);
    }

    @Test
    public void getUserDonations_Success() {
        List<Donation> user1Donations = List.of(donationList.get(0), donationList.get(1));
        DonationFilterDto filterDto = DonationFilterDto.builder().currency("USD").build();

        when(donationFilters.stream()).thenReturn(Stream.of(new CurrencyFilter()));
        when(donationRepository.findAllByUserId(1L)).thenReturn(user1Donations);

        List<DonationDto> userDonations = donationService.getUserDonations(1L, filterDto);

        assertEquals(userDonations.get(0).amount(), user1Donations.get(0).getAmount());
        verify(donationRepository, times(1)).findAllByUserId(1L);
    }

    @Test
    public void paymentToDonate_Success() {

        PaymentRequest paymentRequest = new PaymentRequest(
                123L, new BigDecimal(1000), Currency.USD, Currency.USD);
        PaymentResponse paymentResponse = PaymentResponse.builder().status("SUCCESS").amount(new BigDecimal(1000)).build();

        DonationCreateDto donation = DonationCreateDto
                .builder()
                .currency(Currency.USD)
                .amount(new BigDecimal(1000))
                .build();

        when(donationService.getRandomNumber(1000L, 100000L)).thenReturn(123L);
        when(paymentServiceClient.sendPayment(paymentRequest)).thenReturn(paymentResponse);

        PaymentResponse result = donationService.paymentToDonate(donation);

        assertEquals(result.amount(), paymentRequest.amount());
        assertEquals(donationService.getRandomNumber(1000L, 100000L), paymentRequest.paymentNumber());
        verify(paymentServiceClient, times(1)).sendPayment(paymentRequest);

    }

    @Test
    public void paymentToDonate_PaymentServiceNotWorked() {
        PaymentRequest paymentRequest = new PaymentRequest(
                123L, new BigDecimal(1000), Currency.USD, Currency.USD);

        DonationCreateDto donation = DonationCreateDto
                .builder()
                .currency(Currency.USD)
                .amount(new BigDecimal(1000))
                .build();

        when(donationService.getRandomNumber(1000L, 100000L)).thenReturn(123L);
        when(paymentServiceClient.sendPayment(paymentRequest)).thenThrow(new RuntimeException());

        PaymentServiceConnectException exception = assertThrows(PaymentServiceConnectException.class, () -> {
            donationService.paymentToDonate(donation);
        });

        assertEquals("Payment service not working !", exception.getMessage());
        verify(paymentServiceClient, times(1)).sendPayment(paymentRequest);
    }

    @Test
    public void createDonation_Success() {

        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setCurrency(Currency.USD);

        DonationCreateDto donationCreateDto = DonationCreateDto.builder()
                .userId(1L)
                .amount(new BigDecimal(1000))
                .currency(Currency.USD)
                .donationTime(LocalDateTime.now())
                .campaignId(1L)
                .build();

        Donation donation = new Donation();
        donation.setId(1L);
        donation.setAmount(donationCreateDto.amount());
        donation.setCurrency(donationCreateDto.currency());
        donation.setDonationTime(donationCreateDto.donationTime());
        donation.setCampaign(campaign);
        donation.setUserId(donationCreateDto.userId());

        when(donationRepository.save(any())).thenReturn(donation);
        when(paymentServiceClient.sendPayment(any())).thenReturn(
                PaymentResponse.builder().status("SUCCESS").amount(new BigDecimal(1000)).build());
        when(userServiceClient.getUser(1L)).thenReturn(usersList.get(0));
        when(campaignService.getCampingById(1L)).thenReturn(campaign);

        DonationDto result = donationService.createDonation(donationCreateDto);

        assertEquals(donation.getId(), result.id());
        assertEquals(donation.getDonationTime(), result.donationTime());

    }

    @Test
    public void createDonation_PaymentNotSuccess() {

        Campaign campaign = new Campaign();
        campaign.setId(1L);
        campaign.setCurrency(Currency.USD);

        DonationCreateDto donationCreateDto = DonationCreateDto.builder()
                .userId(1L)
                .amount(new BigDecimal(1000))
                .currency(Currency.USD)
                .donationTime(LocalDateTime.now())
                .campaignId(1L)
                .build();

        Donation donation = new Donation();
        donation.setId(1L);
        donation.setAmount(donationCreateDto.amount());
        donation.setCurrency(donationCreateDto.currency());
        donation.setDonationTime(donationCreateDto.donationTime());
        donation.setCampaign(campaign);
        donation.setUserId(donationCreateDto.userId());

        when(donationRepository.save(any())).thenReturn(donation);
        when(paymentServiceClient.sendPayment(any())).thenReturn(
                PaymentResponse.builder().status("ERROR").amount(new BigDecimal(1000)).build());
        when(userServiceClient.getUser(1L)).thenReturn(usersList.get(0));
        when(campaignService.getCampingById(1L)).thenReturn(campaign);

        PaymentFailedException exception = assertThrows(PaymentFailedException.class, () -> {
            donationService.createDonation(donationCreateDto);
        });

        assertEquals("Payment Failed !", exception.getMessage());
    }

}
