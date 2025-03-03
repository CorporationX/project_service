package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilter;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.DonationMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
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
    private CampaignRepository campaignRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private DonationService donationService;
    private DonationDto donationDto;
    private Donation donation;
    private Campaign campaignOne;
    private Campaign campaignTwo;
    private DonationFilter donationFilter;
    private PaymentRequest paymentRequest;
    private static final long USER_ID = 1L;
    private static final long DONATION_ID = 1L;

    @BeforeEach
    public void init() {
        campaignOne = Campaign.builder().id(2L).status(CampaignStatus.CANCELED).build();
        campaignTwo = Campaign.builder().id(1L).currency(Currency.EUR).status(CampaignStatus.ACTIVE).build();
        donationDto = DonationDto.builder()
                .paymentNumber(1001L)
                .amount(new BigDecimal(100_000))
                .campaignId(1L)
                .currency(Currency.EUR)
                .userId(USER_ID)
                .build();
        donation = Donation.builder()
                .paymentNumber(donationDto.paymentNumber())
                .amount(donationDto.amount())
                .campaign(campaignTwo)
                .currency(campaignTwo.getCurrency())
                .userId(USER_ID)
                .build();
        paymentRequest = PaymentRequest.builder()
                .paymentNumber(donationDto.paymentNumber())
                .amount(donationDto.amount())
                .paymentCurrency(donationDto.currency())
                .targetCurrency(campaignTwo.getCurrency())
                .build();
        donationFilter = DonationFilter.builder().build();
    }

    @Test
    @DisplayName("mapDonationToPaymentRequest: успешно преобразует DonationDto в PaymentRequest")
    public void testMapDonationToPaymentRequestSuccess() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = DonationService.class.getDeclaredMethod("mapDonationToPaymentRequest", DonationDto.class);
        method.setAccessible(true);

        Mockito.when(campaignRepository.findById(donationDto.campaignId())).thenReturn(Optional.of(campaignTwo));
        PaymentRequest paymentRequest = (PaymentRequest) method.invoke(donationService, donationDto);

        Mockito.verify(campaignRepository).findById(donationDto.campaignId());

        Assertions.assertEquals(donationDto.paymentNumber(), paymentRequest.paymentNumber());
        Assertions.assertEquals(donationDto.amount(), paymentRequest.amount());
        Assertions.assertEquals(campaignTwo.getCurrency(), paymentRequest.targetCurrency());
        Assertions.assertEquals(donationDto.currency(), paymentRequest.paymentCurrency());

    }

    @Test
    @DisplayName("mapDonationToPaymentRequest: проверка ошибки при неактивной кампании")
    public void testMapDonationToPaymentRequestCampaignNotActive() throws NoSuchMethodException {
        Mockito.when(campaignRepository.findById(donationDto.campaignId())).thenReturn(Optional.of(campaignOne));

        Method method = DonationService.class.getDeclaredMethod("mapDonationToPaymentRequest", DonationDto.class);
        method.setAccessible(true);

        BusinessException businessException = Assertions.assertThrows(BusinessException.class, () -> {
            try {
                method.invoke(donationService, donationDto);
            } catch (InvocationTargetException e) {
                throw (BusinessException) e.getCause();
            }
        });

        Assertions.assertEquals("Ошибка статуса компании " + campaignOne.getStatus(), businessException.getMessage());
    }

    @Test
    @DisplayName("mapDonationToPaymentRequest: проверка ошибки при отсутствии кампании")
    public void testMapDonationToPaymentRequestCampaignNotFound() throws NoSuchMethodException {
        Mockito.when(campaignRepository.findById(donationDto.campaignId())).thenReturn(Optional.empty());

        Method method = DonationService.class.getDeclaredMethod("mapDonationToPaymentRequest", DonationDto.class);
        method.setAccessible(true);

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            try {
                method.invoke(donationService, donationDto);
            } catch (InvocationTargetException e) {
                throw (EntityNotFoundException) e.getCause();
            }
        });

        Assertions.assertEquals("Компания с ID < " + donationDto.campaignId() + " > не найдена", exception.getMessage());
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
    @DisplayName("findDonationByIdAndUserId: выбрасывает исключение при отсутствии доната")
    public void testFindDonationByIdAndUserIdThrowsEntityNotFoundException() {
        Mockito.when(donationRepository.findByIdAndUserId(DONATION_ID, USER_ID)).thenReturn(Optional.empty());
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            donationService.findDonationByIdAndUserId(DONATION_ID, USER_ID);
        });

        Assertions.assertEquals("Донат с ID " + DONATION_ID + "по юзеру с ID " + USER_ID + " не найден", exception.getMessage());
    }

    @Test
    @DisplayName("findDonationByIdAndUserId: успешно находит донат по userId и donationId")
    public void testFindDonationByIdAndUserIdSuccess() {
        Mockito.when(donationRepository.findByIdAndUserId(DONATION_ID, USER_ID)).thenReturn(Optional.of(donation));
        Mockito.when(donationMapperImpl.toDto(donation)).thenReturn(donationDto);
        donationService.findDonationByIdAndUserId(DONATION_ID, USER_ID);

        Mockito.verify(userService, times(1)).getUserDtoById(USER_ID);
    }

    @Test
    @DisplayName("createDonation: успешное создание доната")
    public void testCreateDonationSuccess() {
        Mockito.when(campaignRepository.findById(donationDto.campaignId())).thenReturn(Optional.of(campaignTwo));
        Mockito.when(paymentServiceClient.sendPayment(paymentRequest)).thenReturn(PaymentResponse.builder().build());
        Mockito.when(donationMapperImpl.toEntity(donationDto)).thenReturn(donation);
        Mockito.when(donationRepository.save(donation)).thenReturn(donation);

        DonationDto result = donationService.createDonation(donationDto);

        Assertions.assertEquals(donationDto, result);
    }

}
