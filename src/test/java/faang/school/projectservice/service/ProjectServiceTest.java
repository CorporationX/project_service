package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Spy
    private ProjectMapper projectMapper = new ProjectMapperImpl();
    @InjectMocks
    private ProjectService projectService;

    private final long userId = 1;

    @Test
    public void shouldCreatedAndReturnNewProject() {
        TeamMember teamMember = TeamMember.builder()
                .userId(userId)
                .team(new Team())
                .build();
        Project notCreateProject = Project.builder()
                .name("Project")
                .description("Cool")
                .build();

        Mockito.when(projectRepository.existsByOwnerUserIdAndName(userId, "Project"))
                .thenReturn(false);

        Mockito.when(teamMemberRepository.findById(userId))
                .thenReturn(teamMember);

        Project desiredProject = notCreateProject;
        desiredProject.setId(1L);
        desiredProject.setOwner(teamMember);
        desiredProject.setTeam(teamMember.getTeam());

        Mockito.when(projectRepository.save(projectMapper.toEntity(notCreateProject)))
                .thenReturn(desiredProject);

        ProjectDto receivedProject = projectService.createProject(notCreateProject, userId);

        Assertions.assertEquals(projectMapper.toDto(desiredProject), receivedProject);
        Mockito.verify(projectService).createProject(notCreateProject, userId);
    }
}
