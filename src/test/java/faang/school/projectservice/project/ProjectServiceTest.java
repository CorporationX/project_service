package faang.school.projectservice.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository repository;
    @Spy
    private ProjectMapper mapper = Mappers.getMapper(ProjectMapper.class);

    @InjectMocks
    private ProjectService service;

    private final long projectId = 1;

    private Project generateProject(long id) {
        Project project = new Project();

        project.setId(id);

        return project;
    };

    @Test
    public void testGetProjectEmpty() {
        Mockito.when(repository.getProjectById(projectId)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> service.getProject(projectId));
    }

    @Test
    public void testGetProject() {
        Mockito.when(repository.getProjectById(projectId)).thenReturn(generateProject(projectId));

        ProjectDto actual = service.getProject(projectId);

        Assertions.assertEquals(projectId, actual.getId());
    }

    @Test
    public void testGetProjectsByIdsEmpty() {
        // given
        List<Long> ids = List.of(1L, 2L);
        List<ProjectDto> expected = new ArrayList<>();
        Mockito.when(repository.findAllByIds(ids)).thenReturn(new ArrayList<>());

        // when
        List<ProjectDto> actual = service.getProjectsByIds(ids);

        // then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetProjectsByIds() {
        // given
        List<Long> ids = List.of(1L, 2L);
        List<Project> projectList = List.of(generateProject(1L), generateProject(2L));
        List<ProjectDto> expected = projectList.stream().map(mapper::toDto).toList();
        Mockito.when(repository.findAllByIds(ids)).thenReturn(projectList);

        // when
        List<ProjectDto> actual = service.getProjectsByIds(ids);

        // then
        Assertions.assertEquals(expected, actual);
    }
}