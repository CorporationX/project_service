package faang.school.projectservice.service;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.service.exception.DataValidationException;
import faang.school.projectservice.filters.campaign.CampaignFilter;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.util.validator.CampaignServiceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
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
    private final List<CampaignFilter> campaignFilters;

    public CampaignDto publish(CampaignDto campaignDto, Long userId) {
        TeamMember foundTeamMember = teamMemberRepository.findById(userId);

        Project project = projectRepository.getProjectById(campaignDto.getProjectId());

        campaignServiceValidator.validate(project, foundTeamMember);

        Campaign campaign = campaignMapper.toEntity(campaignDto);
        campaign.setProject(project);
        campaign.setStatus(CampaignStatus.ACTIVE);

        campaignRepository.save(campaign);

        return campaignMapper.toDto(campaign);
    }

    @Transactional
    public CampaignDto update(CampaignDto campaignDto, Long userId) {
        Optional<Campaign> campaignById = campaignRepository.findById(campaignDto.getId());

        campaignById.orElseThrow(()-> new DataValidationException("No such campaign found."));

        TeamMember foundTeamMember = teamMemberRepository.findById(userId);

        Project project = projectRepository.getProjectById(campaignDto.getProjectId());

        campaignServiceValidator.validate(project, foundTeamMember);

        Campaign campaign = campaignById.get();
        campaign.setTitle(campaignDto.getTitle());
        campaign.setDescription(campaignDto.getDescription());
        campaign.setStatus(campaignDto.getCampaignStatus());

        Campaign save = campaignRepository.save(campaign);
        return campaignMapper.toDto(save);
    }

    public void delete(long campaignId) {
        Optional<Campaign> campaignById = campaignRepository.findById(campaignId);

        Campaign campaign = campaignById
                .orElseThrow(()-> {
                    log.error("No such campaign found.");
                    return new DataValidationException("No such campaign found.");
                });

        campaign.setDeleted(true);

        campaignRepository.save(campaign);
    }

    public CampaignDto getCampaign(long campaignId) {
        Optional<Campaign> campaignById = campaignRepository.findById(campaignId);

        Campaign campaign = campaignById
                .orElseThrow(() -> {
                    log.error("No such campaign found.");
                    return new DataValidationException("No such campaign found.");
                });

        return campaignMapper.toDto(campaign);
    }

    public List<CampaignDto> getAllCampaigns(long projectId) {
        List<Campaign> campaigns = campaignRepository.findAll();
        List<Campaign> campaignsByProjectId = new ArrayList<>();

        for (Campaign campaign : campaigns) {
            if (campaign.getProject().getId() == projectId) {
                campaignsByProjectId.add(campaign);
            }
        }

        return campaignsByProjectId.stream()
                .map(campaign -> campaignMapper.toDto(campaign))
                .toList();
    }

    public List<CampaignDto> getByFilters(CampaignFilterDto campaignFilterDto) {
        List<Campaign> campaignStream = campaignRepository.findAll();
        List<CampaignFilter> campaignFilterList = campaignFilters
                .stream()
                .filter(campaignFilter -> campaignFilter.isApplicable(campaignFilterDto)).toList();

        for (CampaignFilter campaignFilter : campaignFilterList) {
            campaignStream = campaignFilter.apply(campaignStream.stream(), campaignFilterDto).toList();
        }

        List<Campaign> result = doSort(campaignStream, (c1, c2) -> c1.getCreatedAt().compareTo(c2.getCreatedAt()));

        return result.stream()
                .map(campaign -> campaignMapper.toDto(campaign))
                .toList();
    }

    private List<Campaign> doSort(List<Campaign> campaigns, Comparator<Campaign> comparator) {
        return campaigns.stream()
                .sorted(comparator)
                .toList();
    }
}