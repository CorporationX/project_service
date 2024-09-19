package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.validator.project.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    private ProjectService projectService;
    private ProjectValidator projectDtoValidator;
    private ProjectRepository projectRepository;
    private ProjectMapper projectMapper;
    private List<ProjectFilter> filters;

    private static final long ID = 1L;
    private static final String PROJECT_NAME = "name";
    private static final String PROJECT_DESCRIPTION = "description";
    private static final int PROJECT_DTOS_SIZE = 2;

    @Nested
    class PositiveTests {

        @BeforeEach
        public void init() {
            projectDtoValidator = Mockito.mock(ProjectValidator.class);
            projectRepository = Mockito.mock(ProjectRepository.class);
            projectMapper = Mockito.mock(ProjectMapper.class);
            ProjectFilter projectFilter = Mockito.mock(ProjectFilter.class);
            filters = List.of(projectFilter);

            projectService = new ProjectService(projectDtoValidator, projectRepository, filters, projectMapper);
        }

        @Test
        @DisplayName("Successful project creation")
        public void whenCreateThenSaveProject() {
            ProjectDto projectDto = new ProjectDto();
            projectDto.setOwnerId(ID);
            projectDto.setName(PROJECT_NAME);
            projectDto.setDescription(PROJECT_DESCRIPTION);

            Project projectEntity = new Project();
            projectEntity.setId(ID);

            when(projectMapper.toEntity(projectDto)).thenReturn(projectEntity);
            when(projectRepository.save(projectEntity)).thenReturn(projectEntity);
            when(projectMapper.toDto(projectEntity)).thenReturn(projectDto);

            ProjectDto createdProjectDto = projectService.create(projectDto);

            assertNotNull(createdProjectDto);
            assertEquals(PROJECT_NAME, createdProjectDto.getName());
            assertEquals(PROJECT_DESCRIPTION, createdProjectDto.getDescription());
            assertEquals(ProjectStatus.CREATED, createdProjectDto.getStatus());
            verify(projectDtoValidator).validateProject(projectDto);
            verify(projectRepository).save(projectEntity);
        }

        @Test
        @DisplayName("Successful project description and status update")
        public void whenUpdateThenSaveProject() {
            ProjectDto projectDto = new ProjectDto();
            projectDto.setId(ID);
            projectDto.setDescription(PROJECT_DESCRIPTION);
            projectDto.setStatus(ProjectStatus.IN_PROGRESS);

            Project projectEntity = new Project();
            projectEntity.setId(ID);

            when(projectRepository.getProjectById(projectDto.getId())).thenReturn(projectEntity);
            when(projectRepository.save(projectEntity)).thenReturn(projectEntity);
            when(projectMapper.toDto(projectEntity)).thenReturn(projectDto);

            ProjectDto updatedProjectDto = projectService.update(projectDto);

            assertNotNull(updatedProjectDto);
            assertEquals(PROJECT_DESCRIPTION, updatedProjectDto.getDescription());
            assertEquals(ProjectStatus.IN_PROGRESS, updatedProjectDto.getStatus());
            verify(projectRepository).getProjectById(projectDto.getId());
            verify(projectRepository).save(projectEntity);
            verify(projectDtoValidator).validateUpdatedFields(projectDto);
        }

        @Test
        @DisplayName("Successful retrieval of all projects")
        public void whenGetAllProjectsThenSuccess() {
            List<Project> projects = List.of(new Project(), new Project());
            List<ProjectDto> projectDtos = List.of(new ProjectDto(), new ProjectDto());
            when(projectRepository.findAll()).thenReturn(projects);
            when(projectMapper.toDtos(projects)).thenReturn(projectDtos);

            List<ProjectDto> resultProjectDtos = projectService.getAllProject();

            assertNotNull(resultProjectDtos);
            assertEquals(PROJECT_DTOS_SIZE, resultProjectDtos.size());
            verify(projectRepository).findAll();
            verify(projectMapper).toDtos(projects);
        }

        @Test
        @DisplayName("Successful retrieval of a project by ID")
        public void whenGetProjectThenSuccess() {
            Project projectEntity = new Project();
            projectEntity.setId(ID);
            ProjectDto projectDto = new ProjectDto();
            projectDto.setId(ID);
            when(projectRepository.getProjectById(ID)).thenReturn(projectEntity);
            when(projectMapper.toDto(projectEntity)).thenReturn(projectDto);

            ProjectDto existedProjectDto = projectService.getProject(ID);

            assertNotNull(existedProjectDto);
            assertEquals(ID, existedProjectDto.getId());
            verify(projectRepository).getProjectById(ID);
        }

        @Test
        @DisplayName("Successful project retrieval by filter")
        public void whenGetProjectByFilterThenSuccess() {
            ProjectFilterDto projectFilterDto = new ProjectFilterDto();
            projectFilterDto.setStatus(ProjectStatus.CREATED);

            ProjectDto firstDto = new ProjectDto();
            firstDto.setStatus(ProjectStatus.CREATED);
            firstDto.setName("name");
            ProjectDto secondDto = new ProjectDto();
            secondDto.setStatus(ProjectStatus.CREATED);
            secondDto.setName("secondName");

            Project first = new Project();
            first.setName("name");
            first.setStatus(ProjectStatus.CREATED);
            Project second = new Project();
            second.setName("secondName");
            second.setStatus(ProjectStatus.CREATED);

            when(projectRepository.findAll()).thenReturn(List.of(first, second));
            when(projectMapper.toDto(first)).thenReturn(firstDto);
            when(filters.get(0).isApplicable(projectFilterDto)).thenReturn(true);
            when(filters.get(0).apply(any(Stream.class), eq(projectFilterDto))).thenReturn(Stream.of(first, second));

            List<ProjectDto> projectDtos = projectService.getProjectByNameAndStatus(projectFilterDto);

            assertEquals(PROJECT_DTOS_SIZE, projectDtos.size());
            verify(projectRepository).findAll();
            verify(projectMapper).toDto(first);
        }
    }
}
