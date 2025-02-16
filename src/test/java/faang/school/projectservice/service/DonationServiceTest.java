package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.filter.donation.DonationCurrencyFilter;
import faang.school.projectservice.filter.donation.DonationFilter;
import faang.school.projectservice.mapper.DonationMapperImpl;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.validator.CampaignValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DonationServiceTest {
    @Mock
    private DonationRepository donationRepository;
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Mock
    private CampaignValidator campaignValidator;
    @Mock
    private CampaignService campaignService;
    @Mock
    private UserServiceClient userServiceClient;
    @Spy
    private DonationMapperImpl donationMapper;
    private DonationService donationService;

    private DonationFilterDto donationFilterDto;
    private List<DonationFilter> filters;
    private DonationFilter filter;

    @BeforeEach
    void setUp() {
        donationFilterDto = new DonationFilterDto();
        filters = new ArrayList<>();
        filter = new DonationCurrencyFilter();

        donationService = new DonationService(donationRepository, campaignRepository,
                donationMapper, campaignService, paymentServiceClient, filters, campaignValidator, userServiceClient);
    }


    @Test
    void testGetDonationByIdAndUserId_ShouldThrowExceptionWhenDonationNotFound() {
        when(donationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                donationService.getDonationByIdAndUserId(1L, 1L));
    }

    @Test
    void testGetDonationByIdAndUserId_Success() {
        Donation donation = new Donation();
        donation.setId(2L);
        donation.setAmount(new BigDecimal(500));

        when(donationRepository.findByIdAndUserId(2L, 1L)).thenReturn(Optional.of(donation));

        assertEquals(donationMapper.toDto(donation), donationService.getDonationByIdAndUserId(2L, 1L));
    }

    @Test
    void testGetAllDonationsByUser_ShouldReturnOriginalListWhenFilterListIsEmpty() {
        Donation donation1 = Donation.builder().id(1L).userId(1L).build();
        Donation donation2 = Donation.builder().id(2L).userId(1L).build();
        Donation donation3 = Donation.builder().id(3L).userId(1L).build();

        List<Donation> input = List.of(donation1, donation2, donation3);

        when(donationRepository.findAllByUserIdAndSortedByDate(1L)).thenReturn(input);

        List<DonationDto> expected = input.stream().map(donationMapper::toDto).toList();
        List<DonationDto> actual = donationService.getAllDonationsByUser(1L, donationFilterDto);

        assertEquals(expected, actual);
    }

    @Test
    void testGetAllDonationsByUser_ShouldReturnFilteredList() {
        Donation donation1 = Donation.builder().currency(Currency.USD).userId(1L).build();
        Donation donation2 = Donation.builder().currency(Currency.EUR).userId(1L).build();

        filters.add(filter);
        donationFilterDto.setCurrency(Currency.EUR);

        List<Donation> input = List.of(donation1, donation2);

        when(donationRepository.findAllByUserIdAndSortedByDate(1L)).thenReturn(input);

        List<DonationDto> expected = List.of(donationMapper.toDto(donation2));
        List<DonationDto> actual = donationService.getAllDonationsByUser(1L, donationFilterDto);

        assertEquals(expected, actual);
    }
}
