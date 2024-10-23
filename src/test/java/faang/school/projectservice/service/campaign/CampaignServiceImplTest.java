package faang.school.projectservice.service.campaign;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.dto.campaign.CampaignDto;
import faang.school.projectservice.model.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.model.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.model.dto.client.Currency;
import faang.school.projectservice.model.entity.Campaign;
import faang.school.projectservice.model.entity.CampaignStatus;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.filter.campaign.CampaignFilter;
import faang.school.projectservice.model.filter.campaign.CampaignStatusFilter;
import faang.school.projectservice.model.mapper.campaign.CampaignMapperImpl;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.impl.campaign.CampaignServiceImpl;
import faang.school.projectservice.validator.campaign.CampaignValidator;
import faang.school.projectservice.validator.campaign.CampaignValidatorTest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceImplTest {

    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private UserContext userContext;
    @Mock
    private CampaignValidator campaignValidator;
    @Spy
    private CampaignMapperImpl campaignMapper;
    @Mock
    private List<CampaignFilter> filters;
    private CampaignStatusFilter statusFilter;
    @InjectMocks
    private CampaignServiceImpl campaignService;
    @Captor
    private ArgumentCaptor<Campaign> captor;
    private CampaignDto campaignDto;
    private Campaign campaign;
    private CampaignUpdateDto updateDto;

    @BeforeEach
    void setup() {
        statusFilter = new CampaignStatusFilter();
        campaignDto = CampaignDto.builder()
                .amountRaised(BigDecimal.ZERO)
                .currency(Currency.USD)
                .description("desc")
                .projectId(1L)
                .status(CampaignStatus.ACTIVE)
                .build();
        campaign = Campaign.builder()
                .id(1L)
                .amountRaised(BigDecimal.ZERO)
                .title("title")
                .createdBy(1L)
                .description("descr")
                .currency(Currency.USD)
                .status(CampaignStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        updateDto = CampaignUpdateDto.builder()
                .id(1L)
                .title("title2")
                .description("descr2")
                .build();

        campaignService = new CampaignServiceImpl(campaignRepository, projectService, userContext, campaignValidator, campaignMapper,
                List.of(statusFilter));
    }

    @Test
    void publishCampaign_OK() {
        Project project = Project.builder().build();
        when(userContext.getUserId()).thenReturn(1L);
        when(projectService.getProject(anyLong())).thenReturn(project);

        campaignService.publishCampaign(campaignDto);

        verify(campaignRepository).save(captor.capture());
        assertEquals(1L, captor.getValue().getCreatedBy());
        assertEquals(1L, captor.getValue().getUpdatedBy());
    }

    @Test
    void testUpdateCampaign_OK() {
        when(userContext.getUserId()).thenReturn(2L);
        when(campaignRepository.findById(anyLong())).thenReturn(Optional.of(campaign));

        campaignService.updateCampaign(updateDto);

        verify(campaignRepository).save(captor.capture());
        assertEquals(2L, captor.getValue().getUpdatedBy());
        assertEquals(updateDto.title(), captor.getValue().getTitle());
        assertEquals(updateDto.description(), captor.getValue().getDescription());
    }

    @Test
    void testUpdateCampaign_NoCampaignException() {
        when(userContext.getUserId()).thenReturn(2L);
        when(campaignRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> campaignService.updateCampaign(updateDto));
    }

    @Test
    void testDeleteCampaign_OK() {
        when(userContext.getUserId()).thenReturn(2L);
        when(campaignRepository.findById(anyLong())).thenReturn(Optional.of(campaign));

        campaignService.deleteCampaign(1L);

        verify(campaignRepository).save(captor.capture());
        assertTrue(captor.getValue().getRemoved());
    }

    @Test
    void testGetCampaign_OK() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        campaignService.getCampaign(1L);

        verify(campaignRepository).findById(anyLong());
    }

    @Test
    void testGetAllCampaignsByFilter_OK() {
        CampaignFilterDto filterDto = CampaignFilterDto.builder()
                .status(CampaignStatus.ACTIVE)
                .build();
        when(campaignRepository.findAll()).thenReturn(List.of(campaign));

        List<CampaignDto> allCampaigns = campaignService.getAllCampaignsByFilter(filterDto);

        verify(campaignRepository).findAll();
        assertEquals(1, allCampaigns.size());
    }

    @Test
    void testGetAllCampaignsByFilter_OKButNoCampaigns() {
        CampaignFilterDto filterDto = CampaignFilterDto.builder()
                .status(CampaignStatus.COMPLETED)
                .build();
        when(campaignRepository.findAll()).thenReturn(List.of(campaign));

        List<CampaignDto> allCampaigns = campaignService.getAllCampaignsByFilter(filterDto);

        verify(campaignRepository).findAll();
        assertEquals(0, allCampaigns.size());
    }
}
