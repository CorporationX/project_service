package faang.school.projectservice.service.moment;

import faang.school.projectservice.exception.ResourceNotFoundException;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {

    @InjectMocks
    private MomentService momentService;

    @Mock
    private MomentRepository momentRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private List<MomentFilter> momentFilter;

    @Captor
    private ArgumentCaptor<List<Long>> idsArgumentCaptor;

    @Captor
    private ArgumentCaptor<Moment> momentCaptor;

    @Test
    public void testCreateMoment_shouldCombineProjectIdsCorrectlyWhenProjectIdsNotNull() {
        long userId = 1L;
        long projectId = 1L;
        Moment momentRequest = Moment.builder().build();
        List<Long> projectIds = List.of(1L, 2L, 3L);

        List<Long> expectedProjectIds = new ArrayList<>();
        expectedProjectIds.addAll(projectIds);
        expectedProjectIds.add(projectId);

        momentService.createMoment(userId, projectId, momentRequest, projectIds);

        verify(projectService, times(1))
                .getProjectsByIds(idsArgumentCaptor.capture(), eq(userId));

        assertEquals(expectedProjectIds, idsArgumentCaptor.getValue());
    }

    @Test
    public void testCreateMoment_isGetProjectByIdBeingCalledCorrectly() {
        long userId = 1L;
        long projectId = 1L;
        Moment momentRequest = Moment.builder().build();
        List<Long> projectIds = Collections.emptyList();

        momentService.createMoment(userId, projectId, momentRequest, projectIds);

        verify(projectService, times(1))
                .getProjectById(eq(projectId), eq(userId));
    }

    @Test
    public void testCreateMoment_isGetUserIdsByProjectIdsBeingCalledCorrectly() {
        long userId = 1L;
        long projectId = 1L;
        Moment momentRequest = Moment.builder().build();
        List<Long> projectIds = List.of(1L, 2L, 3L);

        List<Long> expectedProjectIds = new ArrayList<>();
        expectedProjectIds.addAll(projectIds);
        expectedProjectIds.add(projectId);

        momentService.createMoment(userId, projectId, momentRequest, projectIds);

        verify(projectService, times(1))
                .getUserIdsByProjectIds(eq(expectedProjectIds));
    }

    @Test
    public void testCreateMoment_momentHasCorrectData() {
        long userId = 1L;
        long projectId = 1L;
        Moment momentRequest = Moment.builder().build();
        List<Long> projectIds = List.of(1L, 2L, 3L);

        List<Long> expectedProjectIds = new ArrayList<>();
        expectedProjectIds.addAll(projectIds);
        expectedProjectIds.add(projectId);
        expectedProjectIds.addAll(projectIds);

        List<Long> mockUserIds = List.of(1L, 2L, 3L, 4L, 5L);

        List<Project> mockProjectsList = new ArrayList<>();
        expectedProjectIds.forEach(currentProjectId -> {
            Project project = Project.builder()
                    .id(currentProjectId)
                    .build();
            mockProjectsList.add(project);
        });

        when(projectService.getProjectsByIds(eq(expectedProjectIds), eq(userId)))
                .thenReturn(mockProjectsList);

        when(projectService.getUserIdsByProjectIds(eq(expectedProjectIds)))
                .thenReturn(mockUserIds);

        momentService.createMoment(userId, projectId, momentRequest, projectIds);

        verify(momentRepository, times(1))
                .save(momentCaptor.capture());

        Moment actualMoment = momentCaptor.getValue();

        assertEquals(mockProjectsList, actualMoment.getProjects());
        assertEquals(mockUserIds, actualMoment.getUserIds());
        assertEquals(userId, actualMoment.getCreatedBy());
        assertEquals(userId, actualMoment.getUpdatedBy());
    }

    @Test
    public void testUpdateMoment_shouldThrowResourceNotFoundExceptionWhenMomentIdNotFound() {
        long userId = 1L;
        long momentId = 1L;
        Moment momentRequest = Moment.builder().build();
        List<Long> projectIds = Collections.emptyList();

        when(momentRepository.findById(eq(momentId)))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> momentService.updateMoment(userId, momentId, momentRequest, projectIds));
    }

    @Test
    public void testUpdateMoment_shouldThrowIllegalArgumentExceptionWhenMomentNotHaveAtLeastOneProject() {
        long userId = 1L;
        long momentId = 1L;
        Moment momentRequest = Moment.builder().build();
        List<Long> projectIds = Collections.emptyList();

        Moment mockMoment = Moment.builder().build();

        when(momentRepository.findById(eq(momentId)))
                .thenReturn(Optional.ofNullable(mockMoment));

        when(projectService.getProjectsByIds(eq(projectIds), eq(userId)))
                .thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class,
                () -> momentService.updateMoment(userId, momentId, momentRequest, projectIds));
    }

    @Test
    public void testUpdateMoment_getUserIdsByProjectIdsBeingCalledCorrectly() {
        long userId = 1L;
        long momentId = 1L;
        Moment momentRequest = Moment.builder().build();

        List<Long> expectedProjectIds = List.of(1L, 2L, 3L, 4L, 5L);

        List<Project> mockProjectsList = new ArrayList<>();
        expectedProjectIds.forEach(currentProjectId -> {
            Project project = Project.builder()
                    .id(currentProjectId)
                    .build();
            mockProjectsList.add(project);
        });

        Moment mockMoment = Moment.builder().build();

        when(momentRepository.findById(eq(momentId)))
                .thenReturn(Optional.ofNullable(mockMoment));

        when(projectService.getProjectsByIds(eq(expectedProjectIds), eq(userId)))
                .thenReturn(mockProjectsList);

        momentService.updateMoment(userId, momentId, momentRequest, expectedProjectIds);

        verify(projectService, times(1))
                .getUserIdsByProjectIds(idsArgumentCaptor.capture());

        assertEquals(expectedProjectIds, idsArgumentCaptor.getValue());
    }

    @Test
    public void testUpdateMoment_momentHasCorrectData() {
        long userId = 1L;
        long momentId = 1L;
        LocalDateTime pastMomentDateTime = LocalDateTime.of(2025, 2, 22, 8, 32);
        Moment pastMoment = buildMoment(
                momentId,
                "Moment1",
                "Desc1",
                pastMomentDateTime,
                2,
                6);

        LocalDateTime momentRequestDateTime = LocalDateTime.of(2025, 1, 22, 8, 32);
        Moment momentRequest = buildMoment(
                momentId,
                "Moment2",
                "Desc2",
                momentRequestDateTime,
                1,
                5);

        List<Long> mockProjectIds = List.of(1L, 2L, 3L, 4L, 5L);
        List<Long> mockUserIds = List.of(1L, 2L, 3L, 4L, 5L);

        when(momentRepository.findById(pastMoment.getId()))
                .thenReturn(Optional.of(pastMoment));

        when(projectService.getProjectsByIds(mockProjectIds, userId))
                .thenReturn(momentRequest.getProjects());

        when(projectService.getUserIdsByProjectIds(mockProjectIds))
                .thenReturn(mockUserIds);

        momentService.updateMoment(userId, momentId, momentRequest, mockProjectIds);

        verify(momentRepository, times(1))
                .save(momentCaptor.capture());

        Moment actualMoment = momentCaptor.getValue();

        assertEquals(momentRequest.getName(), actualMoment.getName());
        assertEquals(momentRequest.getDescription(), actualMoment.getDescription());
        assertEquals(momentRequest.getDate(), actualMoment.getDate());
        assertEquals(momentRequest.getProjects(), actualMoment.getProjects());
        assertEquals(momentRequest.getUserIds(), actualMoment.getUserIds());
        assertEquals(userId, actualMoment.getUpdatedBy());
    }

    @Test
    public void testGetAllMomentsByProjectId_getAllByProjectIdBeingCalledCorrectly() {
        long projectId = 1L;

        momentService.getAllMomentsByProjectId(projectId, null);

        verify(momentRepository, times(1))
                .getAllByProjectId(eq(projectId));
    }

    @Test
    public void testGetMomentById_shouldThrowResourceNotFoundExceptionWhenMomentIdNonExisting() {
        long momentId = 1L;

        when(momentRepository.findById(momentId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> momentService.getMomentById(momentId));
    }

    private Moment buildMoment(
            long id,
            String name,
            String desc,
            LocalDateTime date,
            long rangeStartForProjectsAndUsers,
            long rangeEndForProjectsAndUsers) {

        List<Project> projects = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();

        LongStream.rangeClosed(rangeStartForProjectsAndUsers, rangeEndForProjectsAndUsers)
                .forEach(num -> {
                    Project project = Project.builder()
                            .id(num)
                            .build();

                    projects.add(project);
                    userIds.add(num);
                });

        return Moment.builder()
                .id(id)
                .name(name)
                .description(desc)
                .projects(projects)
                .userIds(userIds)
                .date(date)
                .build();
    }
}
