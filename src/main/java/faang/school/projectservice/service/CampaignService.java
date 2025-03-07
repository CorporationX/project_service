package faang.school.projectservice.service;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CreateCampaignDto;
import faang.school.projectservice.dto.campaign.UpdateCampaignDto;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.project.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private final ProjectRepository projectRepository;
    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;
    private final ProjectValidator projectValidator;

    @Transactional
    public CampaignDto createCampaign(Long userId, CreateCampaignDto createCampaignDto) {
        Long projectId = createCampaignDto.getProjectId();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));

        projectValidator.validateUserOwnerOrManager(project, userId);

        Campaign campaign = campaignMapper.toEntity(createCampaignDto);
        campaign.setCreatedBy(userId);
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setProject(project);
        campaign.setAmountRaised(new BigDecimal(0));
        return campaignMapper.toCampaignDto(campaignRepository.save(campaign));
    }

    @Transactional
    public CampaignDto updateCampaign(Long userId, Long campaignId, UpdateCampaignDto updateCampaignDto) {
        Campaign campaign = findCampaignById(campaignId);

        projectValidator.validateUserOwnerOrManager(campaign.getProject(), userId);

        campaignMapper.update(campaign, updateCampaignDto);
        campaign.setUpdatedBy(userId);
        return campaignMapper.toCampaignDto(campaignRepository.save(campaign));
    }

    @Transactional
    public void deleteCampaign(Long userId, Long campaignId) {
        Campaign campaign = findCampaignById(campaignId);

        projectValidator.validateUserOwnerOrManager(campaign.getProject(), userId);

        campaign.setStatus(CampaignStatus.CANCELED);
        campaignRepository.save(campaign);
    }

    @Transactional(readOnly = true)
    public CampaignDto getCampaignDtoById(Long campaignId) {
        return campaignMapper.toCampaignDto(findCampaignById(campaignId));
    }

    @Transactional(readOnly = true)
    public List<CampaignDto> getFilteredCampaigns(CampaignFilterDto campaignFilterDto) {
        Stream<Campaign> campaigns = campaignRepository.findAllByFilters(
                        campaignFilterDto.getProjectId(),
                        campaignFilterDto.getNamePattern(),
                        campaignFilterDto.getMinGoal(),
                        campaignFilterDto.getMaxGoal(),
                        campaignFilterDto.getStatus(),
                        campaignFilterDto.getCreatedBy(),
                        campaignFilterDto.getStartDate(),
                        campaignFilterDto.getEndDate())
                .stream();

        return campaigns
                .map(campaignMapper::toCampaignDto)
                .toList();
    }

    public Campaign findCampaignById(Long campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("Campaign not found with id: " + campaignId));
    }
}
