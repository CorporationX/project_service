package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ResponseProjectDto;
import faang.school.projectservice.mapper.project.CreateProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Spy
    private CreateProjectMapper createProjectMapper = CreateProjectMapper.INSTANCE;
    @InjectMocks
    private ProjectService projectService;

    @Test
    void createWithExistingName() {
        CreateProjectDto dto = CreateProjectDto.builder()
                .name("Existing")
                .build();

        when(projectRepository.existsByOwnerUserIdAndName(1L, "Existing")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.create(dto, 1));

        assertEquals("User with id 1 already has a project with name Existing", exception.getMessage());
    }

    @Test
    void createTest() {

        CreateProjectDto dto = CreateProjectDto.builder()
                .name("NotExisting")
                .parentProjectId(1L)
                .childrenIds(List.of(1L))
                .build();

        Project project = Project.builder()
                .ownerId(1L)
                .name("NotExisting")
                .createdAt(LocalDateTime.now())
                .status(ProjectStatus.CREATED)
                .build();
        project.setParentProject(project);
        List<Project> children = new ArrayList<>(List.of(project));
        project.setChildren(children);


        when(projectRepository.existsByOwnerUserIdAndName(1L, "NotExisting")).thenReturn(false);
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(projectRepository.findAllByIds(anyList())).thenReturn(List.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ResponseProjectDto result = projectService.create(dto, 1);

        assertEquals(project.getOwnerId(), result.getOwnerId());
        assertEquals(project.getName(), result.getName());
        assertEquals(project.getStatus(), result.getStatus());
        assertEquals(project.getCreatedAt(), result.getCreatedAt());
        assertEquals(project.getParentProject().getId(), result.getParentProjectId());
        assertEquals(project.getChildren().get(0).getId(), result.getChildrenIds().get(0));
    }
}