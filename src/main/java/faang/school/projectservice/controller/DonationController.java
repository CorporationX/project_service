package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.PaymentResponse;
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

@RestController
@RequestMapping("/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    public PaymentResponse sendDonation(@RequestBody DonationDto donationDto) {
        return donationService.sendDonation(donationDto);
    }

    @GetMapping("/{donationId}")
    public DonationDto findDonationById(@PathVariable Long donationId) {
        return donationService.findDonationById(donationId);
    }

    @PostMapping("/all-filtered")
    public List<DonationDto> findDonationsByFilters(@RequestBody DonationFilterDto filter) {
        return donationService.findDonationsByFilters(filter);
    }
}
