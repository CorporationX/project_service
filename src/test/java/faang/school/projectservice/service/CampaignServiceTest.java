package faang.school.projectservice.service;

import faang.school.projectservice.dto.Currency;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilter;
import faang.school.projectservice.dto.campaign.CampaignGetDto;
import faang.school.projectservice.dto.campaign.CampaignUpdatedDto;
import faang.school.projectservice.mapper.CampaignMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.CampaignRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaPath;
import org.hibernate.query.criteria.JpaPredicate;
import org.hibernate.query.criteria.JpaRoot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @InjectMocks
    private CampaignService campaignService;
    @Mock
    private CampaignRepository campaignRepository;
    @Spy
    private CampaignMapperImpl campaignMapper;
    @Mock
    private ProjectService projectService;
    @Mock
    private SessionFactory sessionFactory;
    private Campaign campaign;
    private CampaignGetDto campaignGetDto;

    @BeforeEach
    void setUp() {
        campaign = Campaign.builder()
                .title("Z")
                .description("Z description")
                .goal(new BigDecimal(1000_000_000))
                .amountRaised(new BigDecimal(0))
                .status(CampaignStatus.ACTIVE)
                .project(Project.builder().id(1L).build())
                .currency(Currency.USD)
                .createdBy(1L)
                .build();

        campaignGetDto = CampaignGetDto.builder()
                .title("Z")
                .description("Z description")
                .goal(new BigDecimal(1000_000_000))
                .amountRaised(new BigDecimal(0))
                .status(CampaignStatus.ACTIVE)
                .projectId(1L)
                .currency(Currency.USD)
                .createdBy(1L)
                .updatedBy(1L)
                .build();
    }

    @Test
    void testCreateCampaign() {
        Mockito.when(projectService.checkManagerRole(1L, 1L)).thenReturn(true);
        Mockito.when(projectService.checkOwnerProject(1L, 1L)).thenReturn(true);

        CampaignDto campaignDto = CampaignDto.builder()
                .title("Z")
                .description("Z description")
                .goal(new BigDecimal(1000_000_000))
                .status(CampaignStatus.ACTIVE)
                .projectId(1L)
                .currency(Currency.USD)
                .build();

        campaignService.createCampaign(campaignDto, 1L);

        Mockito.verify(campaignRepository, Mockito.times(1)).save(campaign);
    }

    @Test
    void testUpdateCampaign() {
        Mockito.when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
        Mockito.when(projectService.checkManagerRole(1L, 1L)).thenReturn(true);
        Mockito.when(projectService.checkOwnerProject(1L, 1L)).thenReturn(true);
        campaignGetDto.setTitle("X");
        campaignGetDto.setDescription("X description");

        CampaignUpdatedDto updatedDto = CampaignUpdatedDto.builder().title("X").description("X description").build();

        CampaignGetDto actual = campaignService.updateCampaign(updatedDto, 1L, 1L);
        System.out.println(campaignGetDto);

        assertEquals(campaignGetDto, actual);
    }

    @Test
    void testGetCampaignsByFilter() {
        CampaignFilter campaignFilter = CampaignFilter.builder()
                .createdAt(LocalDateTime.of(2023, 9, 5, 16, 0))
                .status(CampaignStatus.ACTIVE)
                .createdBy(1L)
                .build();
        campaign.setUpdatedBy(1L);

        Session session = Mockito.mock(Session.class);
        HibernateCriteriaBuilder criteriaBuilder = Mockito.mock(HibernateCriteriaBuilder.class);
        JpaCriteriaQuery<Campaign> criteriaQuery = Mockito.mock(JpaCriteriaQuery.class);
        JpaRoot<Campaign> root = Mockito.mock(JpaRoot.class);
        Query query = Mockito.mock(Query.class);

        Mockito.when(sessionFactory.openSession()).thenReturn(session);
        Mockito.when(session.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        Mockito.when(criteriaBuilder.createQuery(Campaign.class)).thenReturn(criteriaQuery);
        Mockito.when(criteriaQuery.from(Campaign.class)).thenReturn(root);

        JpaPath path = Mockito.mock(JpaPath.class);
        JpaPredicate jpaPredicate = Mockito.mock(JpaPredicate.class);
        Mockito.when(root.get("createdAt")).thenReturn(path);
        Mockito.when(root.get("status")).thenReturn(path);
        Mockito.when(root.get("createdBy")).thenReturn(path);
        Mockito.when(criteriaBuilder.equal(path, campaignFilter.getCreatedAt())).thenReturn(jpaPredicate);
        Mockito.when(criteriaBuilder.equal(path, campaignFilter.getStatus())).thenReturn(jpaPredicate);
        Mockito.when(criteriaBuilder.equal(path, campaignFilter.getCreatedBy())).thenReturn(jpaPredicate);
        Mockito.when(criteriaQuery.select(root)).thenReturn(criteriaQuery);
        Mockito.when(criteriaQuery.select(root).where(jpaPredicate, jpaPredicate, jpaPredicate)).thenReturn(criteriaQuery);
        Mockito.when(session.createQuery(criteriaQuery)).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(List.of(campaign));

        List<CampaignGetDto> actual = campaignService.getCampaignsByFilter(campaignFilter);

        assertEquals(List.of(campaignGetDto), actual);
    }
}