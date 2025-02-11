package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.dto.stage.*;
import faang.school.projectservice.dto.stage.stage_role.StageRolesCreateRequestDto;
import faang.school.projectservice.dto.stage.stage_role.StageRolesUpdateRequestDto;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.mapper.StageRolesMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.stratagy.stage.StageDeletionStrategy;
import faang.school.projectservice.stratagy.stage.StageDeletionStrategyFactory;
import faang.school.projectservice.stratagy.stage.StageDeletionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {

    @Mock
    private StageRepository stageRepository;

    @Spy
    @InjectMocks
    private StageMapper stageMapper = Mappers.getMapper(StageMapper.class);

    @Spy
    private StageRolesMapper stageRolesMapper = Mappers.getMapper(StageRolesMapper.class);

    @Mock
    private ProjectService projectService;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private StageInvitationService stageInvitationService;

    @Mock
    private StageDeletionStrategyFactory stageDeletionStrategyFactory;

    @Mock
    private StageDeletionStrategy stageDeletionStrategy;

    @InjectMocks
    private StageService stageService;

    private Stage stage;


    @BeforeEach
    void setUp() {
        stageService.setStageInvitationService(stageInvitationService);
        stage = new Stage();
        stage.setStageId(1L);
        Project project = new Project();
        project.setId(1L);
        stage.setProject(project);
    }

    @Test
    void createStage_ShouldSaveStageWhenValidRequest() {
        StageCreateRequestDto input = new StageCreateRequestDto();
        input.setStageName("testStage");
        input.setProjectId(1L);
        input.setStageRolesDtos(generateAndGetStageRolesCreateRequestDtos());

        Stage expectedStage = stageMapper.toStage(input);
        expectedStage.setProject(stage.getProject());
        List<StageRoles> stageRoles = expectedStage.getStageRoles();
        for (StageRoles stageRole : stageRoles) {
            stageRole.setStage(expectedStage);
        }

        when(projectService.getProjectById(1L)).thenReturn(stage.getProject());
        when(stageRepository.save(any(Stage.class))).thenReturn(expectedStage);

        StageCreateResponseDto result = stageService.createStage(input);

        assertEquals(stageMapper.toCreateResponseDto(expectedStage), result);
    }

    @Test
    void deleteStage_ShouldDeleteStageWhenStageExists() {
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));
        when(stageDeletionStrategyFactory.getStrategy(any())).thenReturn(stageDeletionStrategy);

        stageService.deleteStage(1L, StageDeletionType.CASCADE_DELETE);

        verify(stageRepository, times(1)).delete(stage);
    }

    @Test
    void deleteStage_ShouldThrowExceptionWhenStageNotFound() {
        when(stageRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                stageService.deleteStage(1L, StageDeletionType.CASCADE_DELETE));
    }

    @Test
    void getStageById_ShouldReturnStageWhenExists() {
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));

        Stage result = stageService.getStageById(1L);

        assertEquals(stage, result);
    }

    @Test
    void getStageById_ShouldThrowExceptionWhenNotFound() {
        when(stageRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> stageService.getStageById(1L));
        assertEquals("No stage found with id: 1", exception.getMessage());
    }

    @Test
    void getStageDtoById_ShouldReturnDtoWhenStageExists() {
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));

        StageResponseDto result = stageService.getStageDtoById(1L);

        assertEquals(stageMapper.toResponseDto(stage), result);
    }

    @Test
    void updateStage_ShouldUpdateStageWhenValidRequest() {
        List<TeamMember> teamMembers = generateAndGetTeamMembers();

        Team team = new Team();
        team.setId(1L);
        team.setTeamMembers(teamMembers);

        Project project = new Project();
        project.setId(1L);
        project.setTeams(List.of(team));

        Stage existingStage = new Stage();
        existingStage.setStageId(1L);
        existingStage.setProject(project);
        existingStage.setStageRoles(generateAndGetStageRoles(existingStage, 1, 1));

        Stage expectedStage = new Stage();
        expectedStage.setStageId(1L);
        expectedStage.setProject(project);
        expectedStage.setExecutors(teamMembers);
        expectedStage.setStageRoles(generateAndGetStageRoles(expectedStage, 2, 3));

        StageUpdateRequestDto input = generateAndGetStageUpdateRequestDto(teamMembers);

        when(stageRepository.findById(1L)).thenReturn(Optional.of(existingStage));
        when(teamMemberService.findAllByIds(input.getTeamMemberIds())).thenReturn(teamMembers);
        when(stageRepository.save(any(Stage.class))).thenReturn(existingStage);
        when(stageInvitationService.createStageInvitationAndGetDto(any(Stage.class),
                any(Long.class),
                any(TeamMember.class)))
                .thenReturn(new StageInvitationDto());

        StageUpdateResponseDto result = stageService.updateStage(input);

        assertEquals(stageMapper.toUpdateResponseDto(expectedStage), result);
        verify(stageRepository).save(existingStage);
    }

    @Test
    void updateStage_ShouldThrowExceptionWhenStageNotFound() {
        StageUpdateRequestDto input = new StageUpdateRequestDto();
        input.setStageId(1L);

        when(stageRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> stageService.updateStage(input));
    }

    private List<StageRolesCreateRequestDto> generateAndGetStageRolesCreateRequestDtos() {
        StageRolesCreateRequestDto stageRolesCreateRequestDto1 = new StageRolesCreateRequestDto();
        stageRolesCreateRequestDto1.setTeamRole(TeamRole.DESIGNER);
        stageRolesCreateRequestDto1.setCount(2);

        StageRolesCreateRequestDto stageRolesCreateRequestDto2 = new StageRolesCreateRequestDto();
        stageRolesCreateRequestDto2.setTeamRole(TeamRole.DEVELOPER);
        stageRolesCreateRequestDto2.setCount(3);

        return List.of(stageRolesCreateRequestDto1, stageRolesCreateRequestDto2);
    }

    private static StageUpdateRequestDto generateAndGetStageUpdateRequestDto(List<TeamMember> teamMembers) {
        StageUpdateRequestDto input = new StageUpdateRequestDto();
        input.setStageId(1L);
        input.setStageUpdateAuthorId(1L);
        input.setTeamMemberIds(teamMembers.stream().map(TeamMember::getId).toList());
        input.setStageRolesDtos(generateAndGetStageRolesUpdateRequestDtos());
        return input;
    }

    private static List<StageRolesUpdateRequestDto> generateAndGetStageRolesUpdateRequestDtos() {
        StageRolesUpdateRequestDto stageRolesUpdateRequestDto1 = new StageRolesUpdateRequestDto();
        stageRolesUpdateRequestDto1.setId(1L);
        stageRolesUpdateRequestDto1.setCount(2);

        StageRolesUpdateRequestDto stageRolesCreateRequestDto2 = new StageRolesUpdateRequestDto();
        stageRolesCreateRequestDto2.setId(2L);
        stageRolesCreateRequestDto2.setCount(3);
        return List.of(stageRolesUpdateRequestDto1, stageRolesCreateRequestDto2);
    }

    private static List<TeamMember> generateAndGetTeamMembers() {
        TeamMember teamMember1 = new TeamMember();
        teamMember1.setId(1L);
        teamMember1.setRoles(List.of(TeamRole.DESIGNER));

        TeamMember teamMember2 = new TeamMember();
        teamMember2.setId(2L);
        teamMember2.setRoles(List.of(TeamRole.DEVELOPER));

        return List.of(teamMember1, teamMember2);
    }

    private List<StageRoles> generateAndGetStageRoles(Stage stage, int count1, int count2) {
        StageRoles stageRoles1 = new StageRoles();
        stageRoles1.setId(1L);
        stageRoles1.setTeamRole(TeamRole.DESIGNER);
        stageRoles1.setCount(count1);
        stageRoles1.setStage(stage);

        StageRoles stageRoles2 = new StageRoles();
        stageRoles2.setId(2L);
        stageRoles2.setTeamRole(TeamRole.DEVELOPER);
        stageRoles2.setCount(count2);
        stageRoles2.setStage(stage);
        return List.of(stageRoles1, stageRoles2);
    }
}
