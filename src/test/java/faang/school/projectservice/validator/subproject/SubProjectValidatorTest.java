package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
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
    @InjectMocks
    private SubProjectValidator subProjectValidator;
    private Method validateOwnerId;
    private Method validateParentProject;
    private Long rightId;
    private ProjectStatus defaultStatus = ProjectStatus.CREATED;

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
    public void testValidateSubProjectStatusInProgress_True() {
        Long inProgressId = 12L;

        ProjectDto projectInProgressDto = ProjectDto.builder()
                .id(inProgressId).status(defaultStatus).build();

        assertDoesNotThrow(()
                -> subProjectValidator.validateSubProjectStatus(projectInProgressDto, ProjectStatus.IN_PROGRESS));
    }

    @Test
    public void testValidateSubProjectStatusWithoutChild_True() {
        Long withoutChildId = 10L;

        ProjectDto projectWithOutChildrenDto = ProjectDto.builder()
                .id(withoutChildId).status(ProjectStatus.COMPLETED).build();

        assertDoesNotThrow(()
                -> subProjectValidator.validateSubProjectStatus(projectWithOutChildrenDto, ProjectStatus.COMPLETED));
    }

    @Test
    public void testValidateSubProjectStatusToCompletedId_True() {
        Long completedId = 11L;

        List<Long> childrenIds = List.of(1L, 2L);

        ProjectDto projectToCompleted = ProjectDto.builder()
                .id(completedId)
                .status(defaultStatus)
                .childrenIds(childrenIds)
                .build();
        Mockito.when(projectService.getProjectById(Mockito.anyLong()))
                .thenReturn(ProjectDto.builder().status(ProjectStatus.COMPLETED).build());

        assertDoesNotThrow(()
                -> subProjectValidator.validateSubProjectStatus(projectToCompleted, ProjectStatus.COMPLETED));
    }

    @Test
    public void testValidateSubProjectStatus_Throw() {
        Long completedId = 11L;
        List<Long> childrenIds = List.of(1L, 2L);

        ProjectDto projectCompletedDto = ProjectDto.builder()
                .id(completedId)
                .status(defaultStatus)
                .childrenIds(childrenIds)
                .build();

        Mockito.when(projectService.getProjectById(Mockito.anyLong()))
                .thenReturn(ProjectDto.builder().status(defaultStatus).build());

        assertThrows(DataValidationException.class,
                () -> subProjectValidator.validateSubProjectStatus(projectCompletedDto, ProjectStatus.COMPLETED));
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