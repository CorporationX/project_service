package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.filter.donation.DonationFilters;
import faang.school.projectservice.filter.donation.FilterDonationDto;
import faang.school.projectservice.filter.donation.filterDonationDto.DonationCurrencyFilters;
import faang.school.projectservice.filter.donation.filterDonationDto.DonationMaxAmountFilters;
import faang.school.projectservice.filter.donation.filterDonationDto.DonationMinAmountFilters;
import faang.school.projectservice.mapper.donation.DonationMapper;
import faang.school.projectservice.mapper.donation.DonationMapperImpl;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.model.campaign.Campaign;
import faang.school.projectservice.model.campaign.CampaignStatus;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;
    @Mock
    private CampaignRepository campaignRepository;

    private Campaign campaign;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private DonationService donationService;
    @Mock
    private List<DonationFilters> filters;
    @Spy
    private DonationMapper donationMapper = new DonationMapperImpl();
    @Mock
    private PaymentServiceClient paymentServiceClient;
    private FilterDonationDto filterDonationDto;

    @BeforeEach
    void setUp() {
        filters = List.of(new DonationCurrencyFilters(), new DonationMaxAmountFilters(), new DonationMinAmountFilters());
        donationService = new DonationService(donationRepository, donationMapper, paymentServiceClient, userServiceClient, filters, campaignRepository);
        campaign = new Campaign();
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setId(1L);
        campaign.setDescription("test");
        campaign.setTitle("test");

        paymentServiceClient.sendPayment(new PaymentRequest(1L, new BigDecimal(1), Currency.EUR, Currency.EUR));

        filterDonationDto = new FilterDonationDto();
        filterDonationDto.setMinAmountPattern(new BigDecimal(5));
        filterDonationDto.setMaxAmountPattern(new BigDecimal(7));
        filterDonationDto.setCurrencyPattern(Currency.EUR);

    }

    @Test
    void testSendDonation() {

        Currency currency = Currency.EUR;
        DonationDto donationDto = DonationDto.builder().paymentNumber(1L).amount(new BigDecimal(1)).campaign(1L).currency(currency).userId(1L).build();
        Donation donation = donationMapper.toEntity(donationDto);

        Mockito.when(donationRepository.save(donationMapper.toEntity(donationDto))).thenReturn(donation);
        Mockito.when(campaignRepository.findById(donationDto.getCampaign())).thenReturn(Optional.ofNullable(campaign));

        DonationDto sendDonation = donationService.sendDonation(donationDto);
        Mockito.verify(donationRepository, Mockito.times(1)).save(donationMapper.toEntity(donationDto));

        assertEquals(sendDonation.getPaymentNumber(), donationDto.getPaymentNumber());
        assertEquals(sendDonation.getUserId(), donationDto.getUserId());
    }

    @Test
    void testGetDonation() {
        Currency currency = Currency.EUR;
        DonationDto donationDto = DonationDto.builder().paymentNumber(1L).amount(new BigDecimal(1)).campaign(1L).currency(currency).userId(1L).build();
        Donation donation = donationMapper.toEntity(donationDto);

        Mockito.when(donationRepository.findById(1L)).thenReturn(Optional.ofNullable(donation));
        DonationDto getDonation = donationService.getDonation(1L);

        assertEquals(getDonation.getPaymentNumber(), donation.getPaymentNumber());
        assertEquals(getDonation.getUserId(), donation.getUserId());

    }

    @Test
    void testGetDonationsUser() {
        Currency currency = Currency.EUR;
        DonationDto donationDto = DonationDto.builder().paymentNumber(1L).amount(new BigDecimal(1)).campaign(1L).currency(currency).userId(1L).build();
        Donation donation = donationMapper.toEntity(donationDto);

        Mockito.when(donationRepository.findAllByUserId(1L)).thenReturn(List.of(donation));
        List<DonationDto> getDonations = donationService.getDonations(1L);

        assertEquals(getDonations.get(0).getPaymentNumber(), donation.getPaymentNumber());
        assertEquals(getDonations.get(0).getUserId(), donation.getUserId());
    }

    @Test
    void testGetDonationsFilter() {
        DonationDto donationDto = DonationDto.builder().paymentNumber(1L).amount(new BigDecimal(1)).campaign(1L).currency(Currency.EUR).userId(1L).build();
        Donation donation = donationMapper.toEntity(donationDto);
        donation.setDonationTime(LocalDateTime.now());

        DonationDto donationDto2 = DonationDto.builder().paymentNumber(1L).amount(new BigDecimal(5)).campaign(1L).currency(Currency.EUR).userId(1L).build();
        Donation donation2 = donationMapper.toEntity(donationDto2);
        donation2.setDonationTime(LocalDateTime.now());

        DonationDto donationDto3 = DonationDto.builder().paymentNumber(1L).amount(new BigDecimal(10)).campaign(1L).currency(Currency.EUR).userId(1L).build();
        Donation donation3 = donationMapper.toEntity(donationDto3);
        donation3.setDonationTime(LocalDateTime.now());

        DonationDto donationDto4 = DonationDto.builder().paymentNumber(1L).amount(new BigDecimal(2)).campaign(1L).currency(Currency.USD).userId(1L).build();
        Donation donation4 = donationMapper.toEntity(donationDto4);
        donation4.setDonationTime(LocalDateTime.now());

        List<Donation> donations = List.of(donation, donation2, donation3, donation4);
        Mockito.when(donationRepository.findAllByUserId(1L)).thenReturn(donations);
        Mockito.when(donationRepository.findAll()).thenReturn(donations);

        assertEquals(4, donationRepository.findAll().size());
        assertEquals(1, donationService.getDonationsFilter(filterDonationDto, 1L).size());
    }
}
