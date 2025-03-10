package faang.school.projectservice.controller;

import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.service.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/donations")
public class DonationController {
    private final DonationService donationService;

    @PostMapping
    public DonationDto sendDonation(@Valid @RequestBody DonationCreateDto donationCreateDto,
                                    @RequestHeader("x-user-id") Long userId) {
        return donationService.sendDonation(donationCreateDto, userId);
    }

    @GetMapping("/{donationId}")
    public DonationDto getDonationByIdAndUserId(@PathVariable Long donationId,
                                                @RequestHeader("x-user-id") Long userId) {
        return donationService.getDonationByIdAndUserId(donationId, userId);
    }

    @GetMapping
    public List<DonationDto> getFilteredDonations(@RequestHeader("x-user-id") Long userId,
                                                  @ModelAttribute DonationFilterDto filters) {
        return donationService.getAllDonationsByUser(userId, filters);
    }
}
