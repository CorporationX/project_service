package faang.school.projectservice.moment.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.adapter.moment.MomentRepositoryAdapter;
import faang.school.projectservice.repository.adapter.project.ProjectRepositoryAdapter;
import faang.school.projectservice.repository.adapter.team.TeamRepositoryAdapter;
import faang.school.projectservice.repository.adapter.teammember.TeamMemberRepoAdapter;
import faang.school.projectservice.service.MomentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {
    @Mock
    private MomentRepositoryAdapter momentRepositoryAdapter;

    @Mock
    private ProjectRepositoryAdapter projectRepositoryAdapter;

    @Mock
    private TeamRepositoryAdapter teamRepositoryAdapter;

    @Mock
    private TeamMemberRepoAdapter teamMemberRepoAdapter;

    @Mock
    private MomentMapper momentMapper;

    @InjectMocks
    private MomentService momentService;

    private MomentDto inputDto;
    private Project mainProject;
    private Project partnerProject;
    private Moment entity;
    private Moment savedEntity;
    private MomentDto outputDto;
    private List<MomentFilter> momentFilters;

    @BeforeEach
    void setUp() {
        inputDto = new MomentDto();
        inputDto.setProjectId(1L);
        inputDto.setProjectIds(Arrays.asList(1L, 2L));
        inputDto.setName("Test Moment");
        inputDto.setDate(LocalDateTime.of(2025, 5, 18, 10, 0));

        mainProject = new Project();
        mainProject.setId(1L);

        partnerProject = new Project();
        partnerProject.setId(2L);

        entity = new Moment();
        entity.setProjects(new ArrayList<>());
        entity.setUserIds(new ArrayList<>());

        savedEntity = new Moment();
        savedEntity.setId(100L);

        outputDto = new MomentDto();
        outputDto.setId(100L);

        momentFilters = new ArrayList<>();
        MomentFilter filter1 = Mockito.mock(MomentFilter.class);
        MomentFilter filter2 = Mockito.mock(MomentFilter.class);
        momentFilters.add(filter1);
        momentFilters.add(filter2);

        ReflectionTestUtils.setField(momentService, "momentFilters", momentFilters);

    }

    @Test
    public void testCreateMoment_withNoPartners() {
        inputDto.setProjectIds(null);
        Mockito.when(projectRepositoryAdapter.getProjectById(1L)).thenReturn(mainProject);
        Mockito.when(momentMapper.toEntity(inputDto)).thenReturn(entity);
        Mockito.when(momentRepositoryAdapter.save(entity)).thenReturn(entity);
        Mockito.when(momentMapper.toDto(entity)).thenReturn(outputDto);

        MomentDto out = momentService.createMoment(inputDto);

        verify(projectRepositoryAdapter).getProjectById(1L);
        verify(projectRepositoryAdapter, never()).getAllProjectsById(anyList());

        ArgumentCaptor<Moment> momentCaptor = ArgumentCaptor.forClass(Moment.class);
        verify(momentRepositoryAdapter).save(momentCaptor.capture());
        assertThat(momentCaptor.getValue().getProjects()).containsExactly(mainProject);
        assertThat(out).isEqualTo(outputDto);
    }

    @Test
    public void testCreateMoment_withDuplicates() {
        when(projectRepositoryAdapter.getProjectById(1L)).thenReturn(mainProject);
        when(projectRepositoryAdapter.getAllProjectsById(Arrays.asList(1L, 2L)))
                .thenReturn(Arrays.asList(mainProject, partnerProject));
        when(momentMapper.toEntity(inputDto)).thenReturn(entity);
        when(momentRepositoryAdapter.save(entity)).thenReturn(entity);
        when(momentMapper.toDto(entity)).thenReturn(outputDto);

        MomentDto out = momentService.createMoment(inputDto);

        ArgumentCaptor<Moment> momentCaptor = ArgumentCaptor.forClass(Moment.class);
        verify(momentRepositoryAdapter).save(momentCaptor.capture());
        assertThat(momentCaptor.getValue().getProjects())
                .containsExactly(mainProject, partnerProject);
        assertThat(out).isEqualTo(outputDto);
    }

    @Test
    public void testCreateMoment_withPartners() {
        inputDto.setProjectIds(Collections.singletonList(2L));
        when(projectRepositoryAdapter.getProjectById(1L)).thenReturn(mainProject);
        when(projectRepositoryAdapter.getAllProjectsById(Collections.singletonList(2L)))
                .thenReturn(Collections.singletonList(partnerProject));
        when(momentMapper.toEntity(inputDto)).thenReturn(entity);
        when(momentRepositoryAdapter.save(entity)).thenReturn(entity);
        when(momentMapper.toDto(entity)).thenReturn(outputDto);

        MomentDto out = momentService.createMoment(inputDto);

        ArgumentCaptor<Moment> momentCaptor = ArgumentCaptor.forClass(Moment.class);
        verify(momentRepositoryAdapter).save(momentCaptor.capture());
        assertThat(momentCaptor.getValue().getProjects())
                .containsExactly(mainProject, partnerProject);
        assertThat(out).isEqualTo(outputDto);
    }

    @Test
    public void testCreateMoment_mapperRepoInteraction() {
        when(projectRepositoryAdapter.getProjectById(1L)).thenReturn(mainProject);
        when(momentMapper.toEntity(inputDto)).thenReturn(entity);
        when(momentRepositoryAdapter.save(entity)).thenReturn(savedEntity);
        when(momentMapper.toDto(savedEntity)).thenReturn(outputDto);

        MomentDto out = momentService.createMoment(inputDto);

        verify(momentMapper).toEntity(inputDto);
        verify(momentRepositoryAdapter).save(entity);
        verify(momentMapper).toDto(savedEntity);
        assertThat(out).isEqualTo(outputDto);
    }

    @Test
    public void testCreateMoment_canceledProject_Throws() {
        when(projectRepositoryAdapter.getProjectById(1L))
                .thenThrow(new IllegalArgumentException("Project not found"));

        assertThatThrownBy(() -> momentService.createMoment(inputDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Project not found");
    }

    @Test
    public void testUpdateMoment_fieldsUpdated() {
        when(momentRepositoryAdapter.getMomentById(5L)).thenReturn(entity);
        inputDto.setProjectId(null);
        inputDto.setName("Updated");
        inputDto.setDescription("Desc");
        inputDto.setDate(LocalDateTime.of(2025, 5, 19, 15, 0));
        inputDto.setImageId("imgX");

        when(momentRepositoryAdapter.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(momentMapper.toDto(any())).thenReturn(outputDto);

        MomentDto result = momentService.updateMoment(5L, inputDto);

        assertThat(entity.getName()).isEqualTo("Updated");
        assertThat(entity.getDescription()).isEqualTo("Desc");
        assertThat(entity.getDate()).isEqualTo(inputDto.getDate());
        assertThat(entity.getImageId()).isEqualTo("imgX");
        assertThat(result).isEqualTo(outputDto);
    }

    @Test
    public void testUpdateMoment_associations() {
        entity.setProjects(Collections.singletonList(mainProject)); // проект с ID=1
        entity.setUserIds(Collections.singletonList(100L));
        when(momentRepositoryAdapter.getMomentById(5L)).thenReturn(entity);

        inputDto.setProjectIds(Collections.singletonList(2L));
        inputDto.setUserIds(Collections.singletonList(200L));

        Team t1 = new Team();
        t1.setProject(mainProject);
        TeamMember m1 = new TeamMember();
        m1.setUserId(300L);
        t1.setTeamMembers(Collections.singletonList(m1));

        when(teamRepositoryAdapter.getTeamsByProjectIds(anyCollection()))
                .thenReturn(Collections.singletonList(t1));


        Project p3 = new Project(); p3.setId(3L);
        Team t3 = new Team(); t3.setProject(p3);
        TeamMember m2 = new TeamMember();
        m2.setUserId(200L);
        m2.setTeam(t3);

        when(teamMemberRepoAdapter.getTeamMembersByUserIds(anyCollection()))
                .thenAnswer(inv -> {
                    Collection<Long> uids = inv.getArgument(0);
                    if (uids.contains(200L)) {
                        return Collections.singletonList(m2);
                    }
                    return Collections.emptyList();
                });

        when(projectRepositoryAdapter.getAllProjectsById(anyList()))
                .thenReturn(Arrays.asList(mainProject, partnerProject, p3));

        when(momentRepositoryAdapter.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(momentMapper.toDto(any())).thenReturn(outputDto);

        MomentDto result = momentService.updateMoment(5L, inputDto);

        ArgumentCaptor<Moment> captor = ArgumentCaptor.forClass(Moment.class);
        verify(momentRepositoryAdapter).save(captor.capture());
        Moment saved = captor.getValue();

        assertThat(saved.getProjects()).extracting(Project::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L);
        assertThat(saved.getUserIds())
                .containsExactlyInAnyOrder(100L, 200L, 300L);
        assertThat(result).isEqualTo(outputDto);

        verify(teamRepositoryAdapter).getTeamsByProjectIds(anyCollection());
        verify(teamMemberRepoAdapter).getTeamMembersByUserIds(anyCollection());
        verify(projectRepositoryAdapter).getAllProjectsById(anyList());
    }

    @Test
    public void testUpdateMoment_noDtoChanges() {
        entity.setProjects(Collections.singletonList(mainProject));
        entity.setUserIds(Collections.singletonList(100L));

        when(momentRepositoryAdapter.getMomentById(5L)).thenReturn(entity);

        inputDto.setProjectIds(null);
        inputDto.setUserIds(null);

        when(teamRepositoryAdapter.getTeamsByProjectIds(anyCollection()))
                .thenReturn(Collections.emptyList());
        when(teamMemberRepoAdapter.getTeamMembersByUserIds(anyCollection()))
                .thenReturn(Collections.emptyList());
        when(projectRepositoryAdapter.getAllProjectsById(anyList()))
                .thenReturn(Collections.singletonList(mainProject));

        when(momentRepositoryAdapter.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));
        when(momentMapper.toDto(any()))
                .thenReturn(outputDto);

        MomentDto out = momentService.updateMoment(5L, inputDto);

        assertThat(entity.getProjects()).containsExactly(mainProject);
        assertThat(entity.getUserIds()).containsExactly(100L);
        assertThat(out).isEqualTo(outputDto);

        verify(teamRepositoryAdapter).getTeamsByProjectIds(anyCollection());
        verify(teamMemberRepoAdapter).getTeamMembersByUserIds(anyCollection());
        verify(projectRepositoryAdapter).getAllProjectsById(anyList());
    }

    @Test
    public void testGetMomentsWithFilter_noApplicableFilters() {
        Moment m1 = new Moment();
        m1.setId(1L);

        Moment m2 = new Moment();
        m2.setId(2L);

        when(momentRepositoryAdapter.getAllMoments())
                .thenReturn(Arrays.asList(m1, m2));

        when(momentFilters.get(0).isApplicable(inputDto)).thenReturn(false);
        when(momentFilters.get(1).isApplicable(inputDto)).thenReturn(false);

        MomentDto dto1 = new MomentDto();
        dto1.setId(1L);

        MomentDto dto2 = new MomentDto();
        dto2.setId(2L);

        when(momentMapper.toDto(m1)).thenReturn(dto1);
        when(momentMapper.toDto(m2)).thenReturn(dto2);

        List<MomentDto> result = momentService.getMomentsWithFilter(inputDto);

        assertThat(result).containsExactly(dto1, dto2);

        verify(momentFilters.get(0), never()).apply(any(), any());
        verify(momentFilters.get(1), never()).apply(any(), any());
    }

    @Test
    public void testGetMomentsWithFilter_oneFilterApplied() {
        Moment m1 = new Moment();
        m1.setId(1L);

        Moment m2 = new Moment();
        m2.setId(2L);

        Moment m3 = new Moment();
        m3.setId(3L);

        when(momentRepositoryAdapter.getAllMoments())
                .thenReturn(Arrays.asList(m1, m2, m3));

        MomentFilter filterA = momentFilters.get(0);
        when(filterA.isApplicable(inputDto)).thenReturn(true);
        when(filterA.apply(any(), eq(inputDto)))
                .thenAnswer(inv -> {
                    Stream<Moment> in = inv.getArgument(0);
                    return in.filter(m -> m.getId() > 1);
                });

        MomentFilter filterB = momentFilters.get(1);
        when(filterB.isApplicable(inputDto)).thenReturn(false);

        MomentDto dto2 = new MomentDto();
        dto2.setId(2L);

        MomentDto dto3 = new MomentDto();
        dto3.setId(3L);

        when(momentMapper.toDto(m2)).thenReturn(dto2);
        when(momentMapper.toDto(m3)).thenReturn(dto3);

        List<MomentDto> result = momentService.getMomentsWithFilter(inputDto);

        assertThat(result).containsExactly(dto2, dto3);

        verify(filterA).apply(any(), eq(inputDto));
        verify(filterB, never()).apply(any(), any());
    }

    @Test
    public void testGetAllMoments_returnsAllMoments() {
        Moment moment1 = new Moment();
        moment1.setId(1L);

        Moment moment2 = new Moment();
        moment2.setId(2L);

        List<Moment> moments = Arrays.asList(moment1, moment2);
        when(momentRepositoryAdapter.getAllMoments()).thenReturn(moments);

        MomentDto momentDto1 = new MomentDto();
        momentDto1.setId(1L);

        MomentDto momentDto2 = new MomentDto();
        momentDto2.setId(2L);

        when(momentMapper.toDto(moment1)).thenReturn(momentDto1);
        when(momentMapper.toDto(moment2)).thenReturn(momentDto2);

        List<MomentDto> result = momentService.getAllMoments();

        assertThat(result).containsExactly(momentDto1, momentDto2);

        verify(momentRepositoryAdapter).getAllMoments();
        verify(momentMapper).toDto(moment1);
        verify(momentMapper).toDto(moment2);
    }

    @Test
    public void testGetAllMoments_emptyList() {
        when(momentRepositoryAdapter.getAllMoments()).thenReturn(Collections.emptyList());

        List<MomentDto> result = momentService.getAllMoments();

        assertThat(result).isEmpty();
        verify(momentRepositoryAdapter).getAllMoments();
        verifyNoInteractions(momentMapper);
    }

    @Test
    public void testGetMomentById_validId_returnsMoment() {
        when(momentRepositoryAdapter.getMomentById(1L)).thenReturn(entity);
        when(momentMapper.toDto(entity)).thenReturn(outputDto);

        MomentDto result = momentService.getMomentById(1L);

        assertThat(result).isEqualTo(outputDto);
        verify(momentRepositoryAdapter).getMomentById(1L);
        verify(momentMapper).toDto(entity);
    }

    @Test
    public void testGetMomentById_invalidId_throwsException() {
        when(momentRepositoryAdapter.getMomentById(999L))
                .thenThrow(new IllegalArgumentException("Moment not found"));

        assertThatThrownBy(() -> momentService.getMomentById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Moment not found");

        verify(momentRepositoryAdapter).getMomentById(999L);
        verifyNoInteractions(momentMapper);
    }
}
