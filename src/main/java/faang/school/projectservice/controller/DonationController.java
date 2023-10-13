package faang.school.projectservice.controller;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/donation")
public class DonationController {

    private final DonationService donationService;

    @PostMapping("/send")
    public DonationDto sendDonation(@RequestBody DonationDto donationDto) {
        return donationService.send(donationDto);
    }

    @GetMapping("/{donationId}")
    public DonationDto getDonation(@PathVariable long donationId) {
        return donationService.getDonation(donationId);
    }

    @GetMapping("/{userId}/get-all-donation")
    public List<DonationDto> getDonationsByUserId(@PathVariable long userId) {
        return donationService.getDonationsByUserId(userId);
    }

    @GetMapping("/get_all_by_filter")
    public List<DonationDto> getAllDonationsWithFilters(@RequestParam(value = "currency", required = false) Currency currency,
                                                        @RequestParam(value = "minAmount", required = false) BigDecimal minAmount,
                                                        @RequestParam(value = "maxAmount", required = false) BigDecimal maxAmount,
                                                        @RequestParam(value = "createdAt", required = false) LocalDateTime createdAt) {
        return donationService.getAllByFilter(currency, minAmount, maxAmount, createdAt);
    }
}
