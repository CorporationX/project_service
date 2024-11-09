package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.specification.CampaignSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignService {
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final CampaignRepository campaignRepository;
    private final ProjectRepository projectRepository;
    private final UserContext userContext;

    @Transactional(readOnly = true)
    public Campaign findCampaignById(Long campaignId) {
        return campaignRepository.findById(campaignId).orElseThrow(
                () -> new EntityNotFoundException("Campaign with id " + campaignId + " not found"));
    }

    @Transactional(readOnly = true)
    public List<Campaign> findFilteredCampaigns(Long projectId, CampaignFilterDto filter, Pageable pageable) {
        validateProjectExists(projectId);
        if (Objects.isNull(filter)) {
            return campaignRepository.findAllByProjectId(projectId, pageable).getContent();
        }
        Specification<Campaign> specification = CampaignSpecification.filteredCampaigns(
                projectId,
                filter.getCreatedById(),
                filter.getTitle(),
                filter.getMinGoal(),
                filter.getMaxGoal(),
                filter.getStatus(),
                filter.getCreatedAfter()
        );
        return campaignRepository.findAll(specification, pageable).getContent();
    }

    @Transactional
    public Campaign createNewCampaign(Campaign toCreate) {
        Long currentUserId = userContext.getUserId();
        Project toReceiveCampaign = projectRepository.getByIdOrThrow(toCreate.getProject().getId());

        validateProjectOwnerOrManager(toReceiveCampaign, currentUserId);
        validateNoCampaignWithTitle(toCreate.getTitle(), toReceiveCampaign.getId());

        toCreate.setAmountRaised(BigDecimal.ZERO);
        toCreate.setStatus(CampaignStatus.ACTIVE);
        toCreate.setCreatedBy(currentUserId);
        toCreate.setProject(toReceiveCampaign);
        toReceiveCampaign.getCampaigns().add(toCreate);

        Campaign created = campaignRepository.save(toCreate);
        log.info("Created a new campaign (title: '{}') for project (id: {})",
                created.getTitle(), created.getProject().getId());
        return created;
    }

    @Transactional
    public Campaign updateCampaignInfo(Long campaignId, String title, String description) {
        validateDataForUpdate(title, description);
        Campaign toBeUpdated = campaignRepository.findById(campaignId).orElseThrow(
                () -> new EntityNotFoundException("Campaign with id " + campaignId + " not found"));
        if (Objects.nonNull(title)) {
            toBeUpdated.setTitle(title);
        }
        if (Objects.nonNull(description)) {
            toBeUpdated.setDescription(description);
        }
        toBeUpdated.setUpdatedBy(userContext.getUserId());
        Campaign updated = campaignRepository.save(toBeUpdated);
        log.info("Updated campaign info (id: {})", updated.getId());
        return updated;
    }

    @Transactional
    public Campaign softDeleteById(Long campaignId) {
        Campaign toBeMarked = campaignRepository.findById(campaignId).orElseThrow(
                () -> new EntityNotFoundException("Campaign with id " + campaignId + " not found"));
        validateCampaignForDeletion(toBeMarked);
        toBeMarked.setDeleted(true);
        log.info("Marked campaign for deletion (id: {})", toBeMarked.getId());
        return campaignRepository.save(toBeMarked);
    }

    private void validateProjectOwnerOrManager(Project toReceiveCampaign, Long currentUserId) {
        TeamMember currentMember = teamMemberJpaRepository.findByUserIdAndProjectId(currentUserId, toReceiveCampaign.getId());
        if (Objects.isNull(currentMember)) {
            throw new IllegalStateException("Team member not present for provided project and user id");
        }
        boolean projectOwner = toReceiveCampaign.getOwnerId().equals(currentUserId);
        boolean projectManager = currentMember.getRoles().contains(TeamRole.MANAGER);
        if (!(projectOwner || projectManager)) {
            throw new IllegalStateException("Campaigns can only be started by project owner or manager");
        }
    }

    private void validateProjectExists(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project with id " + projectId + " not found");
        }
    }

    private void validateNoCampaignWithTitle(String title, Long id) {
        if (campaignRepository.findByTitleAndProjectId(title, id).isPresent()) {
            throw new IllegalStateException("Campaign with title '" + title + "' already exists");
        }
    }

    private void validateDataForUpdate(String title, String description) {
        if (Objects.isNull(title) || Objects.isNull(description) || title.isBlank() || description.isBlank()) {
            throw new IllegalArgumentException("Data required for update was null/empty");
        }
    }

    private void validateCampaignForDeletion(Campaign tobeMarked) {
        if (tobeMarked.getStatus().equals(CampaignStatus.ACTIVE)) {
            throw new IllegalStateException("Can't mark an active campaign for deletion");
        }
    }
}
