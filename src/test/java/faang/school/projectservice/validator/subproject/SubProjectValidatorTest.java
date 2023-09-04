package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubProjectValidatorTest {
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectService projectService;
    @Mock
    private ProjectMapper projectMapper;
    @InjectMocks
    private SubProjectValidator subProjectValidator;
    private Method validateOwnerId;
    private Method validateParentProject;
    private Long rightId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        rightId = 1L;
    }

    @Test
    public void testValidateVisibility_Throw() {
        assertThrows(DataValidationException.class, () -> subProjectValidator.validateVisibility(ProjectVisibility.PUBLIC, ProjectVisibility.PRIVATE));
    }

    @Test
    public void testValidateSubProjectStatus_True() {
        Long withoutChildId = 10L;
        Long completedId = 11L;
        Long inProgressId = 12L;

        Project projectChildrenCompleted = Project.builder().status(ProjectStatus.COMPLETED).build();

        Project projectWithOutChildren = Project.builder().id(withoutChildId).status(ProjectStatus.COMPLETED).build();
        ProjectDto projectWithOutChildrenDto = ProjectDto.builder().id(withoutChildId).status(ProjectStatus.COMPLETED).build();

        Project projectCompleted = Project.builder().id(completedId).status(ProjectStatus.COMPLETED).children(List.of(projectChildrenCompleted)).build();
        ProjectDto projectCompletedDto = ProjectDto.builder().id(completedId).status(ProjectStatus.COMPLETED).build();

        Project projectInProgress = Project.builder().id(inProgressId).status(ProjectStatus.IN_PROGRESS).build();
        ProjectDto projectInProgressDto = ProjectDto.builder().id(inProgressId).status(ProjectStatus.IN_PROGRESS).build();

        Mockito.when(projectMapper.toProject(projectWithOutChildrenDto)).thenReturn(projectWithOutChildren);
        Mockito.when(projectMapper.toProject(projectCompletedDto)).thenReturn(projectCompleted);
        Mockito.when(projectMapper.toProject(projectInProgressDto)).thenReturn(projectInProgress);

        assertDoesNotThrow(() -> subProjectValidator.validateSubProjectStatus(projectWithOutChildrenDto));
        assertDoesNotThrow(() -> subProjectValidator.validateSubProjectStatus(projectCompletedDto));
        assertDoesNotThrow(() -> subProjectValidator.validateSubProjectStatus(projectInProgressDto));
    }

    @Test
    public void testValidateSubProjectStatus_Throw() {
        Long completedId = 11L;

        Project projectChildrenInProgress = Project.builder().status(ProjectStatus.IN_PROGRESS).build();
        Project projectCompleted = Project.builder().id(completedId).status(ProjectStatus.COMPLETED).children(List.of(projectChildrenInProgress)).build();
        ProjectDto projectCompletedDto = ProjectDto.builder().id(completedId).status(ProjectStatus.COMPLETED).build();

        Mockito.when(projectMapper.toProject(projectCompletedDto)).thenReturn(projectCompleted);

        assertThrows(DataValidationException.class, () -> subProjectValidator.validateSubProjectStatus(projectCompletedDto));
    }

    @Test
    public void testValidateOwnerId() throws NoSuchMethodException {
        validateOwnerId = subProjectValidator.getClass().getDeclaredMethod("validateOwnerId", Long.class);
        validateOwnerId.setAccessible(true);

        assertDoesNotThrow(() -> validateOwnerId.invoke(subProjectValidator, rightId));
        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(rightId);

        try {
            validateOwnerId.invoke(subProjectValidator, -rightId);
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof DataValidationException);
        }
    }

    @Test
    public void testValidateParentProject() throws NoSuchMethodException {
        validateParentProject = subProjectValidator.getClass().getDeclaredMethod("validateParentProject", Long.class);
        validateParentProject.setAccessible(true);

        Mockito.when(projectService.isExistProjectById(rightId))
                .thenReturn(true);

        assertDoesNotThrow(() -> validateParentProject.invoke(subProjectValidator, rightId));
        Mockito.verify(projectService, Mockito.times(1)).isExistProjectById(rightId);

        try {
            validateParentProject.invoke(subProjectValidator, -rightId);
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof DataValidationException);
        }
    }
}