package faang.school.projectservice.service.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.filter.campaign.CampaignFilter;

import faang.school.projectservice.mapper.campaign.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.adapter.campaign.CampaignRepoAdapter;
import faang.school.projectservice.repository.adapter.project.ProjectRepositoryAdapter;
import faang.school.projectservice.repository.adapter.teammember.TeamMemberRepoAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignService {
    private final CampaignRepoAdapter campaignRepoAdapter;
    private final ProjectRepositoryAdapter projectRepoAdapter;
    private final TeamMemberRepoAdapter teamMemberRepoAdapter;
    private final CampaignMapper campaignMapper;
    private final List<CampaignFilter> filters;

    public CampaignDto createCampaign(CampaignDto campaignDto, Long userId) {
        Project project = projectRepoAdapter.getProjectById(campaignDto.getProjectId());

        validateUserProjectPermissions(userId, project);
        Campaign campaign = campaignMapper.toEntity(campaignDto);
        campaign.setProject(project);
        campaign.setStatus(campaignDto.getStatus() != null ?
                campaignDto.getStatus() : CampaignStatus.ACTIVE);
        campaign.setCreatedBy(userId);
        campaign.setAmountRaised(campaignDto.getAmountRaised() != null ?
                campaignDto.getAmountRaised() : BigDecimal.ZERO);

        return campaignMapper.toDto(campaignRepoAdapter.save(campaign));
    }

    private void validateUserProjectPermissions(Long userId, Project project) {
        boolean isManager = teamMemberRepoAdapter.getByUserId(userId).stream()
                .filter(teamMember ->
                        Objects.equals(teamMember.getTeam().getProject().getId(), project.getId()))
                .flatMap(teamMember -> teamMember.getRoles().stream())
                .anyMatch(teamRole ->
                        Objects.equals(teamRole.name(), "MANAGER"));

        if (!Objects.equals(project.getOwnerId(), userId) && !isManager) {
            String errorMessage =
                    String.format("User %d is not allowed to perform this operation on project %d." +
                            " Reason: Not project owner and not a manager.", userId, project.getId());
            log.warn(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public CampaignDto updateCampaign(Long campaignId, CampaignDto campaignDto, Long userId) {
        Campaign existing = campaignRepoAdapter.getCampaignById(campaignId);

        validateUserProjectPermissions(userId, existing.getProject());

        if (!existing.getCreatedBy().equals(campaignDto.getCreatedBy())) {
            throw new IllegalArgumentException("Cannot change author");
        }

        Optional.ofNullable(campaignDto.getTitle())
                .ifPresent(existing::setTitle);
        Optional.ofNullable(campaignDto.getDescription())
                .ifPresent(existing::setDescription);
        Optional.ofNullable(campaignDto.getGoal())
                .ifPresent(existing::setGoal);
        Optional.ofNullable(campaignDto.getStatus())
                .ifPresent(existing::setStatus);

        existing.setUpdatedBy(userId);

        return campaignMapper.toDto(campaignRepoAdapter.save(existing));
    }

    public CampaignDto softDelete(Long campaignId, Long userId) {
        Campaign existing = campaignRepoAdapter.getCampaignById(campaignId);

        if (!existing.getCreatedBy().equals(userId)
                && !existing.getProject().getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("Not allowed to delete campaign");
        }

        existing.setStatus(CampaignStatus.CANCELED);
        existing.setUpdatedBy(userId);

        return campaignMapper.toDto(campaignRepoAdapter.save(existing));
    }

    public CampaignDto getCampaignById(Long campaignId) {
        Campaign existing = campaignRepoAdapter.getCampaignById(campaignId);

        return campaignMapper.toDto(existing);
    }

    public Page<CampaignDto> getCampaignDtoWithFilters(
            CampaignFilterDto campaignFilterDto,
            Pageable pageable
    ) {
        List<Specification<Campaign>> specifications = new ArrayList<>();

        for (CampaignFilter campaignFilter : filters) {
            if (campaignFilter.isApplicable(campaignFilterDto)) {
                specifications.add(campaignFilter.apply(campaignFilterDto));
            }
        }

        Specification<Campaign> combinedSpec = specifications.stream()
                .reduce(Specification.where(null), Specification::and);

        Page<Campaign> campaignPage = campaignRepoAdapter.getAllPages(combinedSpec, pageable);

        return campaignPage.map(campaignMapper::toDto);
    }
}
