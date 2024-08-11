package faang.school.projectservice.service;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFiltersDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.campaignFilter.CampaignFilter;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;
    private final List<CampaignFilter> campaignFilters;
    private final ProjectValidator projectValidator;

    @Transactional
    public CampaignDto createCampaign(CampaignDto campaignDto, long userId) {

        campaignDto.setCreatedBy(userId);
        campaignDto.setUpdatedBy(userId);

        projectValidator.checkUserPermission(campaignDto.getProjectId(), userId);

        Campaign savedCampaign = campaignRepository.save(campaignMapper.toEntity(campaignDto));
        return campaignMapper.toDto(savedCampaign);
    }

    @Transactional
    public CampaignDto updateCampaign(CampaignUpdateDto campaignUpdateDto, long userId, long campaignId) {

        Campaign campaign = getCampaignById(campaignId);

        projectValidator.checkUserPermission(campaign.getProject().getId(), userId);

        Campaign updatedCampaign = campaignMapper.updateCampaign(campaignUpdateDto, campaign);
        updatedCampaign.setUpdatedBy(userId);
        return campaignMapper.toDto(campaignRepository.save(updatedCampaign));
    }

    @Transactional
    public void softDeleteCampaign(long campaignId, long userId) {

        projectValidator.checkUserPermission(getCampaignById(campaignId).getProject().getId(), userId);
        Campaign campaign = getCampaignById(campaignId);
        campaign.setStatus(CampaignStatus.DELETED);

        campaignRepository.save(campaign);
    }

    @Transactional(readOnly = true)
    public CampaignDto getCampaignDtoById(long campaignId) {
        return campaignMapper.toDto(getCampaignById(campaignId));
    }

    @Transactional(readOnly = true)
    public List<CampaignDto> getAllCampaignsByFilter(CampaignFiltersDto campaignFiltersDto) {

        List<Campaign> campaigns = campaignRepository.findAll().stream().sorted(Comparator.comparing(Campaign::getCreatedAt).reversed()).toList();

        return campaignMapper.toDtoList(campaignFilters.stream()
                .filter(filter -> filter.isApplicable(campaignFiltersDto))
                .flatMap(filter -> filter.apply(campaigns, campaignFiltersDto))
                .toList());
    }

    private Campaign getCampaignById(long campaignId) {
        return campaignRepository.findById(campaignId).orElseThrow(() -> {
            log.warn("Campaign with id: {} not found", campaignId);
            return new DataValidationException("Campaign not found");
        });
    }
}
