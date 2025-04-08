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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping("/campaigns/{campaignId}/donations")
    public ResponseEntity<DonationResponse> createDonation(
            @Positive @PathVariable long campaignId,
            @Valid @RequestBody DonationCreateRequest donationCreateRequest,
            @Positive @RequestHeader("x-user-id") long userId
    ) {
        donationCreateRequest.setCampaignId(campaignId);
        donationCreateRequest.setUserId(userId);

        DonationResponse response = donationService.createDonation(donationCreateRequest);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/donations/{donationId}")
    public ResponseEntity<DonationResponse> getDonation(
            @Positive @PathVariable long donationId,
            @Positive @RequestHeader("x-user-id") long userId
    ) {
        DonationResponse response = donationService.getDonation(donationId, userId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/donations")
    public ResponseEntity<List<DonationResponse>> getDonations(
            @Valid @RequestBody SearchDonationDto searchDonationDto,
            @Positive @RequestHeader("x-user-id") long userId
    ) {
        List<DonationResponse> response = donationService.getDonations(userId, searchDonationDto);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
