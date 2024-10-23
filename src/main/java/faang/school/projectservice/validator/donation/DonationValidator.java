package faang.school.projectservice.validator.donation;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.dto.donation.DonationDto;
import faang.school.projectservice.model.entity.Campaign;
import faang.school.projectservice.model.entity.CampaignStatus;
import faang.school.projectservice.service.jira.CampaignService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DonationValidator {

    private final UserServiceClient userServiceClient;
    private final CampaignService campaignService;

    public void validateSending(DonationDto donationDto) {
        try {
            userServiceClient.getUser(donationDto.userId());
        } catch (FeignException e) {
            throw new DataValidationException("Donation user does not exist in database");
        }

        Campaign campaign = campaignService.findCampaignById(donationDto.campaignId());
        if (campaign.getStatus() != CampaignStatus.ACTIVE) {
            throw new DataValidationException("Campaign is not active");
        }
    }
}
