package school.faang.project_service.controller;

import faang.school.projectservice.dto.client.CreateSubProjectDto;
import faang.school.projectservice.dto.client.SubProjectDto;
import faang.school.projectservice.dto.client.UpdateSubProjectDto;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.SubProjectServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubProjectServiceImplTest {

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private ProjectRepository subProjectRepository;

    @MockBean
    private SubProjectMapper subProjectMapper;

    @MockBean
    private CreateSubProjectDto createSubProjectDto;

    @MockBean
    private SubProjectDto subProjectDto;

    @MockBean
    private UpdateSubProjectDto updateSubProjectDto;

    @MockBean
    private SubProjectServiceImpl subProjectService;

    void setUp() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");

        Project subProject = new Project();
        subProject.setId(2L);
        subProject.setName("Test SubProject");
        List<Project> projects = Collections.singletonList(project);
        subProject.setChildren(projects);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(subProjectRepository.findById(2L)).thenReturn(Optional.of(subProject));
        when(subProjectRepository.save(any(Project.class))).thenReturn(subProject);
    }

    @Test
    public void testGetSubProject() {
        Project project = new Project();
        project.setId(1L);
        project.setDescription("Test Description");

        List<SubProjectDto> subProjectDtos = subProjectService.getSubprojects(
                project, "Test Title", ProjectStatus.CREATED);

        assertNotNull(subProjectDtos);
        assertEquals(1L, subProjectDtos.get(0).id());
        assertEquals("Test Title", subProjectDtos.get(0).title());
    }
}