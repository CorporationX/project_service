package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ProjectMapper projectMapper;


    @Test
    void createValidProject() {
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(1L);

        ProjectDto projectDto = new ProjectDto();
        projectDto.setOwner(teamMember);
        projectDto.setName("crud");

        projectService.create(projectDto);
        Mockito.verify(projectRepository, Mockito.times(1))
                .save(projectMapper.toEntity(projectDto));
    }

    @Test
    void createWithDataValidationException() {
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(1L);

        ProjectDto projectDto = new ProjectDto();
        projectDto.setOwner(teamMember);
        projectDto.setName("crud");


        Mockito.when(projectRepository.existsByOwnerUserIdAndName(teamMember.getUserId(), projectDto.getName()))
                .thenReturn(true);

        Assert.assertThrows(DataValidationException.class, () -> projectService.create(projectDto));
    }

    @Test
    void updateStatusAndDescription() {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);

        Mockito.when(projectRepository.getProjectById(projectDto.getId()))
                .thenReturn(Project.builder().build());
        projectService.updateStatusAndDescription(projectDto, projectDto.getId());

        Mockito.verify(projectRepository, Mockito.times(1))
                .save(projectMapper.toEntity(projectDto));
    }
}