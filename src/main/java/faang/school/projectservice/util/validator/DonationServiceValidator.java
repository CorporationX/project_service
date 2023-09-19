package faang.school.projectservice.util.validator;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityStatusException;
import faang.school.projectservice.exception.UserNotFoundException;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import feign.FeignException;
import org.springframework.stereotype.Component;

@Component
public class DonationServiceValidator {
    public void validateStatus(Campaign campaign) {
        if (campaign.getStatus() != CampaignStatus.ACTIVE) {
            throw new EntityStatusException("Campaign is not active");
        }
    }

    public void isUserExist(UserServiceClient userServiceClient, DonationDto donationDto) {
        try {
            userServiceClient.getUser(donationDto.getUserId());
        } catch (FeignException.FeignClientException exception) {
            throw new UserNotFoundException("This user doesn't exist");
        }
    }
}
