package faang.school.projectservice.service.campaign;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.exeption.EntityCampaignNotFoundException;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.campaignfilter.CampaignFilter;
import faang.school.projectservice.validator.CampaignValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final ProjectService projectService;
    private final CampaignRepository campaignRepository;
    private final CampaignValidator validatorCampaign;
    private final CampaignMapper campaignMapper;
    private final List<CampaignFilter> filters;

    @Transactional
    public CampaignDto publishCampaign(CampaignDto campaignDto) {
        long authorId = userServiceClient.getUser(userContext.getUserId()).id();

        Project project = projectService.findProjectById(campaignDto.getProjectId());
        validatorCampaign.validateCampaignAuthor(authorId, project);
        Campaign campaign = campaignMapper.toEntity(campaignDto);
        campaign.setCreatedBy(authorId);
        campaign.setUpdatedBy(authorId);

        return campaignMapper.toCampaignDto(campaignRepository.save(campaign));
    }

    @Transactional
    public CampaignDto updateCampaign(CampaignUpdateDto updateDto) {
        long authorId = userServiceClient.getUser(userContext.getUserId()).id();
        Campaign campaign = findCampaignById(updateDto.id);
        campaignMapper.updateEntity(campaign, updateDto);
        campaign.setUpdatedBy(authorId);

        return campaignMapper.toCampaignDto(campaignRepository.save(campaign));
    }

    @Transactional
    public void deleteCampaign(long id) {
        long authorId = userServiceClient.getUser(userContext.getUserId()).id();
        Campaign campaign = findCampaignById(id);
        campaign.setRemoved(true);
        campaign.setUpdatedBy(authorId);
        campaignRepository.save(campaign);
    }

    @Transactional
    public CampaignDto getCampaign(Long id) {
        Campaign campaign = findCampaignById(id);

        return campaignMapper.toCampaignDto(campaign);
    }

    @Transactional
    public Campaign findCampaignById(long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new EntityCampaignNotFoundException("Campaign with id %s was not found"
                        .formatted(id)));
    }

    @Transactional()
    public List<CampaignDto> getAllCampaignsByFilter(CampaignFilterDto filterDto) {
        Stream<Campaign> campaigns = campaignRepository.findAll().stream();
        List<Campaign> campaignsList = filters
                .stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(campaigns, (stream, filter) -> filter.apply(stream, filterDto),
                        (newStream, oldStream) -> newStream)
                .sorted(Comparator.comparing(Campaign::getCreatedAt).reversed())
                .toList();
        return campaignMapper.toDtoList(campaignsList);
    }
}

