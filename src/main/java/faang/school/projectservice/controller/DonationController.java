package faang.school.projectservice.controller;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.DonationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/donation")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Long> createDonation(@RequestBody DonationDto donationDto) {
        log.info("Create donation");
        validateDonation(donationDto);
        Long donationId = donationService.createDonation(donationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(donationId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DonationDto> getDonation(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                donationService.getDonationByIdAndUserId(id)
        );
    }

    @GetMapping("/donations")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<DonationDto>> getDonationsByUserId(
            @RequestBody DonationFilterDto donationFilter
    ) {
        log.info("donationFilter: {}", donationFilter.toString());
        return ResponseEntity.ok(
                donationService.getAllDonationsByUserId(donationFilter)
        );
    }

    private void validateDonation(DonationDto donationDto) {
        if (donationDto == null) {
            log.error("Donation must not be null");
            throw new DataValidationException("Donation must not be null");
        } else if (donationDto.amount() == null || donationDto.amount().compareTo(BigDecimal.ONE) < 0) {
            log.error("Donation must have a valid amount");
            throw new DataValidationException("Donation must have a valid amount");
        } else if (donationDto.currency() == null) {
            log.error("Donation must have a currency");
            throw new DataValidationException("Donation must have a currency");
        } else if (donationDto.donationTime() == null || !donationDto.donationTime().isBefore(LocalDateTime.now())) {
            log.error("Donation must have a valid donation time");
            throw new DataValidationException("Donation must have a valid donation time");
        } else if (donationDto.campaignId() == null) {
            log.error("Donation must have a campaign id");
            throw new DataValidationException("Donation must have a campaign id");
        }
    }
}
