package faang.school.projectservice.service.donation;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.filter.DonationFilterStrategy;
import faang.school.projectservice.mapper.donation.DonationMapperImpl;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DonationServiceImplTest {

    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Mock
    private DonationRepository donationRepository;
    @Mock
    private DonationFilterStrategy donationCreatedAtFilter;
    @Mock
    private DonationFilterStrategy donationCurrencyFilter;
    @Spy
    private DonationMapperImpl donationMapper;

    private DonationServiceImpl donationService;

    DonationDto donationDto;
    DonationFilterDto donationFilterDto;
    long donationId = 1L;
    long userId = 1L;
    List<Donation> donations;
    LocalDateTime now = LocalDateTime.now();
    PaymentRequest request;
    PaymentResponse wrongResponse;
    PaymentResponse successfulResponse;

    @BeforeEach
    public void setUp() {
        donationService = new DonationServiceImpl(
                donationMapper,
                donationRepository,
                paymentServiceClient,
                List.of(
                        donationCreatedAtFilter,
                        donationCurrencyFilter
                ),
                userServiceClient
        );

        donationDto = DonationDto.builder()
                .userId(userId)
                .paymentNumber(2L)
                .amount(new BigDecimal(3))
                .campaignId(4L)
                .currency(Currency.EUR)
                .build();

        donationFilterDto = DonationFilterDto.builder()
                .createdAt(null)
                .currency(null)
                .minAmount(null)
                .maxAmount(null)
                .build();

        donations = List.of(
                Donation.builder()
                        .id(donationId)
                        .amount(new BigDecimal(1))
                        .currency(Currency.EUR)
                        .donationTime(now.plus(Period.ofMonths(1)))
                        .build(),
                Donation.builder()
                        .id(donationId + 1)
                        .amount(new BigDecimal(2))
                        .currency(Currency.EUR)
                        .donationTime(now)
                        .build(),
                Donation.builder()
                        .id(donationId + 2)
                        .amount(new BigDecimal(3))
                        .currency(Currency.USD)
                        .donationTime(now.minus(Period.ofMonths(1)))
                        .build()
        );

        request = new PaymentRequest(
                donationDto.getPaymentNumber(),
                donationDto.getAmount(),
                donationDto.getCurrency(),
                donationDto.getCurrency()
        );

        wrongResponse = new PaymentResponse(
                "WRONG",
                1,
                request.paymentNumber(),
                request.amount(),
                request.paymentCurrency(),
                request.targetCurrency(),
                "message"
        );

        successfulResponse = new PaymentResponse(
                "SUCCESS",
                1,
                request.paymentNumber(),
                request.amount(),
                request.paymentCurrency(),
                request.targetCurrency(),
                "message"
        );
    }

    @Test
    public void testSendDonation_BadPaymentResponse() {
        when(paymentServiceClient.sendPayment(request))
                .thenReturn(wrongResponse);

        assertThrows(IllegalArgumentException.class, () -> donationService.sendDonation(donationDto));
    }

    @Test
    public void testSendDonation_ReturnDonationDto() {
        Donation donationEntity = donationMapper.toEntity(donationDto);

        when(paymentServiceClient.sendPayment(request))
                .thenReturn(successfulResponse);

        // Вот это
        when(donationRepository.save(donationEntity)).thenReturn(donationEntity);

        DonationDto returnedDto = donationService.sendDonation(donationDto);

        // Совместно с вот эти - это же полный бред?
        // Сам создал, сказал чтоб мок вернул его же, отслеживаю что верну то что сказал заранее, сравниваю само с собой, фигня же?
        ArgumentCaptor<Donation> argumentCaptor = ArgumentCaptor.forClass(Donation.class);
        verify(donationRepository, times(1)).save(argumentCaptor.capture());
        Donation donation = argumentCaptor.getValue();

        assertEquals(donationMapper.toDto(donation), returnedDto);
    }

    @Test
    public void testGetDonationByIdAndUserId_ThrowsDonationWasNotFound() {
        when(donationRepository.findByIdAndUserId(donationId, userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> donationService.getDonationByIdAndUserId(donationId, userId));
    }

    @Test
    public void testGetDonationByIdAndUserId_ReturnsDonationDto() {
        Donation entity = donationMapper.toEntity(donationDto);

        when(donationRepository.findByIdAndUserId(donationId, userId)).thenReturn(Optional.of(entity));

        // В изначальном ДТО есть ид компании.
        // При превращении в энтити ид теряется, тк меняется на сущность компании равную налл
        // При обратном превращении в ДТО сущность пытается превратиться в ид, но тк налл поле остается пустым.
        donationDto.setCampaignId(null);
        assertEquals(donationDto, donationService.getDonationByIdAndUserId(donationId, userId));
    }

    @Test
    public void testGetAllDonations_NothingFound() {
        when(userServiceClient.getUser(userId)).thenReturn(new UserDto(userId, "name", "email"));
        when(donationRepository.findAllByUserId(userId)).thenReturn(List.of());

        assertEquals(List.of(), donationService.getAllDonationsByUserId(userId, donationFilterDto));
    }

    @Test
    public void testGetAllDonations_NoFilters() {
        List<DonationDto> dtos = new ArrayList<>();
        for (int i = 0; i < donations.size(); i++) {
            dtos.add(donationMapper.toDto(donations.get(i)));
        }

        when(userServiceClient.getUser(userId)).thenReturn(new UserDto(userId, "name", "email"));
        when(donationRepository.findAllByUserId(userId)).thenReturn(donations);

        when(donationCreatedAtFilter.isApplicable(any())).thenReturn(false);
        when(donationCurrencyFilter.isApplicable(any())).thenReturn(false);

        assertEquals(
                dtos,
                donationService.getAllDonationsByUserId(userId, donationFilterDto)
        );
    }

    @Test
    public void testGetAllDonations_OneOfThreePasses() {
        donationFilterDto.setCreatedAt(now);

        when(userServiceClient.getUser(userId)).thenReturn(new UserDto(userId, "name", "email"));
        when(donationRepository.findAllByUserId(userId)).thenReturn(donations);

        when(donationCreatedAtFilter.isApplicable(any())).thenReturn(true);
        when(donationCurrencyFilter.isApplicable(any())).thenReturn(true);

        when(donationCreatedAtFilter.apply(any(), any())).thenAnswer(invocation -> {
            Stream<Donation> stream = invocation.getArgument(0);

            return stream.filter(donation -> donation.getDonationTime() != null
                    && donation.getDonationTime().isEqual(donationFilterDto.createdAt));
        });

        when(donationCurrencyFilter.apply(any(), any())).thenAnswer(invocation -> {
            Stream<Donation> stream = invocation.getArgument(0);

            return stream.filter(donation -> donation.getCurrency().equals(Currency.EUR));
        });

        assertEquals(List.of(donationMapper.toDto(donations.get(1))), donationService.getAllDonationsByUserId(userId, donationFilterDto));
    }
}
