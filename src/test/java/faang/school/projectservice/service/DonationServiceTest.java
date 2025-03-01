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
import faang.school.projectservice.kafka.producer.FundRaisedEventProducer;
import faang.school.projectservice.mapper.DonationMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.filter.donation.CurrencyFilter;
import faang.school.projectservice.service.filter.donation.DonationFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertNotNull;
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
    @Mock
    private FundRaisedEventProducer fundRaisedEventProducer;
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
    public void getUserById_Success() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Long userId = 1L;
        UserDto mockUser = new UserDto(userId, "John Doe", "user1@gmail.com");

        when(userServiceClient.getUser(userId)).thenReturn(mockUser);

        Method method = DonationService.class.getDeclaredMethod("getUserById", Long.class);
        method.setAccessible(true);

        UserDto result = (UserDto) method.invoke(donationService, userId);

        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("John Doe", result.username());
    }

    @Test
    void getUserById_UserIsNull() throws Exception {
        when(userServiceClient.getUser(6L)).thenReturn(null);

        Method method = DonationService.class.getDeclaredMethod("getUserById", Long.class);
        method.setAccessible(true);

        Exception exception = Assertions.assertThrows(InvocationTargetException.class, () ->
                method.invoke(donationService, 6L)
        );
        Throwable cause = exception.getCause();
        Assertions.assertInstanceOf(EntityNotFoundException.class, cause);
        assertEquals("User not found with id = 6", exception.getCause().getMessage());
    }

    @Test
    public void getUserById_UserServiceConnectionException() throws Exception {
        when(userServiceClient.getUser(1L)).thenThrow(new UserServiceConnectionException("User service not working !!!"));

        Method method = DonationService.class.getDeclaredMethod("getUserById", Long.class);
        method.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () ->
                method.invoke(donationService, 1L)
        );

        Throwable cause = exception.getCause();
        Assertions.assertInstanceOf(UserServiceConnectionException.class, cause);
        assertEquals("User service not working !!!", cause.getMessage());
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
    public void paymentToDonate_Success() throws Exception {

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .status("SUCCESS")
                .amount(new BigDecimal(1000))
                .build();

        DonationCreateDto donation = DonationCreateDto.builder()
                .currency(Currency.USD)
                .amount(new BigDecimal(1000))
                .build();

        when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        Method method = DonationService.class.getDeclaredMethod("paymentToDonate", DonationCreateDto.class);
        method.setAccessible(true);

        PaymentResponse result = (PaymentResponse) method.invoke(donationService, donation);

        assertNotNull(result);
        assertEquals(paymentResponse.amount(), result.amount());
        assertEquals(paymentResponse.status(), result.status());

        verify(paymentServiceClient, times(1)).sendPayment(any(PaymentRequest.class));
    }

    @Test
    public void paymentToDonate_PaymentServiceNotWorked() throws NoSuchMethodException {
        PaymentRequest paymentRequest = new PaymentRequest(
                123L, new BigDecimal(1000), Currency.USD, Currency.USD);

        DonationCreateDto donation = DonationCreateDto
                .builder()
                .currency(Currency.USD)
                .amount(new BigDecimal(1000))
                .build();

        when(paymentServiceClient.sendPayment(paymentRequest)).thenThrow(new RuntimeException());

        Method method = DonationService.class.getDeclaredMethod("paymentToDonate", DonationCreateDto.class);
        method.setAccessible(true);

        Exception exception = assertThrows(InvocationTargetException.class, () -> {
            method.invoke(donationService, donation);
        });

        Throwable cause = exception.getCause();
        Assertions.assertInstanceOf(PaymentServiceConnectException.class, cause);

        assertEquals("Payment service not working !", exception.getCause().getMessage());
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
