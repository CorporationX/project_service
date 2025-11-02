package faang.school.projectservice.service.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;

    @Override
    public CampaignDto getCampaignById(long campaignId) {
        log.debug("Fetching campaign request by id={}", campaignId);
        Campaign campaign = campaignRepository.getByIdOrThrow(campaignId);
        log.debug("Campaign request found: id={}, status={}, title={}",
                campaign.getId(), campaign.getStatus(), campaign.getTitle());
        return campaignMapper.toCampaignDto(campaign);
    }
}