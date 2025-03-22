package faang.school.projectservice.service;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.campaign.CampaignFilter;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

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

    public CampaignDto create(CampaignDto campaignDto) {
        if (!isManagerOrOwner(campaignDto.getCreatedBy(), campaignDto.getProjectId())) {
            log.info(CREATING_EXCEPTION);
            throw new DataValidationException(CREATING_EXCEPTION);
        }

        Campaign campaign = campaignMapper.toCampaign(campaignDto);

        campaignRepository.save(campaign);
        return campaignMapper.toCampaignDto(campaign);
    }

    public CampaignDto update(CampaignUpdateDto campaignUpdateDto) {
        Campaign campaign = campaignRepository.findById(campaignUpdateDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Campaign not found"));

        if (!isManagerOrOwner(campaign.getCreatedBy(), campaign.getProject().getId())) {
            log.info(UPDATING_EXCEPTION);
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
            log.info(ID_NULL_EXCEPTION);
            throw new DataValidationException(ID_NULL_EXCEPTION);
        }

        Campaign campaign = campaignRepository.findById(id).get();
        campaign.setDeleted(true);
    }

    public CampaignDto getCampaign(Long id) {
        if (id == null) {
            log.info(ID_NULL_EXCEPTION);
            throw new DataValidationException(ID_NULL_EXCEPTION);
        }

        Campaign campaign = campaignRepository.findById(id).get();
        return campaignMapper.toCampaignDto(campaign);
    }

    public List<CampaignDto> getCampaignsByProject(CampaignFilterDto campaignFilterDto) {
        Stream<Campaign> allCampaigns = campaignRepository.findAll().stream();

        for (CampaignFilter campaignFilter : campaignFilters) {
            if (campaignFilter.isApplicable(campaignFilterDto)) {
                allCampaigns = campaignFilter.apply(allCampaigns, campaignFilterDto);
            }
        }

        return allCampaigns
                .sorted(Comparator.comparing(Campaign::getCreatedBy).reversed())
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
