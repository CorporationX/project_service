package faang.school.projectservice.controller.donation;

import faang.school.projectservice.model.dto.donation.DonationDto;
import faang.school.projectservice.model.dto.donation.DonationFilterDto;
import faang.school.projectservice.service.DonationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/donations")
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DonationDto sendDonation(@Valid @RequestBody DonationDto donationDto) {
        return donationService.sendDonation(donationDto);
    }

    @GetMapping("/{id}")
    public DonationDto getDonationById(@PathVariable @Positive long id) {
        return donationService.getDonationById(id);
    }

    @GetMapping("/users/{userId}/all")
    public List<DonationDto> getAllDonationsByUserId(@PathVariable @Positive long userId, DonationFilterDto filterDto) {
        return donationService.getAllDonationsByUserId(userId, filterDto);
    }
}
