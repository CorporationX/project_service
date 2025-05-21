package faang.school.projectservice.controller.donation;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.service.donation.DonationService;
import faang.school.projectservice.validator.donation.DonationDtoValidator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DonationController {

    private DonationDtoValidator donationDtoValidator;
    private DonationService donationService;

    @PostMapping(value = "/donation")
    public DonationDto sendDonation(DonationDto donationDto) {
        if (donationDtoValidator.validate(donationDto)) {
            return donationService.sendDonation(donationDto);
        }

        return donationDto;
    }

    @GetMapping(value = "/donations/{donationId}")
    public DonationDto getDonationById(@PathVariable long donationId) {
        return donationService.getDonationById(donationId);
    }

    @GetMapping(value = "/userDonations/{userId}")
    public List<DonationDto> getAllDonationsByUserId(long userId, DonationFilterDto donationFilterDto) {
        return donationService.getAllDonationsByUserId(userId, donationFilterDto);
    }
}
