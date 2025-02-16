package faang.school.projectservice.service;

import faang.school.projectservice.adapter.CampaignRepositoryAdapter;
import faang.school.projectservice.adapter.ProjectRepositoryAdapter;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.CampaignDto;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.validator.CampaignValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignMapper mapper;
    private final CampaignRepositoryAdapter campaignRepositoryAdapter;
    private final ProjectRepositoryAdapter projectRepositoryAdapter;
    private final CampaignValidator validator;
    private final UserContext userContext;

    @Transactional
    public CampaignDto createCampaign(CampaignDto campaignDto) {
        validator.creatorStatusValidation(campaignDto.getProjectId());
        validator.statusByCreateValidation(campaignDto.getStatus());

        Campaign campaign = mapper.toEntity(campaignDto);
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setAmountRaised(BigDecimal.valueOf(0));
        campaign.setCreatedBy(userContext.getUserId());
        campaign.setProject(projectRepositoryAdapter.getById(campaignDto.getProjectId()));

        Campaign campaignWithId = campaignRepositoryAdapter.save(campaign);

        return mapper.toDto(campaignWithId);
    }

    @Transactional
    public CampaignDto updateCampaign(CampaignDto campaignDto, Long campaignId) {
        Campaign targetCampaign = campaignRepositoryAdapter.findById(campaignId);
        mapper.updateCampaign(campaignDto, targetCampaign);
        return mapper.toDto(targetCampaign);
    }
}
