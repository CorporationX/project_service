package faang.school.projectservice.service.campaign;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.exception.CampaignCreationException;
import faang.school.projectservice.filter.campaign.CampaignFilterStrategy;
import faang.school.projectservice.mapper.CampaignDtoMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;
    private final ProjectRepository projectRepository;
    private final CampaignDtoMapper campaignDtoMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final List<CampaignFilterStrategy> campaignFilterStrategies;

    @Transactional
    @Override
    public void createCampaign(CampaignDto campaignDto) {
        Campaign campaign = campaignDtoMapper.toCampaign(campaignDto);
        projectRepository.existsById(campaign.getProject().getId());
        boolean isManagerOrOwner = getTeamRole(campaign).contains(TeamRole.MANAGER)
                                   || getTeamRole(campaign).contains(TeamRole.OWNER);
        if (isManagerOrOwner) {
            campaignRepository.save(campaign);
        } else {
            throw new CampaignCreationException("No members with roles MANAGER or OWNER in project");
        }
    }

    @Transactional
    @Override
    public void updateCampaign(CampaignDto campaignDto, Long id) {
        Campaign campaign = findCampaignByID(id);
        if (!campaign.getCreatedBy().equals(campaignDto.getCreatedBy())) {
            throw new CampaignCreationException(String.format(
                    "Id of creator cannot be changed. Campaign was created by user with ID = %d"
                    , campaignDto.getCreatedBy()));
        }
        if (campaign.getUpdatedBy() == null) {
            throw new CampaignCreationException("Info of update Author not filled in");
        }
        campaignRepository.save(campaign);
    }

    @Transactional
    @Override
    public void deleteCampaign(Long id) {
        Campaign campaign = findCampaignByID(id);
        campaign.setStatus(CampaignStatus.CANCELED);
        campaignRepository.save(campaign);
    }

    @Override
    public CampaignDto findById(Long id) {
         Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Campaign with ID = %d not found", id)));
        return campaignDtoMapper.toCampaignDto(campaign);
    }

    @Override
    public List<CampaignDto> findAll(CampaignFilterDto campaignFilterDto) {
        List<Campaign> campaignList = campaignRepository.findAll();
        List<CampaignDto> campaignDtoList = campaignDtoMapper.toListCampaignDto(campaignList);
        if (campaignFilterDto == null) {
            return campaignDtoList;
        }
        List <Campaign> filteredList =  campaignList.stream()
                .filter(campaign -> filterCampaigns(campaign, campaignFilterDto)).toList();
        return campaignDtoMapper.toListCampaignDto(filteredList);
    }

    private List<TeamRole> getTeamRole(Campaign campaign) {
        return teamMemberRepository.findByUserIdAndProjectId(campaign.getCreatedBy(), campaign.getProject()
                .getId()).getRoles();
    }

    private boolean filterCampaigns(Campaign campaign, CampaignFilterDto filterDto) {
        return campaignFilterStrategies.stream().allMatch(strategy -> strategy.filter(campaign, filterDto));
    }

    private Campaign findCampaignByID(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Campaign with ID = %d not found", id)));
    }
}
