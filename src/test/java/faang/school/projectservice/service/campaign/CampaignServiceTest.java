package faang.school.projectservice.service.campaign;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.campaign.CampaignCreateDto;
import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilter;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.mapper.campaign.CampaignMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {
    public static final long CAMPAIGN_ID = 1L;

    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private TeamMemberService teamMemberService;
    @Mock
    private ProjectService projectService;
    @Mock
    private UserContext userContext;
    @Spy
    private CampaignMapperImpl campaignMapper;
    @InjectMocks
    private CampaignService campaignService;

    @Test
    void testGetCampaignByInvalidId() {
        Mockito.when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> campaignService.getCampaignById(CAMPAIGN_ID));
    }

    @Test
    void testGetById() {
        Mockito.when(campaignRepository.findById(CAMPAIGN_ID)).thenReturn(Optional.of(provideCampaign()));

        CampaignDto campaignDto = campaignService.getById(CAMPAIGN_ID);

        assertAll(
                () -> assertEquals("Title", campaignDto.getTitle()),
                () -> assertEquals("Description", campaignDto.getDescription())
        );
    }

    @Test
    void testGetByIdNotFound() {
        Mockito.when(campaignRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> campaignService.getById(CAMPAIGN_ID));
    }

    @Test
    void testCreateCampaign() {
        Mockito.when(teamMemberService.validateUserIsProjectMember(anyLong(), anyLong())).thenReturn(provideMember());
        Mockito.when(campaignRepository.save(any())).thenReturn(provideCampaign());

        CampaignCreateDto createDto = CampaignCreateDto.builder()
                .title("Title")
                .description("desc")
                .creatorId(1L)
                .projectId(1L)
                .build();

        CampaignDto campaignDto = campaignService.createCampaign(createDto);

        assertNotNull(campaignDto);
    }

    @Test
    void testCreateCampaignNoAccess() {
        Mockito.when(projectService.getById(anyLong())).thenReturn(provideProject());
        Mockito.when(teamMemberService.validateUserIsProjectMember(anyLong(), anyLong())).thenReturn(provideMemberWithNoAccess());

        CampaignCreateDto createDto = CampaignCreateDto.builder()
                .creatorId(1L)
                .projectId(1L)
                .build();
        Assertions.assertThrows(AccessDeniedException.class, () -> campaignService.createCampaign(createDto));
    }

    @Test
    void testUpdateCampaign() {
        String newTitle = "new title";
        String newDescription = "new desc";

        Campaign campaign = provideCampaign();
        Mockito.when(campaignRepository.findById(anyLong())).thenReturn(Optional.of(campaign));
        Mockito.when(teamMemberService.validateUserIsProjectMember(anyLong(), anyLong())).thenReturn(provideMember());

        Campaign campaignAfterUpdate = campaign.toBuilder()
                .title(newTitle)
                .description(newDescription)
                .build();
        Mockito.when(campaignRepository.save(any())).thenReturn(campaignAfterUpdate);

        CampaignUpdateDto updateDto = CampaignUpdateDto.builder()
                .title(newTitle)
                .description(newDescription)
                .build();

        assertNotEquals(newTitle, campaign.getTitle());
        assertNotEquals(newDescription, campaign.getDescription());

        CampaignDto campaignDto = campaignService.updateCampaign(1L, updateDto);

        assertAll(
                () -> assertEquals(newTitle, campaignDto.getTitle()),
                () -> assertEquals(newDescription, campaignDto.getDescription())
        );
    }

    @Test
    void testUpdateCampaignNoAccess() {
        Mockito.when(campaignRepository.findById(anyLong())).thenReturn(Optional.of(provideCampaign()));
        Mockito.when(teamMemberService.validateUserIsProjectMember(anyLong(), anyLong())).thenReturn(provideMemberWithNoAccess());

        CampaignUpdateDto updateDto = CampaignUpdateDto.builder()
                .title("New title")
                .build();
        Assertions.assertThrows(AccessDeniedException.class, () -> campaignService.updateCampaign(1L, updateDto));
    }

    @Test
    void testCancelCampaign() {
        Campaign campaign = provideCampaign();
        Mockito.when(campaignRepository.findById(anyLong())).thenReturn(Optional.of(campaign));
        Mockito.when(teamMemberService.validateUserIsProjectMember(anyLong(), anyLong())).thenReturn(provideMember());
        Campaign campaignAfterUpdate = campaign.toBuilder()
                .status(CampaignStatus.CANCELED)
                .build();
        Mockito.when(campaignRepository.save(any())).thenReturn(campaignAfterUpdate);

        CampaignDto campaignDto = campaignService.cancelCampaign(1L);

        assertEquals(CampaignStatus.CANCELED, campaignDto.getStatus());
    }

    @Test
    void testCancelCampaignNoAccess() {
        Mockito.when(campaignRepository.findById(anyLong())).thenReturn(Optional.of(provideCampaign()));
        Mockito.when(teamMemberService.validateUserIsProjectMember(anyLong(), anyLong())).thenReturn(provideMemberWithNoAccess());

        Assertions.assertThrows(AccessDeniedException.class, () -> campaignService.cancelCampaign(1L));
    }

    @Test
    void testGetCampaignsByProject() {
        List<Campaign> campaigns = List.of(provideCampaign(1L), provideCampaign(2L), provideCampaign(3L));
        Mockito.when(campaignRepository.findAllByProjectAndFilters(anyLong(), any(), any(), any(), any())).thenReturn(campaigns);

        CampaignFilter filter = CampaignFilter.builder().build();

        List<CampaignDto> result = campaignService.getCampaignsByProject(1L, filter);

        assertAll(
                () -> assertEquals(3, result.size()),
                () -> assertEquals(1, result.stream().filter(c -> c.getId() == 1L).count()),
                () -> assertEquals(1, result.stream().filter(c -> c.getId() == 2L).count()),
                () -> assertEquals(1, result.stream().filter(c -> c.getId() == 3L).count())
        );

    }

    private TeamMember provideMember() {
        return TeamMember.builder()
                .id(1L)
                .roles(List.of(TeamRole.OWNER))
                .build();
    }

    private TeamMember provideMemberWithNoAccess() {
        return TeamMember.builder()
                .id(1L)
                .roles(List.of(TeamRole.ANALYST))
                .build();
    }

    private Project provideProject() {
        return Project.builder()
                .id(1L)
                .build();
    }

    private Campaign provideCampaign() {
        return Campaign.builder()
                .id(1L)
                .status(CampaignStatus.ACTIVE)
                .title("Title")
                .description("Description")
                .project(provideProject())
                .build();
    }

    private Campaign provideCampaign(Long id) {
        return Campaign.builder()
                .id(id)
                .status(CampaignStatus.ACTIVE)
                .title("Title_" + id)
                .description("Description_" + id)
                .project(provideProject())
                .build();
    }

}