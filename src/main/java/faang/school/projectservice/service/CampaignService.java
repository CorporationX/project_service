package faang.school.projectservice.service;

import faang.school.projectservice.dto.campaign.CampaignCreateDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.exception.CampaignCreatorModificationException;
import faang.school.projectservice.exception.CampaignNotFoundException;
import faang.school.projectservice.exception.DateParseException;
import faang.school.projectservice.exception.EmptyFilterException;
import faang.school.projectservice.exception.ExceptionMessage;
import faang.school.projectservice.exception.PermissionDeniedException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final CampaignRepository campaignRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;
    private final CampaignMapper campaignMapper;

    public CampaignUpdateDto create(CampaignCreateDto campaignCreateDto) {
        var userId = campaignCreateDto.getUpdatedBy();
        campaignCreateDto.setCreatedBy(userId);
        Campaign campaign = campaignMapper.toEntity(campaignCreateDto);

        if (isManager(userId) || isProjectOwner(campaign, userId, campaignCreateDto.getProjectId())) {
            return campaignMapper.toDto(campaignRepository.save(campaign));
        }

        throw new PermissionDeniedException(ExceptionMessage.PERMISSION_DENIED);
    }

    public CampaignUpdateDto update(Long id, CampaignUpdateDto dto) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new CampaignNotFoundException(ExceptionMessage.CAMPAIGN_NOT_FOUND, id));

        if (dto.getCreatedBy() != null && !dto.getCreatedBy().equals(campaign.getCreatedBy())) {
            throw new CampaignCreatorModificationException(ExceptionMessage.CAMPAIGN_CREATOR_MODIFICATION);
        }

        campaignMapper.update(campaign, dto);
        return campaignMapper.toDto(campaignRepository.save(campaign));
    }

    public CampaignUpdateDto delete(long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new CampaignNotFoundException(ExceptionMessage.CAMPAIGN_NOT_FOUND, id));

        campaign.setStatus(CampaignStatus.CANCELED);
        return campaignMapper.toDto(campaignRepository.save(campaign));
    }

    public CampaignUpdateDto findById(long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new CampaignNotFoundException(ExceptionMessage.CAMPAIGN_NOT_FOUND, id));

        return campaignMapper.toDto(campaign);
    }

    public List<CampaignUpdateDto> getFilteredCampaigns(CampaignFilterDto filterDto) {
        if (filterDto.startDate()==null && filterDto.status()==null && filterDto.createdId()==null) {
            throw new EmptyFilterException(ExceptionMessage.EMPTY_FILTER);
        }

        LocalDateTime startDate = null;
        if (filterDto.startDate()!=null && !filterDto.startDate().isBlank()) {
            startDate = getDate(filterDto.startDate());
        }

        List<Campaign> campaigns = campaignRepository.findCampaignsByFilters(filterDto.status(), filterDto.createdId(), startDate);

        return campaigns.stream()
                .map(campaignMapper::toDto)
                .toList();
    }

    private boolean isManager(Long userId) {
        return teamMemberRepository.findByUserId(userId).stream()
                .flatMap(teamMember -> teamMember.getRoles().stream())
                .anyMatch(teamRole -> teamRole == TeamRole.MANAGER);
    }

    private boolean isProjectOwner(Campaign campaign, Long userId, Long projectId) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(ExceptionMessage.PROJECT_NOT_FOUND, projectId));
        campaign.setProject(project);

        return project.getOwnerId().equals(userId);
    }

    private LocalDateTime getDate(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
            return localDate.atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new DateParseException(ExceptionMessage.DATE_PARSE, date);
        }
    }

}
