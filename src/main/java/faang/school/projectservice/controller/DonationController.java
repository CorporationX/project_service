package faang.school.projectservice.controller;

import faang.school.projectservice.dto.donate.DonationCreateDto;
import faang.school.projectservice.dto.donate.DonationDto;
import faang.school.projectservice.dto.donate.DonationFilterDto;
import faang.school.projectservice.service.DonationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/donation")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    public DonationDto createDonation(@RequestBody DonationCreateDto donationDto) {
        return donationService.createDonation(donationDto);
    }

    @GetMapping("/{id}")
    public DonationDto getDonationById(@Valid @NotNull @PathVariable Long id, @Valid @NotNull @RequestParam Long userId) {
        return donationService.getDonationById(id, userId);
    }

    @GetMapping("/all")
    public List<DonationDto> getUserDonations(@Valid @NotNull @RequestParam Long userId, @RequestBody DonationFilterDto filterDto) {
        return donationService.getUserDonations(userId, filterDto);
    }

}
