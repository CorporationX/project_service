package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.filter.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.publisher.ProjectViewEventPublisher;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.project.ProjectValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;
    @Mock
    private ProjectValidator projectDtoValidator;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private List<Filter<ProjectFilterDto, Project>> filters;
    @Mock
    private Filter<ProjectFilterDto, Project> projectFilter;
    @Mock
    private UserContext userContext;
    @Mock
    private ProjectViewEventPublisher projectViewEventPublisher;

    private static final long ID = 1L;
    private static final long FIVE_SECOND_AWAIT = 5L;

    private static final String PROJECT_NAME = "name";
    private static final String PROJECT_DESCRIPTION = "description";

    private static final int PROJECT_DTOS_SIZE = 2;

    @Nested
    class PositiveTests {

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
            verify(projectRepository).save(projectEntity);
        }

        @Test
        @DisplayName("Successful project description and status updateCampaign")
        public void whenUpdateThenSaveProject() {
            ProjectDto projectDto = new ProjectDto();
            projectDto.setId(ID);
            projectDto.setDescription(PROJECT_DESCRIPTION);
            projectDto.setStatus(ProjectStatus.IN_PROGRESS);

            Project projectEntity = new Project();
            projectEntity.setId(ID);

            when(projectRepository.findById(projectDto.getId())).thenReturn(Optional.of(projectEntity));
            when(projectRepository.save(projectEntity)).thenReturn(projectEntity);
            when(projectMapper.toDto(projectEntity)).thenReturn(projectDto);

            ProjectDto updatedProjectDto = projectService.update(projectDto);

            assertNotNull(updatedProjectDto);
            assertEquals(PROJECT_DESCRIPTION, updatedProjectDto.getDescription());
            assertEquals(ProjectStatus.IN_PROGRESS, updatedProjectDto.getStatus());
            verify(projectRepository).findById(projectDto.getId());
            verify(projectRepository).save(projectEntity);
        }

        @Test
        @DisplayName("Successful retrieval of all projects")
        public void whenGetAllProjectsThenSuccess() {
            List<Project> projects = List.of(new Project(), new Project());
            List<ProjectDto> projectDtos = List.of(new ProjectDto(), new ProjectDto());
            when(projectRepository.findAll()).thenReturn(projects);
            when(projectMapper.toDtos(projects)).thenReturn(projectDtos);

            List<ProjectDto> resultProjectDtos = projectService.getAllProjectDto();

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
            when(projectRepository.findById(ID)).thenReturn(Optional.of(projectEntity));
            when(projectMapper.toDto(projectEntity)).thenReturn(projectDto);
            when(userContext.getUserId()).thenReturn(ID);

            ProjectDto existedProjectDto = projectService.getProject(ID);

            assertNotNull(existedProjectDto);
            assertEquals(ID, existedProjectDto.getId());
            verify(projectRepository).findById(ID);

            await().atMost(FIVE_SECOND_AWAIT, SECONDS).untilAsserted(() -> {
                verify(projectViewEventPublisher).publish(any());
            });
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

            when(projectFilter.isApplicable(projectFilterDto)).thenReturn(true);
            when(projectFilter.applyFilter(any(Stream.class), eq(projectFilterDto))).thenReturn(Stream.of(first, second));
            when(filters.stream()).thenReturn(Stream.of(projectFilter));

            List<ProjectDto> projectDtos = projectService.getProjectByNameAndStatus(projectFilterDto);

            assertEquals(PROJECT_DTOS_SIZE, projectDtos.size());
            verify(projectRepository).findAll();
            verify(projectMapper).toDto(first);
        }
    }
}
