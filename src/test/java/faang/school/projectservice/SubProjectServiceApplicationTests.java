package faang.school.projectservice;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.SubProjectServiceImpl;
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


@ExtendWith(MockitoExtension.class)
class SubProjectServiceApplicationTests {
    @Mock
    private ProjectRepository repository;
    @Spy
    private ProjectMapper mapper;
    @InjectMocks
    private SubProjectServiceImpl service;
    CreateSubProjectDto dto;

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
}
