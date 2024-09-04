package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    @InjectMocks
    private ProjectService projectService;

    @Test
    void testGetProjectDtoById_Success() {
        Project project = new Project();
        project.setId(1L);

        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);

        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.getProjectById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(projectRepository, times(1)).getProjectById(1L);
        verify(projectMapper, times(1)).toDto(project);
    }

    @Test
    void testGetProjectDtoById_ProjectNotFound() {
        when(projectRepository.getProjectById(anyLong())).thenThrow(new EmptyResultDataAccessException("Project not found", 1));

        assertThrows(EmptyResultDataAccessException.class, () ->
                projectService.getProjectById(1L));
    }
}