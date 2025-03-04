package faang.school.projectservice.service;

import faang.school.projectservice.client.CurrencyConverter;
import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.exception.DonationNotFoundException;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.service.donation.DonationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private DonationMapper donationMapper;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CurrencyConverter currencyConverter;

    @Mock
    private CampaignRepository campaignRepository;

    @InjectMocks
    private DonationService donationService;

    private DonationDto donationDto;
    private Donation donationEntity;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        donationDto = new DonationDto();
        donationDto.setId(1L);
        donationDto.setPaymentNumber(12345L);
        donationDto.setAmount(BigDecimal.valueOf(100));
        donationDto.setDonationDate(LocalDateTime.now());
        donationDto.setCampaignId(1L);
        donationDto.setCurrency(Currency.USD);
        donationDto.setUserId(1L);

        donationEntity = new Donation();
        donationEntity.setId(1L);
        donationEntity.setPaymentNumber(12345L);
        donationEntity.setAmount(BigDecimal.valueOf(100));
        donationEntity.setCurrency(Currency.USD);
        donationEntity.setUserId(1L);
    }

    @Test
    public void testSendDonation() {
        when(userServiceClient.getUser(anyLong())).thenReturn(null); // Mock user service
        when(campaignRepository.getById(anyLong())).thenReturn(null); // Mock campaign repository
        when(donationMapper.toEntity(any(DonationDto.class))).thenReturn(donationEntity);
        when(donationRepository.save(any(Donation.class))).thenReturn(donationEntity);

        DonationDto result = donationService.sendDonation(donationDto);

        verify(paymentServiceClient).sendPayment(any(PaymentRequest.class)); // Check if payment was sent
        assertNotNull(result);
        assertEquals(donationDto.getId(), result.getId());
        verify(donationRepository).save(any(Donation.class)); // Verify donation was saved
    }

    @Test
    public void testGetDonation() {
        when(donationRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(donationEntity));
        when(donationMapper.toDto(any(Donation.class))).thenReturn(donationDto);

        DonationDto result = donationService.getDonation(1L, 1L);

        assertNotNull(result);
        assertEquals(donationDto.getId(), result.getId());
    }

    @Test
    public void testGetDonationNotFound() {
        when(donationRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        DonationNotFoundException exception = assertThrows(DonationNotFoundException.class, () ->
                donationService.getDonation(1L, 1L));

        assertEquals("Donation not found for userId: 1 and donationId: 1", exception.getMessage());
    }

    @Test
    public void testGetAllDonationsUser() {
        DonationFilterDto filterDto = new DonationFilterDto();
        filterDto.setCurrency(Currency.USD);
        filterDto.setMaxAmount(BigDecimal.valueOf(200));
        filterDto.setMinAmount(BigDecimal.valueOf(50));

        when(userServiceClient.getUser(anyLong())).thenReturn(null); // Mock user service
        when(donationRepository.findAllByUserId(anyLong())).thenReturn(Collections.singletonList(donationEntity));
        when(donationMapper.toDto(any(Donation.class))).thenReturn(donationDto);

        var result = donationService.getAllDonationsUser(1L, filterDto);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(donationRepository).findAllByUserId(anyLong());
    }

    @Test
    public void testGetAllDonationsUserNoDonations() {
        when(userServiceClient.getUser(anyLong())).thenReturn(null); // Mock user service
        when(donationRepository.findAllByUserId(anyLong())).thenReturn(Collections.emptyList());

        var result = donationService.getAllDonationsUser(1L, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
