package faang.school.projectservice.controller;

import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.service.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/donations")
public class DonationController {
    private final DonationService donationService;

    @PostMapping
    public DonationDto sendDonation(@Valid @RequestBody DonationCreateDto donationCreateDto) {
        return donationService.sendDonation(donationCreateDto);
    }

    @GetMapping("/{donationId}/users/{userId}")
    public DonationDto getDonationByIdAndUserId(@PathVariable Long donationId, @PathVariable Long userId) {
        return donationService.getDonationByIdAndUserId(donationId, userId);
    }

    @GetMapping("/users/{userId}")
    public List<DonationDto> getFilteredDonations(@PathVariable Long userId,
                                                   @ModelAttribute DonationFilterDto filters) {
        return donationService.getAllDonationsByUser(userId, filters);
    }
}
