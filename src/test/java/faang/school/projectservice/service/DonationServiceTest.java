package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilter;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.DonationMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.spetification.DonationSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class DonationServiceTest {
    @Mock
    private DonationRepository donationRepository;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Mock
    private DonationSpecification donationSpecification;
    @Spy
    private DonationMapperImpl donationMapperImpl;
    @Mock
    private CampaignService campaignService;
    @Mock
    private UserService userService;
    @InjectMocks
    private DonationService donationService;
    private DonationDto donationDto;
    private Donation donation;
    private Campaign campaignOne;
    private Campaign campaignTwo;
    private DonationFilter donationFilter;
    private PaymentRequest paymentRequestOne;
    private static final long USER_ID = 1L;
    private static final long DONATION_ID = 1L;

    @BeforeEach
    public void init() {
        donation = Donation.builder().id(DONATION_ID).userId(USER_ID).campaign(campaignTwo).build();
        campaignOne = Campaign.builder().id(2L).status(CampaignStatus.CANCELED).build();
        campaignTwo = Campaign.builder().id(1L).currency(Currency.EUR).status(CampaignStatus.ACTIVE).build();
        donationDto = DonationDto.builder()
                .paymentNumber(1001L)
                .amount(new BigDecimal(100_000))
                .currency(Currency.EUR)
                .campaignId(1L)
                .userId(USER_ID)
                .build();
        paymentRequestOne = PaymentRequest.builder()
                .paymentNumber(donationDto.paymentNumber())
                .amount(donationDto.amount())
                .paymentCurrency(donationDto.currency())
                .targetCurrency(campaignTwo.getCurrency())
                .build();
        donationFilter = DonationFilter.builder().build();
    }

    @Test
    @DisplayName("ValidCampaignStatusThrows(): выброс исключения при невалидном статусе")
    public void testValidCampaignStatusThrows() throws NoSuchMethodException {
        Method method = DonationService.class.getDeclaredMethod("validCampaignStatus", Campaign.class);
        method.setAccessible(true);

        BusinessException businessException = Assertions.assertThrows(BusinessException.class, () -> {
            try {
                method.invoke(donationService, campaignOne);
            } catch (InvocationTargetException e) {
                throw (BusinessException) e.getCause();
            }
        });

        Assertions.assertEquals(businessException.getMessage(),
                "Ошибка статуса компании " + campaignOne.getStatus());

    }

    @Test
    @DisplayName("mapDonationToPaymentRequest: успешно преобразует DonationDto в PaymentRequest")
    public void testMapDonationToPaymentRequestSuccess() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = DonationService.class.getDeclaredMethod("mapDonationToPaymentRequest", DonationDto.class);
        method.setAccessible(true);

        Mockito.when(campaignService.findById(donationDto.campaignId())).thenReturn(campaignTwo);
        PaymentRequest paymentRequest = (PaymentRequest) method.invoke(donationService, donationDto);

        Mockito.verify(campaignService).findById(donationDto.campaignId());

        Assertions.assertEquals(donationDto.paymentNumber(), paymentRequest.paymentNumber());
        Assertions.assertEquals(donationDto.amount(), paymentRequest.amount());
        Assertions.assertEquals(campaignTwo.getCurrency(), paymentRequest.targetCurrency());
        Assertions.assertEquals(donationDto.currency(), paymentRequest.paymentCurrency());

    }

    @Test
    @DisplayName("testGetDonationByIdUserSuccess: успешно находит донат по фильтру и userId")
    public void testGetDonationByIdUserSuccess() {
        Specification<Donation> donationSpec = mock(Specification.class);

        List<Donation> donations = List.of(new Donation(), new Donation());
        List<DonationDto> expectedDtos = List.of(
                DonationDto.builder().build(),
                DonationDto.builder().build()
        );

        Mockito.when(donationSpecification.build(USER_ID, donationFilter)).thenReturn(donationSpec);
        Mockito.when(donationRepository.findAll(donationSpec)).thenReturn(donations);
        Mockito.when(donationMapperImpl.toDto(any(Donation.class))).thenReturn(DonationDto.builder().build());

        List<DonationDto> result = donationService.getDonationByIdUser(USER_ID, donationFilter);

        Assertions.assertEquals(expectedDtos.size(), result.size());
    }

    @Test
    @DisplayName("findDonationByIdAndUserId: выбрасывает исключение при null userId")
    public void testFindDonationByIdAndUserIdWithUserIdNull() {
        DataValidationException exception = Assertions.assertThrows(DataValidationException.class, () -> {
            donationService.findDonationByIdAndUserId(DONATION_ID, null);
        });

        Assertions.assertEquals("Ошибка валидации метода findDonationByIdAndUserId", exception.getMessage());
    }

    @Test
    @DisplayName("findDonationByIdAndUserId: выбрасывает исключение при null donationId")
    public void testFindDonationByIdAndUserIdWithDonationIdNull() {
        DataValidationException exception = Assertions.assertThrows(DataValidationException.class, () -> {
            donationService.findDonationByIdAndUserId(null, USER_ID);
        });

        Assertions.assertEquals("Ошибка валидации метода findDonationByIdAndUserId", exception.getMessage());
    }

    @Test
    @DisplayName("findDonationByIdAndUserId: выбрасывает исключение при отсутствии доната")
    public void testFindDonationByIdAndUserIdThrowsEntityNotFoundException() {
        Mockito.when(donationRepository.findByIdAndUserId(DONATION_ID, USER_ID)).thenReturn(Optional.empty());
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            donationService.findDonationByIdAndUserId(DONATION_ID, USER_ID);
        });

        Assertions.assertEquals("Донат с ID " + DONATION_ID + " не найден", exception.getMessage());
    }

    @Test
    @DisplayName("findDonationByIdAndUserId: успешно находит донат по userId и donationId")
    public void testFindDonationByIdAndUserIdSuccess() {
        Mockito.when(donationRepository.findByIdAndUserId(DONATION_ID, USER_ID)).thenReturn(Optional.of(donation));
        Mockito.when(donationMapperImpl.toDto(donation)).thenReturn(donationDto);
        donationService.findDonationByIdAndUserId(DONATION_ID, USER_ID);

        Mockito.verify(userService, times(1)).getUserDtoById(USER_ID);
    }

}
