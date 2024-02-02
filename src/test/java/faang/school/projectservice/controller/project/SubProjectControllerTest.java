package faang.school.projectservice.controller.project;

import faang.school.projectservice.controller.SubProjectController;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private SubProjectController subProjectController;

    Project project;
    ProjectDto projectDto;
    CreateSubProjectDto createSubProjectDto;
    CreateSubProjectDto createSubProjectNameIsBlankDto;

    ProjectFilterDto projectFilterDto;
    UpdateSubProjectDto updateSubProjectDto;


    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(1L)
                .name("BootCamp")
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.CREATED)
                .build();
        projectDto = ProjectDto.builder()
                .name("Onboarding")
                .build();

        projectFilterDto = ProjectFilterDto.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        createSubProjectDto = CreateSubProjectDto.builder()
                .name("Onboarding")
                .build();

        createSubProjectNameIsBlankDto = CreateSubProjectDto.builder()
                .name(" ")
                .build();

        updateSubProjectDto = UpdateSubProjectDto.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    @Test
    public void testInvokeCreateSubProjectOnce() {
        subProjectController.create(createSubProjectDto);

        verify(projectService, times(1)).createSubProject(createSubProjectDto);
    }

    @Test
    public void testCallUpdateProjectServiceOnce() {
        subProjectController.updateProject(1L, updateSubProjectDto);

        verify(projectService, times(1)).updateProject(1L, updateSubProjectDto);
    }

    @Test
    public void testGetFilteredPublicSubProjectsValidatesServiceInvocation() {
        subProjectController.getFilteredSubProjects(1L, projectFilterDto);

        verify(projectService, times(1)).getFilteredSubProjects(1L, projectFilterDto);
    }

    @Test
    public void testCreateSubProjectWithBlankNameThrowsDataValidationException() {
        assertThrows(DataValidationException.class, () -> subProjectController.create(createSubProjectNameIsBlankDto));
    }

    @Test
    public void testUpdateProjectWithInvalidIdThrowsDataValidationException() {
        assertThrows(DataValidationException.class,
                () -> subProjectController.updateProject(-1L, updateSubProjectDto));
    }
}