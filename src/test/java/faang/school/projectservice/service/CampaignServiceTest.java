package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceTest {
    @InjectMocks
    private CampaignService campaignService;
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserContext userContext;

    private Campaign testCampaign;
    private Project testProject;
    private TeamMember testTeamMember;
    private ArgumentCaptor<Campaign> captor;

    @BeforeEach
    public void setUp() {
        testTeamMember = new TeamMember();
        testTeamMember.setId(1L);
        testTeamMember.setUserId(1L);
        testTeamMember.setRoles(List.of(TeamRole.OWNER));

        testProject = new Project();
        testProject.setId(1L);
        testProject.setOwnerId(1L);
        testProject.setCampaigns(new ArrayList<>());

        testCampaign = new Campaign();
        testCampaign.setId(1L);
        testCampaign.setTitle("Test Campaign Title");
        testCampaign.setDescription("Test Campaign Description");
        testCampaign.setGoal(BigDecimal.valueOf(100000000000000L));
        testCampaign.setStatus(CampaignStatus.ACTIVE);
        testCampaign.setProject(testProject);

        captor = ArgumentCaptor.forClass(Campaign.class);
    }

    @Nested
    public class Positive {
        @Test
        public void testFindCampaignById() {
            when(campaignRepository.findById(testCampaign.getId()))
                    .thenReturn(Optional.of(testCampaign));
            Campaign campaign = campaignService.findCampaignById(testCampaign.getId());
            assertNotNull(campaign);
            verify(campaignRepository, times(1))
                    .findById(testCampaign.getId());
        }

        @Test
        public void testCreateNewCampaign() {
            when(userContext.getUserId()).thenReturn(1L);
            when(projectRepository.getByIdOrThrow(testProject.getId()))
                    .thenReturn(testProject);
            when(teamMemberJpaRepository.findByUserIdAndProjectId(testTeamMember.getUserId(), testProject.getId()))
                    .thenReturn(testTeamMember);
            when(campaignRepository.save(testCampaign)).thenReturn(testCampaign);

            Campaign created = campaignService.createNewCampaign(testCampaign);
            assertNotNull(created);
            assertThat(testCampaign).usingRecursiveComparison().isEqualTo(created);
        }

        @Test
        public void testUpdateCampaignInfo() {
            String newTitle = "New Title";
            String newDescription = "New Description";
            when(campaignRepository.findById(testCampaign.getId()))
                    .thenReturn(Optional.of(testCampaign));
            when(userContext.getUserId()).thenReturn(1L);
            when(campaignRepository.save(testCampaign)).thenReturn(testCampaign);

            Campaign updated = campaignService.updateCampaignInfo(testCampaign.getId(), newTitle, newDescription);
            assertNotNull(updated);
            assertEquals(newTitle, updated.getTitle());
            assertEquals(newDescription, updated.getDescription());
            assertEquals(1L, updated.getUpdatedBy());
            verify(campaignRepository, times(1)).save(captor.capture());
            assertThat(updated).usingRecursiveComparison().isEqualTo(captor.getValue());
        }

        @Test
        public void testSoftDeleteById() {
            testCampaign.setStatus(CampaignStatus.CANCELED);
            when(campaignRepository.findById(testCampaign.getId()))
                    .thenReturn(Optional.of(testCampaign));
            when(campaignRepository.save(testCampaign))
                    .thenReturn(testCampaign);
            Campaign marked = campaignService.softDeleteById(testCampaign.getId());
            verify(campaignRepository, times(1))
                    .save(marked);
            assertTrue(marked.getDeleted());
        }
    }

    @Nested
    public class Negative {
        @Test
        @DisplayName("Find by id: invalid campaign id")
        public void testFindCampaignById_InvalidId() {
            when(campaignRepository.findById(1L))
                    .thenThrow(EntityNotFoundException.class);
            assertThrows(EntityNotFoundException.class,
                    () -> campaignService.findCampaignById(testCampaign.getId()));
        }

        @Test
        @DisplayName("Find by project id: invalid project id")
        public void testFindFilteredCampaigns() {
            assertThrows(EntityNotFoundException.class,
                    () -> campaignService.findFilteredCampaigns(testProject.getId(), null, null));
        }

        @Test
        @DisplayName("Create campaign: user id in context missing")
        public void testCreateNewCampaign_UserContext() {
            when(userContext.getUserId()).thenThrow(NullPointerException.class);
            assertThrows(NullPointerException.class,
                    () -> campaignService.createNewCampaign(testCampaign));
        }

        @Test
        @DisplayName("Create campaign: invalid project id")
        public void testCreateNewCampaign_InvalidProjectId() {
            when(userContext.getUserId()).thenReturn(1L);
            when(projectRepository.getByIdOrThrow(testProject.getId()))
                    .thenThrow(EntityNotFoundException.class);
            assertThrows(EntityNotFoundException.class,
                    () -> campaignService.createNewCampaign(testCampaign));
        }

        @Test
        @DisplayName("Create campaign: team member missing")
        public void testCreateNewCampaign_NoTeamMember() {
            when(userContext.getUserId()).thenReturn(1L);
            when(projectRepository.getByIdOrThrow(testProject.getId()))
                    .thenReturn(testProject);
            when(teamMemberJpaRepository.findByUserIdAndProjectId(1L, testProject.getId()))
                    .thenReturn(null);
            assertThrows(IllegalStateException.class,
                    () -> campaignService.createNewCampaign(testCampaign));
        }

        @Test
        @DisplayName("Create campaign: team member not a manager or owner")
        public void testCreateNewCampaign_NoValidRole() {
            testTeamMember.setRoles(List.of(TeamRole.ANALYST));
            when(userContext.getUserId()).thenReturn(2L);
            when(projectRepository.getByIdOrThrow(testProject.getId()))
                    .thenReturn(testProject);
            when(teamMemberJpaRepository.findByUserIdAndProjectId(2L, testProject.getId()))
                    .thenReturn(testTeamMember);
            assertThrows(IllegalStateException.class,
                    () -> campaignService.createNewCampaign(testCampaign));
        }

        @Test
        @DisplayName("Update campaign: empty/null title and description")
        public void testUpdateCampaignInfo_InvalidData() {
            assertThrows(IllegalArgumentException.class,
                    () -> campaignService.updateCampaignInfo(testCampaign.getId(), null, null));
            assertThrows(IllegalArgumentException.class,
                    () -> campaignService.updateCampaignInfo(testCampaign.getId(), "", ""));
        }

        @Test
        @DisplayName("Update campaign: invalid campaign id")
        public void testUpdateCampaignInfo_InvalidId() {
            when(campaignRepository.findById(testCampaign.getId()))
                    .thenThrow(EntityNotFoundException.class);
            assertThrows(EntityNotFoundException.class,
                    () -> campaignService.updateCampaignInfo(
                            testCampaign.getId(),
                            "New Title",
                            "New Description")
            );
        }

        @Test
        @DisplayName("Soft delete by id: invalid ID")
        public void testSoftDeleteById_InvalidId() {
            when(campaignRepository.findById(testCampaign.getId()))
                    .thenThrow(EntityNotFoundException.class);
            assertThrows(EntityNotFoundException.class,
                    () -> campaignService.softDeleteById(testCampaign.getId()));
        }

        @Test
        @DisplayName("Soft delete by id: active campaign status")
        public void testSoftDeleteById_StatusActive() {
            when(campaignRepository.findById(testCampaign.getId()))
                    .thenReturn(Optional.of(testCampaign));
            assertThrows(IllegalStateException.class,
                    () -> campaignService.softDeleteById(testCampaign.getId()));
        }
    }
}
