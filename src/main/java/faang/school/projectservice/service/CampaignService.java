package faang.school.projectservice.service;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilter;
import faang.school.projectservice.dto.campaign.CampaignGetDto;
import faang.school.projectservice.dto.campaign.CampaignUpdatedDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.repository.CampaignRepository;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;
    private final ProjectService projectService;
    private final SessionFactory sessionFactory;

    @Transactional
    public CampaignGetDto createCampaign(CampaignDto campaignDto, long userId) {
        validateCampaign(campaignDto.getProjectId(), userId);

        Campaign campaign = campaignMapper.toEntity(campaignDto);
        campaign.setAmountRaised(new BigDecimal(0));
        campaign.setCreatedBy(userId);
        Campaign campaignSaved = campaignRepository.save(campaign);
        return campaignMapper.toDto(campaignSaved);
    }

    @Transactional
    public CampaignGetDto updateCampaign(CampaignUpdatedDto campaignDto, long userId, long campaignId) {
        Campaign campaign = getById(campaignId);
        validateCampaign(campaign.getProject().getId(), userId);

        campaignMapper.updateCampaign(campaignDto, campaign);
        campaign.setUpdatedBy(userId);
        campaign.setUpdatedAt(null);
        return campaignMapper.toDto(campaign);
    }

    @Transactional
    public void deleteSoftCampaign(long id) {
        Campaign campaign = getById(id);
        campaign.setRemoved(true);
    }

    @Transactional(readOnly = true)
    public CampaignGetDto getCampaignById(long campaignId) {
        Campaign campaign = getById(campaignId);
        return campaignMapper.toDto(campaign);
    }

    @Transactional(readOnly = true)
    public List<CampaignGetDto> getCampaignsByFilter(CampaignFilter campaignFilter) {
        try (Session session = sessionFactory.openSession()) {
            HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Campaign> criteriaQuery = criteriaBuilder.createQuery(Campaign.class);
            Root<Campaign> root = criteriaQuery.from(Campaign.class);

            List<Predicate> list = createPredicates(campaignFilter, criteriaBuilder, root);
            criteriaQuery.select(root).where(list.toArray(new Predicate[0]));

            criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createdAt")));

            Query<Campaign> query = session.createQuery(criteriaQuery);
            List<Campaign> resultList = query.getResultList();
            return campaignMapper.toDtoList(resultList);
        }
    }

    private List<Predicate> createPredicates(CampaignFilter campaignFilter, HibernateCriteriaBuilder cb, Root<Campaign> root) {
        List<Predicate> list = new ArrayList<>();
        if (campaignFilter.getCreatedAt() != null) {
            list.add(cb.equal(root.get("createdAt"), campaignFilter.getCreatedAt()));
        }
        if (campaignFilter.getStatus() != null) {
            list.add(cb.equal(root.get("status"), campaignFilter.getStatus()));
        }
        if (campaignFilter.getCreatedBy() != null) {
            list.add(cb.equal(root.get("createdBy"), campaignFilter.getCreatedBy()));
        }
        return list;
    }

    private Campaign getById(long campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("Campaign not found"));
    }

    private void validateCampaign(long projectId, long userId) {
        boolean isMemberManager = projectService.checkManagerRole(projectId, userId);
        boolean isOwnerProject = projectService.checkOwnerProject(projectId, userId);

        if (!isMemberManager || !isOwnerProject) {
            throw new DataValidationException("You can't create campaign");
        }
    }
}
