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
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.service.project.ProjectService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    @Spy
    private CampaignMapperImpl campaignMapper;

    @Captor
    private ArgumentCaptor<Campaign> captor;
    private CampaignFilterDto campaignFilterDto;
    private CampaignDto campaignDto;
    private Campaign campaign;
    private CampaignUpdateDto updateDto;
    private UserDto userDto;

    @InjectMocks
    private CampaignService campaignService;
    private List<TeamMember> testTeams;
    private Project project;

    @BeforeEach
    void setup() {
        Project project = new Project();
        project.setId(1L);

        campaignDto = CampaignDto.builder()
                .amountRaised(BigDecimal.ZERO)
                .currency(Currency.USD)
                .description("desc")
                .projectId(1L)
                .status(CampaignStatus.ACTIVE)
                .build();

        campaignFilterDto = CampaignFilterDto.builder()
                .createdAt(LocalDateTime.now())
                .createdBy(2L)
                .status(CampaignStatus.ACTIVE)
                .build();

        campaign = Campaign.builder()
                .id(1L)
                .amountRaised(BigDecimal.ZERO)
                .title("Test Campaign")
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

        Team java = Team.builder()
                .id(1L)
                .teamMembers(testTeams)
                .build();
        Project project = Project.builder().build();
        project.setId(1L);
        project.setTeams(List.of(java));

        TeamMember teamMember = TeamMember.builder()
                .id(1L)
                .userId(1L)
                .roles(List.of(TeamRole.MANAGER))
                .build();
        java.setTeamMembers(List.of(teamMember));

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
    void TestGetCampaignsByProjectIdAndFilter_ShouldReturnCampaignList() {

        campaignFilterDto = CampaignFilterDto.builder()
                .createdAt(LocalDateTime.now())
                .createdBy(2L)
                .status(CampaignStatus.ACTIVE)
                .build();
        List<Campaign> campaigns = List.of(campaign);

        when(campaignRepository.findAllByFiltersAndProjectId(1l, campaignFilterDto.getCreatedBy(),
                campaignFilterDto.getCreatedAt(), campaignFilterDto.getStatus()))
                .thenReturn(campaigns);

        List<Campaign> result = campaignService.getCampaignsByProjectIdAndFilter(1L, campaignFilterDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Campaign", result.get(0).getTitle());
    }
}