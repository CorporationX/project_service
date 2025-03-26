package faang.school.projectservice.service;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.campaign.CampaignFilter;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.utils.validationsUtils.CampaignValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.utils.validationsUtils.CampaignValidator.PROJECT_ID_NULL_EXCEPTION;

@Slf4j
@RequiredArgsConstructor
@Service
public class CampaignService {

    public static final String ID_NULL_EXCEPTION = "ID can't be null";
    public static final String UPDATING_EXCEPTION = "Only project owners and managers can update fundraising campaigns";
    public static final String CREATING_EXCEPTION = "Only project owners and managers can create fundraising campaigns";
    private final CampaignMapper campaignMapper;
    private final CampaignRepository campaignRepository;
    private final ProjectRepository projectRepository;
    private final List<CampaignFilter> campaignFilters;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;

    public CampaignDto create(CampaignDto campaignDto) {
        CampaignValidator.validationCampaignDto(campaignDto);
        log.info("All validation have been verifeied.\nStarting the campaign creation process");

        if (!isManagerOrOwner(campaignDto.getCreatedBy(), campaignDto.getProjectId())) {
            log.error(CREATING_EXCEPTION);
            throw new DataValidationException(CREATING_EXCEPTION);
        }

        Campaign campaign = campaignMapper.toCampaign(campaignDto);

        campaignRepository.save(campaign);
        return campaignMapper.toCampaignDto(campaign);
    }

    public CampaignDto update(CampaignUpdateDto campaignUpdateDto) {
        CampaignValidator.validateCampaignUpdateDto(campaignUpdateDto);
        log.info("All validation have been verifeied.\nStarting the campaign updating process");

        Campaign campaign = campaignRepository.findById(campaignUpdateDto.getId()).get();

        if (!isManagerOrOwner(campaign.getCreatedBy(), campaign.getProject().getId())) {
            log.error(UPDATING_EXCEPTION);
            throw new DataValidationException(UPDATING_EXCEPTION);
        }

        campaign.setTitle(campaignUpdateDto.getTitle());
        campaign.setDescription(campaignUpdateDto.getDescription());
        campaign.setUpdatedBy(campaignUpdateDto.getUpdatedBy());

        campaignRepository.save(campaign);
        return campaignMapper.toCampaignDto(campaign);
    }

    public void delete(Long id) {
        if (id == null) {
            log.error(ID_NULL_EXCEPTION);
            throw new DataValidationException(ID_NULL_EXCEPTION);
        }

        Campaign campaign = campaignRepository.findById(id).get();
        campaign.setDeleted(true);
        campaignRepository.save(campaign);
    }

    public CampaignDto getCampaign(Long id) {
        if (id == null) {
            log.error(ID_NULL_EXCEPTION);
            throw new DataValidationException(ID_NULL_EXCEPTION);
        }

        Campaign campaign = campaignRepository.findById(id).get();
        return campaignMapper.toCampaignDto(campaign);
    }

    public List<CampaignDto> getCampaignsByProject(CampaignFilterDto campaignFilterDto) {
        if (campaignFilterDto.getProjectId() == null) {
            log.error(PROJECT_ID_NULL_EXCEPTION);
            throw new DataValidationException(PROJECT_ID_NULL_EXCEPTION);
        }

        Stream<Campaign> allCampaigns = campaignRepository.findAll().stream();

        for (CampaignFilter campaignFilter : campaignFilters) {
            if (campaignFilter.isApplicable(campaignFilterDto)) {
                allCampaigns = campaignFilter.apply(allCampaigns, campaignFilterDto);
            }
        }

        return allCampaigns
                .sorted(Comparator.comparing(Campaign::getCreatedAt).reversed())
                .map(campaignMapper::toCampaignDto)
                .toList();
    }

    private boolean isManagerOrOwner(Long userId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(member -> member.getUserId().equals(userId))
                .anyMatch(member -> member.getRoles().stream()
                        .anyMatch(role -> role == TeamRole.OWNER || role == TeamRole.MANAGER));
    }
}
