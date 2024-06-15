package faang.school.projectservice.validation.donation;

import faang.school.projectservice.dto.donation.DonationCreateDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DonationValidator {
    private final CampaignRepository campaignRepository;
    public void validateSendDonation(DonationCreateDto donationDto) {
        validateCampaign(donationDto);

    }

    private void validateCampaign(DonationCreateDto donationDto) {
        boolean isActive = campaignRepository.isCampaignActive(donationDto.getCampaignId());
        if(!isActive){
            throw new DataValidationException("Donation cannot be sent to inactive campaign");
        }
    }
}
