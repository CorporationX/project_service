package faang.school.projectservice.service;

import faang.school.projectservice.dto.CampaignDto;
import faang.school.projectservice.exception.PermissionDeniedException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;
    private final CampaignMapper campaignMapper;

    public CampaignDto create(CampaignDto campaignDto) {
        var userId = campaignDto.getUpdatedBy();
        campaignDto.setCreatedBy(userId);
        Campaign campaign = campaignMapper.toEntity(campaignDto);

        if (isManager(userId) || isProjectOwner(campaign, userId, campaignDto.getProjectId())) {
            return campaignMapper.toDto(campaignRepository.save(campaign));
        }

        throw new PermissionDeniedException();
    }

    private boolean isManager(Long userId) {
        return teamMemberRepository.findByUserId(userId).stream()
                .flatMap(teamMember -> teamMember.getRoles().stream())
                .anyMatch(teamRole -> teamRole == TeamRole.MANAGER);
    }

    private boolean isProjectOwner(Campaign campaign, Long userId, Long projectId) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
        campaign.setProject(project);

        return project.getOwnerId().equals(userId);
    }

}
