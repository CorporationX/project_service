package faang.school.project_service.service.donation;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.donation.CreateDonationDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.filter.donation.DonationAmountFilter;
import faang.school.projectservice.filter.donation.DonationCurrencyFilter;
import faang.school.projectservice.filter.donation.DonationTimeFilter;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.donation.DonationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DonationServiceImplTest {

    private final DonationMapper donationMapper = Mappers.getMapper(DonationMapper.class);
    private final Campaign campaign = Campaign.builder()
            .id(1L)
            .status(CampaignStatus.ACTIVE)
            .build();
    private final CreateDonationDto createDonationDto = CreateDonationDto.builder()
            .campaignId(campaign.getId())
            .paymentCurrency(Currency.EUR)
            .build();
    private final DonationFilterDto donationFilterDto = new DonationFilterDto(LocalDateTime.now(), Currency.EUR,
            new BigDecimal("10.00"), new BigDecimal("100.00"));

    private final Donation donationOne = Donation.builder()
            .id(1L)
            .donationTime(donationFilterDto.donationTime())
            .currency(donationFilterDto.currency())
            .amount(donationFilterDto.amountMin())
            .build();

    private final Donation donationTwo = Donation.builder()
            .id(2L)
            .donationTime(donationFilterDto.donationTime())
            .currency(donationFilterDto.currency())
            .amount(donationFilterDto.amountMax())
            .build();

    private final Donation donationWithWrongTime = Donation.builder()
            .id(3L)
            .donationTime(donationFilterDto.donationTime().plusDays(1))
            .currency(donationFilterDto.currency())
            .amount(donationFilterDto.amountMax())
            .build();

    private final Donation donationWithWrongCurrency = Donation.builder()
            .id(4L)
            .donationTime(donationFilterDto.donationTime())
            .currency(getCurrenciesWithExclude(donationFilterDto.currency()))
            .amount(donationFilterDto.amountMax())
            .build();

    private final Donation donationWithWrongMinAmount = Donation.builder()
            .id(5L)
            .donationTime(donationFilterDto.donationTime())
            .currency(donationFilterDto.currency())
            .amount(donationFilterDto.amountMin().subtract(new BigDecimal("1.00")))
            .build();

    private final Donation donationWithWrongMaxAmount = Donation.builder()
            .id(6L)
            .donationTime(donationFilterDto.donationTime())
            .currency(donationFilterDto.currency())
            .amount(donationFilterDto.amountMax().add(new BigDecimal("1.00")))
            .build();

    private final List<Donation> donationList = new ArrayList<>(List.of(donationOne, donationTwo, donationWithWrongTime,
            donationWithWrongCurrency, donationWithWrongMinAmount, donationWithWrongMaxAmount));

    @Captor
    ArgumentCaptor<Donation> donationArgumentCaptor;

    @Mock
    private DonationRepository donationRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PaymentServiceClient paymentServiceClient;

    private DonationServiceImpl donationService;

    @BeforeEach
    void setup() {
        donationService = new DonationServiceImpl(donationRepository, userContext, donationMapper, campaignRepository,
                userServiceClient, paymentServiceClient, List.of(new DonationAmountFilter(),
                new DonationCurrencyFilter(), new DonationTimeFilter()));
    }

    @Test
    void testSendDonationThrowsExceptionIfCampaignNotFound() {
        long campaignId = 1L;
        when(campaignRepository.getByIdOrThrow(campaignId)).thenThrow(EntityNotFoundException.class);

        CreateDonationDto createDonationDto = CreateDonationDto.builder()
                .campaignId(campaignId)
                .build();

        assertThrows(EntityNotFoundException.class, () -> donationService.sendDonation(createDonationDto));
    }

    @Test
    void testSendDonationThrowsExceptionIfCampaignStatusNotActive() {
        campaign.setStatus(CampaignStatus.CANCELED);

        getSendDonationCustomMocks();

        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class,
                () -> donationService.sendDonation(createDonationDto));
        assertEquals("Campaign %d is not active. Cant send donation for none active campaignDto"
                .formatted(campaign.getId()), forbiddenException.getMessage());
    }

    @Test
    void testSendDonationPositive() {
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .paymentNumber(3554L)
                .build();
        Donation donationToReturn = Donation.builder().id(432L).build();
        long userId = 3445L;

        getSendDonationCustomMocks();

        when(userContext.getUserId()).thenReturn(userId);
        when(donationRepository.save(any(Donation.class))).thenReturn(donationToReturn);
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        DonationDto donationDto = donationService.sendDonation(createDonationDto);

        verify(donationRepository).save(donationArgumentCaptor.capture());
        Donation savedDonation = donationArgumentCaptor.getValue();

        assertEquals(donationToReturn.getId(), donationDto.id());
        assertEquals(campaign.getId(), savedDonation.getCampaign().getId());
        assertEquals(paymentResponse.paymentNumber(), savedDonation.getPaymentNumber());
        assertEquals(userId, savedDonation.getUserId());
    }

    @Test
    void testValidateUserThrowsExceptionIfUserNull() {
        long userId = 334L;
        when(userServiceClient.getUser(userId)).thenReturn(null);

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> donationService.getDonationByIdAndUserId(23L, userId));
        assertEquals("User %d not found".formatted(userId), entityNotFoundException.getLocalizedMessage());
    }

    @Test
    void testValidateUserThrowsExceptionIfUserIdNull() {
        long userId = 334L;
        when(userServiceClient.getUser(userId)).thenReturn(UserDto.builder().build());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> donationService.getDonationByIdAndUserId(23L, userId));
        assertEquals("User %d not found".formatted(userId), entityNotFoundException.getLocalizedMessage());
    }

    @Test
    void testValidateUserThrowsExceptionIfReceivedUserIdNotEqualsArgumentUserId() {
        long userId = 334L;
        when(userServiceClient.getUser(userId)).thenReturn(UserDto.builder().id(userId + 3).build());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> donationService.getDonationByIdAndUserId(23L, userId));
        assertEquals("User %d not found".formatted(userId), entityNotFoundException.getLocalizedMessage());
    }

    @Test
    void testGetDonationByIdAndUserIDThrowsExceptionIfDonationNotFound() {
        long donationId = 432L;
        long userId = getGetDonationByIdAndUserIdCustomMocks(donationId, Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> donationService.getDonationByIdAndUserId(donationId, userId));
        assertEquals("Donation %d for user %d not found"
                .formatted(donationId, userId), entityNotFoundException.getLocalizedMessage());
    }

    @Test
    void testGetDonationByIdAndUserIdPositive() {
        long donationId = 432L;
        Donation donation = Donation.builder()
                .id(235L)
                .build();

        long userId = getGetDonationByIdAndUserIdCustomMocks(donationId, Optional.of(donation));

        DonationDto donationDto = donationService.getDonationByIdAndUserId(donationId, userId);

        assertEquals(donation.getId(), donationDto.id());
    }

    @Test
    void testGetDonationsByUserIdReturnAllUserDonations() {
        long userId = getGetDonationsByUserIdCustomMocks();

        List<Long> actualDonationsIds = donationService.getDonationsByUserId(userId, null).stream()
                .map(DonationDto::id)
                .sorted()
                .toList();
        List<Long> expectedDonationsIds = donationList.stream()
                .map(Donation::getId)
                .sorted()
                .toList();

        assertEquals(expectedDonationsIds, actualDonationsIds);
    }

    @Test
    void testGetDonationsByUserIdPositive() {
        long userId = getGetDonationsByUserIdCustomMocks();

        List<Long> actualDonationsIds = donationService.getDonationsByUserId(userId, donationFilterDto).stream()
                .map(DonationDto::id)
                .sorted()
                .toList();
        List<Long> expectedDonationsIds = List.of(donationOne, donationTwo).stream()
                .map(Donation::getId)
                .sorted()
                .toList();

        assertEquals(expectedDonationsIds, actualDonationsIds);
    }

    private void getSendDonationCustomMocks() {
        when(campaignRepository.getByIdOrThrow(campaign.getId())).thenReturn(campaign);
    }

    private long getGetDonationByIdAndUserIdCustomMocks(long donationId, Optional optional) {
        long userId = getGetDonationsGeneralMocks();
        when(donationRepository.findByIdAndUserId(donationId, userId)).thenReturn(optional);

        return userId;
    }

    private long getGetDonationsByUserIdCustomMocks() {
        long userId = getGetDonationsGeneralMocks();
        when(donationRepository.findAllByUserId(userId)).thenReturn(donationList);

        return userId;
    }

    private long getGetDonationsGeneralMocks() {
        UserDto userDto = UserDto.builder().id(5343L).build();

        when(userServiceClient.getUser(userDto.id())).thenReturn(userDto);

        return userDto.id();
    }

    private Currency getCurrenciesWithExclude(Currency currencyToExclude) {
        List<Currency> currencies = Arrays.stream(Currency.values())
                .filter(currency -> !currency.equals(currencyToExclude)).toList();
        return currencies.get(new Random().nextInt(currencies.size()));
    }
}