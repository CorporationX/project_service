package faang.school.projectservice.service;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.dto.SubProjectDto;
import faang.school.projectservice.dto.SubProjectsFilterDto;
import faang.school.projectservice.mapper.CreateSubProjectMapper;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.MomentType;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class SubProjectServiceTest {

    @Mock
    ProjectRepository projectRepository;

    @Mock
    MomentRepository momentRepository;

    @Mock
    StageRepository stageRepository;

    @Mock
    CreateSubProjectMapper createdSubProjectMapper;

    @Mock
    SubProjectMapper subProjectMapper;

    @InjectMocks
    SubProjectService subProjectService;

    private static Stream<Arguments> providedInvalidSubProjectInputs() {
        return Stream.of(
                Arguments.of(null, 1L, ProjectVisibility.PUBLIC, 10L, "Name project is required"),
                Arguments.of("test", null, ProjectVisibility.PUBLIC, 10L, "Owner is required"),
                Arguments.of("test", 1L, null, 10L, "Visibility project is required"),
                Arguments.of("test", 1L, ProjectVisibility.PUBLIC, null, "Parent project is required")
        );
    }

    @ParameterizedTest
    @MethodSource("providedInvalidSubProjectInputs")
    @DisplayName("Negative: error when required fields are missing")
    void testCrateNegativeMissingFields(String name, Long ownerId, ProjectVisibility visibility, Long parentProject, String expectedMessage) {
        CreateSubProjectDto subProjectDto = createSubProjectDto(name, ownerId, visibility, parentProject);

        assertException(() -> subProjectService.createSubProject(subProjectDto), IllegalArgumentException.class, expectedMessage);
    }

    @Test
    @DisplayName("Negative: error when parentProject value is missing")
    void testCreateNegativeNoParentProject() {
        CreateSubProjectDto subProjectDto = createSubProjectDto();

        when(projectRepository.findById(subProjectDto.getParentProject())).thenReturn(Optional.empty());

        assertException(() -> subProjectService.createSubProject(subProjectDto), EntityNotFoundException.class,
                String.format("Parent project with id = %d doesn't exist", subProjectDto.getParentProject()));
    }

    @Test
    @DisplayName("Negative: error when parentProject visibility is private and subProject visibility is public")
    void testCreateNegativeIfParentIsPrivate() {
        CreateSubProjectDto subProjectDto = createSubProjectDto();
        Project parentProject = new Project();
        parentProject.setVisibility(ProjectVisibility.PRIVATE);

        when(projectRepository.findById(subProjectDto.getParentProject())).thenReturn(Optional.of(parentProject));

        assertException(() -> subProjectService.createSubProject(subProjectDto), IllegalArgumentException.class,
                String.format("Project %s cannot be public", subProjectDto.getName()));
    }

    @Test
    @DisplayName("Positive: successful creation of subProject without children")
    void testCreateSuccess() {
        Project subProject = createProject();
        CreateSubProjectDto subProjectDto = createSubProjectDto();

        when(projectRepository.findById(subProjectDto.getParentProject())).thenReturn(Optional.of(new Project()));
        when(createdSubProjectMapper.toEntity(subProjectDto)).thenReturn(subProject);
        when(projectRepository.save(any(Project.class))).thenReturn(subProject);
        when(subProjectMapper.toDto(subProject)).thenReturn(createProjectDto());

        subProjectService.createSubProject(subProjectDto);

        verify(projectRepository, times(3)).save(any(Project.class));
        assertEquals(ProjectStatus.CREATED, subProject.getStatus());
    }

    @Test
    @DisplayName("Positive: successful creation of subProject with children")
    void testCreateSuccessWithChildren() {
        Project subProject = createProject();
        CreateSubProjectDto subProjectDto = createSubProjectDto();
        Stage stage = createStage();
        subProjectDto.setStages(List.of(stage.getStageName()));
        subProjectDto.setChildren(List.of(createSubProjectDto()));

        when(projectRepository.findById(subProjectDto.getParentProject())).thenReturn(Optional.of(new Project()));
        when(createdSubProjectMapper.toEntity(any(CreateSubProjectDto.class))).thenReturn(subProject);
        when(projectRepository.save(any(Project.class))).thenReturn(subProject);
        when(stageRepository.save(any(Stage.class))).thenReturn(stage);
        when(subProjectMapper.toDto(subProject)).thenReturn(createProjectDto());

        subProjectService.createSubProject(subProjectDto);

        verify(projectRepository, times(6)).save(any(Project.class));
        verify(stageRepository, times(1)).save(any(Stage.class));
        assertEquals(ProjectStatus.CREATED, subProject.getStatus());
        assertEquals(1, subProject.getStages().size());
        assertEquals(1, subProject.getChildren().size());
    }

    private static Stream<Arguments> providedInvalidProjectInputs() {
        return Stream.of(
                Arguments.of(null, 1L, ProjectVisibility.PUBLIC, ProjectStatus.CREATED, "Name project is required"),
                Arguments.of("test", null, ProjectVisibility.PUBLIC, ProjectStatus.CREATED, "Owner is required"),
                Arguments.of("test", 1L, null, ProjectStatus.CREATED, "Visibility project is required"),
                Arguments.of("test", 1L, ProjectVisibility.PUBLIC, null, "Project status is required")
        );
    }

    @ParameterizedTest
    @MethodSource("providedInvalidProjectInputs")
    void testUpdateNegativeMissingFields(String name, Long ownerId, ProjectVisibility visibility, ProjectStatus status, String expectedMessage) {
        SubProjectDto subProjectDto = createProjectDto(name, ownerId, visibility, status);

        assertException(() -> subProjectService.updateSubProject(subProjectDto), IllegalArgumentException.class, expectedMessage);
    }

    @Test
    @DisplayName("Negative: error when project value is missing")
    void testUpdateNoProject() {
        SubProjectDto subProjectDto = createProjectDto();

        when(projectRepository.findById(subProjectDto.getId())).thenReturn(Optional.empty());

        assertException(() -> subProjectService.updateSubProject(subProjectDto), EntityNotFoundException.class,
                String.format("Project with id = %d doesn't exist", subProjectDto.getId()));
    }

    @Test
    @DisplayName("Positive: successful update of project")
    void testUpdateSuccess() {
        Project project = createProject();
        SubProjectDto subProjectDto = createProjectDto();

        when(projectRepository.findById(subProjectDto.getId())).thenReturn(Optional.of(project));
        when(projectRepository.save(project)).thenReturn(project);
        when(subProjectMapper.toDto(project)).thenReturn(subProjectDto);

        subProjectService.updateSubProject(subProjectDto);

        assertEquals(ProjectStatus.CREATED, project.getStatus());
    }

    @Test
    @DisplayName("Negative: error when project is completed but subprojects are not completed")
    void testUpdateNegativeSubProjectNotCompleted() {
        Project project = createProject();
        Project subProject = createSubProject(ProjectStatus.IN_PROGRESS);
        project.setChildren(List.of(subProject));

        SubProjectDto subProjectDto = createProjectDto(ProjectStatus.COMPLETED);

        when(projectRepository.findById(subProjectDto.getId())).thenReturn(Optional.of(project));

        assertException(() -> subProjectService.updateSubProject(subProjectDto), IllegalArgumentException.class,
                "Not all subprojects are completed");
    }

    @Test
    @DisplayName("Positive: successful update check create moment")
    void testUpdateSuccessCreateMoment() {
        Project project = createProject();
        Project subProject = createSubProject(ProjectStatus.COMPLETED);
        project.setChildren(List.of(subProject));

        SubProjectDto subProjectDto = createProjectDto(ProjectStatus.COMPLETED);

        when(projectRepository.findById(subProjectDto.getId())).thenReturn(Optional.of(project));
        when(momentRepository.save(any(Moment.class))).thenReturn(new Moment());
        when(projectRepository.save(project)).thenReturn(project);
        when(subProjectMapper.toDto(project)).thenReturn(subProjectDto);

        subProjectService.updateSubProject(subProjectDto);

        ArgumentCaptor<Moment> argumentCaptor = ArgumentCaptor.forClass(Moment.class);
        verify(momentRepository, times(1)).save(argumentCaptor.capture());
        Moment captureMoment = argumentCaptor.getValue();

        assertEquals(project.getName(), captureMoment.getName());
        assertEquals(MomentType.COMPLETED.getDescription(), captureMoment.getDescription());
    }

    @Test
    @DisplayName("Positive: successful update subprojects visibility")
    void testUpdateSuccessVisibility() {
        Project project = createProject();
        Project subProject = createSubProject(ProjectVisibility.PUBLIC);
        project.setChildren(List.of(subProject));

        SubProjectDto subProjectDto = createProjectDto(ProjectVisibility.PRIVATE);

        when(projectRepository.findById(subProjectDto.getId())).thenReturn(Optional.of(project));
        when(projectRepository.save(subProject)).thenReturn(subProject);
        when(projectRepository.save(project)).thenReturn(project);
        when(subProjectMapper.toDto(project)).thenReturn(subProjectDto);

        subProjectService.updateSubProject(subProjectDto);

        verify(projectRepository, times(2)).save(any(Project.class));
        assertEquals(project.getVisibility(), subProject.getVisibility());
    }

    @Test
    @DisplayName("Negative: error when projectId is null")
    void testGetSubProjectsNegativeNoProject() {
        SubProjectsFilterDto filterDto = createFilter(null, "test", ProjectStatus.CREATED);

        assertException(() -> subProjectService.getSubProjects(filterDto), IllegalArgumentException.class, "Project id is required");
    }

    @Test
    @DisplayName("Negative: error when project doesn't exist")
    void testGetSubProjectsNegativeNoProjectNotFound() {
        SubProjectsFilterDto filterDto = createFilter(10L, "test", ProjectStatus.CREATED);

        when(projectRepository.findById(filterDto.projectId())).thenReturn(Optional.empty());

        assertException(() -> subProjectService.getSubProjects(filterDto), EntityNotFoundException.class,
                String.format("Project with id = %d doesn't exist", filterDto.projectId()));
    }

    @Test
    @DisplayName("Positive: when project has no subprojects")
    void testGetSubProjectsPositiveNoChildren() {
        SubProjectsFilterDto filterDto = createFilter(10L, "test", ProjectStatus.CREATED);
        Project project = createProject();
        project.setChildren(new ArrayList<>());

        when(projectRepository.findById(filterDto.projectId())).thenReturn(Optional.of(project));

        List<SubProjectDto> subProjects = subProjectService.getSubProjects(filterDto);

        assertEquals(0, subProjects.size());
    }

    private void assertException(Executable executable, Class<? extends Exception> expectedException, String expectedMessage) {
        var exception = assertThrows(expectedException, executable);

        assertEquals(exception.getMessage(), expectedMessage);
    }

    private CreateSubProjectDto createSubProjectDto(String name, Long ownerId, ProjectVisibility visibility, Long parentProject) {
        return CreateSubProjectDto.builder()
                .name(name)
                .ownerId(ownerId)
                .visibility(visibility)
                .parentProject(parentProject)
                .build();
    }

    private SubProjectDto createProjectDto(String name, Long ownerId, ProjectVisibility visibility, ProjectStatus status) {
        return SubProjectDto.builder()
                .name(name)
                .ownerId(ownerId)
                .visibility(visibility)
                .status(status)
                .build();
    }

    private SubProjectDto createProjectDto() {
        return SubProjectDto.builder()
                .name("test")
                .ownerId(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.CREATED)
                .build();
    }

    private SubProjectDto createProjectDto(ProjectStatus status) {
        SubProjectDto subProjectDto = createProjectDto();
        subProjectDto.setStatus(status);
        return subProjectDto;
    }

    private SubProjectDto createProjectDto(ProjectVisibility visibility) {
        SubProjectDto subProjectDto = createProjectDto();
        subProjectDto.setVisibility(visibility);
        return subProjectDto;
    }

    private CreateSubProjectDto createSubProjectDto() {
        return CreateSubProjectDto.builder()
                .name("test")
                .ownerId(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .parentProject(10L)
                .build();
    }

    private Project createProject() {
        return Project.builder()
                .id(10L)
                .name("test")
                .ownerId(1L)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private Project createSubProject(ProjectStatus status) {
        Project subProject = new Project();
        subProject.setStatus(status);
        return subProject;
    }

    private Project createSubProject(ProjectVisibility visibility) {
        Project subProject = new Project();
        subProject.setVisibility(visibility);
        return subProject;
    }

    private Stage createStage() {
        Stage stage = new Stage();
        stage.setStageName("test");
        return stage;
    }

    private SubProjectsFilterDto createFilter(Long id, String name, ProjectStatus status) {
        return new SubProjectsFilterDto(id, name, status);
    }
}
