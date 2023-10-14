package faang.school.projectservice.service;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignRepository campaignRepository;

    @Transactional(readOnly = true)
    public Campaign getCampaign(DonationDto donationDto) {
        return campaignRepository.findById(donationDto.getCampaignId())
                .orElseThrow(() -> new EntityNotFoundException("Campaign not found"));
    }
}
