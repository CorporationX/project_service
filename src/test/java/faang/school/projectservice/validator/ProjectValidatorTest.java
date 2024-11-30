package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exception.NoStatusChangeException;
import faang.school.projectservice.exception.NotUniqueProjectException;
import faang.school.projectservice.exception.ProjectVisibilityException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectValidatorTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectValidator projectValidator;

    private ProjectDto projectDto;
    private Project project;
    private Long ownerId;
    private String projectName;
    private Long projectId = 1L;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        project = Project.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        projectName = projectDto.getName();
        ownerId = projectDto.getOwnerId();
    }

    @Test
    void testValidateUniqueProjectFailed() {
        when(projectRepository.existsByOwnerIdAndName(ownerId, projectName)).thenReturn(true);

        assertThrows(NotUniqueProjectException.class,
                () -> projectValidator.validateUniqueProject(projectDto));
    }

    @Test
    void testValidateUniqueProjectSuccess() {
        when(projectRepository.existsByOwnerIdAndName(ownerId, projectName)).thenReturn(false);

        assertDoesNotThrow(() -> projectValidator.validateUniqueProject(projectDto));
    }

    @Test
    void testUserCanAccessPrivateProject() {
        assertTrue(projectValidator.canUserAccessProject(project, ownerId));
    }

    @Test
    void testUserCanNotAccessPrivateProject() {
        project.setOwnerId(2L);
        assertFalse(projectValidator.canUserAccessProject(project, ownerId));
    }

    @Test
    void testUserCanAccessPublicProject() {
        project.setOwnerId(2L);
        project.setVisibility(ProjectVisibility.PUBLIC);
        assertTrue(projectValidator.canUserAccessProject(project, ownerId));
    }

    @Test
    @DisplayName("Check project exists")
    void testValidateProjectExistsById() {
        when(projectRepository.existsById(projectId)).thenReturn(true);

        assertDoesNotThrow(() -> projectValidator.validateProjectExistsById(projectId));

        verify(projectRepository, times(1)).existsById(projectId);
    }

    @Test
    @DisplayName("Check project doesn't exist")
    void testValidateProjectInVacancyNotExists() {
        when(projectRepository.existsById(projectId)).thenReturn(false);

        Exception ex = assertThrows(EntityNotFoundException.class, () -> projectValidator.validateProjectExistsById(projectId));
        assertEquals("Project with id 1 doesn't exist", ex.getMessage());

        verify(projectRepository, times(1)).existsById(projectId);
    }

    @Test
    void testIsOpenProjectWhenStatusInProgress() {
        project.setStatus(ProjectStatus.IN_PROGRESS);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        assertTrue(projectValidator.isOpenProject(projectId));
    }

    @Test
    void testIsOpenProjectWhenStatusCompleted() {
        project.setStatus(ProjectStatus.COMPLETED);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        assertFalse(projectValidator.isOpenProject(projectId));
    }

    @Test
    void testIsOpenProjectWhenStatusCancelled() {
        project.setStatus(ProjectStatus.CANCELLED);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        assertFalse(projectValidator.isOpenProject(projectId));
    }

    @Test
    @DisplayName("Validate project public throws exception if private")
    void testValidateProjectPublicThrowsExceptionIfPrivate() {
        Project project = Project.builder()
                .id(123L)
                .build();
        project.setVisibility(ProjectVisibility.PRIVATE);

        ProjectVisibilityException exception = assertThrows(ProjectVisibilityException.class,
                () -> projectValidator.validateProjectPublic(project));

        assertEquals("Only public projects are allowed for this operation, projectId: 123", exception.getMessage());
    }

    @Test
    @DisplayName("Validate project public doesn't throw exception if public")
    void testValidateProjectPublicDoesNotThrowExceptionIfPublic() {
        Project project = Project.builder().build();
        project.setVisibility(ProjectVisibility.PUBLIC);

        assertDoesNotThrow(() -> projectValidator.validateProjectPublic(project));
    }

    @Test
    @DisplayName("Is public project returns true for public project")
    void testIsPublicProjectReturnsTrueForPublicProject() {
        Project project = Project.builder().build();
        project.setVisibility(ProjectVisibility.PUBLIC);

        assertTrue(projectValidator.isPublicProject(project));
    }

    @Test
    @DisplayName("Is public project returns false for private project")
    void testIsPublicProjectReturnsFalseForPrivateProject() {
        Project project = Project.builder().build();
        project.setVisibility(ProjectVisibility.PRIVATE);

        assertFalse(projectValidator.isPublicProject(project));
    }

    @Test
    @DisplayName("Has parent project returns true if parent exists")
    void testHasParentProjectReturnsTrueIfParentExists() {
        Project project = Project.builder().build();
        project.setParentProject(new Project());

        assertTrue(projectValidator.hasParentProject(project));
    }

    @Test
    @DisplayName("Has parent project returns false if parent does not exist")
    void testHasParentProjectReturnsFalseIfParentDoesNotExist() {
        Project project = Project.builder().build();

        assertFalse(projectValidator.hasParentProject(project));
    }

    @Test
    @DisplayName("Has children projects returns true if children exist")
    void testHasChildrenProjectsReturnsTrueIfChildrenExist() {
        Project project = Project.builder().build();
        project.setChildren(new ArrayList<>(List.of(new Project())));

        assertTrue(projectValidator.hasChildrenProjects(project));
    }

    @Test
    @DisplayName("Has children projects returns false if children do not exist")
    void testHasChildrenProjectsReturnsFalseIfChildrenDoNotExist() {
        Project project = Project.builder().build();
        project.setChildren(new ArrayList<>());

        assertFalse(projectValidator.hasChildrenProjects(project));
    }

    @Test
    @DisplayName("Validate same project status throws exception if same")
    void testValidateSameProjectStatusThrowsExceptionIfSame() {
        Project project = Project.builder()
                .id(123L)
                .status(ProjectStatus.CREATED)
                .build();
        UpdateSubProjectDto dto = UpdateSubProjectDto.builder()
                .status(ProjectStatus.CREATED)
                .build();

        NoStatusChangeException exception = assertThrows(NoStatusChangeException.class,
                () -> projectValidator.validateSameProjectStatus(project, dto));

        assertEquals("Project 123 status is already 'CREATED'. Cannot change to the same status.", exception.getMessage());
    }

    @Test
    @DisplayName("Validate project status completed or cancelled throws exception if status completed")
    void testValidateProjectStatusCompletedOrCancelledThrowsExceptionIfStatusCompleted() {
        Project project = Project.builder()
                .id(123L)
                .status(ProjectStatus.COMPLETED)
                .build();

        NoStatusChangeException exception = assertThrows(NoStatusChangeException.class,
                () -> projectValidator.validateProjectStatusCompletedOrCancelled(project));

        assertEquals("Project 123 is already 'COMPLETED' and cannot have its status changed.", exception.getMessage());
    }

    @Test
    @DisplayName("Validate project status valid to hold throws exception if not in progress")
    void testValidateProjectStatusValidToHoldThrowsExceptionIfNotInProgress() {
        Project project = Project.builder()
                .id(123L)
                .status(ProjectStatus.CREATED)
                .build();

        NoStatusChangeException exception = assertThrows(NoStatusChangeException.class,
                () -> projectValidator.validateProjectStatusValidToHold(project));

        assertEquals("Project 123 is 'CREATED'. It must be in progress before being held.", exception.getMessage());
    }

    @Test
    @DisplayName("Validate project is valid to complete throws exception if not in progress")
    void testValidateProjectIsValidToCompleteThrowsExceptionIfNotInProgress() {
        Project project = Project.builder()
                .id(123L)
                .status(ProjectStatus.CREATED)
                .build();
        Project childProject = Project.builder()
                .status(ProjectStatus.CREATED)
                .build();
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setChildren(new ArrayList<>(List.of(childProject)));

        NoStatusChangeException exception = assertThrows(NoStatusChangeException.class,
                () -> projectValidator.validateProjectIsValidToComplete(project));

        assertEquals("Project 123 cannot be completed. Ensure all subprojects are completed and the project is not cancelled.", exception.getMessage());
    }

    @Test
    @DisplayName("Validate create subproject based on visibility throws exception if visibility different")
    void testValidateCreateSubprojectBasedOnVisibilityThrowsExceptionIfVisibilityDifferent() {
        Project parentProject = Project.builder()
                .id(123L)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        CreateProjectDto dto = CreateProjectDto.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        ProjectVisibilityException exception = assertThrows(ProjectVisibilityException.class,
                () -> projectValidator.validateCreateSubprojectBasedOnVisibility(parentProject, dto));

        assertEquals("Parent project 123 and subproject must have the same visibility. Parent: 'PRIVATE', Subproject: 'PUBLIC'.", exception.getMessage());
    }

    @Test
    @DisplayName("Validate has children projects closed returns true if all children closed")
    void testValidateHasChildrenProjectsClosedReturnsTrueIfAllChildrenClosed() {
        Project project = Project.builder().build();
        project.setChildren(new ArrayList<>(List.of(
                Project.builder()
                        .status(ProjectStatus.COMPLETED)
                        .build(),
                Project.builder()
                        .status(ProjectStatus.CANCELLED)
                        .build()
        )));

        assertTrue(projectValidator.validateHasChildrenProjectsClosed(project));
    }

    @Test
    @DisplayName("Validate has children projects closed returns false if not all children closed")
    void testValidateHasChildrenProjectsClosedReturnsFalseIfNotAllChildrenClosed() {
        Project project = Project.builder().build();
        project.setChildren(new ArrayList<>(List.of(
                Project.builder()
                        .status(ProjectStatus.COMPLETED)
                        .build(),
                Project.builder()
                        .status(ProjectStatus.IN_PROGRESS)
                        .build()
        )));

        assertFalse(projectValidator.validateHasChildrenProjectsClosed(project));
    }

    @Test
    @DisplayName("Test user belongs to the project team by valid userId: success")
    void validateUserInProjectTeam_ValidParameters_Success(){
        TeamMember teamMember = TeamMember.builder().userId(1L).build();
        Team team = Team.builder().teamMembers(List.of(teamMember)).build();
        project.setTeams(List.of(team));

        assertDoesNotThrow(() -> projectValidator.validateUserInProjectTeam(userId, project));
    }

    @Test
    @DisplayName("Test user doesn't belong to the project team: fail")
    void validateUserInProjectTeam_ValidParametersNoTeam_FailException(){
        TeamMember teamMember = TeamMember.builder().userId(2L).build();
        Team team = Team.builder().teamMembers(List.of(teamMember)).build();
        project.setId(5L);
        project.setTeams(List.of(team));

        Exception ex = assertThrows(EntityNotFoundException.class, () -> projectValidator.validateUserInProjectTeam(userId, project));
        assertEquals(String.format("User id: 1 doesn't work on project id: %d", project.getId()), ex.getMessage());
    }

    @Test
    @DisplayName("Test project doesn't have a team: fail")
    void validateUserInProjectTeam_ProjectWithoutAnyTeam_FailException(){
        project.setId(5L);
        project.setTeams(List.of());

        Exception ex = assertThrows(EntityNotFoundException.class, () -> projectValidator.validateUserInProjectTeam(userId, project));
        assertEquals(String.format("User id: 1 doesn't work on project id: %d", project.getId()), ex.getMessage());
    }
}