package faang.school.projectservice.controller.donation;

import faang.school.projectservice.model.dto.donation.DonationDto;
import faang.school.projectservice.model.dto.donation.DonationFilterDto;
import faang.school.projectservice.service.DonationService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/donations")
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    public DonationDto sendDonation(DonationDto donationDto) {
        return donationService.sendDonation(donationDto);
    }

    @GetMapping("/{userId}")
    public DonationDto getDonationById(@PathVariable @Positive long id) {
        return donationService.getDonationById(id);
    }

    @GetMapping("/users/{userId}/all")
    public DonationDto getAllDonationsByUserId(@PathVariable @Positive long userId, DonationFilterDto filterDto) {
        return donationService.getAllDonationsByUserId(userId, filterDto);
    }
}
