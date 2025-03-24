package faang.school.projectservice.service.campaign;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.client.Campaign.CampaignDto;
import faang.school.projectservice.exception.DuplicateTitleException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.filter.campaign.DateStart;
import faang.school.projectservice.filter.campaign.Owner;
import faang.school.projectservice.filter.campaign.Status;
import faang.school.projectservice.mapper.campaign.CampaignMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignService {

    private final CampaignMapper campaignMapper;
    private final ProjectRepository projectRepository;
    private final CampaignRepository campaignRepository;

    @Transactional
    public CampaignDto create(CampaignDto campaignDto, long projectId, long creatorId) {
        checkDuplicateTitle(campaignDto.getTitle(), projectId);
        Project project = checkUser(projectId, creatorId);

        Campaign newCampaign = campaignMapper.toEntity(campaignDto, project, CampaignStatus.ACTIVE);
        newCampaign = campaignRepository.save(newCampaign);

        return campaignMapper.toDto(newCampaign);
    }

    @Transactional
    public CampaignDto update(CampaignDto campaignDto, long projectId, long campaignId, long updaterId) {
        Campaign updatingCampaign = validateCampaignNotDeleted(campaignId);
        Project project = checkUser(projectId, updaterId);

        campaignMapper.updateEntity(updatingCampaign, campaignDto, project, updaterId, CampaignStatus.ACTIVE);
        updatingCampaign = campaignRepository.save(updatingCampaign);
        return campaignMapper.toDto(updatingCampaign);
    }

    @Transactional
    public void softDelete(long projectId, long campaignId, long deleterId) {
        Campaign deletingCampaign = validateCampaignNotDeleted(campaignId);
        checkUser(projectId, deleterId);

        deletingCampaign.setStatus(CampaignStatus.DELETED);
        campaignRepository.save(deletingCampaign);
    }

    @Transactional
    public CampaignDto getCampaign(long projectId, long campaignId) {
        Campaign campaign = validateCampaignNotDeleted(campaignId);
        getProjectByIdAndValidate(projectId);

        return campaignMapper.toDto(campaign);
    }

    @Transactional
    public List<CampaignDto> getCampaignByFilter(long projectId, CampaignDto campaignDto) {
        getProjectByIdAndValidate(projectId);

        List<Filter<Campaign>> filters = new ArrayList<>();
        filters.add(new DateStart(campaignDto.getCreatedAt()));
        filters.add(new Owner(campaignDto.getCreatedBy()));
        filters.add(new Status(campaignDto.getStatus()));

        return campaignRepository.findAll().stream()
                .filter(campaign -> filters.stream()
                        .allMatch(filter -> filter.matches(campaign)))
                .sorted(Comparator.comparing(Campaign::getCreatedAt).reversed())
                .map(campaignMapper::toDto)
                .collect(Collectors.toList());
    }

    private Campaign validateCampaignNotDeleted(long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId).
                orElseThrow(() -> {
                    log.error(ErrorMessages.CAMPAIGN_NOT_FOUND_ID.getMessage(), campaignId);
                    throw new NotFoundException(ErrorMessages.CAMPAIGN_NOT_FOUND.getMessage());
                });

        if (campaign.getStatus() == CampaignStatus.DELETED) {
            log.error(ErrorMessages.CAMPAIGN_ALREADY_DELETED_ID.getMessage(), campaignId);
            throw new NotFoundException(ErrorMessages.CAMPAIGN_ALREADY_DELETED.getMessage());
        }

        return campaign;
    }

    private void checkDuplicateTitle(String title, long projectId) {
        if (campaignRepository.existsByTitleAndProjectId(title, projectId)) {
            log.error(ErrorMessages.CAMPAIGN_TITLE_ALREADY_EXISTS_ID.getMessage(), projectId);
            throw new DuplicateTitleException(ErrorMessages.CAMPAIGN_TITLE_ALREADY_EXISTS.getMessage());
        }
    }

    private Project checkUser(long projectId, long userId) {
        Project project = getProjectByIdAndValidate(projectId);

        if (isOwner(project, userId)) {
            return project;
        }

        List<Team> teams = project.getTeams();
        Optional<TeamMember> user = findUserInTeams(teams, userId);

        if (user.isPresent()) {
            checkUserRole(user.get());
            return project;
        }

        log.error(ErrorMessages.USER_NOT_FOUND_ID.getMessage(), userId);
        throw new NotFoundException(ErrorMessages.USER_NOT_FOUND.getMessage());
    }

    private Project getProjectByIdAndValidate(long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error(ErrorMessages.PROJECT_NOT_FOUND_ID.getMessage(), projectId);
                    throw new NotFoundException(ErrorMessages.PROJECT_NOT_FOUND.getMessage());
                });
    }

    private boolean isOwner(Project project, long userId) {
        return project.getOwnerId() == userId;
    }

    private Optional<TeamMember> findUserInTeams(List<Team> teams, long userId) {
        return teams.stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(member -> member.getUserId() == userId)
                .findFirst();
    }

    private void checkUserRole(TeamMember user) {
        if (user.getRoles().contains(TeamRole.MANAGER)) {
            return;
        }

        log.error(ErrorMessages.USER_HAVE_DIFFERENT_ROLE_ID.getMessage(), user.getId());
        throw new NotFoundException(ErrorMessages.USER_HAVE_DIFFERENT_ROLE.getMessage());
    }
}
