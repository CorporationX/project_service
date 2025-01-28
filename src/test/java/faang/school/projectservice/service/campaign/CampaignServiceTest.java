package faang.school.projectservice.service.campaign;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.exeption.EntityCampaignNotFoundException;
import faang.school.projectservice.mapper.CampaignMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.campaignfilter.CampaignFilter;
import faang.school.projectservice.service.campaignfilter.CampaignStatusFilter;
import faang.school.projectservice.validator.CampaignValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private UserServiceClient userServiceClient;
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
    private CampaignService campaignService;
    @Captor
    private ArgumentCaptor<Campaign> captor;
    private CampaignDto campaignDto;
    private Campaign campaign;
    private CampaignUpdateDto updateDto;
    private UserDto userDto;

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
        userDto = new UserDto(1L, "name", "email");
    }

    @Test
    void publishCampaign_OK() {
        Project project = Project.builder().build();
        //  when(userServiceClient.getUser(u)userContext.getUserId()).id()).thenReturn(1L);

        when(userContext.getUserId()).thenReturn(1L);
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(projectService.findProjectById(anyLong())).thenReturn(project);

        campaignService.publishCampaign(campaignDto);

        verify(campaignRepository).save(captor.capture());
        assertEquals(1L, captor.getValue().getCreatedBy());
        assertEquals(1L, captor.getValue().getUpdatedBy());
    }

    @Test
    void testUpdateCampaign_OK() {
        when(userContext.getUserId()).thenReturn(1L);

        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(campaignRepository.findById(anyLong())).thenReturn(Optional.of(campaign));

        campaignService.updateCampaign(updateDto);

        verify(campaignRepository).save(captor.capture());
        assertEquals(1L, captor.getValue().getUpdatedBy());
        assertEquals(updateDto.getTitle(), captor.getValue().getTitle());
        assertEquals(updateDto.getDescription(), captor.getValue().getDescription());
    }

    @Test
    void testUpdateCampaign_NoCampaignException() {
        when(userContext.getUserId()).thenReturn(2L);
        when(userServiceClient.getUser(2L)).thenReturn(userDto);
        when(campaignRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityCampaignNotFoundException.class, () -> campaignService.updateCampaign(updateDto));
    }

    @Test
    void testDeleteCampaign_OK() {
        when(userContext.getUserId()).thenReturn(1L);
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
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
        assertEquals(1, allCampaigns.size());
    }
}