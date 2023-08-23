package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.GeneralSubprojectDto;
import faang.school.projectservice.dto.subproject.SubprojectUpdateDto;
import faang.school.projectservice.exception.SubprojectException;
import faang.school.projectservice.mapper.subproject.SubprojectMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.model.project.ProjectStatus;
import faang.school.projectservice.model.project.ProjectVisibility;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.messages.SubprojectErrMessage.ERR_VISIBILITY_PARENT_PROJECT_FORMAT;
import static faang.school.projectservice.messages.SubprojectErrMessage.PARENT_STATUS_BLOCKED_CHANGED_SUBPROJECT_FORMAT;
import static faang.school.projectservice.messages.SubprojectErrMessage.PROJECT_IS_NOT_SUBPROJECT_FORMAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SubprojectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private StageRepository stageRepository;

    @Mock
    private MomentRepository momentRepository;

    @Spy
    private SubprojectMapperImpl subprojectMapper;

    @InjectMocks
    private SubprojectService subprojectService;

    private Project parentProject;

    private GeneralSubprojectDto generalSubprojectDto;

    private Long subprojectId;

    @BeforeEach
    void setUp() {
        parentProject = getParentProject();
        generalSubprojectDto = getSubprojectDtoReqCreate();
        subprojectId = 11L;
    }

    @Test
    void testCreateSubproject_WhenInputDataIsValid() {
        Long existParentId = ValuesForTest.PARENT.getId();
        Mockito.when(projectRepository.getProjectById(existParentId)).thenReturn(parentProject);
        Mockito.when(projectRepository.findAllByIds(generalSubprojectDto.getChildrenIds())).thenReturn(new ArrayList<>());
        Mockito.when(stageRepository.findAllByIds(generalSubprojectDto.getStagesIds())).thenReturn(new ArrayList<>());
        Mockito.when(projectRepository.save(Mockito.any())).thenReturn(getSubprojectAfterSave());
        GeneralSubprojectDto expectedDto = getExpectedDtoAfterCreate();

        GeneralSubprojectDto resultDto = subprojectService.createSubproject(existParentId, generalSubprojectDto);

        assertEquals(expectedDto, resultDto);
        Mockito.verify(projectRepository, Mockito.times(1)).getProjectById(existParentId);
        Mockito.verify(projectRepository, Mockito.times(1))
                .findAllByIds(generalSubprojectDto.getChildrenIds());
        Mockito.verify(stageRepository, Mockito.times(1))
                .findAllByIds(generalSubprojectDto.getStagesIds());
        Mockito.verify(projectRepository, Mockito.times(2)).save(Mockito.any());
    }

    @Test
    void testCreateSubproject_WhenParentVisibilityPublicAndRequiredVisibilityPrivate_shouldThrowException() {
        Long parentId = ValuesForTest.PARENT.getId();
        ProjectVisibility requiredVisibility = ProjectVisibility.PRIVATE;
        generalSubprojectDto.setVisibility(requiredVisibility);
        Mockito.when(projectRepository.getProjectById(parentId)).thenReturn(parentProject);
        String expectedMessage = MessageFormat.format(ERR_VISIBILITY_PARENT_PROJECT_FORMAT, requiredVisibility);

        Exception exception = assertThrows(SubprojectException.class,
                () -> subprojectService.createSubproject(parentId, generalSubprojectDto));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testCreateSubproject_WhenParentProjectNotExist_shouldThrowException() {
        Long parentId = ValuesForTest.PARENT.getId();
        String expectedMessage = String.format("Project not found by id: %s", parentId);
        Mockito.when(projectRepository.getProjectById(parentId))
                .thenThrow(new EntityNotFoundException(String.format("Project not found by id: %s", parentId)));

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> subprojectService.createSubproject(parentId, generalSubprojectDto));

        assertEquals(expectedMessage, exception.getMessage());
    }


    @Test
    void testUpdateSubproject_WhenSubprojectNotFound_ShouldThrowException() {
        String expectedMessage = String.format("Project not found by id: %s", subprojectId);
        Mockito.when(projectRepository.getProjectById(subprojectId))
                .thenThrow(new EntityNotFoundException(String.format("Project not found by id: %s", subprojectId)));
        SubprojectUpdateDto updateDto = new SubprojectUpdateDto();

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> subprojectService.updateSubproject(subprojectId, updateDto));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testUpdateSubproject_WhenIsNotSubproject_ShouldThrowException() {
        Project existsSubproject = getExistsSubproject();
        existsSubproject.setParentProject(null);
        Mockito.when(projectRepository.getProjectById(subprojectId)).thenReturn(existsSubproject);
        SubprojectUpdateDto updateDto = new SubprojectUpdateDto();
        String expectedMessage = MessageFormat.format(PROJECT_IS_NOT_SUBPROJECT_FORMAT, subprojectId);

        Exception exception = assertThrows(SubprojectException.class,
                () -> subprojectService.updateSubproject(subprojectId, updateDto));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideParentProjectForTest")
    void testUpdateSubproject_WhenParentProjectBlockedUpdate_ShouldThrowException(Project parentProject,
                                                                                  String expectedMessage) {
        Project existsSubproject = getExistsSubproject();
        existsSubproject.setParentProject(parentProject);
        Mockito.when(projectRepository.getProjectById(subprojectId)).thenReturn(existsSubproject);
        SubprojectUpdateDto updateDto = SubprojectUpdateDto.builder().visibility(ProjectVisibility.PRIVATE).build();

        Exception exception = assertThrows(SubprojectException.class,
                () -> subprojectService.updateSubproject(subprojectId, updateDto));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testUpdateSubproject_WhenAllValid() {
        Project existsSubproject = getExistsSubproject();
        Mockito.when(projectRepository.getProjectById(subprojectId)).thenReturn(existsSubproject);
        SubprojectUpdateDto updateDto = SubprojectUpdateDto.builder()
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        GeneralSubprojectDto expectedDto = getExpectedDtoAfterUpdate();
        Moment expectedMomentForSave = Moment.builder()
                .name("Выполнены все подпроекты")
                .description("Выполнены все подпроекты")
                .projects(List.of(existsSubproject.getParentProject()))
                .userIds(List.of(1L, 3L, 2L, 4L))
                .build();

        GeneralSubprojectDto result = subprojectService.updateSubproject(subprojectId, updateDto);

        assertEquals(expectedDto, result);

        Mockito.verify(projectRepository, Mockito.times(1)).getProjectById(subprojectId);
        Mockito.verify(momentRepository, Mockito.times(1)).save(expectedMomentForSave);
    }

    private static Stream<Arguments> provideParentProjectForTest() {
        String errorMessageCanceled = MessageFormat.format(PARENT_STATUS_BLOCKED_CHANGED_SUBPROJECT_FORMAT,
                ProjectStatus.CANCELLED);
        String errorMessageCompleted = MessageFormat.format(PARENT_STATUS_BLOCKED_CHANGED_SUBPROJECT_FORMAT,
                ProjectStatus.COMPLETED);
        String errorMessageVisibility = MessageFormat.format(ERR_VISIBILITY_PARENT_PROJECT_FORMAT,
                ProjectVisibility.PRIVATE);

        Project parentCanceled = Project.builder().status(ProjectStatus.CANCELLED).build();
        Project parentCompleted = Project.builder().status(ProjectStatus.COMPLETED).build();
        Project parentPublic = Project.builder().status(ProjectStatus.CREATED).visibility(ProjectVisibility.PUBLIC).build();

        return Stream.of(
                Arguments.of(parentCanceled, errorMessageCanceled),
                Arguments.of(parentCompleted, errorMessageCompleted),
                Arguments.of(parentPublic, errorMessageVisibility)
        );
    }

    private GeneralSubprojectDto getExpectedDtoAfterUpdate() {
        return GeneralSubprojectDto.builder()
                .subprojectId(subprojectId)
                .name(ValuesForTest.SUBPROJECT.getName())
                .parentProjectId(ValuesForTest.PARENT.getId())
                .childrenIds(List.of(20L, 21L))
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.COMPLETED)
                .stagesIds(new ArrayList<>())
                .build();
    }

    private Project getParentProject() {
        return Project.builder()
                .id(ValuesForTest.PARENT.getId())
                .name(ValuesForTest.PARENT.getName())
                .children(new ArrayList<>())
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private Project getExistsSubproject() {
        List<TeamMember> teamMembersFirst = List.of(
                TeamMember.builder().userId(1L).build(),
                TeamMember.builder().userId(3L).build()
        );
        List<TeamMember> teamMembersSecond = List.of(
                TeamMember.builder().userId(2L).build(),
                TeamMember.builder().userId(4L).build(),
                TeamMember.builder().userId(3L).build()
        );
        Team firstTeam = Team.builder().teamMembers(teamMembersFirst).build();
        Team secondTeam = Team.builder().teamMembers(teamMembersSecond).build();

        List<Team> teams = List.of(firstTeam, secondTeam);

        List<Project> nestedLevel1ChildrenSet1 = getNestedChildSubprojects(100, 2);
        List<Project> nestedLevel1ChildrenSet2 = getNestedChildSubprojects(200, 2);
        List<Project> childSubprojects = List.of(
                Project.builder()
                        .id(20L)
                        .name("Child subproject 1")
                        .children(nestedLevel1ChildrenSet1)
                        .build(),
                Project.builder()
                        .id(21L)
                        .name("Child subproject 2")
                        .children(nestedLevel1ChildrenSet2)
                        .build()
        );

        return Project.builder()
                .id(ValuesForTest.SUBPROJECT.getId())
                .name(ValuesForTest.SUBPROJECT.getName())
                .parentProject(getParentProject())
                .children(childSubprojects)
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.CREATED)
                .teams(teams)
                .build();
    }

    private List<Project> getNestedChildSubprojects(int startId, int countChild) {
        List<Project> childSubproject = new ArrayList<>(countChild);
        int endId = startId + countChild;
        for (int i = startId; i < endId; i++) {
            Project childProject = Project.builder()
                    .id((long) i)
                    .name("Nested Child " + i)
                    .visibility(ProjectVisibility.PUBLIC)
                    .status(ProjectStatus.CREATED)
                    .build();
            childSubproject.add(childProject);
        }
        return childSubproject;
    }

    private Project getSubprojectAfterSave() {
        return Project.builder()
                .id(ValuesForTest.DTO_INFO.getId())
                .name(ValuesForTest.DTO_INFO.getName())
                .description("Description")
                .parentProject(parentProject)
                .ownerId(5L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>())
                .stages(new ArrayList<>())
                .build();
    }

    private GeneralSubprojectDto getSubprojectDtoReqCreate() {
        return GeneralSubprojectDto.builder()
                .name(ValuesForTest.DTO_INFO.getName())
                .description("Description")
                .ownerId(5L)
                .childrenIds(new ArrayList<>(0))
                .stagesIds(new ArrayList<>(0))
                .build();
    }

    private GeneralSubprojectDto getExpectedDtoAfterCreate() {
        return GeneralSubprojectDto.builder()
                .subprojectId(ValuesForTest.DTO_INFO.getId())
                .name(ValuesForTest.DTO_INFO.getName())
                .description("Description")
                .ownerId(5L)
                .parentProjectId(ValuesForTest.PARENT.getId())
                .childrenIds(new ArrayList<>(0))
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .stagesIds(new ArrayList<>(0))
                .build();
    }
}


enum ValuesForTest {
    PARENT(1L, "Parent name"),
    CHILD1(10L, "Child 1"),
    CHILD2(11L, "Child 2"),
    DTO_INFO(100L, "Name subproject"),
    SUBPROJECT(11L, "Exists subproject");

    private final String name;
    private final Long id;

    ValuesForTest(Long id, String value) {
        this.id = id;
        this.name = value;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }
}