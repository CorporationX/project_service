package faang.school.projectservice.controller.donation;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/donations")
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    public DonationDto sendDonation(@RequestBody DonationDto donationDto) {
        return donationService.sendDonation(donationDto);
    }

    @GetMapping(value = "/{userId}/{donationId}")
    public DonationDto getDonationByIdAndUserId(@PathVariable long donationId, @PathVariable long userId) {
        return donationService.getDonationByIdAndUserId(donationId, userId);
    }

    @GetMapping(value = "/{userId}")
    public List<DonationDto> getAllDonationsByUserId(
            @PathVariable long userId,
            @RequestBody DonationFilterDto donationFilterDto) {
        return donationService.getAllDonationsByUserId(userId, donationFilterDto);
    }
}
