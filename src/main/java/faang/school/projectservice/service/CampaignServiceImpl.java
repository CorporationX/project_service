package faang.school.projectservice.service;

import faang.school.projectservice.dto.CampaignDto;
import faang.school.projectservice.dto.CampaignUpdateDto;
import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.dto.filter.CampaignFilterDto;
import faang.school.projectservice.filter.CampaignFilter;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.CampaignValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private static final int BATCH_SIZE = 100;

    private final CampaignRepository campaignRepository;
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final CampaignValidator campaignValidator;
    private final CampaignMapper campaignMapper;
    private final List<CampaignFilter> campaignFilters;

    @Override
    @Transactional
    public CampaignDto publishCampaign(CampaignDto campaignDto) {
        long creatorId = campaignDto.getCreatorId(), projectId = campaignDto.getProjectId();
        TeamMemberDto teamMember = teamMemberService.getTeamMember(creatorId, projectId);
        ProjectDto project = projectService.findById(campaignDto.getProjectId());
        campaignValidator.validationCampaignCreator(teamMember, project);

        Campaign campaign = campaignMapper.toEntity(campaignDto);
        return campaignMapper.toDto(campaignRepository.save(campaign));
    }

    @Override
    public void updateCampaign(CampaignUpdateDto campaignDto) {
        Long id = campaignDto.getId();
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campaign with id %d not found".formatted(id)));

        campaignMapper.updateFromDto(campaignDto, campaign);
        campaign.setUpdatedAt(LocalDateTime.now());
        campaignRepository.save(campaign);
    }

    @Override
    @Transactional
    public void deleteCampaign(long id) {
        campaignRepository.softDelete(id);
    }

    @Override
    public CampaignDto getCampaign(long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campaign with id %d not found".formatted(id)));
        return campaignMapper.toDto(campaign);
    }

    @Override
    public List<CampaignDto> getCampaignsSortedByCreatedAtAndByFilter(CampaignFilterDto campaignFilterDto) {
        List<Campaign> campaigns = getAllCampaignFilterByCreatedAt();

        return campaignFilters.stream()
                .filter(filter -> filter.isAccepted(campaignFilterDto))
                .reduce(campaigns.stream(),
                        (stream, filter) -> filter.apply(stream, campaignFilterDto),
                        Stream::concat)
                .map(campaignMapper::toDto)
                .toList();
    }

    private List<Campaign> getAllCampaignFilterByCreatedAt() {
        Sort sort = Sort.by("createdAt").ascending();
        List<Campaign> campaigns = new ArrayList<>();
        int page = 0;

        Page<Campaign> campaignPage;
        do {
            Pageable pageable = PageRequest.of(page, BATCH_SIZE, sort);
            campaignPage = campaignRepository.findAll(pageable);
            campaigns.addAll(campaignPage.getContent());
            page++;
        } while (campaignPage.hasContent());

        return campaigns;
    }
}
