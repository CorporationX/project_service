package faang.school.projectservice.service.campaign;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CampaignPublishingDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.campaign.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.teamMember.TeamMemberValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignService {

    private final List<Filter<CampaignFilterDto, Campaign>> campaignFilters;
    private final TeamMemberValidator teamMemberValidator;
    private final CampaignRepository campaignRepository;
    private final ProjectService projectService;
    private final CampaignMapper campaignMapper;
    private final UserContext userContext;

    @Transactional
    public CampaignDto publishingCampaign(CampaignPublishingDto campaignPublishingDto) {
        log.info("start publishingCampaign with dto: {}", campaignPublishingDto);

        Long userId = userContext.getUserId();
        teamMemberValidator.validateUserHasStatusOwnerOrManagerInTeam(userId, campaignPublishingDto.getProjectId());

        Campaign campaign = campaignRepository.save(createCampaign(userId, campaignPublishingDto));
        log.info("finish publishingCampaign with campaign: {}", campaign);

        return campaignMapper.toDto(campaign);
    }

    @Transactional
    public CampaignDto updateCampaign(Long campaignId, CampaignUpdateDto campaignUpdateDto) {
        log.info("start updateCampaign with campaignId: {}", campaignId);

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("Campaign with id " + campaignId + " not found"));

        campaignMapper.updateCampaignFromDto(campaignUpdateDto, campaign);
        campaign.setUpdatedBy(userContext.getUserId());
        log.info("campaign = {}", campaign.toString());

        Campaign updatedCampaign = campaignRepository.save(campaign);
        log.info("finish updateCampaign with campaign: {}", updatedCampaign);

        return campaignMapper.toDto(updatedCampaign);
    }

    @Transactional
    public void deleteCampaignById(Long campaignId) {
        log.info("start deleteCampaignById with campaignId: {}", campaignId);

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("Campaign with id " + campaignId + " not found"));

        campaign.setStatus(CampaignStatus.CANCELED);
        campaignRepository.save(campaign);

        log.info("finish deleteCampaignById with campaignId: {}", campaignId);
    }

    public CampaignDto getCampaignById(Long campaignId) {
        log.info("start getCampaignById with campaignId: {}", campaignId);

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("Campaign with id " + campaignId + " not found"));
        log.info("finish getCampaignById with campaign: {}", campaign.toString());

        return campaignMapper.toDto(campaign);
    }

    public List<CampaignDto> getAllCampaignsByProjectId(Long projectId, CampaignFilterDto campaignFilterDto) {
        log.info("start getAllCampaignsByProjectId with projectId: {}", projectId);
        List<Campaign> campaigns = campaignRepository.findAllByProjectId(projectId);

        return campaignFilters.stream()
                .filter(filter -> filter.isApplicable(campaignFilterDto))
                .reduce(campaigns.stream(),
                        (stream, filter) -> filter.applyFilter(stream, campaignFilterDto),
                        (v1, v2) -> v1)
                .map(campaignMapper::toDto)
                .sorted(Comparator.comparing(CampaignDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    private Campaign createCampaign(Long userId, CampaignPublishingDto campaignPublishingDto) {
        return Campaign.builder()
                .title(campaignPublishingDto.getTitle())
                .description(campaignPublishingDto.getDescription())
                .project(projectService.getProjectById(campaignPublishingDto.getProjectId()))
                .status(CampaignStatus.ACTIVE)
                .createdBy(userId)
                .build();
    }
}
