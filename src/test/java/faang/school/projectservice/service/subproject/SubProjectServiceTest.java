package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.VisibilitySubprojectUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class SubProjectServiceTest {
    @InjectMocks
    private SubProjectService subProjectService;
    @Mock
    private ProjectRepository projectRepository;
    private Project project;
    private Project parentProject;
    private VisibilitySubprojectUpdateDto visibilitySubprojectUpdateDto;
    private Tree tree = new Tree();
    private Long projectId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        project = tree.projectA;
        parentProject = tree.parentProjectA;
        projectId = 1L;

        project.setParentProject(parentProject);
    }

    @Test
    void testUpdateVisibilitySubProjectToPrivate_True() {
        visibilitySubprojectUpdateDto = VisibilitySubprojectUpdateDto.builder()
                .id(projectId)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        Mockito.when(projectRepository.getProjectById(projectId))
                .thenReturn(project);

        subProjectService.updateVisibilitySubProject(visibilitySubprojectUpdateDto);

        assertEquals(ProjectVisibility.PRIVATE, tree.projectA.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, tree.projectB.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, tree.projectC.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, tree.projectD.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, tree.projectG.getVisibility());
    }

    @Test
    void testUpdateVisibilitySubProjectToPublic_True() {
        visibilitySubprojectUpdateDto = VisibilitySubprojectUpdateDto.builder()
                .id(projectId)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        project.setVisibility(ProjectVisibility.PRIVATE);
        parentProject.setVisibility(ProjectVisibility.PUBLIC);

        Mockito.when(projectRepository.getProjectById(projectId))
                .thenReturn(project);

        assertDoesNotThrow(() -> subProjectService.updateVisibilitySubProject(visibilitySubprojectUpdateDto));
        assertEquals(ProjectVisibility.PUBLIC, project.getVisibility());
    }

    @Test
    void testUpdateVisibilitySubProjectToPublic_Throw() {
        visibilitySubprojectUpdateDto = VisibilitySubprojectUpdateDto.builder()
                .id(projectId)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        project.setVisibility(ProjectVisibility.PRIVATE);
        parentProject.setVisibility(ProjectVisibility.PRIVATE);

        Mockito.when(projectRepository.getProjectById(projectId))
                .thenReturn(project);

        assertThrows(DataValidationException.class,
                () -> subProjectService.updateVisibilitySubProject(visibilitySubprojectUpdateDto));
    }
}