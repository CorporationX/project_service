package faang.school.projectservice.service;

import faang.school.projectservice.mapper.project.CreateProjectMapper;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

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


}