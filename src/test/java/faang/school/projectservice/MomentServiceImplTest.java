package faang.school.projectservice;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.mapper.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.MomentServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MomentServiceImplTest {
    @InjectMocks
    private MomentServiceImpl momentServiceImpl;
    @Mock
    private MomentRepository momentRepository;
    @Spy
    private MomentMapperImpl momentMapper;
    @Mock
    private MomentValidator momentValidator;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    private List<Long> addedProjectIds;
    private List<Long> addedUserIds;
    private Long momentId;
    List<Project> projects;

    @BeforeEach
    void setUp() {
        momentId = 1L;
        addedProjectIds = new ArrayList<>(List.of(1L, 2L));
        addedUserIds = new ArrayList<>(List.of(1L, 2L));
        projects = List.of(new Project(), new Project());
    }

    @Test
    void testCreateMoment() {
        Moment moment = new Moment();
        MomentDto momentDto = new MomentDto();
        moment.setId(momentId);
        moment.setProjects(projects);
        when(momentMapper.toEntity(momentDto)).thenReturn(moment);

        MomentDto dto = momentServiceImpl.createMoment(momentDto);

        Assertions.assertEquals(momentId, dto.getId());
    }

    @Test
    void testUpdateMomentAllEmpty() {
        Moment moment = new Moment();
        moment.setId(momentId);
        when(momentRepository.findById(momentId)).thenReturn(Optional.of(moment));

        MomentDto dto = momentServiceImpl.updateMoment(momentId, addedProjectIds, addedUserIds);

        verify(momentValidator, times(1)).validateMoment(moment);
        verify(momentRepository, times(1)).save(moment);
        Assertions.assertEquals(momentId, dto.getId());

    }

    @Test
    void testUpdateMomentWithNewProject() {
        addedUserIds.clear();
        Moment moment = new Moment();
        moment.setId(momentId);
        when(momentRepository.findById(momentId)).thenReturn(Optional.of(moment));

        MomentDto dto = momentServiceImpl.updateMoment(momentId, addedProjectIds, addedUserIds);

        verify(momentValidator, times(1)).validateMoment(moment);
        verify(momentRepository, times(1)).save(moment);
        Assertions.assertEquals(momentId, dto.getId());
    }

    @Test
    void testUpdateMomentWithNewUser() {
        Moment moment = new Moment();
        moment.setId(momentId);
        moment.setUserIds(new ArrayList<>());
        moment.setProjects(List.of(new Project()));
        Team team = new Team();
        team.setId(1L);
        TeamMember teamMember = new TeamMember();
        teamMember.setTeam(team);
        when(momentRepository.findById(momentId)).thenReturn(Optional.of(moment));
        when(teamMemberRepository.findByAllId(addedUserIds)).thenReturn(List.of(teamMember));


        MomentDto dto = momentServiceImpl.updateMoment(momentId, addedProjectIds, addedUserIds);

        verify(momentValidator, times(1)).validateMoment(moment);
        verify(momentRepository, times(1)).save(moment);
        Assertions.assertEquals(momentId, dto.getId());
    }

    @Test
    void getAllProjectMomentsByDate_Success() {
        Long projectId = 1L;
        LocalDateTime month = LocalDateTime.of(2024, 9, 11, 14, 30, 0);
        LocalDateTime endDate = month.plusMonths(1).minusDays(1);

        Moment moment1 = new Moment();
        moment1.setId(projectId);
        moment1.setCreatedAt(month.minusDays(1));
        Moment moment2 = new Moment();
        moment2.setId(projectId);
        moment2.setCreatedAt(month.plusDays(1));
        Moment moment3 = new Moment();
        moment3.setId(projectId);
        moment3.setCreatedAt(endDate.plusDays(1));
        List<Moment> moments = List.of(moment1, moment2, moment3);

        Project project = new Project();
        project.setId(projectId);
        project.setMoments(moments);
        project.setCreatedAt(month.plusDays(1));

        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        List<MomentDto> dtos = momentServiceImpl.getAllProjectMomentsByDate(projectId, month);

        Assertions.assertEquals(1, dtos.size());
        Assertions.assertEquals(moment2.getId(), dtos.get(0).getId());
    }

    @Test
    void getMomentById() {
        Long momentId = 1L;
        Moment moment = new Moment();
        moment.setId(momentId);
        when(momentRepository.findById(momentId)).thenReturn(Optional.of(moment));

        MomentDto dto = momentServiceImpl.getMomentById(momentId);

        Assertions.assertEquals(moment.getId(), dto.getId());
    }

    @Test
    void getAllMoments() {
        Long momentId = 1L;
        Moment moment = new Moment();
        moment.setId(momentId);
        when(momentRepository.findAll()).thenReturn(List.of(moment));

        List<MomentDto> dtos = momentServiceImpl.getAllMoments();

        Assertions.assertEquals(1, dtos.size());
    }
}

