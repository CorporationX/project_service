package faang.school.projectservice.controller.donation;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.service.donation.DonationService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class DonationController {
    private final DonationService donationService;

    @PostMapping("/donations")
    public DonationDto sendDonation(@RequestBody DonationDto donation) {
        if (donation == null) {
            throw new NullPointerException("Donation cannot be empty");
        }
        return donationService.sendDonation(donation);
    }

    @GetMapping("/donations/users/{userId}")
    public DonationDto getDonationById(@PathVariable @Positive(message = "Id must be positive") long userId,
                                       @RequestParam @Positive(message = "Id must be positive") long donationId) {
        return donationService.getDonation(userId, donationId);
    }

    @PostMapping("/donations/users/{userId}/filter")
    public List<DonationDto> getAllDonationsUser(@PathVariable @Positive(message = "Id must be positive") Long userId,
                                                 @RequestBody(required = false) DonationFilterDto filter) {
        return donationService.getAllDonationsUser(userId, filter);
    }

}