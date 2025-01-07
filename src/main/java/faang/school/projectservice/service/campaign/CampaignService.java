package faang.school.projectservice.service.campaign;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignCreateDto;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilter;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.mapper.campaign.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;
    private final UserContext userContext;

    public Campaign getCampaignById(Long id) {
        log.info("retrieving a campaign by id: {}", id);

        return campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("campaign doesnt exist"));
    }

    public CampaignDto getById(Long id) {
        Campaign campaign = getCampaignById(id);
        return campaignMapper.toDto(campaign);
    }

    public CampaignDto createCampaign(CampaignCreateDto createDto) {
        Project project = projectService.getById(createDto.getProjectId());

        validateAccess(createDto.getCreatorId(), createDto.getProjectId());

        LocalDateTime now = LocalDateTime.now();
        Campaign campaign = campaignMapper.toEntity(createDto);
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setAmountRaised(BigDecimal.ZERO);
        campaign.setProject(project);
        campaign.setCreatedBy(createDto.getCreatorId());
        campaign.setCreatedAt(now);
        campaign.setUpdatedBy(createDto.getCreatorId());
        campaign.setUpdatedAt(now);

        campaign = campaignRepository.save(campaign);

        log.info("New campaign '{}' created for project '{}' (id={})", campaign.getTitle(),
                campaign.getProject().getName(), campaign.getProject().getId());
        return campaignMapper.toDto(campaign);
    }

    public CampaignDto updateCampaign(Long id, CampaignUpdateDto updateDto) {
        Campaign campaign = getCampaignById(id);
        validateAccess(userContext.getUserId(), campaign.getProject().getId());

        if (updateDto.getTitle() != null) {
            campaign.setTitle(updateDto.getTitle());
        }
        if (updateDto.getDescription() != null) {
            campaign.setDescription(updateDto.getDescription());
        }
        campaign.setUpdatedAt(LocalDateTime.now());
        campaign.setUpdatedBy(userContext.getUserId());

        campaign = campaignRepository.save(campaign);
        log.info("Campaign '{}' (id={}) updated by {}", campaign.getTitle(), campaign.getId(), campaign.getUpdatedBy());
        return campaignMapper.toDto(campaign);
    }

    public CampaignDto cancelCampaign(Long id) {
        Campaign campaign = getCampaignById(id);
        validateAccess(userContext.getUserId(), campaign.getProject().getId());

        campaign.setStatus(CampaignStatus.CANCELED);
        campaign.setUpdatedAt(LocalDateTime.now());
        campaign.setUpdatedBy(userContext.getUserId());

        campaign = campaignRepository.save(campaign);
        log.info("Campaign '{}' (id={}) canceled by {}", campaign.getTitle(), campaign.getId(), campaign.getUpdatedBy());
        return campaignMapper.toDto(campaign);
    }

    public List<CampaignDto> getCampaignsByProject(Long projectId, CampaignFilter filter) {
        String status = filter.getStatus() != null ? filter.getStatus().name() : null;
        List<Campaign> campaigns = campaignRepository.findAllByProjectAndFilters(projectId,
                filter.getFrom(), filter.getTo(), filter.getCreatedBy(), status);
        return campaignMapper.toDto(campaigns);
    }

    private void validateAccess(Long userId, Long projectId) {
        TeamMember teamMember = teamMemberService.validateUserIsProjectMember(userId, projectId);
        if (!teamMember.getRoles().contains(TeamRole.MANAGER) && !teamMember.getRoles().contains(TeamRole.OWNER)) {
            throw new AccessDeniedException("User role is no Manager or Owner");
        }
    }

}
