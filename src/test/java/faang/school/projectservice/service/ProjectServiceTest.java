package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.ProjectFilter;
import faang.school.projectservice.validator.ProjectDtoValidator;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    private ProjectService projectService;
    private ProjectDtoValidator projectDtoValidator;
    private ProjectRepository projectRepository;
    private ProjectMapperImpl projectMapper;
    private List<ProjectFilter> filters;

    private static final long ID = 1;
    private static final String PROJECT_NAME = "name";
    private static final String PROJECT_DESCRIPTION = "description";

    @Nested
    class PositiveTests {

        @BeforeEach
        public void init() {
            projectDtoValidator = Mockito.mock(ProjectDtoValidator.class);
            projectRepository = Mockito.mock(ProjectRepository.class);
            projectMapper = Mockito.mock(ProjectMapperImpl.class);
            ProjectFilter projectFilter = Mockito.mock(ProjectFilter.class);
            filters = List.of(projectFilter);

            projectService = new ProjectService(projectDtoValidator, projectRepository, filters, projectMapper);
        }

        @Test
        @DisplayName("Успешное создание проекта")
        public void testCreateWithSaveProject() {
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
            verify(projectDtoValidator, times(1))
                    .validateIfProjectNameOrDescriptionIsBlank(projectDto);
            verify(projectDtoValidator, times(1))
                    .validateIfOwnerAlreadyExistProjectWithName(projectDto);
            verify(projectRepository, times(1)).save(projectEntity);
        }

        @Test
        @DisplayName("Успешное обновление описания и статуса проекта")
        public void testUpdateWithSaveUpdatedProject() {
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
            verify(projectDtoValidator, times(1)).validateIfProjectIsExistInDb(projectDto.getId());
            verify(projectRepository, times(1)).getProjectById(projectDto.getId());
            verify(projectDtoValidator, times(1))
                    .validateIfDtoContainsExistedProjectStatus(projectDto.getStatus());
            verify(projectRepository, times(1)).save(projectEntity);
        }

        @Test
        @DisplayName("Успешное получение всех проектов")
        public void testGetAllProjectIsSuccessful() {
            List<Project> projects = List.of(new Project(), new Project());
            List<ProjectDto> projectDtos = List.of(new ProjectDto(), new ProjectDto());
            when(projectRepository.findAll()).thenReturn(projects);
            when(projectMapper.toDtos(projects)).thenReturn(projectDtos);

            List<ProjectDto> resultProjectDtos = projectService.getAllProject();

            assertNotNull(resultProjectDtos);
            assertEquals(2, resultProjectDtos.size());
            verify(projectRepository, times(1)).findAll();
            verify(projectMapper, times(1)).toDtos(projects);
        }

        @Test
        @DisplayName("Успешное получение проекта")
        public void testGetProjectIsSuccessful() {
            Project projectEntity = new Project();
            projectEntity.setId(ID);
            ProjectDto projectDto = new ProjectDto();
            projectDto.setId(ID);
            when(projectRepository.getProjectById(ID)).thenReturn(projectEntity);
            when(projectMapper.toDto(projectEntity)).thenReturn(projectDto);

            ProjectDto existedProjectDto = projectService.getProject(ID);

            assertNotNull(existedProjectDto);
            assertEquals(ID, existedProjectDto.getId());
            verify(projectDtoValidator, times(1)).validateIfProjectIsExistInDb(ID);
            verify(projectRepository, times(1)).getProjectById(ID);
        }

        @Test
        @DisplayName("Успешное получение проекта ")
        public void testGetProjectByNameAndStatusIsSuccessful() {
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

            assertEquals(2, projectDtos.size());
            verify(projectDtoValidator, times(1))
                    .validateIfDtoContainsExistedProjectStatus(firstDto.getStatus());
            verify(projectRepository, times(1)).findAll();
            verify(projectMapper).toDto(first);
        }
    }
}