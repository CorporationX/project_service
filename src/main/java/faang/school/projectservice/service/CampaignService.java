package faang.school.projectservice.service;

import faang.school.projectservice.dto.company.CampaignDto;
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
}
