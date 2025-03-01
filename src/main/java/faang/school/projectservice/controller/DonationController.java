package faang.school.projectservice.controller;

import faang.school.projectservice.dto.DonationDto;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.filter.DonationFilterDto;
import faang.school.projectservice.service.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DonationDto sendDonation(@RequestBody @Valid DonationDto donationDto) {
        return donationService.sendDonation(donationDto);
    }

    @GetMapping("/donation/{donationId}/user/{userId}")
    public DonationDto getDonation(@PathVariable Long donationId, @PathVariable Long userId) {
        return donationService.getDonation(donationId, userId);
    }

    @GetMapping("/user/{userId}")
    public List<DonationDto> getDonations(
            @PathVariable Long userId,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) Currency currency,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount) {
        DonationFilterDto filter = new DonationFilterDto(startDate, endDate, currency, minAmount, maxAmount);
        return donationService.getDonations(userId, filter);
    }


}
