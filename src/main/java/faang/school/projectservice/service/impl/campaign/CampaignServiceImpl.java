package faang.school.projectservice.service.impl.campaign;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.dto.campaign.CampaignDto;
import faang.school.projectservice.model.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.model.entity.Campaign;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.filter.campaign.CampaignFilter;
import faang.school.projectservice.model.mapper.campaign.CampaignMapper;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.service.CampaignService;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.validator.campaign.CampaignValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final ProjectService projectService;
    private final UserContext userContext;
    private final CampaignValidator campaignValidator;
    private final CampaignMapper campaignMapper;
    private final List<CampaignFilter> filters;

    @Override
    @Transactional
    public CampaignDto publishCampaign(CampaignDto campaignDto) {
        long authorId = userContext.getUserId();
        Project project = projectService.getProject(campaignDto.projectId());
        campaignValidator.validateCampaignAuthor(authorId, project);
        Campaign campaign = campaignMapper.toEntity(campaignDto);
        campaign.setCreatedBy(authorId);
        campaign.setUpdatedBy(authorId);

        return campaignMapper.toDto(campaignRepository.save(campaign));
    }

    @Override
    @Transactional
    public CampaignDto updateCampaign(CampaignUpdateDto campaignDto) {
        long authorId = userContext.getUserId();
        Campaign campaign = findCampaignById(campaignDto.id());

        campaignMapper.updateEntity(campaign, campaignDto);
        campaign.setUpdatedBy(authorId);

        return campaignMapper.toDto(campaignRepository.save(campaign));
    }

    @Override
    @Transactional
    public void deleteCampaign(long id) {
        long authorId = userContext.getUserId();
        Campaign campaign = findCampaignById(id);
        campaign.setRemoved(true);
        campaign.setUpdatedBy(authorId);
        campaignRepository.save(campaign);
    }

    @Override
    @Transactional(readOnly = true)
    public CampaignDto getCampaign(long id) {
        Campaign campaign = findCampaignById(id);
        return campaignMapper.toDto(campaign);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CampaignDto> getAllCampaignsByFilter(CampaignFilterDto filterDto) {
        Stream<Campaign> campaigns = campaignRepository.findAll().stream();
        List<Campaign> campaignList = filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(campaigns, (stream, filter) -> filter.apply(stream, filterDto),
                        (newStream, oldStream) -> newStream)
                .sorted(Comparator.comparing(Campaign::getCreatedAt).reversed())
                .toList();

        return campaignMapper.toDtoList(campaignList);
    }

    @Override
    @Transactional(readOnly = true)
    public Campaign findCampaignById(long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campaign with id %s was not found"
                        .formatted(id)));
    }
}
