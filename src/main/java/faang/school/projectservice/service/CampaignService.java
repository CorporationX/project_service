package faang.school.projectservice.service;

import faang.school.projectservice.adapter.CampaignRepositoryAdapter;
import faang.school.projectservice.adapter.ProjectRepositoryAdapter;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.exception.DataValidateException;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.repository.specification.CampaignSpecification;
import faang.school.projectservice.validator.CampaignValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignMapper mapper;
    private final CampaignRepositoryAdapter campaignRepositoryAdapter;
    private final ProjectRepositoryAdapter projectRepositoryAdapter;
    private final CampaignSpecification specification;
    private final CampaignValidator validator;
    private final UserContext userContext;

    @Transactional
    public CampaignDto createCampaign(CampaignDto campaignDto) {
        validator.userStatusValidation(campaignDto.getProjectId());
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
        validator.userStatusValidation(targetCampaign.getProject().getId());
        campaignDto.setUpdatedBy(userContext.getUserId());
        mapper.updateCampaign(campaignDto, targetCampaign);
        return mapper.toDto(targetCampaign);
    }

    @Transactional
    public void deleteCampaign(long id) {
        Campaign campaign = campaignRepositoryAdapter.findById(id);
        validator.userStatusValidation(campaign.getProject().getId());
        campaignRepositoryAdapter.delete(id);
    }

    @Transactional(readOnly = true)
    public CampaignDto getCampaign(long id) {
        Campaign campaign = campaignRepositoryAdapter.findById(id);
        return mapper.toDto(campaign);
    }

    @Transactional(readOnly = true)
    public List<CampaignDto> getCampaigns(CampaignFilterDto filterDto) {
        List<Specification<Campaign>> specs = new ArrayList<>();

    if (filterDto.getCreatedAt() != null) {
        specs.add(specification.getByCreatedAt(filterDto.getCreatedAt()));
    }
    if (filterDto.getStatus() != null) {
        specs.add(specification.getByStatus(filterDto.getStatus()));
    }
    if (filterDto.getCreatorId() != null) {
        if (filterDto.getCreatorId() > 0) {
            specs.add(specification.getByCreatorId(filterDto.getCreatorId()));
        } else {
            throw new DataValidateException("creatorId must be > 0");
        }
    }
    specs.add(specification.getOrderedByDate());

    Specification<Campaign> spec = specs.stream().reduce(Specification::and).orElse(null);

    List<Campaign> campaigns = campaignRepositoryAdapter.findAll(spec);
    return mapper.toListDto(campaigns);
    }
}