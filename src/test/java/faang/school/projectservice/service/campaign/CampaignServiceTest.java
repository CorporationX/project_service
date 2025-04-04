package faang.school.projectservice.service.campaign;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.client.Campaign.CampaignDto;
import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.exception.DuplicateTitleException;
import faang.school.projectservice.mapper.campaign.CampaignMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @InjectMocks
    CampaignService campaignService;

    @Mock
    private CampaignMapper campaignMapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CampaignRepository campaignRepository;

    @Captor
    ArgumentCaptor<Campaign> campaignCaptor;

    private final long projectId = 1;
    private final long campaignID = 1;
    private final long userID = 1;
    private final CampaignDto campaignDto = new CampaignDto();
    private final Campaign campaign = new Campaign();
    private final Team team = new Team();
    private final TeamMember teamMember = new TeamMember();

    Project project = new Project();

    @BeforeEach
    void setUp() {
        teamMember.setRoles(List.of(TeamRole.MANAGER));
        teamMember.setUserId(1L);
        teamMember.setId(1L);

        team.setTeamMembers(List.of(teamMember));

        campaign.setId(1L);
        campaign.setTitle("title");
        campaign.setDescription("description");
        campaign.setGoal(BigDecimal.valueOf(5000));
        campaign.setAmountRaised(BigDecimal.valueOf(0));
        campaign.setCurrency(Currency.USD);
        campaign.setStatus(CampaignStatus.ACTIVE);
        campaign.setCreatedAt(LocalDateTime.now().minusDays(1));

        project.setId(1L);
        project.setOwnerId(1L);
        project.setTeams(List.of(team));

        campaign.setProject(project);

        campaignDto.setTitle("title");
        campaignDto.setDescription("description");
        campaignDto.setGoal(BigDecimal.valueOf(5000));
        campaignDto.setAmountRaised(BigDecimal.valueOf(0));
        campaignDto.setCurrency(Currency.USD);
        campaignDto.setStatus(CampaignStatus.ACTIVE);
        campaignDto.setCreatedAt(LocalDateTime.now().minusDays(1));
        campaignDto.setUpdatedAt(LocalDateTime.now());
    }

    private void shouldAbortCampaignCreation() {
        verify(campaignMapper, never()).toEntity(campaignDto, project, CampaignStatus.ACTIVE);
        verify(campaignRepository, never()).save(campaign);
        verify(campaignMapper, never()).toDto(campaign);
    }

    private void shouldAbortCampaignUpdating() {
        verify(campaignMapper, never()).updateEntity(new Campaign(), campaignDto, project, userID, CampaignStatus.ACTIVE);
        verify(campaignRepository, never()).save(campaign);
        verify(campaignMapper, never()).toDto(campaign);
    }

    @Nested
    class Create {

        @Test
        public void duplicateTitle() {
            when(campaignRepository.existsByTitleAndProjectId(campaign.getTitle(), projectId)).thenReturn(true);

            assertThrows(DuplicateTitleException.class, () -> campaignService.create(campaignDto, projectId, userID));

            shouldAbortCampaignCreation();
        }

        @Test
        public void projectNotFound() {
            when(campaignRepository.existsByTitleAndProjectId(campaign.getTitle(), projectId)).thenReturn(false);
            when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> campaignService.create(campaignDto, projectId, userID));

            shouldAbortCampaignCreation();
        }

        @Test
        public void projectFoundAndOwner() {
            when(campaignRepository.existsByTitleAndProjectId(campaignDto.getTitle(), projectId)).thenReturn(false);
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
            when(campaignMapper.toEntity(campaignDto, project, CampaignStatus.ACTIVE)).thenReturn(campaign);
            when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);
            when(campaignMapper.toDto(campaign)).thenReturn(campaignDto);

            CampaignDto result = campaignService.create(campaignDto, projectId, userID);

            verify(campaignMapper, times(1)).toEntity(campaignDto, project, CampaignStatus.ACTIVE);
            verify(campaignRepository, times(1)).save(campaign);
            verify(campaignMapper, times(1)).toDto(campaign);

            assertEquals(campaignDto, result);
        }

        @Test
        public void notOwnerUserRoleValid() {
            project.setOwnerId(2L);

            when(campaignRepository.existsByTitleAndProjectId(campaignDto.getTitle(), projectId)).thenReturn(false);
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
            when(campaignMapper.toEntity(campaignDto, project, CampaignStatus.ACTIVE)).thenReturn(campaign);
            when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);
            when(campaignMapper.toDto(campaign)).thenReturn(campaignDto);

            CampaignDto result = campaignService.create(campaignDto, projectId, userID);

            verify(campaignMapper, times(1)).toEntity(campaignDto, project, CampaignStatus.ACTIVE);
            verify(campaignRepository, times(1)).save(campaign);
            verify(campaignMapper, times(1)).toDto(campaign);

            assertEquals(campaignDto, result);
        }

        @Test
        public void notOwnerUserRoleInvalid() {
            project.setOwnerId(2L);
            teamMember.setRoles(List.of(TeamRole.TESTER));


            when(campaignRepository.existsByTitleAndProjectId(campaignDto.getTitle(), projectId)).thenReturn(false);
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

            assertThrows(NotFoundException.class, () -> campaignService.create(campaignDto, projectId, userID));

            shouldAbortCampaignCreation();
        }

        @Test
        public void notOwnerUserNotFound() {
            project.setOwnerId(2L);
            teamMember.setUserId(2L);

            when(campaignRepository.existsByTitleAndProjectId(campaignDto.getTitle(), projectId)).thenReturn(false);
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

            assertThrows(NotFoundException.class, () -> campaignService.create(campaignDto, projectId, userID));

            shouldAbortCampaignCreation();
        }

    }

    @Nested
    class Update {

        @Test
        public void campaignNotFound() {
            when(campaignRepository.findById(campaignID)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> campaignService.update(campaignDto, projectId, campaignID, userID));

            shouldAbortCampaignUpdating();
        }

        @Test
        public void campaignFoundProjectNotFound() {
            when(campaignRepository.findById(campaignID)).thenReturn(Optional.of(campaign));
            when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> campaignService.update(campaignDto, projectId, campaignID, userID));

            verify(campaignMapper, never()).updateEntity(new Campaign(), campaignDto, project, userID, CampaignStatus.ACTIVE);
            verify(campaignRepository, never()).save(campaign);
            verify(campaignMapper, never()).toDto(campaign);
        }

        @Test
        public void campaignFoundProjectFoundAndOwner() {
            when(campaignRepository.findById(campaignID)).thenReturn(Optional.of(campaign));
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
            doNothing().when(campaignMapper)
                    .updateEntity(any(Campaign.class), eq(campaignDto), eq(project), eq(userID), eq(CampaignStatus.ACTIVE));
            when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);
            when(campaignMapper.toDto(campaign)).thenReturn(campaignDto);

            CampaignDto result = campaignService.update(campaignDto, projectId, campaignID, userID);

            ArgumentCaptor<Campaign> campaignCaptor = ArgumentCaptor.forClass(Campaign.class);

            verify(campaignMapper, times(1)).updateEntity(campaignCaptor.capture(), eq(campaignDto), eq(project), eq(userID), eq(CampaignStatus.ACTIVE));
            verify(campaignRepository, times(1)).save(campaign);
            verify(campaignMapper, times(1)).toDto(campaign);

            Campaign capturedCampaign = campaignCaptor.getValue();
            assertNotNull(capturedCampaign);
            assertEquals(campaign.getId(), capturedCampaign.getId());

            assertEquals(campaignDto, result);
        }

        @Test
        public void notOwnerUserRoleValid() {
            project.setOwnerId(2L);
            when(campaignRepository.findById(campaignID)).thenReturn(Optional.of(campaign));
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
            doNothing().when(campaignMapper)
                    .updateEntity(any(Campaign.class), eq(campaignDto), eq(project), eq(userID), eq(CampaignStatus.ACTIVE));
            when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);
            when(campaignMapper.toDto(campaign)).thenReturn(campaignDto);

            CampaignDto result = campaignService.update(campaignDto, projectId, campaignID, userID);

            ArgumentCaptor<Campaign> campaignCaptor = ArgumentCaptor.forClass(Campaign.class);

            verify(campaignMapper, times(1)).updateEntity(campaignCaptor.capture(), eq(campaignDto), eq(project), eq(userID), eq(CampaignStatus.ACTIVE));
            verify(campaignRepository, times(1)).save(campaign);
            verify(campaignMapper, times(1)).toDto(campaign);

            Campaign capturedCampaign = campaignCaptor.getValue();
            assertNotNull(capturedCampaign);
            assertEquals(campaign.getId(), capturedCampaign.getId());

            assertEquals(campaignDto, result);
        }

        @Test
        public void notOwnerUserRoleInvalid() {
            project.setOwnerId(2L);
            teamMember.setRoles(List.of(TeamRole.TESTER));

            when(campaignRepository.findById(campaignID)).thenReturn(Optional.of(campaign));
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));


            assertThrows(NotFoundException.class, () -> campaignService.update(campaignDto, projectId, campaignID, userID));

            shouldAbortCampaignUpdating();
        }

        @Test
        public void notOwnerUserNotFound() {
            project.setOwnerId(2L);
            teamMember.setUserId(2L);

            when(campaignRepository.findById(campaignID)).thenReturn(Optional.of(campaign));
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));


            assertThrows(NotFoundException.class, () -> campaignService.update(campaignDto, projectId, campaignID, userID));

            shouldAbortCampaignUpdating();
        }
    }

    @Nested
    class SoftDelete {

        @Test
        public void campaignNotFound() {
            when(campaignRepository.findById(campaignID)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> campaignService.softDelete(projectId, campaignID, userID));

            verify(campaignRepository, never()).save(any());
        }

        @Test
        public void campaignFoundProjectNotFound() {
            when(campaignRepository.findById(campaignID)).thenReturn(Optional.of(campaign));
            when(projectRepository.findById(campaignID)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> campaignService.softDelete(projectId, campaignID, userID));

            verify(campaignRepository, never()).save(any());
        }

        @Test
        public void campaignFoundProjectFoundAndOwner() {
            when(campaignRepository.findById(campaignID)).thenReturn(Optional.of(campaign));
            when(projectRepository.findById(campaignID)).thenReturn(Optional.of(project));
            when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);

            campaignService.softDelete(projectId, campaignID, userID);
            ArgumentCaptor<Campaign> campaignCaptor = ArgumentCaptor.forClass(Campaign.class);

            verify(campaignRepository, times(1)).save(campaignCaptor.capture());

            Campaign capturedCampaign = campaignCaptor.getValue();
            assertEquals(CampaignStatus.DELETED, capturedCampaign.getStatus());
        }

        @Test
        public void notOwnerUserRoleValid() {
            project.setOwnerId(2L);

            when(campaignRepository.findById(campaignID)).thenReturn(Optional.of(campaign));
            when(projectRepository.findById(campaignID)).thenReturn(Optional.of(project));
            when(campaignRepository.save(any(Campaign.class))).thenReturn(campaign);

            campaignService.softDelete(projectId, campaignID, userID);
            ArgumentCaptor<Campaign> campaignCaptor = ArgumentCaptor.forClass(Campaign.class);

            verify(campaignRepository, times(1)).save(campaignCaptor.capture());

            Campaign capturedCampaign = campaignCaptor.getValue();
            assertEquals(CampaignStatus.DELETED, capturedCampaign.getStatus());
        }

        @Test
        public void notOwnerUserRoleInvalid() {
            project.setOwnerId(2L);
            teamMember.setRoles(List.of(TeamRole.TESTER));

            when(campaignRepository.findById(campaignID)).thenReturn(Optional.of(campaign));
            when(projectRepository.findById(campaignID)).thenReturn(Optional.of(project));

            assertThrows(NotFoundException.class, () -> campaignService.softDelete(projectId, campaignID, userID));

            verify(campaignRepository, never()).save(campaign);

        }

        @Test
        public void notOwnerUserNotFound() {
            project.setOwnerId(2L);
            teamMember.setUserId(2L);

            when(campaignRepository.findById(campaignID)).thenReturn(Optional.of(campaign));
            when(projectRepository.findById(campaignID)).thenReturn(Optional.of(project));

            assertThrows(NotFoundException.class, () -> campaignService.softDelete(projectId, campaignID, userID));

            verify(campaignRepository, never()).save(campaign);

        }
    }

    @Nested
    class GetCampaign {

        @Test
        public void campaignNotFound() {
            when(campaignRepository.findById(campaignID)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> campaignService.getCampaign(projectId, campaignID));

            verify(campaignMapper, never()).toDto(campaign);
        }

        @Test
        public void campaignFoundProjectNotFound() {
            when(campaignRepository.findById(campaignID)).thenReturn(Optional.of(campaign));
            when(projectRepository.findById(campaignID)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> campaignService.getCampaign(projectId, campaignID));

            verify(campaignMapper, never()).toDto(campaign);
        }

        @Test
        public void projectFound() {
            when(campaignRepository.findById(campaignID)).thenReturn(Optional.of(campaign));
            when(projectRepository.findById(campaignID)).thenReturn(Optional.of(project));

            campaignService.getCampaign(projectId, campaignID);

            verify(campaignMapper, times(1)).toDto(campaign);
        }
    }

    @Nested
    class getCampaignByFilter {

        @Test
        public void projectNotFound() {
            when(projectRepository.findById(campaignID)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> campaignService.getCampaignByFilter(projectId, campaignDto));
        }

        @Test
        public void projectFound() {
            when(campaignRepository.filterCampaigns(
                    campaignDto.getCreatedAt(),
                    campaignDto.getCreatedBy(),
                    campaignDto.getStatus()))
                    .thenReturn(List.of(campaign));
            when(projectRepository.findById(campaignID)).thenReturn(Optional.of(project));

            List<CampaignDto> results = campaignService.getCampaignByFilter(projectId, campaignDto);
            assertNotNull(results);
            assertEquals(1,results.size());
        }

        @Test
        public void projectFoundAndIncorrectFilter() {
            campaignDto.setCreatedBy(2L);
            campaignDto.setStatus(CampaignStatus.DELETED);
            campaignDto.setCreatedAt(LocalDateTime.now());

            when(projectRepository.findById(campaignID)).thenReturn(Optional.of(project));

            List<CampaignDto> results = campaignService.getCampaignByFilter(projectId, campaignDto);
            assertNotNull(results);
            assertEquals(0,results.size());
        }
    }
}