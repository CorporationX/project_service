package faang.school.projectservice.service;


import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.MomentFilter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.MomentValidator;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {
    @Spy
    private MomentMapperImpl momentMapper;
    @Mock
    private MomentRepository momentRepository;
    @Mock
    private MomentValidator momentValidator;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private MomentFilter filterMock;
    private List<MomentFilter> filters;

    @InjectMocks
    private MomentService momentService;

    private Moment moment;
    private Project project;
    private MomentDto momentDto;
    private Team team;
    private TeamMember teamMember;

    @BeforeEach
    void setUp() {
        project = Project.builder().id(1L).name("projectName").build();
        moment = Moment.builder().id(1L).name("momentName").projects(List.of(project)).build();
        momentDto = MomentDto.builder().id(1L).name("momentName")
                .projectIds(List.of(1L)).build();
        teamMember = TeamMember.builder().userId(4L).build();
        team = Team.builder().id(3L).teamMembers(List.of(teamMember)).build();
        filterMock = Mockito.mock(MomentFilter.class);
        filters = List.of(filterMock);
        momentService = new MomentService(momentRepository, projectRepository, momentValidator, filters);
    }

    @Test
    public void testCreateMoment() {
        Mockito.when(momentRepository.save(Mockito.any(Moment.class))).thenReturn(moment);
        Mockito.when(projectRepository.getProjectById(Mockito.anyLong())).thenReturn(project);
        momentService.createMoment(momentDto);
        Mockito.verify(momentRepository).save(Mockito.any(Moment.class));
    }

    @Test
    public void testUpdateMoment() {
        momentDto.setUserIds(List.of(1L, 2L));
        project.setTeams(List.of(team));
        Moment updatedMoment = Moment.builder().id(1L).name("momentName").projects(List.of(project)).
                userIds(List.of(1L, 2L, 4L)).build();

        Mockito.when(momentRepository.findById(1L)).thenReturn(Optional.of(moment));
        Mockito.when(momentRepository.save(Mockito.any(Moment.class))).thenReturn(moment);
        Mockito.when(projectRepository.getProjectById(1L)).thenReturn(project);

        momentService.updateMoment(momentDto);
        Mockito.verify(momentRepository).save(Mockito.any(Moment.class));
        Mockito.verify(projectRepository).getProjectById(1L);
        Assert.assertEquals(moment, updatedMoment);
    }

    @Test
    public void testUpdateNonexistentMoment() {
        Mockito.when(momentRepository.findById(1L)).thenReturn(Optional.empty());
        Assert.assertThrows(DataValidationException.class, () -> momentService.updateMoment(momentDto));
    }

    @Test
    public void testGetMomentsFilteredByDateFromProjects() {
        MomentFilterDto momentFilterDto = new MomentFilterDto(1L, "name",
                LocalDateTime.of(2000, 10, 1, 1, 1));
        Moment anotherMoment = Moment.builder().id(1L).name("momentName").projects(List.of(project)).
                userIds(List.of(1L, 2L)).
                date(LocalDateTime.of(2000, 10, 10, 0, 0)).build();
        moment.setDate(LocalDateTime.of(2000, 10, 10, 0, 0));
        List<Moment> momentsToFilter = List.of(anotherMoment, moment);

        Mockito.when(momentRepository.findAllByProjectId(1L)).thenReturn(momentsToFilter);
        Mockito.when(filters.get(0).isApplicable(momentFilterDto)).thenReturn(true);
        Mockito.when(filters.get(0).apply(momentsToFilter, momentFilterDto)).thenReturn(momentsToFilter.stream());

        List<MomentDto> actualMomentsDto = momentService.getMomentsFilteredByDateFromProjects(1L, momentFilterDto);
        Mockito.verify(momentRepository).findAllByProjectId(1L);
        Mockito.verify(filterMock).isApplicable(momentFilterDto);
        Mockito.verify(filterMock).apply(momentsToFilter, momentFilterDto);
        Assert.assertEquals(momentsToFilter.stream().map(moment -> momentMapper.toDto(moment)).toList(), actualMomentsDto);
    }

    @Test
    public void testGetAllMoments() {
        List<Moment> singleMoment = List.of(moment);
        Mockito.when(momentRepository.findAllByProjectId(1L)).thenReturn(singleMoment);
        List<MomentDto> actual = momentService.getAllMoments(1L);
        Assertions.assertEquals(singleMoment.stream().map(moment -> momentMapper.toDto(moment)).toList(), actual);
    }

    @Test
    public void testGetAllMomentsByIncorrectProjectId() {
        Mockito.when(momentRepository.findAllByProjectId(1L)).thenReturn(Collections.emptyList());
        Assertions.assertThrows(DataValidationException.class, () -> momentService.getAllMoments(1L));
    }

    @Test
    public void testGetMomentById() {
        momentDto.setUserIds(Collections.emptyList());
        Mockito.when(momentRepository.findById(1L)).thenReturn(Optional.of(moment));
        MomentDto actualDto = momentService.getMomentById(1L);
        Mockito.verify(momentRepository).findById(1L);
        Assertions.assertEquals(momentDto, actualDto);
    }

    @Test
    public void testGetMomentByNoneExistentId() {
        Mockito.when(momentRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(DataValidationException.class, () -> momentService.getMomentById(1L));
    }
}
