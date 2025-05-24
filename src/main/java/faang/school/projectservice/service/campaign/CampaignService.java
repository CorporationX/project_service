package faang.school.projectservice.service.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.filter.campaign.CampaignFilter;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.adapter.campaign.CampaignRepoAdapter;
import faang.school.projectservice.repository.adapter.project.ProjectRepoAdapter;
import faang.school.projectservice.repository.adapter.teammember.TeamMemberRepoAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignRepoAdapter campaignRepoAdapter;
    private final ProjectRepoAdapter projectRepoAdapter;
    private final TeamMemberRepoAdapter teamMemberRepoAdapter;
    private final CampaignMapper campaignMapper;
    private final List<CampaignFilter> filters;

    public CampaignDto createCampaign(CampaignDto campaignDto, Long userId) {
        Project project = projectRepoAdapter.getProjectById(campaignDto.getProjectId());

        boolean isManager = teamMemberRepoAdapter.getByUserId(userId).stream()
                .filter(teamMember ->
                        teamMember.getTeam().getProject().getId().equals(project.getId()))
                .flatMap(teamMember -> teamMember.getRoles().stream())
                .anyMatch(teamRole -> teamRole.name().equals("MANAGER"));

        if (!project.getOwnerId().equals(userId) && !isManager) {
            throw new IllegalArgumentException("User not allowed to create campaign");
        }

        Campaign campaign = campaignMapper.toEntity(campaignDto);
        campaign.setProject(project);
        campaign.setStatus(campaignDto.getStatus() != null ?
                campaignDto.getStatus() : campaign.getStatus());
        campaign.setCreatedAt(LocalDateTime.now());
        campaign.setCreatedBy(userId);
        campaign.setAmountRaised(campaignDto.getAmountRaised() != null ?
                campaignDto.getAmountRaised() : BigDecimal.ZERO);

        return campaignMapper.toDto(campaignRepoAdapter.save(campaign));
    }

    public CampaignDto updateCampaign(Long campaignId, CampaignDto campaignDto, Long userId) {
        Campaign existing = campaignRepoAdapter.getCampaignById(campaignId);

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

        existing.setUpdatedAt(LocalDateTime.now());
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
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdatedBy(userId);

        return campaignMapper.toDto(campaignRepoAdapter.save(existing));
    }

    public CampaignDto getCampaignById(Long campaignId) {
        Campaign existing = campaignRepoAdapter.getCampaignById(campaignId);

        return campaignMapper.toDto(existing);
    }

    public List<CampaignDto> getCampaignDtoWithFilters(CampaignDto campaignDto) {
        Stream<Campaign> filteredCampaign = campaignRepoAdapter.getAll().stream();

        for (CampaignFilter campaignFilter : filters) {
            if (campaignFilter.isApplicable(campaignDto)) {
                filteredCampaign = campaignFilter.apply(filteredCampaign, campaignDto);
            }
        }

        return filteredCampaign
                .map(campaignMapper::toDto)
                .sorted(Comparator.comparing(CampaignDto::getCreatedAt)
                .reversed())
                .toList();
    }
}
