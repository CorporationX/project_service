package faang.school.projectservice.controller;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {
    private ProjectMapper projectMapper = new ProjectMapperImpl();
    @Mock
    private ProjectService projectService;
    @InjectMocks
    private ProjectController projectController;

    private final long userId = 1;

    @Test
    public void shouldReturnAndCreateNewProject() {
        TeamMember desiredTeamMember = TeamMember.builder()
                .userId(userId)
                .team(new Team())
                .build();
        ProjectDto notCreateProject = ProjectDto.builder()
                .name("Project")
                .description("Cool")
                .build();

        Project desiredProject = projectMapper.toEntity(notCreateProject);
        desiredProject.setId(1L);
        desiredProject.setOwner(desiredTeamMember);
        desiredProject.setTeam(desiredTeamMember.getTeam());

        Mockito.when(projectService.createProject(notCreateProject, userId))
                .thenReturn(projectMapper.toDto(desiredProject));
        ProjectDto receivedProject = projectController.createProject(notCreateProject, userId);

        Assertions.assertEquals(projectMapper.toDto(desiredProject), receivedProject);

        Mockito.verify(projectService).createProject(notCreateProject, userId);
    }
}
