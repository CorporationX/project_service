package faang.school.projectservice.service.donation;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.filter.donation.DonationFilterStrategy;
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
                .userId(1L)
                .paymentNumber(3L)
                .amount(new BigDecimal(1))
                .build();

        donationFilterDto = DonationFilterDto.builder()
                .createdAt(null)
                .currency(null)
                .minAmount(null)
                .maxAmount(null)
                .build();

        donations = List.of(
                Donation.builder().id(1L).amount(new BigDecimal(1)).currency(Currency.EUR).donationTime(now.plus(Period.ofMonths(1))).build(),
                Donation.builder().id(2L).amount(new BigDecimal(2)).currency(Currency.EUR).donationTime(now).build(),
                Donation.builder().id(3L).amount(new BigDecimal(3)).currency(Currency.USD).donationTime(now.minus(Period.ofMonths(1))).build()
        );
    }

    @Test
    public void testSendDonation_ReturnDonationDto() {
        Donation donationEntity = donationMapper.toEntity(donationDto);

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
    public void testGetDonationById_ThrowsDonationWasNotFound() {
        when(donationRepository.findById(donationId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> donationService.getDonationById(donationId));

        assertEquals("Donation with Id (%s) was not found".formatted(donationId), exception.getMessage());
    }

    @Test
    public void testGetDonationById_ReturnsDonationDto() {
        Donation entity = donationMapper.toEntity(donationDto);

        when(donationRepository.findById(donationId)).thenReturn(Optional.of(entity));

        assertEquals(donationDto, donationService.getDonationById(donationId));
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
