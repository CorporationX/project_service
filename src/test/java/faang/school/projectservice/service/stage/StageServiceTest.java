package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.stage.StageMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {

    @Mock
    StageRepository stageRepository;
    @Mock
    ProjectRepository projectRepository;
    @Mock
    TeamMemberJpaRepository teamMemberRepository;
    @Mock
    StageRolesRepository stageRolesRepository;
    @Spy
    StageMapperImpl stageMapper;

    @InjectMocks
    StageService stageService;

    @ParameterizedTest
    @MethodSource("getInactiveProjects")
    @DisplayName("Create. Project is not active.")
    void createProjectNotActive(Project project) {
        when(projectRepository.getProjectById(anyLong()))
                .thenReturn(project);
        StageDto stageDto = StageDto.builder()
                .projectId(new Random().nextLong())
                .build();
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> stageService.create(stageDto));
        assertEquals("Project is not active", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("getIncorrectStageRolesAndExecutors")
    @DisplayName("Create. Check unnecessary executors exist")
    void checkUnnecessaryExecutorsExist(List<TeamMember> members, List<StageRoles> stageRoles) {
        when(projectRepository.getProjectById(anyLong()))
                .thenReturn(Project.builder()
                        .status(ProjectStatus.IN_PROGRESS)
                        .build());
        when(teamMemberRepository.findAllById(anyList()))
                .thenReturn(members);
        when(stageRolesRepository.findAllById(anyList()))
                .thenReturn(stageRoles);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> stageService.create(StageDto.builder()
                        .projectId(anyLong())
                        .stageRoleIds(new ArrayList<>())
                        .teamMemberIds(new ArrayList<>())
                        .build()
                ));
        assertTrue(exception.getMessage().contains("Unnecessary role: "));
    }

    @Test
    @DisplayName("Create. Successful creation")
    void createSuccessful() {
        Long id = new Random().nextLong();
        StageDto stageDto = StageDto.builder()
                .stageRoleIds(new ArrayList<>())
                .teamMemberIds(new ArrayList<>())
                .projectId(id)
                .build();
        Project project = Project.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        when(projectRepository.getProjectById(anyLong()))
                .thenReturn(project);
        when(stageRolesRepository.findAllById(anyList()))
                .thenReturn(new ArrayList<>());
        when(teamMemberRepository.findAllById(anyList()))
                .thenReturn(new ArrayList<>());
        stageService.create(stageDto);
        verify(stageRepository).save(any(Stage.class));
    }

    @ParameterizedTest
    @MethodSource("getRandomIdsAndProject")
    @DisplayName("Get stages by project id")
    void getStagesByProjectId(Long projectId, List<Stage> stages) {
        when(projectRepository.getProjectById(projectId))
                .thenReturn(Project.builder()
                        .id(projectId)
                        .stages(stages)
                        .build());
        List<StageDto> stageDtos = stageService.getStagesByProjectId(projectId);
        assertEquals(stageDtos.size(), stages.size());

    }

    @ParameterizedTest
    @MethodSource("getRandomIds")
    @DisplayName("Get stage by id")
    void getStageById(Long stageId) {
        when(stageRepository.getById(stageId))
                .thenReturn(Stage.builder()
                        .stageId(stageId)
                        .stageRoles(new ArrayList<>())
                        .project(new Project())
                        .executors(new ArrayList<>())
                        .build());
        StageDto stageDto = stageService.getStageById(stageId);
        assertEquals(stageDto.getStageId(), stageId);
    }

    private static Stream<Arguments> getRandomIds() {
        return Stream.of(
                Arguments.of(
                        new Random().nextLong()
                ),
                Arguments.of(
                        new Random().nextLong()
                ),
                Arguments.of(
                        new Random().nextLong()
                ),
                Arguments.of(
                        new Random().nextLong()
                ),
                Arguments.of(
                        new Random().nextLong()
                ),
                Arguments.of(
                        new Random().nextLong()
                )
        );
    }

    private static Stream<Arguments> getRandomIdsAndProject() {
        Stage stage = Stage.builder()
                .project(Project.builder()
                        .id(new Random().nextLong())
                        .build())
                .stageRoles(new ArrayList<>())
                .executors(new ArrayList<>())
                .build();
        return Stream.of(
                Arguments.of(
                        new Random().nextLong(),
                        List.of(stage)
                ),
                Arguments.of(
                        new Random().nextLong(),
                        List.of(stage, stage)
                ),
                Arguments.of(
                        new Random().nextLong(),
                        Collections.emptyList()
                ),
                Arguments.of(
                        new Random().nextLong(),
                        List.of(stage, stage, stage)
                ),
                Arguments.of(
                        new Random().nextLong(),
                        List.of(stage, stage, stage, stage)
                ),
                Arguments.of(
                        new Random().nextLong(),
                        List.of(stage, stage, stage, stage, stage)
                ),
                Arguments.of(
                        new Random().nextLong(),
                        List.of(stage, stage, stage, stage, stage, stage)
                ),
                Arguments.of(
                        new Random().nextLong(),
                        List.of(stage, stage, stage, stage, stage, stage, stage)
                ),
                Arguments.of(
                        new Random().nextLong(),
                        Collections.emptyList()
                )
        );
    }

    private static Stream<Arguments> getIncorrectStageRolesAndExecutors() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                TeamMember.builder()
                                        .roles(List.of(
                                                TeamRole.TESTER,
                                                TeamRole.DEVELOPER
                                        ))
                                        .build(),
                                TeamMember.builder()
                                        .roles(List.of(
                                                TeamRole.INTERN,
                                                TeamRole.DEVELOPER
                                        ))
                                        .build()
                        ),
                        List.of(
                                StageRoles.builder()
                                        .teamRole(TeamRole.TESTER)
                                        .count(1)
                                        .build()
                        )
                ),
                Arguments.of(
                        List.of(
                                TeamMember.builder()
                                        .roles(List.of(
                                                TeamRole.TESTER,
                                                TeamRole.DEVELOPER
                                        ))
                                        .build(),
                                TeamMember.builder()
                                        .roles(List.of(
                                                TeamRole.INTERN,
                                                TeamRole.DEVELOPER
                                        ))
                                        .build()
                        ),
                        List.of(
                                StageRoles.builder()
                                        .teamRole(TeamRole.DEVELOPER)
                                        .count(2)
                                        .build()
                        )
                )
        );
    }

    private static Stream<Arguments> getInactiveProjects() {
        return Stream.of(
                Arguments.of(
                        Project.builder()
                                .status(ProjectStatus.CANCELLED)
                                .build()
                ),
                Arguments.of(
                        Project.builder()
                                .status(ProjectStatus.COMPLETED)
                                .build()
                )
        );
    }
}