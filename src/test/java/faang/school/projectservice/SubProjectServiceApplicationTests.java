package faang.school.projectservice;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;


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
                () -> service.createSubProject(dto)
        );
    }

    @Test
    public void if_subproject_have_parent_id_repository_must_be_called(){
        when(repository.getProjectById(any())).thenReturn(Project.builder().build());
        dto.setId(1L);
        dto.setParentProjectId(1L);

        service.createSubProject(dto);

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
                () -> service.refreshSubProject(subProjectDto)
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
        service.refreshSubProject(subProjectDto);

        // Assert
        verify(repository, times(1)).getProjectById(1L);
        verify(repository, times(1)).save(any(Project.class));
    }
}
