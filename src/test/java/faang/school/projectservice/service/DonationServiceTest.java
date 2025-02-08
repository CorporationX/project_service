package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.dto.payment.Currency;
import faang.school.projectservice.dto.payment.PaymentRequest;
import faang.school.projectservice.dto.payment.PaymentResponse;
import faang.school.projectservice.filter.DonationFilter;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private DonationMapper donationMapper;

    @Mock
    private List<DonationFilter> donationFilters;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private DonationService donationService;

    DonationDto donationDto;
    Donation donation;

    @BeforeEach
    void setUp() {
        donationDto = new DonationDto();
        donation = new Donation();
    }

    @Test
    void testSendingDonation() {
        DonationDto donationDto = new DonationDto();
        donationDto.setPaymentNumber(123456789L);
        donationDto.setAmount(BigDecimal.valueOf(100));
        donationDto.setCurrency(Currency.USD);

        PaymentRequest paymentRequest = new PaymentRequest(
                donationDto.getPaymentNumber(),
                donationDto.getAmount(),
                donationDto.getCurrency()
        );
        PaymentResponse paymentResponse = new PaymentResponse(
                "SUCCESS",
                12345,
                987654321L,
                new BigDecimal("100.00"),
                Currency.EUR,
                "Payment processed successfully"
        );
        Donation savedDonation = new Donation();
        DonationDto expectedDonationDto = new DonationDto();
        when(paymentServiceClient.sendPayment(paymentRequest)).thenReturn(paymentResponse);
        when(donationMapper.toEntity(donationDto)).thenReturn(donation);
        when(donationRepository.save(donation)).thenReturn(savedDonation);
        when(donationMapper.toDto(savedDonation)).thenReturn(expectedDonationDto);

        DonationDto result = donationService.sendDonation(donationDto);

        assertEquals(expectedDonationDto, result);
        verify(paymentServiceClient, times(1)).sendPayment(paymentRequest);
        verify(donationMapper, times(1)).toEntity(donationDto);
        verify(donationRepository, times(1)).save(donation);
        verify(donationMapper, times(1)).toDto(savedDonation);
    }

    @Test
    void testGettingDonationByUserId() {
        Long userId = 1L;
        Long donationId = 1L;
        when(donationRepository.findByIdAndUserId(donationId, userId)).thenReturn(Optional.ofNullable(donation));
        when(donationMapper.toDto(donation)).thenReturn(donationDto);

        DonationDto result = donationService.getDonationByUserId(donationId, userId);

        assertEquals(donationDto, result);
        verify(donationRepository, times(1)).findByIdAndUserId(donationId, userId);
    }

    @Test
    void testGettingUserDonationsByFilters() {
        Long userId = 1L;
        DonationFilterDto filter = new DonationFilterDto();
        when(donationRepository.findAllByUserId(userId)).thenReturn(List.of(donation));
        DonationFilter mockFilter = mock(DonationFilter.class);
        when(mockFilter.isApplicable(filter)).thenReturn(true);
        when(mockFilter.apply(any(), eq(filter))).thenReturn(Stream.of(donation));
        when(donationFilters.stream()).thenReturn(Stream.of(mockFilter));
        when(donationMapper.toDto(donation)).thenReturn(donationDto);

        List<DonationDto> result = donationService.getUserDonationsByFilters(filter, userId);

        assertEquals(1, result.size());
        assertEquals(donationDto, result.get(0));
        verify(donationRepository, times(1)).findAllByUserId(userId);
        verify(mockFilter, times(1)).isApplicable(filter);
        verify(mockFilter, times(1)).apply(any(), eq(filter));
        verify(donationMapper, times(1)).toDto(donation);    }
}