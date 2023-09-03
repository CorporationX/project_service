package faang.school.projectservice.service;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.util.validator.CampaignServiceValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignMapper campaignMapper;
    private final CampaignRepository campaignRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final CampaignServiceValidator campaignServiceValidator;

    public CampaignDto publish(CampaignDto campaignDto, Long userId) {
        TeamMember foundTeamMember = teamMemberRepository.findById(userId);

        Project project = projectRepository.getProjectById(campaignDto.getProjectId());

        campaignServiceValidator.validatePublish(project, foundTeamMember);

        Campaign campaign = campaignMapper.toEntity(campaignDto);
        campaign.setProject(project);

        campaignRepository.save(campaign);

        return campaignMapper.toDto(campaign);
    }

    @Transactional
    public CampaignDto update(CampaignDto campaignDto, Long userId) {
        Optional<Campaign> campaignById = campaignRepository.findById(campaignDto.getId());

        campaignById.orElseThrow(()-> new DataValidationException("No such campaign found."));

        TeamMember foundTeamMember = teamMemberRepository.findById(userId);

        Project project = projectRepository.getProjectById(campaignDto.getProjectId());

        campaignServiceValidator.validatePublish(project, foundTeamMember);

        Campaign campaign = campaignById.get();
        campaign.setTitle(campaignDto.getTitle());
        campaign.setDescription(campaignDto.getDescription());

        Campaign save = campaignRepository.save(campaign);
        return campaignMapper.toDto(save);
    }
}