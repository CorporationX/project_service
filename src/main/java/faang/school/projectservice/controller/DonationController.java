package faang.school.projectservice.controller;

import faang.school.projectservice.dto.donation.DonationCreateRequest;
import faang.school.projectservice.dto.donation.DonationResponse;
import faang.school.projectservice.service.donation.DonationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping("/campaigns/{campaignId}/donations")
    @ResponseStatus(HttpStatus.CREATED)
    public DonationResponse createDonation(
            @Min(1) @PathVariable long campaignId,
            @Valid @RequestBody @NotNull DonationCreateRequest donationCreateRequest,
            @Min(1) @RequestHeader("x-user-id") long userId
    ) {
        donationCreateRequest.setCampaignId(campaignId);
        donationCreateRequest.setUserId(userId);

        return donationService.createDonation(donationCreateRequest);
    }
}
