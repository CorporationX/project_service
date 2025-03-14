package faang.school.projectservice.service;

import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ProjectMapper projectMapper;

    @Mock
    private ProjectFilter projectNameFilter;

    @Mock
    private ProjectFilter projectStatusFilter;

    @BeforeEach
    public void setUp() {
        projectService = new ProjectService(projectRepository, projectMapper,
                List.of(projectNameFilter, projectStatusFilter));
    }

}
