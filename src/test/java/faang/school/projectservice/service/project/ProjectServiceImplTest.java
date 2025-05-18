package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    @InjectMocks
    private ProjectServiceImpl service;


    private List<ProjectFilter> filters;
    private ProjectDto projectDto;
    private long userId = 1L;
    private long projectId = 333L;
    private Project project;

    @BeforeEach
    public void setUp() {
        projectDto = ProjectDto.builder().build();
        project = Project.builder()
                .id(projectId)
                .ownerId(userId)
                .build();
    }

    @Test
    public void createOwnerAlreadyHasProjectWithSameName(){
        when(projectRepository.findAll()).thenReturn(List.of(project));

    }
}