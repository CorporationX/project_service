package faang.school.projectservice.controller;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilter;
import faang.school.projectservice.service.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/donations")
public class DonationController {
    private final DonationService donationService;

    @PostMapping
    public ResponseEntity<DonationDto> createDonation(@RequestBody @Valid DonationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(donationService.createDonation(dto));
    }

    @PostMapping("/{id}/user/{userId}")
    public ResponseEntity<DonationDto> findDonationByIdAndUserId(@PathVariable Long id,
                                                                 @PathVariable Long userId) {
        return ResponseEntity.ok(donationService.findDonationByIdAndUserId(id, userId));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<List<DonationDto>> getDonationByIdUserWithFilter(@PathVariable Long userId,
                                                                           @RequestBody(required = false)
                                                                           @Valid DonationFilter dto) {
        return ResponseEntity.ok(donationService.getDonationByIdUser(userId, dto));
    }

}
