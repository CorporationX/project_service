package faang.school.projectservice.service.projectService;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private ProjectMapperImpl projectMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private UserContext userContext;
    @Mock
    private List<Filter<Project, ProjectFilterDto>> filters;
    @InjectMocks
    private ProjectService projectService;
    private ProjectDto correctProjectDto;
    private Project correctProjectEntity;
    private final long CORRECT_PROJECT_ID = 5L;
    private final long OWNER_ACCESSED_ID = 1L;
    private final long OWNER_NOT_ACCESSED_ID = 2L;

    @BeforeEach
    public void setUp() {
        correctProjectDto = new ProjectDto();
        correctProjectDto.setId(CORRECT_PROJECT_ID);
        correctProjectDto.setOwnerId(OWNER_ACCESSED_ID);
        correctProjectDto.setName("project name");
        correctProjectDto.setDescription("project description");
        correctProjectDto.setStatus(ProjectStatus.CREATED);
        correctProjectDto.setVisibility(ProjectVisibility.PUBLIC);

        correctProjectEntity = new Project();
        correctProjectEntity.setId(CORRECT_PROJECT_ID);
        correctProjectEntity.setOwnerId(OWNER_ACCESSED_ID);
        correctProjectEntity.setName("project name");
        correctProjectEntity.setDescription("project description");
        correctProjectEntity.setStatus(ProjectStatus.CREATED);
        correctProjectEntity.setVisibility(ProjectVisibility.PUBLIC);
    }

    @Test
    public void testCreate_noAccessToProject_throwsSecurityException() {
        accessAllowed(false);

        assertThrows(
                SecurityException.class,
                () -> projectService.create(correctProjectDto)
        );
    }

    @Test
    public void testCreate_ownerUserNotExist_throwsEntityNotFoundException() {
        accessAllowed(true);
        userFromDatabase(null);

        assertThrows(
                EntityNotFoundException.class,
                () -> projectService.create(correctProjectDto)
        );
    }

    @Test
    public void testCreate_projectNameAlreadyExists_throwsIllegalArgumentException() {
        accessAllowed(true);
        userFromDatabase(new UserDto());
        projectNameExists(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> projectService.create(correctProjectDto)
        );
    }

    @Test
    public void testCreate_nameNotValid_throwsIllegalArgumentException() {
        correctProjectDto.setName("   ");
        validated();

        assertThrows(
                IllegalArgumentException.class,
                () -> projectService.create(correctProjectDto)
        );
    }

    @Test
    public void testCreate_descriptionNotValid_throwsIllegalArgumentException() {
        correctProjectDto.setDescription("   ");
        validated();

        assertThrows(
                IllegalArgumentException.class,
                () -> projectService.create(correctProjectDto)
        );
    }

    @Test
    public void testCreate_validProjectDto_projectSaved() {
        validated();
        Project savedProjectEntity = projectMapper.toEntity(correctProjectDto);
        when(projectRepository.save(savedProjectEntity)).thenReturn(savedProjectEntity);

        ProjectDto expectedProjectDto = projectService.create(correctProjectDto);

        assertNotNull(expectedProjectDto);
        assertEquals(correctProjectDto, expectedProjectDto);
    }

    @Test
    public void testGetById_notValidAccessToProject_throwsSecurityException() {
        accessAllowed(false);

        assertThrows(
                SecurityException.class,
                () -> projectService.create(correctProjectDto)
        );
    }

    @Test
    public void testGetById_ValidAccess_returnsProject() {
        accessAllowed(true);
        when(projectRepository.getProjectById(OWNER_ACCESSED_ID)).thenReturn(correctProjectEntity);

        ProjectDto expectedProjectDto = projectService.getById(OWNER_ACCESSED_ID);

        assertEquals(expectedProjectDto, correctProjectDto);
    }

    @Test
    public void testGetAll () {
        ///
    }

    @Test
    public void testGetAllWithFilters () {
        ///
    }

    private void validated() {
        accessAllowed(true);
        userFromDatabase(new UserDto());
        projectNameExists(false);
    }

    private void projectNameExists(boolean isExist) {
        when(projectRepository.existsByOwnerUserIdAndName(OWNER_ACCESSED_ID, correctProjectDto.getName())).thenReturn(isExist);
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