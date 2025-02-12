package faang.school.projectservice.service.donation;

import faang.school.projectservice.config.context.user.UserContext;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.exception.payment.CampaignNotActiveException;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.payment.PaymentService;
import faang.school.projectservice.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DonationServiceTest {

    @InjectMocks
    private DonationService donationService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserContext userContext;

    @Mock
    private List<DonationFilter> donationFilters;

    @Captor
    private ArgumentCaptor<Long> idCaptor;

    @Captor
    private ArgumentCaptor<Donation> donationCaptor;

    @Mock
    private UserDto mockUserDto;

    @Test
    public void testCreateDonation_shouldThrowExceptionWhenCampaignIdNotExists() {
        long campaignId = 1L;
        Campaign campaign = Campaign.builder()
                .id(campaignId)
                .build();

        Donation mockDonation = Donation.builder()
                .campaign(campaign)
                .build();

        when(userService.getUser(idCaptor.capture()))
                .thenReturn(mockUserDto);

        when(campaignRepository.findById(eq(campaignId)))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                donationService.createDonation(mockDonation));
    }

    @Test
    public void testCreateDonation_shouldThrowExceptionWhenCampaignStatusIsNotActive() {
        long campaignId = 1L;
        Campaign campaign = Campaign.builder()
                .id(campaignId)
                .status(CampaignStatus.CANCELED)
                .build();

        Donation mockDonation = Donation.builder()
                .campaign(campaign)
                .build();

        when(userService.getUser(idCaptor.capture()))
                .thenReturn(mockUserDto);

        when(campaignRepository.findById(campaignId))
                .thenReturn(Optional.ofNullable(campaign));

        assertThrows(CampaignNotActiveException.class, () ->
                donationService.createDonation(mockDonation));
    }

    @Test
    public void testCreateDonationPositive() {
        long campaignId = 1L;
        Campaign campaign = Campaign.builder()
                .id(campaignId)
                .status(CampaignStatus.ACTIVE)
                .build();

        Donation mockDonation = Donation.builder()
                .amount(BigDecimal.valueOf(5.0))
                .currency(Currency.USD)
                .campaign(campaign)
                .build();

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .paymentNumber(123123)
                .build();

        when(userService.getUser(idCaptor.capture()))
                .thenReturn(mockUserDto);

        when(campaignRepository.findById(campaignId))
                .thenReturn(Optional.ofNullable(campaign));

        when(paymentService.makePayment(mockDonation.getAmount(), mockDonation.getCurrency()))
                .thenReturn(paymentResponse);

        donationService.createDonation(mockDonation);

        verify(paymentService, times(1))
                .makePayment(mockDonation.getAmount(), mockDonation.getCurrency());

        verify(donationRepository, times(1))
                .save(donationCaptor.capture());

        assertEquals(paymentResponse.paymentNumber(),
                donationCaptor.getValue().getPaymentNumber());
    }

    @Test
    public void testGetDonationById_shouldThrowExceptionWhenDonationIdDoesNotExists() {
        long donationId = 1L;

        when(userService.getUser(idCaptor.capture()))
                .thenReturn(mockUserDto);

        when(donationRepository.findById(donationId))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                donationService.getDonationById(donationId));
    }

    @Test
    public void testGetDonationById_shouldThrowExceptionWhenDonationUserIdNotEqualUserId() {
        long donationId = 1L;
        long userIdFirst = 1L;
        long userIdSecond = 2L;
        Donation mockDonation = Donation.builder()
                .id(donationId)
                .userId(userIdFirst)
                .build();

        when(userContext.getUserId())
                .thenReturn(userIdSecond);

        when(userService.getUser(userIdSecond))
                .thenReturn(mockUserDto);

        when(donationRepository.findById(donationId))
                .thenReturn(Optional.ofNullable(mockDonation));

        assertThrows(IllegalStateException.class, () ->
                donationService.getDonationById(donationId));
    }

    @Test
    public void testGetAllUserDonationsPositive() {
        DonationFilterDto filterDto = DonationFilterDto.builder().build();
        long userId = 1L;

        when(userContext.getUserId())
                .thenReturn(userId);

        when(userService.getUser(eq(userId)))
                .thenReturn(mockUserDto);

        donationService.getAllUserDonations(filterDto);

        verify(donationRepository, times(1))
                .findAllByUserId(eq(userId));
    }
}
