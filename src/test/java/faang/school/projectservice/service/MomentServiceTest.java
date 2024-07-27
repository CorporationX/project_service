package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.filter.MomentFilter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.MomentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {
    @Mock
    private MomentRepository momentRepository;

    @Spy
    private MomentMapper mapper = Mappers.getMapper(MomentMapper.class);

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private MomentValidator momentValidator;

    @Mock
    private List<MomentFilter> momentFilters;

    @InjectMocks
    private MomentService momentService;

    private MomentDto momentDto;
    private Moment moment;
    private Project project;
    private Team team;
    private TeamMember member;

    @BeforeEach
    public void setUp() {
        momentDto = new MomentDto(1L, "dto", Collections.emptyList(), List.of(1L), List.of(1L), LocalDateTime.now());
        member = TeamMember.builder().id(1L).userId(1L).build();
        team = Team.builder().id(1L).teamMembers(List.of(member)).build();
        project = Project.builder().id(1L).name("project").teams(List.of(team)).build();
        team.setProject(project);
        member.setTeam(team);
        moment = Moment.builder().id(1L).name("moment").projects(List.of(project)).userIds(List.of(1L)).members(List.of(member)).build();
    }

    @Test
    public void createMomentTest() {
        Mockito.doNothing().when(momentValidator).validateMoment(moment);
        Mockito.doNothing().when(momentValidator).validateProject(project);
        Mockito.when(teamMemberRepository.findById(1L)).thenReturn(member);
        Mockito.when(momentRepository.save(Mockito.any(Moment.class))).thenReturn(moment);
        momentService.createMoment(momentDto);
        Mockito.verify(momentValidator).validateMoment(moment);
        Mockito.verify(momentValidator).validateProject(project);
        Mockito.verify(momentRepository).save(moment);
    }

}
