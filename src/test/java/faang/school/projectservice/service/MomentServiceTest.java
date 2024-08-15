package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.MomentFilter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.validator.MomentValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {

    @Spy
    private MomentMapper momentMapper;
    @Mock
    private MomentValidator momentValidator;
    @Mock
    private MomentRepository momentRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private MomentFilter filterMock;
    private List<MomentFilter> filters;
    @InjectMocks
    private MomentService momentService;

    private Moment moment;
    private MomentDto momentDto;
    private Team team;
    private TeamMember teamMember;
    private Project project;

    @BeforeEach
    public void setUp() {
        project = Project.builder().
                id(1L)
                .name("project")
                .build();
        moment = Moment.builder().
                id(1L).
                name("momentName")
                .date(LocalDateTime.of(2021, 1, 1, 1, 1))
                .projects(new ArrayList<>(List.of(project)))
                .build();
        momentDto = MomentDto.builder()
                .id(1L).
                name("momentName")
                .projectIds(new ArrayList<>(List.of(1L)))
                .build();
        teamMember = TeamMember.builder()
                .userId(4L)
                .build();
        team = Team.builder()
                .id(3L)
                .teamMembers(List.of(teamMember))
                .build();
        filterMock = Mockito.mock(MomentFilter.class);
        filters = List.of(filterMock);
        momentService = new MomentService(projectService, momentRepository, filters, momentValidator);
        momentMapper = Mappers.getMapper(MomentMapper.class);
    }

    @Test
    public void testCreate() {
        project.setTeams(List.of(team));
        momentDto.getProjectIds().add(1L);
        Mockito.when(projectService.getProjectById(Mockito.anyLong())).thenReturn(project);
        Mockito.when(momentRepository.save(Mockito.any(Moment.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.doNothing().when(momentValidator).validateProject(Mockito.any(Project.class));
        MomentDto actualMoment = momentService.create(momentDto);
        Mockito.verify(momentRepository, Mockito.times(1)).save(Mockito.any(Moment.class));
        Mockito.verify(momentValidator, Mockito.times(1)).validateProject(Mockito.any(Project.class));
        Mockito.verify(projectService, Mockito.times(2)).getProjectById(Mockito.anyLong());
        Assertions.assertEquals(List.of(4L), actualMoment.getUserIds());
        Assertions.assertEquals(List.of(1L), actualMoment.getProjectIds());
    }

    @Test
    public void testUpdate() {
        Project newProject = Project.builder()
                .id(2L)
                .name("project")
                .build();
        TeamMember newTeamMember = TeamMember.builder()
                .userId(5L)
                .build();
        Team newTeam = Team.builder()
                .id(3L)
                .teamMembers(new ArrayList<>(List.of(teamMember, newTeamMember)))
                .build();
        momentDto.getProjectIds().add(2L);
        momentDto.getProjectIds().remove(0);
        momentDto.setDate(LocalDateTime.of(2024, 1, 1, 1, 1));
        newProject.setTeams(List.of(newTeam));
        Mockito.when(momentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(moment));
        Mockito.when(projectService.getProjectById(Mockito.anyLong())).thenReturn(newProject);
        Mockito.when(momentRepository.save(Mockito.any(Moment.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.doNothing().when(momentValidator).validateProject(Mockito.any(Project.class));
        MomentDto actualMoment = momentService.update(momentDto);
        Mockito.verify(momentRepository, Mockito.times(1)).save(Mockito.any(Moment.class));
        Mockito.verify(momentValidator, Mockito.times(1)).validateProject(Mockito.any(Project.class));
        Mockito.verify(projectService, Mockito.times(1)).getProjectById(Mockito.anyLong());
        Assertions.assertEquals(List.of(2L), actualMoment.getProjectIds());
        Assertions.assertEquals(LocalDateTime.of(2024, 1, 1, 1, 1), actualMoment.getDate());
        Assertions.assertEquals(List.of(4L, 5L), actualMoment.getUserIds());
    }

    @Test
    public void testUpdateNoneExistingMoment() {
        Mockito.when(momentRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> momentService.update(momentDto));
    }

    @Test
    public void testGetMomentsFiltered() {
        MomentFilterDto momentFilterDto = new MomentFilterDto(1L, "name",
                LocalDateTime.of(2000, 10, 1, 1, 1));
        Moment anotherMoment = Moment.builder().id(1L).name("momentName").projects(List.of(project)).
                userIds(List.of(1L, 2L)).
                date(LocalDateTime.of(2000, 10, 10, 0, 0)).build();
        moment.setDate(LocalDateTime.of(2000, 10, 10, 0, 0));
        List<Moment> momentsToFilter = List.of(anotherMoment, moment);
        Mockito.when(momentValidator.getMomentsAttachedToProject(1L)).thenReturn(momentsToFilter);
        Mockito.when(filters.get(0).isApplicable(momentFilterDto)).thenReturn(true);
        Mockito.when(filters.get(0).apply(momentsToFilter, momentFilterDto)).thenReturn(momentsToFilter.stream());
        List<MomentDto> actualMomentsDto = momentService.getMomentsFiltered(1L, momentFilterDto);
        Mockito.verify(momentValidator).getMomentsAttachedToProject(1L);
        Mockito.verify(filterMock).isApplicable(momentFilterDto);
        Mockito.verify(filterMock).apply(momentsToFilter, momentFilterDto);
        Assert.assertEquals(momentsToFilter.stream().map(moment -> momentMapper.toDto(moment)).toList(), actualMomentsDto);
    }

    @Test
    public void testGetAllMoments() {
        List<Moment> singleMoment = List.of(moment);
        Mockito.when(momentValidator.getMomentsAttachedToProject(1L)).thenReturn(singleMoment);
        List<MomentDto> actual = momentService.getAllMoments(1L);
        Assertions.assertEquals(singleMoment.stream().map(moment -> momentMapper.toDto(moment)).toList(), actual);
    }

    @Test
    public void testGetMomentById() {
        momentDto.setDate(LocalDateTime.of(2021, 1, 1, 1, 1));
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