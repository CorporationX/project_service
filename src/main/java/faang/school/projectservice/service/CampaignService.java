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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private final ProjectRepository projectRepository;
    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;

    public CampaignDto createCampaign(Long userId, CreateCampaignDto createCampaignDto) {
        return null;
    }

    public CampaignDto updateCampaign(Long userId, Long campaignId, UpdateCampaignDto updateCampaignDto) {
        Campaign campaign = findCampaignById(campaignId);
        campaignMapper.update(campaign, updateCampaignDto);
        campaign.setUpdatedBy(userId);
        return campaignMapper.toCampaignDto(campaignRepository.save(campaign));
    }

    public void deleteCampaign(Long campaignId) {
        Campaign campaign = findCampaignById(campaignId);
        campaign.setStatus(CampaignStatus.DELETED);
        campaignRepository.save(campaign);
    }

    public CampaignDto getCampaignDtoById(Long campaignId) {
        return campaignMapper.toCampaignDto(findCampaignById(campaignId));
    }

    public List<CampaignDto> getAllCampaignsByProject(Long projectId, CampaignFilterDto campaignFilterDto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));
        Stream<Campaign> campaigns = campaignRepository.findAllByFilters(
                        project,
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
