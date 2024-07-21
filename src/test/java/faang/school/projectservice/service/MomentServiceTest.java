package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.filter.moment.MomentDateFilter;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.jpa.MomentJpaRepository;
import faang.school.projectservice.mapper.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.MomentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static faang.school.projectservice.util.TestMoment.NOW;
import static faang.school.projectservice.util.TestProject.EXPECTED_PROJECT_IDS;
import static faang.school.projectservice.util.TestProject.PROJECTS_ID;
import static faang.school.projectservice.util.TestUser.EXPECTED_USERS_IDS;
import static faang.school.projectservice.util.TestUser.USERS_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {

    @Mock
    MomentJpaRepository momentRepository;
    @Mock
    ProjectRepository projectRepository;
    @Mock
    TeamMemberRepository teamMemberRepository;
    @Mock
    MomentValidator momentValidator;

    @Spy
    private MomentMapperImpl momentMapper;
    @InjectMocks
    MomentService momentService;

    private final List<MomentFilter> momentFilters = new ArrayList<>();

    @BeforeEach
    void init() {
        momentFilters.add(new MomentDateFilter());
        momentService = new MomentService(momentRepository, momentValidator, projectRepository, teamMemberRepository, momentMapper, momentFilters);
    }


    @Test
    public void testCreateMoment() {
        MomentDto momentDto = MomentDto.builder()
                .name("Test")
                .description("Description")
                .date(NOW)
                .projectsId(PROJECTS_ID)
                .usersId(USERS_ID)
                .build();

        doNothing().when(momentValidator).validateMoment(any(), any());

        for (Long projectId : EXPECTED_PROJECT_IDS) {
            when(teamMemberRepository.findAllByProjectId(projectId))
                    .thenReturn(List.of(TeamMember.builder().id(projectId).build()));
        }

        for (Long userId : USERS_ID) {
            when(projectRepository.findProjectByTeamMember(userId)).thenReturn(List.of(Project.builder().id(userId).build()));
        }

        for (Long projectId : PROJECTS_ID) {
            when(projectRepository.getProjectById(projectId)).thenReturn(Project.builder().id(projectId).build());
        }

        momentService.create(momentDto);

        verify(momentValidator, times(1)).validateMoment(any(), any());

        ArgumentCaptor<Moment> captor = ArgumentCaptor.forClass(Moment.class);
        verify(momentRepository, times(1)).save(captor.capture());

        Moment capturedMoment = captor.getValue();
        assertEquals(momentDto.getName(), capturedMoment.getName());
        assertEquals(momentDto.getDescription(), capturedMoment.getDescription());
        assertEquals(momentDto.getDate(), capturedMoment.getDate());
        assertEquals(EXPECTED_PROJECT_IDS.stream()
                        .sorted()
                        .toList(),
                capturedMoment.getProjects().stream()
                        .map(Project::getId)
                        .sorted()
                        .toList());
        assertEquals(EXPECTED_USERS_IDS.stream()
                        .sorted()
                        .toList(),
                capturedMoment.getUserIds().stream()
                        .sorted()
                        .toList());
    }

    @Test
    public void testUpdateMoment() {
        MomentDto momentDto = MomentDto.builder()
                .id(1L)
                .name("Test")
                .description("Description")
                .date(NOW)
                .projectsId(PROJECTS_ID)
                .usersId(USERS_ID)
                .build();

        doNothing().when(momentValidator).existsMoment(anyLong());
        doNothing().when(momentValidator).validateMoment(any(), any());

        for (Long projectId : EXPECTED_PROJECT_IDS) {
            when(teamMemberRepository.findAllByProjectId(projectId))
                    .thenReturn(List.of(TeamMember.builder().id(projectId).build()));
        }

        for (Long userId : USERS_ID) {
            when(projectRepository.findProjectByTeamMember(userId)).thenReturn(List.of(Project.builder().id(userId).build()));
        }

        for (Long projectId : PROJECTS_ID) {
            when(projectRepository.getProjectById(projectId)).thenReturn(Project.builder().id(projectId).build());
        }

        momentService.update(momentDto);

        verify(momentValidator, times(1)).validateMoment(any(), any());

        ArgumentCaptor<Moment> captor = ArgumentCaptor.forClass(Moment.class);
        verify(momentRepository, times(1)).save(captor.capture());

        Moment capturedMoment = captor.getValue();
        assertEquals(momentDto.getName(), capturedMoment.getName());
        assertEquals(momentDto.getDescription(), capturedMoment.getDescription());
        assertEquals(momentDto.getDate(), capturedMoment.getDate());
        assertEquals(EXPECTED_PROJECT_IDS.stream()
                        .sorted()
                        .toList(),
                capturedMoment.getProjects().stream()
                        .map(Project::getId)
                        .sorted()
                        .toList());
        assertEquals(EXPECTED_USERS_IDS.stream()
                        .sorted()
                        .toList(),
                capturedMoment.getUserIds().stream()
                        .sorted()
                        .toList());
    }
}