package faang.school.projectservice.service;

import faang.school.projectservice.dto.CampaignDto;
import faang.school.projectservice.dto.filter.CampaignFilterDto;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CampaignRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;

    @Transactional
    public CampaignDto createCampaign(CampaignDto campaignDto, Long userId) {
        if (nonNull(campaignDto.getId())) {
            throw new RuntimeException("Campaign id must be null");
        }
        validateUserToManipulateCampaign(userId, campaignDto.getProjectId());
        campaignRepository.findByTitleAndProjectId(campaignDto.getTitle(), campaignDto.getProjectId())
                .ifPresent(campaign -> { throw new RuntimeException("Campaign already exists"); });
        var campaign = campaignMapper.toEntity(campaignDto);
        campaign.setCreatedBy(userId);
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setAmountRaised(new BigDecimal(0));
        campaign.setUpdatedBy(userId);
        campaign.setProject(projectService.getProjectById(campaignDto.getProjectId()));
        return campaignMapper.toDto(campaignRepository.save(campaign));
    }

    @Transactional
    public CampaignDto updateCampaign(Long campaignId, CampaignDto campaignCreateDto, Long userId) {
        var campaign = getById(campaignId);
        validateUserToManipulateCampaign(userId, campaign.getProject().getId());
        if (!campaignCreateDto.getProjectId().equals(campaign.getProject().getId())) {
            throw new RuntimeException("Project cannot be changed");
        }
        BeanUtils.copyProperties(campaign, campaignCreateDto);
        campaign.setUpdatedBy(userId);
        return campaignMapper.toDto(campaignRepository.save(campaign));
    }

    @Transactional
    public void donateToCampaign(Long campaignId, BigDecimal amount) {
        var campaign = getById(campaignId);
        campaign.setAmountRaised(campaign.getAmountRaised().add(amount));
        if (campaign.getAmountRaised().compareTo(campaign.getGoal()) >= 0) {
            campaign.setStatus(CampaignStatus.COMPLETED);
        }
        campaignRepository.save(campaign);
    }

    public CampaignDto getCampaign(Long id) {
        return campaignMapper.toDto(getById(id));
    }

    public List<CampaignDto> getAllCampaigns(CampaignFilterDto filterDto) {
        Pageable pageable = PageRequest.of(filterDto.getPage(), filterDto.getPageSize());
        var statusName = isNull(filterDto.getStatus()) ? null : filterDto.getStatus().name();
        var campaigns = campaignRepository.findAllByFilters(filterDto.getNamePattern(), filterDto.getMinGoal(),
                filterDto.getMaxGoal(), statusName, pageable);

        return campaignMapper.toDto(campaigns);
    }

    public CampaignDto cancelCampaign(Long campaignId, Long userId) {
        var campaign = getById(campaignId);
        validateUserToManipulateCampaign(userId, campaign.getProject().getId());
        campaign.setStatus(CampaignStatus.CANCELED);

        return campaignMapper.toDto(campaignRepository.save(campaign));
    }

    private void validateUserToManipulateCampaign(Long userId, Long projectId) {
        var teamMember = teamMemberService.getTeamMember(userId, projectId);
        if (!teamMember.getRoles().contains(TeamRole.MANAGER) || !teamMember.getRoles().contains(TeamRole.OWNER)) {
            throw new RuntimeException("Only owner or manager can update campaign");
        }
    }

    public Campaign getById(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campaign not found"));
    }
}
