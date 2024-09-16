package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.InternshipServiceImpl;
import faang.school.projectservice.service.filter.InternshipFilter;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceImplTest {

    @InjectMocks
    private InternshipServiceImpl internshipService;

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private InternshipFilter filterMock;

    private InternshipMapper internshipMapper;

    private CreateInternshipDto internshipDto;

    @BeforeEach
    public void setUp() {
        internshipMapper = Mappers.getMapper(InternshipMapper.class);
        List<InternshipFilter> filters = List.of(filterMock);
        internshipService = new InternshipServiceImpl(internshipMapper, filters, internshipRepository, projectRepository);

        List<Long> interns = List.of(1L, 2L, 3L);
        Long mentorId = 2L;
        Long projectId = 1L;
        Long internshipId = 1L;

        internshipDto = new CreateInternshipDto("name"
                , "description"
                , projectId
                , interns
                , mentorId
                , LocalDateTime.now().plusMonths(3));
    }

    @Test
    public void testCreateInternship_Success() {
        Project project = new Project();
        project.setId(1L);
        Team team = new Team();
        TeamMember intern = new TeamMember();
        intern.setId(1L);
        TeamMember mentor = new TeamMember();
        mentor.setId(2L);
        intern.setRoles(List.of(TeamRole.INTERN));
        mentor.setRoles(List.of(TeamRole.DEVELOPER));
        team.setTeamMembers(List.of(intern, mentor));
        project.setTeams(List.of(team));

        when(projectRepository.getProjectById(internshipDto.getProjectId())).thenReturn(project);
        internshipService.createInternship(internshipDto);

        verify(internshipRepository, times(1)).save(any((Internship.class)));
    }
}
