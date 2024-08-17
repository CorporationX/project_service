package faang.school.projectservice.service.utilservice;

import faang.school.projectservice.exception.NotFoundException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectUtilServiceTest {

    @InjectMocks
    private ProjectUtilService projectUtilService;

    @Mock
    private ProjectJpaRepository projectJpaRepository;

    @Test
    void testGetById() {
        long projectId = 11L;
        Project project = Project.builder()
                .id(projectId)
                .build();

        when(projectJpaRepository.findById(projectId)).thenReturn(Optional.of(project));

        Project result = projectUtilService.getById(projectId);

        assertEquals(project, result);
        verify(projectJpaRepository, times(1)).findById(projectId);
    }

    @Test
    void testGetById_notExists_throws() {
        long projectId = 11L;

        when(projectJpaRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectUtilService.getById(projectId));
        verify(projectJpaRepository, times(1)).findById(projectId);
    }

    @Test
    void testSave() {
        Project project = Project.builder().build();

        projectUtilService.save(project);

        verify(projectJpaRepository, times(1)).save(project);
    }
}