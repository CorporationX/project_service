package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    private static final long OWNER_ID = 1L;
    private static final long PARENT_PROJECT_ID = 100L;
    private static final long PROJECT_ID = 1000L;
    private static final String PROJECT_NAME = "Test Project";
    private static final String PROJECT_DESCRIPTION = "Test Description";
    private static final BigInteger MAX_STORAGE_SIZE = BigInteger.valueOf(1000L);
    private static final String COVER_IMAGE_ID = "test-image-id";

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ProjectMapperImpl projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    public void shouldCreateProjectSuccessfully() {
        ProjectCreateDto dto = createValidProjectCreateDto();

        Project project = createModelFromCreateDto();
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectDto result = projectService.create(dto);

        verify(projectRepository).save(any(Project.class));

        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getOwnerId(), result.getOwnerId());
        assertEquals(dto.getVisibility(), result.getVisibility());
        assertEquals(ProjectStatus.CREATED, dto.getStatus());
    }

    @Test
    public void shouldThrowExceptionWhenParentProjectDoesNotExist() {
        ProjectCreateDto dto = createValidProjectCreateDto();
        dto.setParentProjectId(PARENT_PROJECT_ID);
        when(projectRepository.existsById(PARENT_PROJECT_ID)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> projectService.create(dto));
    }

    @Test
    public void shouldThrowExceptionWhenProjectAlreadyExists() {
        ProjectCreateDto dto = createValidProjectCreateDto();
        when(projectRepository.existsByOwnerIdAndName(dto.getOwnerId(), dto.getName())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> projectService.create(dto));
    }

    @Test
    public void shouldUpdateProjectSuccessfully() {
        ProjectUpdateDto updateDto = createValidProjectUpdateDto();
        updateDto.setName(null);
        updateDto.setDescription(null);

        Project project = createModelFromCreateDto();
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        projectService.update(updateDto, PROJECT_ID);

        verify(projectRepository).findById(PROJECT_ID);
        verify(projectMapper).updateModel(updateDto, project);

        assertNotNull(project.getName());
        assertNotNull(project.getDescription());
        assertEquals(updateDto.getMaxStorageSize(), project.getMaxStorageSize());
        assertEquals(updateDto.getStatus(), project.getStatus());
        assertEquals(updateDto.getVisibility(), project.getVisibility());
        assertEquals(updateDto.getCoverImageId(), project.getCoverImageId());
    }

    @Test
    public void shouldThrowExceptionIfProjectCanNotBeModified() {
        Project project = createModelFromCreateDto();
        project.setStatus(ProjectStatus.CANCELLED);
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        ProjectUpdateDto updateDto = createValidProjectUpdateDto();
        updateDto.setStatus(ProjectStatus.COMPLETED);

        assertThrows(IllegalArgumentException.class, () -> projectService.update(updateDto, PROJECT_ID));
    }

    @Test
    public void shouldThrowExceptionIfMaxStorageGreaterThanCurrentUsage() {
        Project project = createModelFromCreateDto();
        project.setStorageSize(MAX_STORAGE_SIZE);
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        ProjectUpdateDto updateDto = createValidProjectUpdateDto();
        updateDto.setMaxStorageSize(MAX_STORAGE_SIZE.subtract(BigInteger.ONE));

        assertThrows(IllegalArgumentException.class, () -> projectService.update(updateDto, PROJECT_ID));
    }

    @Test
    public void shouldReturnAllWhenNoFiltersApplied() {
        List<ProjectDto> result = executeFilter(filterDto -> {
        });

        assertNotNull(result);
        assertEquals(9, result.size());
    }

    @Test
    public void shouldFilterByName() {
        List<ProjectDto> result = executeFilter(filterDto ->
                filterDto.setName("Project 1"));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.stream()
                .allMatch(dto -> dto.getName().equals("Project 1"))
        );
    }

    @Test
    public void shouldFilterByOwnerId() {
        List<ProjectDto> result = executeFilter(filterDto ->
                filterDto.setOwnerId(OWNER_ID + 1));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.stream()
                .allMatch(dto -> dto.getOwnerId() == OWNER_ID + 1)
        );
    }

    @Test
    public void shouldFilterByParentProjectId() {
        List<ProjectDto> result = executeFilter(filterDto ->
                filterDto.setParentProjectId(PARENT_PROJECT_ID));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.stream()
                .allMatch(dto -> dto.getParentProjectId() == PARENT_PROJECT_ID)
        );
    }

    @Test
    public void shouldFilterByStatus() {
        List<ProjectDto> result = executeFilter(filterDto ->
                filterDto.setStatus(ProjectStatus.CANCELLED));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.stream()
                .allMatch(dto -> dto.getStatus() == ProjectStatus.CANCELLED)
        );
    }

    @Test
    public void shouldFilterByVisibility() {
        List<ProjectDto> result = executeFilter(filterDto ->
                filterDto.setVisibility(ProjectVisibility.PRIVATE));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream()
                .allMatch(dto -> dto.getVisibility() == ProjectVisibility.PRIVATE)
        );
    }

    @Test
    public void shouldFilterByCreatedAfter() {
        List<ProjectDto> result = executeFilter(filterDto ->
                filterDto.setCreatedAfter(LocalDateTime.of(2025, 12, 10, 21, 40)));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.stream()
                .allMatch(dto -> dto.getCreatedAt().isAfter(LocalDateTime.of(2025, 12, 10, 21, 40)))
        );
    }

    @Test
    public void shouldFilterByCreatedBefore() {
        List<ProjectDto> result = executeFilter(filterDto ->
                filterDto.setCreatedBefore(LocalDateTime.of(2025, 12, 10, 21, 40)));

        assertNotNull(result);
        assertEquals(8, result.size());
        assertTrue(result.stream()
                .allMatch(dto -> dto.getCreatedAt().isBefore(LocalDateTime.of(2025, 12, 10, 21, 40)))
        );
    }

    private List<ProjectDto> executeFilter(Consumer<ProjectFilterDto> filterConfigurator) {
        ProjectFilterDto filters = createDefaultFilters();
        filterConfigurator.accept(filters);

        Page<Project> projectPage = prepareProjectsForTests();
        when(projectRepository.findAll(any(Pageable.class))).thenReturn(projectPage);

        return projectService.getByFilter(filters);
    }

    private Page<Project> prepareProjectsForTests() {
        Project project1 = createModelFromCreateDto();
        project1.setName("Project 1");
        Project project2 = createModelFromCreateDto();
        project2.setName("Project 2");
        Project project3 = createModelFromCreateDto();
        project3.setOwnerId(OWNER_ID + 1);
        Project project4 = createModelFromCreateDto();
        project4.setParentProject(Project.builder().id(PARENT_PROJECT_ID).build());
        Project project5 = createModelFromCreateDto();
        project5.setVisibility(ProjectVisibility.PRIVATE);
        Project project6 = createModelFromCreateDto();
        project6.setVisibility(ProjectVisibility.PRIVATE);
        Project project7 = createModelFromCreateDto();
        project7.setStatus(ProjectStatus.CANCELLED);
        Project project8 = createModelFromCreateDto();
        project8.setCreatedAt(LocalDateTime.of(2024, 12, 10, 21, 40));
        Project project9 = createModelFromCreateDto();
        project9.setCreatedAt(LocalDateTime.of(2026, 12, 10, 21, 40));
        return new PageImpl<>(List.of(project1, project2, project3,
                project4, project5, project6,
                project7, project8, project9));
    }

    private ProjectCreateDto createValidProjectCreateDto() {
        return ProjectCreateDto.builder()
                .name(PROJECT_NAME)
                .description(PROJECT_DESCRIPTION)
                .status(ProjectStatus.CREATED)
                .ownerId(OWNER_ID)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private ProjectUpdateDto createValidProjectUpdateDto() {
        return ProjectUpdateDto.builder()
                .name(PROJECT_NAME)
                .description(PROJECT_DESCRIPTION)
                .maxStorageSize(MAX_STORAGE_SIZE)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .coverImageId(COVER_IMAGE_ID)
                .build();
    }

    private Project createModelFromCreateDto() {
        ProjectCreateDto dto = createValidProjectCreateDto();
        return Project.builder()
                .id(PROJECT_ID)
                .name(dto.getName())
                .description(dto.getDescription())
                .maxStorageSize(dto.getMaxStorageSize())
                .ownerId(dto.getOwnerId())
                .status(dto.getStatus())
                .visibility(dto.getVisibility())
                .coverImageId(dto.getCoverImageId())
                .createdAt(LocalDateTime.of(2025, 11, 10, 21, 40))
                .build();
    }

    private ProjectFilterDto createDefaultFilters() {
        return ProjectFilterDto.builder()
                .page(0)
                .size(10)
                .sortBy("id")
                .sortDirection("ASC")
                .build();
    }
}
