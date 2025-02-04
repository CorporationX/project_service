package school.faang.project_service.service.subproject;

import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.exeption.EntityNotFoundException;
import faang.school.projectservice.exeption.ProjectNotClosableException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filters.subproject.SubProjectFilter;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.service.subproject.SubProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MomentService momentService;

    @Mock
    private List<SubProjectFilter> subProjectFilters;
    @InjectMocks
    private SubProjectService subProjectService;

    private Project firstProject;
    private Project secondProject;
    private Project thirdProject;
    private Stage stage;
    private Vacancy vacancy;
    private List<Project> children;
    private List<Stage> stages;
    private List<Vacancy> vacancies;

    @BeforeEach
    void setUp() {
        children = new ArrayList<>();
        stage = new Stage();
        vacancy = new Vacancy();
        stages = new ArrayList<>();
        vacancies = new ArrayList<>();
        stages.add(stage);
        vacancies.add(vacancy);

        firstProject = Project.builder()
                .id(1L)
                .name("first_project")
                .children(children)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .stages(stages)
                .vacancies(vacancies)
                .build();

        secondProject = Project.builder()
                .id(2L)
                .name("second_project")
                .updatedAt(LocalDateTime.now())
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        thirdProject = Project.builder()
                .id(3L)
                .children(children)
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    @Test
    public void testcreateSubProject_ProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                subProjectService.createSubProject(1L, firstProject));

        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    public void testcreateSubProject_SetProjectVisibility() {

        if (Objects.equals(firstProject.getVisibility(), ProjectVisibility.PUBLIC)) {
            secondProject.setVisibility(ProjectVisibility.PUBLIC);
        } else {
            secondProject.setVisibility(ProjectVisibility.PRIVATE);
        }

        assertEquals(ProjectVisibility.PUBLIC, secondProject.getVisibility());
    }


    @Test
    public void testCreateSubProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(firstProject));

        subProjectService.createSubProject(1L, secondProject);

        verify(projectRepository, times(1)).save(secondProject);
        assertEquals(secondProject.getParentProject().getId(), firstProject.getId());
        assertNotNull(secondProject.getCreatedAt());
        assertNotNull(secondProject.getUpdatedAt());
        assertEquals(ProjectStatus.CREATED, secondProject.getStatus());
        assertTrue(secondProject.getStages().contains(stage));
        assertTrue(secondProject.getVacancies().contains(vacancy));
    }

    @Test
    public void testUpdateSubProject_ProjectNotFound() {
        when(projectRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                subProjectService.createSubProject(2L, secondProject));

        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    public void testUpdateSubProject_Success() {
        when(projectRepository.findById(2L)).thenReturn(Optional.of(secondProject));

        subProjectService.updateSubProject(2L, secondProject);

        verify(projectRepository, times(1)).save(secondProject);
        assertEquals(secondProject.getDescription(), firstProject.getDescription());
        assertEquals(secondProject.getStatus(), firstProject.getStatus());
        assertEquals(secondProject.getVisibility(), firstProject.getVisibility());
        assertNotNull(secondProject.getUpdatedAt());
    }

    @Test
    public void testUpdateSubProject_ThrowsException() {
        thirdProject.getChildren().add(secondProject);

        when(projectRepository.findById(3L)).thenReturn(Optional.of(thirdProject));

        assertThrows(ProjectNotClosableException.class, () -> {
            subProjectService.updateSubProject(3L, thirdProject);
        });
    }

    @Test
    public void testGetGoalsByUserId() {
        long parentProjectId = 1L;
        SubProjectFilterDto filters = new SubProjectFilterDto();

        List<Project> mockSubProjects = List.of(new Project());
        when(projectRepository.findByParentId(parentProjectId)).thenReturn(mockSubProjects);

        List<Project> filteredSubProjects = subProjectService.getSubProjectsByProjectId(parentProjectId, filters);

        assertEquals(mockSubProjects, filteredSubProjects);
        verify(projectRepository, times(1)).findByParentId(parentProjectId);
    }
}