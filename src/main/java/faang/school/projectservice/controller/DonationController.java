package faang.school.projectservice.controller;

import faang.school.projectservice.dto.donation.DonationCreateRequest;
import faang.school.projectservice.dto.donation.DonationResponse;
import faang.school.projectservice.dto.donation.SearchDonationDto;
import faang.school.projectservice.service.donation.DonationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping("/campaigns/{campaignId}")
    public ResponseEntity<DonationResponse> createDonation(
            @Positive @PathVariable long campaignId,
            @Valid @RequestBody DonationCreateRequest donationCreateRequest,
            @Positive @RequestHeader("x-user-id") long userId
    ) {
        DonationResponse response = donationService.createDonation(userId, donationCreateRequest, campaignId);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{donationId}")
    public ResponseEntity<DonationResponse> getDonation(
            @Positive @PathVariable long donationId,
            @Positive @RequestHeader("x-user-id") long userId
    ) {
        DonationResponse response = donationService.getDonation(donationId, userId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<DonationResponse>> getDonations(
            @Valid SearchDonationDto searchDonationDto,
            @Positive @RequestHeader("x-user-id") long userId
    ) {
        List<DonationResponse> response = donationService.getDonations(userId, searchDonationDto);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
