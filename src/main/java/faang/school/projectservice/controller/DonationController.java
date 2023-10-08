package faang.school.projectservice.controller;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.filter.donation.FilterDonationDto;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.service.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class DonationController {

    private final DonationService donationService;

    @PostMapping("/api/v1/donations/send")
    public DonationDto sendDonation(@Valid @RequestBody DonationDto donationDto) {
        return donationService.sendDonation(donationDto);
    }

    @GetMapping("/api/v1/donations/{id}")
    public DonationDto getDonation(@PathVariable Long id) {
        return donationService.getDonation(id);
    }

    @GetMapping("/api/v1/donations/{userId}")
    public List<DonationDto> getDonationsUser(@PathVariable Long userId) {
        return donationService.getDonations(userId);
    }

    @GetMapping("/api/v1/donations/{userId}/filter")
    public List<Donation> getDonationsUserFilter(FilterDonationDto filterDonationDto, @PathVariable Long userId) {
        return donationService.getDonationsFilter(filterDonationDto, userId);
    }
}
