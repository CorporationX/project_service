package faang.school.projectservice.service.projectService;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.filter.project.NameFilter;
import faang.school.projectservice.filter.project.StatusFilter;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.project.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    private ProjectRepository projectRepository;
    private UserServiceClient userServiceClient;
    private UserContext userContext;
    private List<Filter<Project, ProjectFilterDto>> filters;
    @Spy
    private ProjectMapperImpl projectMapper;
    private ProjectService projectService;
    @Captor
    private ArgumentCaptor<Project> captor;

    Filter<Project, ProjectFilterDto> nameFilter;
    Filter<Project, ProjectFilterDto> statusFilter;
    private ProjectDto filledProjectDto;
    private Project filledProjectEntity;
    private final long OWNER_ACCESSED_ID = 1L;
    private final long OWNER_NOT_ACCESSED_ID = 2L;

    @BeforeEach
    public void setUp() {
        long FILLED_PROJECT_ID = 5L;
        filledProjectDto = new ProjectDto();
        filledProjectDto.setId(FILLED_PROJECT_ID);
        filledProjectDto.setOwnerId(OWNER_ACCESSED_ID);
        filledProjectDto.setName("project name");
        filledProjectDto.setDescription("project description");
        filledProjectDto.setStatus(ProjectStatus.CREATED);
        filledProjectDto.setVisibility(ProjectVisibility.PUBLIC);

        filledProjectEntity = new Project();
        filledProjectEntity.setId(FILLED_PROJECT_ID);
        filledProjectEntity.setOwnerId(OWNER_ACCESSED_ID);
        filledProjectEntity.setName("project name");
        filledProjectEntity.setDescription("project description");
        filledProjectEntity.setStatus(ProjectStatus.CREATED);
        filledProjectEntity.setVisibility(ProjectVisibility.PUBLIC);

        projectRepository = Mockito.mock(ProjectRepository.class);
        userServiceClient = Mockito.mock(UserServiceClient.class);
        userContext = Mockito.mock(UserContext.class);
        userContext = Mockito.mock(UserContext.class);
        nameFilter = Mockito.mock(NameFilter.class);
        statusFilter = Mockito.mock(StatusFilter.class);
        filters = new ArrayList<>(List.of(nameFilter, statusFilter));
        ProjectValidator projectValidator = new ProjectValidator(projectRepository, userServiceClient, userContext);

        projectService = new ProjectService(projectRepository, projectMapper, filters, projectValidator);
    }

    @Test
    public void testCreate_noAccessToProject_throwsSecurityException() {
        accessAllowed(false);

        assertThrows(
                SecurityException.class,
                () -> projectService.create(filledProjectDto)
        );
    }

    @Test
    public void testCreate_ownerUserNotExist_throwsEntityNotFoundException() {
        accessAllowed(true);
        userFromDatabase(null);

        assertThrows(
                EntityNotFoundException.class,
                () -> projectService.create(filledProjectDto)
        );
    }

    @Test
    public void testCreate_projectNameAlreadyExists_throwsIllegalArgumentException() {
        accessAllowed(true);
        userFromDatabase(new UserDto());
        projectNameExists(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> projectService.create(filledProjectDto)
        );
    }

    @Test
    public void testCreate_nameNotValid_throwsIllegalArgumentException() {
        filledProjectDto.setName("   ");
        validated();

        assertThrows(
                IllegalArgumentException.class,
                () -> projectService.create(filledProjectDto)
        );
    }

    @Test
    public void testCreate_descriptionNotValid_throwsIllegalArgumentException() {
        filledProjectDto.setDescription("   ");
        validated();

        assertThrows(
                IllegalArgumentException.class,
                () -> projectService.create(filledProjectDto)
        );
    }

    @Test
    public void testCreate_validProjectDto_projectSaved() {
        validated();

        projectService.create(filledProjectDto);

        verify(projectRepository, times(1)).save(captor.capture());

        Project savedProject = captor.getValue();
        assertNotNull(savedProject);
        assertEquals(savedProject.getStatus(), filledProjectDto.getStatus());
        assertEquals(savedProject.getVisibility(), filledProjectDto.getVisibility());
    }

    @Test
    public void testGetById_notValidAccessToProject_throwsSecurityException() {
        accessAllowed(false);

        assertThrows(
                SecurityException.class,
                () -> projectService.create(filledProjectDto)
        );
    }

    @Test
    public void testGetById_ValidAccess_returnsProject() {
        accessAllowed(true);
        when(projectRepository.getProjectById(OWNER_ACCESSED_ID)).thenReturn(filledProjectEntity);

        ProjectDto expectedProjectDto = projectService.getById(OWNER_ACCESSED_ID);

        assertEquals(expectedProjectDto, filledProjectDto);
    }

    @Test
    public void testGetAll_noProjectsFromDB_returnsEmptyList() {
        when(projectRepository.findAll()).thenReturn(Collections.emptyList());
        List<ProjectDto> all = projectService.getAll();
        assertEquals(Collections.emptyList(), all);
    }

    @Test
    public void testGetAll_containsPublicAndOwnProjects_returnsOnlyVisibleProjects() {
        List<ProjectDto> expected = projectMapper.entitiesToDtos(getVisibleProjectsSim());

        List<ProjectDto> returnedProjects = projectService.getAll();

        assertEquals(expected, returnedProjects);
    }

    @Test
    public void testGetAllWithFilters_notFilledFilterDto_returnsAllVisibleProjects() {
        ProjectFilterDto notFilledFilterDto = new ProjectFilterDto();
        when(nameFilter.isApplicable(any())).thenReturn(false);
        List<ProjectDto> expected = projectMapper.entitiesToDtos(getVisibleProjectsSim());

        List<ProjectDto> returnedProjectDtos = projectService.getAll(notFilledFilterDto);

        assertEquals(expected, returnedProjectDtos);
    }

    @Test
    public void testGetAllWithFilters_filledNameFilterDto_returnsFilteredDtos() {
        ProjectFilterDto filterDto = new ProjectFilterDto();
        String subString = "Industry";
        filterDto.setName(subString);
        Stream<Project> allVisibleProjects = getVisibleProjectsSim().stream();

        when(filters.get(0).isApplicable(filterDto)).thenReturn(true);
        when(filters.get(1).isApplicable(filterDto)).thenReturn(false);
        when(filters.get(0).apply(any(), any())).thenReturn(
                allVisibleProjects.filter(prj -> prj.getName().contains(filterDto.getName())));

        List<ProjectDto> filteredProjectDtos = projectService.getAll(filterDto);

        assertEquals(1, filteredProjectDtos.size());
        assertTrue(filteredProjectDtos.get(0).getName().contains(subString));
    }

    private List<Project> getVisibleProjectsSim() {
        Project publicAndNotOwnPrj = new Project();
        publicAndNotOwnPrj.setName("Space X Project");
        publicAndNotOwnPrj.setStatus(ProjectStatus.CREATED);
        publicAndNotOwnPrj.setOwnerId(OWNER_NOT_ACCESSED_ID);
        publicAndNotOwnPrj.setVisibility(ProjectVisibility.PUBLIC);
        Project privateAndOwnPrj = new Project();
        privateAndOwnPrj.setName("Apple Industry");
        privateAndOwnPrj.setStatus(ProjectStatus.COMPLETED);
        privateAndOwnPrj.setOwnerId(OWNER_ACCESSED_ID);
        privateAndOwnPrj.setVisibility(ProjectVisibility.PRIVATE);
        Project privateAndNotOwnPrj = new Project();
        privateAndNotOwnPrj.setName("Faang School Industry");
        privateAndNotOwnPrj.setStatus(ProjectStatus.COMPLETED);
        privateAndNotOwnPrj.setOwnerId(OWNER_NOT_ACCESSED_ID);
        privateAndNotOwnPrj.setVisibility(ProjectVisibility.PRIVATE);

        List<Project> projects = List.of(publicAndNotOwnPrj, privateAndOwnPrj, privateAndNotOwnPrj);

        when(projectRepository.findAll()).thenReturn(projects);
        accessAllowed(true);

        return List.of(publicAndNotOwnPrj, privateAndOwnPrj);
    }

    private void validated() {
        accessAllowed(true);
        userFromDatabase(new UserDto());
        projectNameExists(false);
    }

    private void projectNameExists(boolean isExist) {
        when(projectRepository.existsByOwnerUserIdAndName(OWNER_ACCESSED_ID, filledProjectDto.getName())).thenReturn(isExist);
    }

    private void userFromDatabase(UserDto userFromDatabase) {
        when(userServiceClient.getUser(OWNER_ACCESSED_ID)).thenReturn(userFromDatabase);
    }

    private void accessAllowed(boolean isAllowed) {
        if (isAllowed) {
            when(userContext.getUserId()).thenReturn(OWNER_ACCESSED_ID);
        }
        if (!isAllowed) {
            when(userContext.getUserId()).thenReturn(OWNER_NOT_ACCESSED_ID);
        }
    }
}