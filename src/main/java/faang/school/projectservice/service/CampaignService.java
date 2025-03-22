package faang.school.projectservice.service;

import faang.school.projectservice.dto.CampaignCreateDto;
import faang.school.projectservice.dto.CampaignUpdateDto;
import faang.school.projectservice.exception.CampaignCreatorModificationException;
import faang.school.projectservice.exception.CampaignNotFoundException;
import faang.school.projectservice.exception.PermissionDeniedException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
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

    public CampaignUpdateDto create(CampaignCreateDto campaignCreateDto) {
        var userId = campaignCreateDto.getUpdatedBy();
        campaignCreateDto.setCreatedBy(userId);
        Campaign campaign = campaignMapper.toEntity(campaignCreateDto);

        if (isManager(userId) || isProjectOwner(campaign, userId, campaignCreateDto.getProjectId())) {
            return campaignMapper.toDto(campaignRepository.save(campaign));
        }

        throw new PermissionDeniedException();
    }

    public CampaignUpdateDto update(CampaignUpdateDto dto) {
        Campaign campaign = campaignRepository.findById(dto.getId())
                .orElseThrow(() -> new CampaignNotFoundException(dto.getId()));

        if (dto.getCreatedBy() != null && !dto.getCreatedBy().equals(campaign.getCreatedBy())) {
            throw new CampaignCreatorModificationException();
        }

        campaignMapper.update(campaign, dto);
        return campaignMapper.toDto(campaignRepository.save(campaign));
    }

    public CampaignUpdateDto delete(long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new CampaignNotFoundException(id));

        campaign.setStatus(CampaignStatus.CANCELED);
        return campaignMapper.toDto(campaignRepository.save(campaign));
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
