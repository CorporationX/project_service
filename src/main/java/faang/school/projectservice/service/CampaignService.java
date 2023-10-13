package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.service.exception.DataValidationException;
import faang.school.projectservice.dto.campaign.UpdateCampaignDto;
import faang.school.projectservice.filters.campaign.CampaignFilter;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.exception.notFoundException.UserNotFoundException;
import faang.school.projectservice.util.validator.CampaignServiceValidator;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserServiceClient userServiceClient;

    public CampaignDto publish(CampaignDto campaignDto) {
        isUserExist(campaignDto.getCreatedBy());
        TeamMember foundTeamMember = teamMemberRepository.findById(campaignDto.getCreatedBy());

        Project project = projectRepository.getProjectById(campaignDto.getProjectId());

        campaignServiceValidator.validate(project, foundTeamMember);

        Campaign campaign = campaignMapper.toEntityCampaign(campaignDto);
        campaign.setProject(project);
        campaign.setStatus(CampaignStatus.ACTIVE);

        campaignRepository.save(campaign);

        return campaignMapper.toCampaignDto(campaign);
    }

    @Transactional
    public UpdateCampaignDto update(UpdateCampaignDto updateCampaignDto) {
        isUserExist(updateCampaignDto.getUpdatedBy());
        Optional<Campaign> campaignById = campaignRepository.findById(updateCampaignDto.getId());

        campaignById.orElseThrow(()-> new DataValidationException("No such campaign found."));

        TeamMember foundTeamMember = teamMemberRepository.findById(updateCampaignDto.getUpdatedBy());

        Long projectId = campaignById.get().getProject().getId();
        Project project = projectRepository.getProjectById(projectId);

        campaignServiceValidator.validate(project, foundTeamMember);

        Campaign campaign = campaignById.get();
        campaign.setTitle(updateCampaignDto.getTitle());
        campaign.setDescription(updateCampaignDto.getDescription());
        campaign.setUpdatedBy(updateCampaignDto.getUpdatedBy());

        Campaign save = campaignRepository.save(campaign);
        return campaignMapper.toUpdateCampaignDto(save);
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

        return campaignMapper.toCampaignDto(campaign);
    }

    public List<CampaignDto> getAllCampaigns(long projectId) {
        List<Optional<Campaign>> campaigns = campaignRepository.findAllByProjectId(projectId);
        return campaigns.stream()
                .map(campaign -> campaignMapper.toCampaignDto(campaign.orElseThrow(() -> new DataValidationException("No such campaign found."))))
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
                .map(campaign -> campaignMapper.toCampaignDto(campaign))
                .toList();
    }

    private void isUserExist(long id) {
        try {
            userServiceClient.getUser(id);
        } catch (FeignException.FeignClientException exception) {
            throw new UserNotFoundException("This user doesn't exist");
        }
    }

    private List<Campaign> doSort(List<Campaign> campaigns, Comparator<Campaign> comparator) {
        return campaigns.stream()
                .sorted(comparator)
                .toList();
    }
}