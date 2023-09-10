package faang.school.projectservice.service;

import faang.school.projectservice.dto.company.CampaignDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.util.CampaignServiceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignService {
    private final CampaignMapper campaignMapper;
    private final CampaignRepository campaignRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final CampaignServiceValidator campaignServiceValidator;

    @Transactional
    public CampaignDto publishCampaign(CampaignDto dto, long userId) {
        TeamMember foundTeamMember = teamMemberRepository.findById(userId);
        Project foundProject = projectRepository.getProjectById(dto.getProjectId());

        campaignServiceValidator.validatePublishedCampaign(foundProject, foundTeamMember);

        Campaign campaign = campaignMapper.toEntity(dto);
        campaign.setProject(foundProject);

        campaignRepository.save(campaign);
        log.info("Published campaign: {}", campaign);
        return campaignMapper.toDto(campaign);
    }

    @Transactional
    public CampaignDto updateCampaign(CampaignDto dto, long userId) {
        Campaign campaign = campaignRepository.findById(dto.getId())
                .orElseThrow(() -> new DataValidationException("Campaign not found"));
        TeamMember teamMember = teamMemberRepository.findById(userId);
        Project project = projectRepository.getProjectById(dto.getProjectId());

        campaignServiceValidator.validatePublishedCampaign(project, teamMember);

        campaign.setTitle(dto.getTitle());
        campaign.setDescription(dto.getDescription());

        Campaign savedCompany = campaignRepository.save(campaign);
        log.info("Updated campaign: {}", savedCompany);
        return campaignMapper.toDto(savedCompany);
    }

    @Transactional(readOnly = true)
    public CampaignDto getCampaignById(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Campaign with id" + id + " not found"));

        log.info("Retrieved campaign: {}", campaign);
        return campaignMapper.toDto(campaign);
    }

    @Transactional
    public void deleteCampaign(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Campaign with id" + id + " not found"));

        campaign.setDeleted(true);
        log.info("Set company with id: {} in deleted mode", id);
    }

    @Transactional(readOnly = true)
    public List<CampaignDto> getAllCampaignsWithFilters(String status, Long id) {
        campaignServiceValidator.statusValidation(status);
        return campaignRepository.getAllWithFilters(status, id);
    }

}
