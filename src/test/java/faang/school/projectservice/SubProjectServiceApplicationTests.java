package faang.school.projectservice;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.SubProjectServiceImpl;
import faang.school.projectservice.util.ChildrenNotFinishedException;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@ExtendWith(MockitoExtension.class)
class SubProjectServiceApplicationTests {
    @Mock
    private ProjectRepository repository;
    @Spy
    private ProjectMapperImpl mapper = new ProjectMapperImpl();
    @InjectMocks
    private SubProjectServiceImpl service;
    CreateSubProjectDto dto;
    private Project subProject;
    private Project childProject1;
    private Project childProject2;

    @BeforeEach
    public void setUp(){
         dto = CreateSubProjectDto.builder().id(1L).parentProjectId(1L).build();
    }

    @Test
    public void if_subproject_does_not_have_parent_id_exeption_must_thrown(){
        when(repository.getProjectById(any())).thenThrow(new EntityNotFoundException());
        dto.setId(1L);
        dto.setParentProjectId(1L);
        assertThrows(
                EntityNotFoundException.class,
                () -> service.createSubProject(mapper.toEntity(dto))
        );
    }

    @Test
    public void if_subproject_have_parent_id_repository_must_be_called(){
        when(repository.getProjectById(any())).thenReturn(Project.builder().build());
        dto.setId(1L);
        dto.setParentProjectId(1L);

        service.createSubProject(mapper.toEntity(dto));

        verify(repository).save(mapper.toEntity(dto));

    }

    @Test
    public void shouldThrowChildrenNotFinishedException_WhenChildProjectsNotCompleted() {
        // Create a sub-project with child projects


        childProject1 = Project.builder()
                .id(2L)
                .status(ProjectStatus.IN_PROGRESS) // Child project not completed
                .build();

        childProject2 = Project.builder()
                .id(3L)
                .status(ProjectStatus.COMPLETED) // This child is completed
                .build();
        subProject = Project.builder()
                .id(1L)
                .status(ProjectStatus.COMPLETED)
                .children(List.of(childProject1, childProject2))
                .build();
        // Arrange
        CreateSubProjectDto subProjectDto = new CreateSubProjectDto();
        subProjectDto.setId(1L);

        when(repository.getProjectById(subProjectDto.getId())).thenReturn(subProject);

        // Act & Assert
        ChildrenNotFinishedException exception = assertThrows(
                ChildrenNotFinishedException.class,
                () -> service.refreshSubProject(mapper.toEntity(subProjectDto))
        );

        // Assert exception message contains IDs and status

        verify(repository, times(1)).getProjectById(1L);
        verify(repository, times(0)).save(any(Project.class)); // Save should not be called
    }

    @Test
    public void shouldSaveProject_WhenAllChildProjectsAreCompleted() throws ChildrenNotFinishedException {

        childProject1 = Project.builder()
                .id(2L)
                .status(ProjectStatus.COMPLETED)
                .build();

        childProject2 = Project.builder()
                .id(3L)
                .status(ProjectStatus.COMPLETED)
                .build();
        subProject = Project.builder()
                .id(1L)
                .status(ProjectStatus.COMPLETED)
                .children(List.of(childProject1, childProject2))
                .build();

        CreateSubProjectDto subProjectDto = new CreateSubProjectDto();
        subProjectDto.setId(1L);

        when(repository.getProjectById(subProjectDto.getId())).thenReturn(subProject);
        when(repository.save(any(Project.class))).thenReturn(subProject);

        // Act
        service.refreshSubProject(mapper.toEntity(subProjectDto));

        // Assert
        verify(repository, times(1)).getProjectById(1L);
        verify(repository, times(1)).save(any(Project.class));
    }

    @Test
    public void testSetStatusAndTime() {

        Project project = new Project();
        project.setStatus(ProjectStatus.IN_PROGRESS);
        LocalDateTime initialTime = LocalDateTime.now();

        Project result = service.getSetStatusAndTime().apply(project);


        assertThat(result.getStatus()).isEqualTo(ProjectStatus.IN_PROGRESS);
        assertThat(result.getUpdatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isEqualToIgnoringSeconds(initialTime);
    }

    @Test
    public void testAssignTeamMemberMoment() {
        TeamMember member1 = TeamMember.builder().id(1L).userId(101L).build();
        TeamMember member2 = TeamMember.builder().id(2L).userId(102L).build();

        Team team1 = Team.builder().teamMembers(List.of(member1)).build();
        Team team2 = Team.builder().teamMembers(List.of(member2)).build();

        Moment moment1 = new Moment();
        Moment moment2 = new Moment();

        Project project = new Project();
        project.setTeams(List.of(team1, team2));
        project.setMoments(List.of(moment1, moment2));

        Project result = service.getAssignTeamMemberMoment().apply(project);

        List<Long> expectedTeamMemberIds = List.of(1L, 2L);
        assertThat(result.getMoments()).hasSize(2);
        result.getMoments().forEach(moment -> {
            assertThat(moment.getUserIds()).containsExactlyInAnyOrderElementsOf(expectedTeamMemberIds);
        });
    }

}
