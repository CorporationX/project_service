package faang.school.projectservice.service.impl;

import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectServiceImpl;

    private final long id = 1L;

    @Test
    void existsProjectById_ExistsProject() {
        when(projectRepository.existsById(id)).thenReturn(true);

        boolean exists = projectServiceImpl.existsProjectById(id);

        assertTrue(exists);
        verify(projectRepository).existsById(id);
    }

    @Test
    void existsProjectById_NotExistsProject() {
        when(projectRepository.existsById(id)).thenReturn(false);

        boolean exists = projectServiceImpl.existsProjectById(id);

        assertFalse(exists);
        verify(projectRepository).existsById(id);
    }
}