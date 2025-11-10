package faang.school.projectservice.service.campaign;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CreateCampaignDto;
import faang.school.projectservice.dto.campaign.UpdateCampaignDto;
import faang.school.projectservice.filter.campaign.CampaignFilter;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validation.campaign.CampaignValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final ProjectRepository projectRepository;
    private final CampaignMapper campaignMapper;
    private final UserContext userContext;
    private final CampaignValidator campaignValidator;
    private final List<CampaignFilter> campaignFilters;

    @Override
    public CampaignDto create(CreateCampaignDto createCampaignDto) {
        Project project = projectRepository.getByIdOrThrow(createCampaignDto.projectId());

        campaignValidator.validateUser(project, userContext);

        Campaign campaign = campaignMapper.toCampaign(createCampaignDto);
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setProject(project);
        campaign.setCreatedBy(userContext.getUserId());

        Campaign savedCampaign = campaignRepository.save(campaign);
        log.info("Campaign {} for project {} has been created by user {}",
                savedCampaign.getId(), project.getId(), userContext.getUserId());

        return campaignMapper.toCampaignDto(savedCampaign);
    }

    @Override
    public CampaignDto update(long campaignId, UpdateCampaignDto updateCampaignDto) {
        Campaign campaign = campaignRepository.getByIdOrThrow(campaignId);

        campaignValidator.validateUser(campaign.getProject(), userContext);

        campaignMapper.update(updateCampaignDto, campaign);
        campaign.setUpdatedBy(userContext.getUserId());

        Campaign updatedCampaign = campaignRepository.save(campaign);
        log.info("Campaign {} for project {} has been updated by user {}",
                updatedCampaign.getId(), updatedCampaign.getProject().getId(), userContext.getUserId());

        return campaignMapper.toCampaignDto(updatedCampaign);
    }

    @Override
    public void softDelete(long campaignId) {
        Campaign campaign = campaignRepository.getByIdOrThrow(campaignId);

        campaignValidator.validateUser(campaign.getProject(), userContext);

        campaign.setStatus(CampaignStatus.DELETED);
        campaign.setUpdatedBy(userContext.getUserId());

        campaign = campaignRepository.save(campaign);
        log.info("Campaign {} status has been updated on 'DELETED' by user {}",
                campaign.getId(), userContext.getUserId());
    }

    @Override
    public CampaignDto getCampaignById(long campaignId) {
        log.debug("Fetching campaign request by id={}", campaignId);
        Campaign campaign = campaignRepository.getByIdOrThrow(campaignId);
        log.debug("Campaign request found: id={}, status={}, title={}",
                campaign.getId(), campaign.getStatus(), campaign.getTitle());
        return campaignMapper.toCampaignDto(campaign);
    }

    @Override
    public List<CampaignDto> getByFilters(CampaignFilterDto campaignFilterDto) {
        Stream<Campaign> campaignStream = campaignRepository.findAll().stream();

        for (CampaignFilter campaignFilter : campaignFilters) {
            if (campaignFilter.isApplicable(campaignFilterDto)) {
                campaignStream = campaignFilter.apply(campaignStream, campaignFilterDto);
            }
        }

        return campaignStream.map(campaignMapper::toCampaignDto)
                .sorted((x, y) -> y.createdAt().compareTo(x.createdAt())).toList();
    }
}