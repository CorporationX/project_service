package faang.school.projectservice.service;

import faang.school.projectservice.dto.company.CreateCampanyDto;
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
    public CreateCampanyDto publishCampaign(CreateCampanyDto dto, long userId) {
        Optional<Campaign> foundCompany = campaignRepository.findById(dto.getId());
        TeamMember foundTeamMember = teamMemberRepository.findById(userId);
        Project foundProject = projectRepository.getProjectById(dto.getProjectId());

        if (foundCompany.isPresent()) {
            log.info("Campaign already published: {}", foundCompany.get());
            return campaignMapper.toDto(foundCompany.get());
        }

        campaignServiceValidator.validatePublishedCampaign(foundProject, foundTeamMember);

        Campaign campaign = campaignMapper.toEntity(dto);
        campaign.setProject(foundProject);
        campaignRepository.save(campaign);
        log.info("Published campaign: {}", campaign);
        return campaignMapper.toDto(campaign);
    }
}
