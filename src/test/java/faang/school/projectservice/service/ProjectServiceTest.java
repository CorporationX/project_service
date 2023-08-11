package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


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

       assertThrows(DataValidationException.class, () -> projectService.create(projectDto));
    }

    @Test
    void updateStatusAndDescription() {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setStatus(ProjectStatus.CREATED);
        projectDto.setDescription("crud");
        projectDto.setUpdatedAt(LocalDateTime.now());

        Long id = projectDto.getId();
        Mockito.when(projectRepository.getProjectById(id))
                .thenReturn(Project.builder().build());

        projectService.updateStatusAndDescription(projectDto, id);

        Mockito.verify(projectRepository, Mockito.times(1))
                .save(projectMapper.toEntity(projectDto));

        Project projectById = projectRepository.getProjectById(id);
        assertEquals(projectDto.getDescription(), projectById.getDescription());
        assertEquals(projectDto.getStatus(), projectById.getStatus());
        assertEquals(projectDto.getUpdatedAt(), projectById.getUpdatedAt());
    }

    @Test
    void updateStatusAndDescriptionProjectNotFound() {
       assertThrows(DataValidationException.class, () -> projectService.updateStatusAndDescription(new ProjectDto(), null));
       Assert.assertThrows(DataValidationException.class, () -> projectService.create(projectDto));
    }
}