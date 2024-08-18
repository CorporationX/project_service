package faang.school.projectservice.validator;

import faang.school.projectservice.dto.subprojectdto.SubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class SubProjectValidatorTest {

    @InjectMocks
    private SubProjectValidator subProjectValidator;

    long projectId = 1L;
    long projectId2 = 2L;
    Project parentProject;
    Project project;
    ProjectVisibility projectVisibility = ProjectVisibility.PUBLIC;
    SubProjectDto subProjectDto;


    @BeforeEach
    void setUp() {
        parentProject = Project.builder().id(projectId).build();
        project = Project.builder().id(projectId2).build();
        ProjectVisibility projectVisibility = ProjectVisibility.PUBLIC;
        parentProject.setVisibility(projectVisibility);
        project.setVisibility(projectVisibility);
        subProjectDto = SubProjectDto.builder().id(projectId)
                .visibility(projectVisibility).build();
    }


    @Test
    void testValidateRootProjectHasNotParentProject() {
        subProjectValidator.validateRootProjectHasNotParentProject(projectId, parentProject);
        assertDoesNotThrow(() -> subProjectValidator
                .validateRootProjectHasNotParentProject(projectId, parentProject));
    }

    @Test
    void testCheckIfParentExists() {
        subProjectValidator.checkIfParentExists(projectId, parentProject);
        assertDoesNotThrow(() ->
            subProjectValidator.checkIfParentExists(projectId, parentProject));

    }

    @Test
    void testCheckIfVisible() {
        subProjectValidator.checkIfVisible(projectVisibility, parentProject);
        assertDoesNotThrow(() ->
            subProjectValidator.checkIfVisible(projectVisibility, parentProject));

    }

    @Test
    void testCheckAllValidationsForCreateSubProject() {
        subProjectValidator.checkAllValidationsForCreateSubProject(subProjectDto, parentProject);
        assertDoesNotThrow(() -> subProjectValidator
                .validateRootProjectHasNotParentProject(projectId, parentProject));
        assertDoesNotThrow(() ->
                subProjectValidator.checkIfParentExists(projectId, parentProject));
        assertDoesNotThrow(() -> subProjectValidator.checkIfVisible(projectVisibility, parentProject));
    }
}