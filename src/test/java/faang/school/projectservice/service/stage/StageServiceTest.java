package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.filter.stage.StageFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.dto.stageroles.StageRolesDto;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.stageroles.StageRolesService;
import faang.school.projectservice.service.task.TaskService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {

    private static final String TEXT = "Test";
    private static final TeamRole DESIGNER = TeamRole.DESIGNER;
    private static final TeamRole DEVELOPER = TeamRole.DEVELOPER;
    private static final Long ID_ONE = 1L;
    private static final Long ID_TWO = 2L;
    private static final int COUNT_ONE = 1;
    private static final int COUNT_TWO = 2;
    @InjectMocks
    private StageService stageService;
    @Mock
    private StageRepository stageRepository;
    @Mock
    private TeamMemberService teamMemberService;
    @Mock
    private StageRolesService stageRolesService;
    @Mock
    private TaskService taskService;
    @Mock
    private ProjectService projectService;
    @Mock
    private StageMapper stageMapper;
    @Mock
    private List<Filter<StageFilterDto, Stage>> stagesFilters;
    @Mock
    private Filter<StageFilterDto, Stage> filter;
    private Stage stageFirst;
    private Stage stage;
    private Project project;
    private StageRoles stageRolesOne;
    private StageRoles stageRolesTwo;
    private StageDto stageDto;
    private StageCreateDto stageCreateDto;
    private ProjectDto projectDto;
    private StageRolesDto stageRolesDtoOne;
    private StageRolesDto stageRolesDtoTwo;
    private Task taskOne;
    private Task taskTwo;
    private TaskDto taskDtoOne;
    private TaskDto taskDtoTwo;
    private TeamMember teamMemberOne;
    private TeamMember teamMemberTwo;
    private TeamMemberDto teamMemberDtoOne;
    private TeamMemberDto teamMemberDtoTwo;
    private StageUpdateDto stageUpdateDto;
    private List<StageRoles> stageRoles = new ArrayList<>();
    private List<StageRolesDto> stageRolesDtos = new ArrayList<>();
    private List<Task> tasks = new ArrayList<>();
    private List<TaskDto> taskDtos = new ArrayList<>();
    private List<TeamMember> teamMembers = new ArrayList<>();
    private List<TeamMemberDto> teamMemberDtos = new ArrayList<>();
    private List<Stage> stages = new ArrayList<>();
    private List<StageDto> stageDtos = new ArrayList<>();
    private List<Long> ids = new ArrayList<>();


    @Nested
    class PositiveTest {

        @BeforeEach
        void init() {
            ids.add(ID_ONE);
            ids.add(ID_TWO);

            project = Project.builder()
                    .id(ID_ONE)
                    .build();

            stageRolesOne = StageRoles.builder()
                    .id(ID_ONE)
                    .teamRole(DEVELOPER)
                    .count(COUNT_TWO)
                    .build();

            stageRolesTwo = StageRoles.builder()
                    .id(ID_TWO)
                    .teamRole(DEVELOPER)
                    .count(COUNT_TWO)
                    .build();

            stageRoles.add(stageRolesOne);
            stageRoles.add(stageRolesTwo);

            stageFirst = Stage.builder()
                    .stageId(ID_ONE)
                    .stageName(TEXT)
                    .stageRoles(stageRoles)
                    .build();

            stageRolesDtoOne = StageRolesDto.builder()
                    .id(ID_ONE)
                    .teamRole(DESIGNER)
                    .count(COUNT_TWO)
                    .build();

            stageRolesDtoTwo = StageRolesDto.builder()
                    .id(ID_TWO)
                    .teamRole(DEVELOPER)
                    .count(COUNT_TWO)
                    .build();

            stageRolesDtos.add(stageRolesDtoOne);
            stageRolesDtos.add(stageRolesDtoTwo);

            stageCreateDto = StageCreateDto.builder()
                    .stageName(TEXT)
                    .projectId(ID_ONE)
                    .stageRolesDtos(stageRolesDtos)
                    .build();

            projectDto = ProjectDto.builder()
                    .id(ID_ONE)
                    .build();

            taskOne = Task.builder()
                    .id(ID_ONE)
                    .build();

            taskTwo = Task.builder()
                    .id(ID_TWO)
                    .build();

            tasks.add(taskOne);
            tasks.add(taskTwo);

            taskDtoOne = TaskDto.builder()
                    .id(ID_ONE)
                    .build();

            taskDtoTwo = TaskDto.builder()
                    .id(ID_TWO)
                    .build();

            taskDtos.add(taskDtoOne);
            taskDtos.add(taskDtoTwo);

            teamMemberOne = TeamMember.builder()
                    .id(ID_ONE)
                    .build();

            teamMemberTwo = TeamMember.builder()
                    .id(ID_TWO)
                    .build();

            teamMembers.add(teamMemberOne);
            teamMembers.add(teamMemberTwo);

            teamMemberDtoOne = TeamMemberDto.builder()
                    .id(ID_ONE)
                    .build();

            teamMemberDtoTwo = TeamMemberDto.builder()
                    .id(ID_TWO)
                    .build();

            teamMemberDtos.add(teamMemberDtoOne);
            teamMemberDtos.add(teamMemberDtoTwo);

            stageDto = StageDto.builder()
                    .stageId(ID_ONE)
                    .stageName(TEXT)
                    .project(projectDto)
                    .stageRoles(stageRolesDtos)
                    .tasks(taskDtos)
                    .executors(teamMemberDtos)
                    .build();

            stage = Stage.builder()
                    .stageId(ID_ONE)
                    .stageName(TEXT)
                    .project(project)
                    .stageRoles(stageRoles)
                    .tasks(tasks)
                    .executors(teamMembers)
                    .build();

            stages.add(stage);

            stageDtos.add(stageDto);

            stageUpdateDto = StageUpdateDto.builder()
                    .projectId(ID_ONE)
                    .stageName(TEXT)
                    .stageRoleIds(ids)
                    .taskIds(ids)
                    .executorIds(ids)
                    .build();
        }

        @Test
        @DisplayName("Successful request creation")
        void whenCreateThenSaveStage() {
            Stage stage = Stage.builder()
                    .stageId(ID_ONE)
                    .stageName(TEXT)
                    .project(project)
                    .stageRoles(stageRoles)
                    .build();

            when(stageMapper.toStageEntity(stageCreateDto)).thenReturn(stageFirst);
            when(projectService.getProjectById(stageCreateDto.getProjectId())).thenReturn(project);
            when(stageRepository.save(stage)).thenReturn(stage);
            when(stageMapper.toStageDto(stage)).thenReturn(stageDto);

            StageDto result = stageService.createStage(stageCreateDto);

            assertNotNull(result);
            assertEquals(result.getStageId(), ID_ONE);
            assertEquals(result.getStageName(), TEXT);
            assertEquals(result.getProject(), projectDto);
            assertEquals(result.getStageRoles(), stageRolesDtos);

            verify(stageMapper).toStageEntity(stageCreateDto);
            verify(projectService).getProjectById(stageCreateDto.getProjectId());
            verify(stageRepository).save(stage);
            verify(stageMapper).toStageDto(stage);
        }

        @Test
        @DisplayName("Successful receipt of the request")
        void whenGetRequestsByFilterThenSuccess() {
            TeamRole teamRole = TeamRole.DEVELOPER;
            List<TeamRole> teamRoles = new ArrayList<>(List.of(teamRole));

            StageFilterDto stageFilterDto = new StageFilterDto();
            stageFilterDto.setTeamRoles(teamRoles);

            when(stageRepository.findAll()).thenReturn(List.of(stage));
            when(filter.isApplicable(stageFilterDto)).thenReturn(true);
            when(filter.applyFilter(any(Stream.class),
                    eq(stageFilterDto))).thenReturn(Stream.of(stage));
            when(stagesFilters.stream()).thenReturn(Stream.of(filter));
            when(stageMapper.toStageDto(any())).thenReturn(stageDto);

            List<StageDto> result = stageService.getStagesByFilters(stageFilterDto);

            assertEquals(ID_ONE, result.size());
            assertEquals(result.get(0), stageDto);
            assertEquals(stageDto.getStageRoles().get(1).getTeamRole(), DEVELOPER);
            assertEquals(stageDto.getStageId(), ID_ONE);
            assertEquals(stageDto.getStageName(), TEXT);

            verify(stageRepository).findAll();
            verify(filter).isApplicable(stageFilterDto);
            verify(filter).applyFilter(any(), eq(stageFilterDto));
            verify(stageMapper).toStageDto(any());
        }

        @Test
        @DisplayName("Delete stage")
        void whenValidateAcceptThenDelete() {
            when(stageRepository.findById(ID_ONE)).thenReturn(Optional.of(stage));

            stageService.deleteStage(ID_ONE);

            verify(stageRepository).findById(ID_ONE);
            verify(stageRepository).delete(stage);
        }

        @Test
        @DisplayName("We contact the database if there is such a stage, update it")
        void whenValidateAcceptThenUpdate() {
            when(stageRepository.findById(ID_ONE)).thenReturn(Optional.of(stage));
            when(projectService.getProjectById(stageUpdateDto.getProjectId())).thenReturn(project);
            when(stageRolesService.getAllById(stageUpdateDto.getStageRoleIds())).thenReturn(stageRoles);
            when(taskService.getAllById(stageUpdateDto.getTaskIds())).thenReturn(tasks);
            when(teamMemberService.getAllById(stageUpdateDto.getExecutorIds())).thenReturn(teamMembers);
            when(stageRepository.save(stage)).thenReturn(stage);
            when(stageMapper.toStageDto(stage)).thenReturn(stageDto);

            StageDto result = stageService.updateStage(ID_ONE, stageUpdateDto);

            assertNotNull(result);
            assertEquals(result.getStageId(), ID_ONE);
            assertEquals(result.getStageName(), TEXT);
            assertEquals(result.getProject(), projectDto);
            assertEquals(result.getStageRoles().size(), COUNT_TWO);
            assertEquals(result.getTasks().size(), COUNT_TWO);
            assertEquals(result.getExecutors().size(), COUNT_TWO);


            verify(stageRepository).findById(ID_ONE);
            verify(stageRepository).save(stage);
            verify(stageMapper).toStageDto(stage);
            verify(projectService).getProjectById(stageUpdateDto.getProjectId());
            verify(stageRolesService).getAllById(stageUpdateDto.getStageRoleIds());
            verify(taskService).getAllById(stageUpdateDto.getTaskIds());
        }

        @Test
        @DisplayName("Return the stages by id if successful")
        void whenValidateAcceptThenReturnAllById() {
            when(stageRepository.findAll()).thenReturn(stages);
            when(stageMapper.toStageDtos(stages)).thenReturn(stageDtos);

            List<StageDto> result = stageService.getAllStage();

            assertEquals(result.size(), COUNT_ONE);

            verify(stageRepository).findAll();
            verify(stageMapper).toStageDtos(stages);
        }

        @Test
        @DisplayName("Return the stage by ID if successful")
        void whenValidateAcceptThenReturnById() {
            when(stageRepository.findById(ID_ONE)).thenReturn(Optional.of(stage));
            when(stageMapper.toStageDto(stage)).thenReturn(stageDto);

            StageDto result = stageService.getStageById(ID_ONE);

            assertEquals(result.getStageId(), ID_ONE);
            assertEquals(result.getStageName(), TEXT);
            assertEquals(result.getProject(), projectDto);
            assertEquals(result.getStageRoles().size(), COUNT_TWO);
            assertEquals(result.getTasks().size(), COUNT_TWO);
            assertEquals(result.getExecutors().size(), COUNT_TWO);

            verify(stageRepository).findById(ID_ONE);
            verify(stageMapper).toStageDto(stage);
        }
    }

    @Nested
    class NegativeTest {
        @Test
        @DisplayName("Throw an exception if there is no such stage when deleting it from the database")
        void whenValidateDeleteAcceptThenException() {
            when(stageRepository.findById(ID_ONE)).thenReturn(Optional.empty());

            assertEquals("No such request was found " + ID_ONE,
                    assertThrows(EntityNotFoundException.class, () -> {
                        stageService.deleteStage(ID_ONE);
                    }).getMessage());

            verify(stageRepository).findById(ID_ONE);
        }

        @Test
        @DisplayName("Throw an exception if there is no such stage when changing it")
        void whenValidateUpdateAcceptThenException() {
            when(stageRepository.findById(ID_ONE)).thenReturn(Optional.empty());

            assertEquals("No such request was found " + ID_ONE,
                    assertThrows(EntityNotFoundException.class, () -> {
                        stageService.updateStage(ID_ONE, stageUpdateDto);
                    }).getMessage());

            verify(stageRepository).findById(ID_ONE);
        }

        @Test
        @DisplayName("Throw an exception if there is no such stage when returning it")
        void whenValidateGetAcceptThenException() {
            when(stageRepository.findById(ID_ONE)).thenReturn(Optional.empty());

            assertEquals("No such request was found " + ID_ONE,
                    assertThrows(EntityNotFoundException.class, () -> {
                        stageService.getStageById(ID_ONE);
                    }).getMessage());

            verify(stageRepository).findById(ID_ONE);
        }
    }
}