package faang.school.projectservice.controller;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.service.DonationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class DonationControllerTest {
    @Mock
    private DonationService donationService;
    @InjectMocks
    private DonationController donationController;

    @Test
    void testCreateDonation() {
        donationController.createDonation(DonationDto.builder().build());
        verify(donationService, times(1)).createDonation(any(DonationDto.class));
    }

    @Test
    void getDonationById() {
        donationController.getDonationById(1L);
        verify(donationService, times(1)).getDonationById(anyLong());
    }

    @Test
    void getAllDonations() {
        donationController.getAllDonations();
        verify(donationService, times(1)).getAllDonations();
    }

    @Test
    void getAllDonationsWithFilter() {
        donationController.getAllDonationsWithFilter(DonationFilterDto.builder().build());
        verify(donationService, times(1)).getAllDonationsByFilters(any(DonationFilterDto.class));
    }
}