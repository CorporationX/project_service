package faang.school.projectservice.service;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CreateCampaignDto;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.repository.CampaignRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;

    public CampaignDto createCampaign(Long userId, CreateCampaignDto createCampaignDto) {
        return null;
    }

    public void deleteCampaign(Long campaignId) {
        Campaign campaign = findCampaignById(campaignId);
        campaign.setStatus(CampaignStatus.DELETED);
        campaignRepository.save(campaign);
    }

    public CampaignDto getCampaignById(Long campaignId) {
        return campaignMapper.toCampaignDto(findCampaignById(campaignId));
    }

    public List<CampaignDto> getAllCampaignsByProject(Long projectId) {

    }

    public Campaign findCampaignById(Long campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("Campaign not found with id: " + campaignId));
    }
}
