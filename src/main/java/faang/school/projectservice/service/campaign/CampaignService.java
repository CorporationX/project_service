package faang.school.projectservice.service.campaign;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.exeption.EntityCampaignNotFoundException;
import faang.school.projectservice.exeption.NotAccessRoleCompaignException;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final ProjectService projectService;
    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;

    @Transactional
    public CampaignDto publishCampaign(CampaignDto campaignDto) {
        long authorId = userServiceClient.getUser(userContext.getUserId()).id();

        Project project = projectService.findProjectById(campaignDto.getProjectId());
        validateCampaignAuthor(authorId, project);
        Campaign campaign = campaignMapper.toEntity(campaignDto);
        campaign.setCreatedBy(authorId);
        campaign.setUpdatedBy(authorId);

        return campaignMapper.toCampaignDto(campaignRepository.save(campaign));
    }

    @Transactional
    public CampaignDto updateCampaign(CampaignUpdateDto updateDto) {
        long authorId = userServiceClient.getUser(userContext.getUserId()).id();
        Campaign campaign = findCampaignById(updateDto.id);
        campaignMapper.updateEntity(campaign, updateDto);
        campaign.setUpdatedBy(authorId);

        return campaignMapper.toCampaignDto(campaignRepository.save(campaign));
    }

    @Transactional
    public void deleteCampaign(long id) {
        long authorId = userServiceClient.getUser(userContext.getUserId()).id();
        Campaign campaign = findCampaignById(id);
        campaign.setRemoved(true);
        campaign.setUpdatedBy(authorId);
        campaignRepository.save(campaign);
    }

    @Transactional
    public CampaignDto getCampaign(Long id) {
        Campaign campaign = findCampaignById(id);

        return campaignMapper.toCampaignDto(campaign);
    }

    @Transactional
    public Campaign findCampaignById(long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new EntityCampaignNotFoundException("Campaign with id %s was not found"
                        .formatted(id)));
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<Campaign> getCampaignsByProjectIdAndFilter(Long projectId, CampaignFilterDto filter) {
        return campaignRepository.findAllByFiltersAndProjectId(projectId, filter.getCreatedBy(),filter.getCreatedAt(),
                filter.getStatus());
    }

    private void validateCampaignAuthor(long authorId, Project project) {

        project.getTeams()
                .stream()
                .flatMap(team -> team.getTeamMembers()
                        .stream())
                .filter(teamMember -> teamMember.getUserId().equals(authorId))
                .filter(teamMember -> teamMember.getRoles().contains(TeamRole.MANAGER) ||
                        teamMember.getRoles().contains(TeamRole.OWNER))
                .findAny()
                .orElseThrow(() -> new NotAccessRoleCompaignException("Role User is not an owner or manager of the project"));
    }


}

