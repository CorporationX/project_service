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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/donation")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createDonation(@RequestBody DonationDto donationDto) {
        log.info("Create donation");
        validateDonation(donationDto);
        donationService.createDonation(donationDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/getById/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DonationDto> getDonation(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                donationService.getDonationByIdAndUserId(id)
        );
    }

    @GetMapping("/getAll")
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
        if (donationDto == null || donationDto.amount() == null) {
            log.error("Invalid donation");
            throw new DataValidationException("Invalid donation");
        }
    }
}
