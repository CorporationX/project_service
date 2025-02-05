package faang.school.projectservice.service.project;

import faang.school.projectservice.exeption.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private ProjectService projectService;

    @Test
    void getProjectById() {
        Project project = Project.builder()
                .id(1L)
                .build();
        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        Project actual = projectService.findProjectById(1L);
        Project expected = Project.builder()
                .id(1L)
                .build();
        Assertions.assertEquals(expected, actual);
        Mockito.verify(projectRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void getProjectByIdNotFound() {
        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> projectService.findProjectById(1L));
        Mockito.verify(projectRepository, Mockito.times(1)).findById(1L);
    }
}