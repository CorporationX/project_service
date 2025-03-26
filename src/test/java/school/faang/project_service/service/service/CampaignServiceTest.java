package school.faang.project_service.service.service;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.campaign.CampaignDateFilter;
import faang.school.projectservice.filter.campaign.CampaignFilter;
import faang.school.projectservice.filter.campaign.CampaignOwnerFilter;
import faang.school.projectservice.filter.campaign.CampaignProjectFilter;
import faang.school.projectservice.filter.campaign.CampaignStatusFilter;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.mapper.CampaignMapperImpl;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.service.CampaignService.CREATING_EXCEPTION;
import static faang.school.projectservice.service.CampaignService.ID_NULL_EXCEPTION;
import static faang.school.projectservice.utils.validationsUtils.CampaignValidator.CAMPAIGN_NULL_EXCEPTION;
import static faang.school.projectservice.utils.validationsUtils.CampaignValidator.DESCRIPTION_MAX_LENGTH_EXCEPTION;
import static faang.school.projectservice.utils.validationsUtils.CampaignValidator.GOAL_MIN_DECIMAL_EXCEPTION;
import static faang.school.projectservice.utils.validationsUtils.CampaignValidator.PROJECT_ID_NULL_EXCEPTION;
import static faang.school.projectservice.utils.validationsUtils.CampaignValidator.TITLE_EMPTY_EXCEPTION;
import static faang.school.projectservice.utils.validationsUtils.CampaignValidator.TITLE_MAX_LENGTH_EXCEPTION;
import static faang.school.projectservice.utils.validationsUtils.CampaignValidator.TITLE_NULL_EXCEPTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceTest {
    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";
    private static final BigDecimal GOAL = new BigDecimal(123.3);
    private static final Long PROJECT_ID = 1L;
    private static final Long CREATED_BY = 1L;
    private static final Long DTO_ID = 1L;
    private CampaignDto campaignDto;
    private CampaignUpdateDto campaignUpdateDto;

    @Spy
    private CampaignMapperImpl campaignMapper = new CampaignMapperImpl();

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private ProjectRepository projectRepository;

    private CampaignFilter campaignProjectFilter = new CampaignProjectFilter();

    private CampaignFilter campaignStatusFilter = new CampaignStatusFilter();

    private CampaignFilter campaignOwnerFilter = new CampaignOwnerFilter();

    private CampaignFilter campaignDateFilter = new CampaignDateFilter();

    @InjectMocks
    private CampaignService campaignService;

    @Captor
    private ArgumentCaptor<Campaign> campaignCaptor;

    @BeforeEach
    void setUp() {
        List<CampaignFilter> filtersList = List.of(campaignProjectFilter, campaignStatusFilter,
                campaignOwnerFilter, campaignDateFilter);

        ReflectionTestUtils.setField(campaignService, "campaignFilters", filtersList);

        campaignDto = CampaignDto.builder()
                .id(DTO_ID)
                .title(TITLE)
                .description(DESCRIPTION)
                .goal(GOAL)
                .projectId(PROJECT_ID)
                .createdBy(CREATED_BY)
                .build();

        campaignUpdateDto = CampaignUpdateDto.builder()
                .id(DTO_ID)
                .title(TITLE)
                .description(DESCRIPTION)
                .updatedBy(CREATED_BY)
                .build();
    }

    @Test
    public void testCampaignWithNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> campaignService.create(null));

        assertEquals(CAMPAIGN_NULL_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testCampaignWithTitleNull() {
        CampaignDto dto = CampaignDto.builder().title(null).build();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> campaignService.create(dto));

        assertEquals(TITLE_NULL_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testCampaignWithTitleEmpty() {
        CampaignDto dto = CampaignDto.builder().title("  ").build();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> campaignService.create(dto));

        assertEquals(TITLE_EMPTY_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testCampaignWithTitleMoreThanMaxLength() {
        String pattern = "pattern";
        String longString = pattern.repeat(19);
        CampaignDto dto = CampaignDto.builder().title(longString).build();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> campaignService.create(dto));

        assertEquals(TITLE_MAX_LENGTH_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testCampaignWithDescriptionMoreThanMaxLength() {
        String pattern = "pattern";
        String longString = pattern.repeat(587);
        campaignDto.setDescription(longString);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> campaignService.create(campaignDto));

        assertEquals(DESCRIPTION_MAX_LENGTH_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testCampaignWithGoalTharSmallerThanMinValue() {
        campaignDto.setGoal(new BigDecimal(0.0));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> campaignService.create(campaignDto));

        assertEquals(GOAL_MIN_DECIMAL_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testCampaignWithProjectIdNull() {
        campaignDto.setProjectId(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> campaignService.create(campaignDto));

        assertEquals(PROJECT_ID_NULL_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testCampaignNotManagerOrOwner() {
        Project project = prepareProject(TeamRole.DESIGNER);

        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> campaignService.create(campaignDto));

        assertEquals(CREATING_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testCampaignCreate() {
        Project project = prepareProject(TeamRole.MANAGER);
        Campaign campaign = campaignMapper.toCampaign(campaignDto);

        when(projectRepository.findById(anyLong()))
                .thenReturn(Optional.of(project));
        campaignService.create(campaignDto);
        verify(campaignRepository).save(campaignCaptor.capture());

        Campaign newCampaign = campaignCaptor.getValue();
        assertEquals(campaign.getTitle(), newCampaign.getTitle());
        assertEquals(campaign.getDescription(), newCampaign.getDescription());
        assertEquals(campaign.getGoal(), newCampaign.getGoal());
        assertEquals(campaign.getProject().getId(), newCampaign.getProject().getId());
        assertEquals(campaign.getCreatedBy(), newCampaign.getCreatedBy());
    }

    @Test
    public void testUpdateCampaign() {
        setupRepositoryMocks();

        CampaignDto result = campaignService.update(campaignUpdateDto);
        verify(campaignRepository).save(campaignCaptor.capture());
        Campaign savedCampaign = campaignCaptor.getValue();
        assertEquals(result.getTitle(), savedCampaign.getTitle());
        assertEquals(result.getDescription(), savedCampaign.getDescription());
        assertEquals(result.getUpdatedBy(), savedCampaign.getUpdatedBy());
    }

    @Test
    public void testCampaignDeleteWithIdNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> campaignService.delete(null));

        assertEquals(ID_NULL_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testCampaignDelete() {
        Campaign campaign = campaignMapper.toCampaign(campaignDto);
        campaign.setDeleted(false);

        when(campaignRepository.findById(campaignUpdateDto.getId()))
                .thenReturn(Optional.of(campaign));
        campaignService.delete(DTO_ID);

        verify(campaignRepository).save(campaignCaptor.capture());
        Campaign savedCampaign = campaignCaptor.getValue();
        assertEquals(campaign.isDeleted(), savedCampaign.isDeleted());
    }

    @Test
    public void testGetCampaignWithIdNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> campaignService.getCampaign(null));

        assertEquals(ID_NULL_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testGetCampaignsByProjectWithProjectIdNull() {
        CampaignFilterDto campaignFilterDto = CampaignFilterDto.builder().projectId(null).build();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> campaignService.getCampaignsByProject(campaignFilterDto));

        assertEquals(PROJECT_ID_NULL_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testGetCampaignsByProjectWithProject() {
        Campaign campaign1 = Campaign.builder()
                .project(Project.builder().id(1L).build())
                .createdBy(null)
                .status(null)
                .createdAt(LocalDateTime.now())
                .build();
        Campaign campaign2 = Campaign.builder()
                .project(Project.builder().id(2L).build())
                .createdBy(null)
                .status(null)
                .createdAt(LocalDateTime.now())
                .build();
        CampaignFilterDto campaignFilterDto =
                new CampaignFilterDto(1L, null, null, null);

        when(campaignRepository.findAll()).thenReturn(List.of(campaign1, campaign2));

        List<CampaignDto> result = campaignService.getCampaignsByProject(campaignFilterDto);
        System.out.println(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetCampaignsByProjectWithProjectAndStatus() {
        Campaign campaign1 = Campaign.builder()
                .project(Project.builder().id(1L).build())
                .createdBy(null)
                .status(CampaignStatus.ACTIVE)
                .createdAt(null)
                .build();
        Campaign campaign2 = Campaign.builder()
                .project(Project.builder().id(2L).build())
                .createdBy(null)
                .status(CampaignStatus.CANCELED)
                .createdAt(null)
                .build();
        CampaignFilterDto campaignFilterDto =
                new CampaignFilterDto(1L, null, CampaignStatus.CANCELED, null);

        when(campaignRepository.findAll()).thenReturn(List.of(campaign1, campaign2));

        List<CampaignDto> result = campaignService.getCampaignsByProject(campaignFilterDto);
        System.out.println(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetCampaignsByProjectWithProjectAndOwner() {
        Campaign campaign1 = Campaign.builder()
                .project(Project.builder().id(1L).build())
                .createdBy(33L)
                .status(null)
                .createdAt(null)
                .build();
        Campaign campaign2 = Campaign.builder()
                .project(Project.builder().id(2L).build())
                .createdBy(3L)
                .status(null)
                .createdAt(null)
                .build();
        CampaignFilterDto campaignFilterDto =
                new CampaignFilterDto(2L, 3L, null, null);

        when(campaignRepository.findAll()).thenReturn(List.of(campaign1, campaign2));

        List<CampaignDto> result = campaignService.getCampaignsByProject(campaignFilterDto);
        System.out.println(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetCampaignsByProjectWithProjectAndDate() {
        Campaign campaign1 = Campaign.builder()
                .project(Project.builder().id(1L).build())
                .createdBy(null)
                .status(null)
                .createdAt(LocalDateTime.of(2025, 3, 20, 1, 1))
                .build();
        Campaign campaign2 = Campaign.builder()
                .project(Project.builder().id(2L).build())
                .createdBy(null)
                .status(null)
                .createdAt(LocalDateTime.of(2025, 2, 1, 1, 1))
                .build();
        LocalDateTime after = LocalDateTime.of(2022, 3, 1, 1, 1);
        CampaignFilterDto campaignFilterDto =
                new CampaignFilterDto(1L, null, null, after);

        when(campaignRepository.findAll()).thenReturn(List.of(campaign1, campaign2));

        List<CampaignDto> result = campaignService.getCampaignsByProject(campaignFilterDto);
        System.out.println(result);
        assertEquals(1, result.size());
    }

    private void setupRepositoryMocks() {
        Project project = prepareProject(TeamRole.MANAGER);
        Campaign campaign = campaignMapper.toCampaign(campaignDto);

        when(projectRepository.findById(anyLong()))
                .thenReturn(Optional.of(project));
        when(campaignRepository.findById(campaignUpdateDto.getId()))
                .thenReturn(Optional.of(campaign));
    }

    private CampaignFilterDto prepareCampaignFilterDto(Long projectId, Long createdBy,
                                                       CampaignStatus status, LocalDateTime date) {
        return CampaignFilterDto.builder()
                .createdAfter(date)
                .projectId(projectId)
                .createdBy(createdBy)
                .status(status)
                .build();
    }

    private Project prepareProject(TeamRole role) {
        List<TeamRole> roles = List.of(role);
        TeamMember member = TeamMember.builder()
                .userId(CREATED_BY)
                .roles(roles)
                .nickname("Aleksey")
                .build();
        List<TeamMember> members = List.of(member);
        Team team = Team.builder().teamMembers(members).build();
        List<Team> teams = List.of(team);
        Project project = Project.builder()
                .id(PROJECT_ID)
                .teams(teams)
                .name("Apple")
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.CANCELLED)
                .galleryFileKeys(List.of("cont"))
                .build();
        return project;
    }
}