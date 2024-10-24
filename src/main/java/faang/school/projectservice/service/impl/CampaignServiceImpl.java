package faang.school.projectservice.service.impl;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.dto.CampaignDto;
import faang.school.projectservice.model.dto.CampaignFilterDto;
import faang.school.projectservice.model.entity.Campaign;
import faang.school.projectservice.model.enums.CampaignStatus;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.service.CampaignService;
import faang.school.projectservice.specification.CampaignSpecificationBuilder;
import faang.school.projectservice.validator.CampaignValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CampaignServiceImpl implements CampaignService {
    private final UserContext userContext;
    private final CampaignValidator validator;
    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;

    @Override
    @Transactional
    public CampaignDto create(CampaignDto campaignDto) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        validator.validateUserIsCreator(userId, campaignDto.getCreatedBy());
        validator.validateProjectExists(campaignDto.getProjectId());
        validator.validateManagerOrOwner(userId, campaignDto.getProjectId());

        Campaign campaign = campaignMapper.toEntity(campaignDto);
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setUpdatedBy(userId);
        Campaign savedCampaign = campaignRepository.save(campaign);
        return campaignMapper.toDto(savedCampaign);
    }

    @Override
    @Transactional
    public CampaignDto update(long id, CampaignDto campaignDto) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        validator.validateIdFromPath(id, campaignDto.getId());
        validator.validateProjectExists(campaignDto.getProjectId());
        validator.validateManagerOrOwner(userId, campaignDto.getProjectId());

        Campaign currentCampaign = campaignRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Campaign with id = %d not found", id)));
        validator.validateCreatorIsTheSame(currentCampaign.getCreatedBy(), campaignDto.getCreatedBy());

        if (campaignMapper.toDto(currentCampaign).equals(campaignDto)) {
            return campaignDto;
        }

        Campaign campaign = campaignMapper.toEntity(campaignDto);
        campaign.setUpdatedBy(userId);
        Campaign savedCampaign = campaignRepository.save(campaign);
        return campaignMapper.toDto(savedCampaign);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        Campaign currentCampaign = campaignRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Campaign with id = %d not found", id)));

        validator.validateManagerOrOwner(userId, currentCampaign.getProject().getId());
        currentCampaign.setDeleted(true);
        currentCampaign.setUpdatedBy(userId);
        campaignRepository.save(currentCampaign);
    }

    @Override
    public CampaignDto getById(Long id) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        Campaign currentCampaign = campaignRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Campaign with id = %d not found", id)));
        return campaignMapper.toDto(currentCampaign);
    }

    @Override
    public List<CampaignDto> getByFilter(CampaignFilterDto filterDto) {
        Specification<Campaign> spec = CampaignSpecificationBuilder.buildSpecification(filterDto);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Campaign> campaigns = campaignRepository.findAll(spec, sort);
        return campaignMapper.toDtoList(campaigns);
    }
}
