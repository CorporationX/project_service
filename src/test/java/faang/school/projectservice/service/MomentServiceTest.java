package faang.school.projectservice.service;


import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.mapper.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {
    @Spy
    MomentMapper momentMapper = new MomentMapperImpl();

    @Mock
    MomentRepository momentRepository;

    @InjectMocks
    MomentService momentService;

    private MomentDto momentDto;
    private Moment moment;
    private Project project;
    private Team team;
    private TeamMember member;

    @BeforeEach
    void setUp() {
        momentDto = MomentDto.builder().id(1L).name("momentDto").projectIds(List.of(1L)).build();
        project = Project.builder().id(1L).name("project").status(ProjectStatus.IN_PROGRESS).build();
        moment = Moment.builder().id(1L).name("moment").projects(List.of(project)).build();


    }

    @Test
    public void TestCreateMoment() {
        Mockito.when(momentRepository.save(Mockito.any(Moment.class))).thenReturn(moment);
        momentService.createMoment(momentDto);
        Mockito.verify(momentRepository).save(Mockito.any(Moment.class));
    }
}
