package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CampaignValidator {

    public boolean validateCampaignStatus(Campaign campaign) {
        if (campaign.getStatus() != CampaignStatus.ACTIVE) {
            throw new DataValidationException(
                    "Campaign with id " + campaign.getId() + " is not active");
        }
        return true;
    }
}
