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
    public DonationDto sendDonation(@RequestHeader("x-user-id") Long userId,
                                    @Valid @RequestBody DonationCreateDto donationCreateDto) {
        return donationService.sendDonation(userId, donationCreateDto);
    }

    @GetMapping("/{donationId}")
    public DonationDto getDonationByIdAndUserId(@RequestHeader("x-user-id") Long userId,
                                                @PathVariable Long donationId) {
        return donationService.getDonationByIdAndUserId(donationId, userId);
    }

    @GetMapping
    public List<DonationDto> getAllDonationsByUser(@RequestHeader("x-user-id") Long userId,
                                                   @ModelAttribute DonationFilterDto filters) {
        return donationService.getAllDonationsByUser(userId, filters);
    }
}
