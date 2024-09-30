package faang.school.projectservice.service;

import faang.school.projectservice.exception.MeetValidationException;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.validator.MeetValidator;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MeetServiceTest {
    @Mock
    private MeetValidator meetValidator;
    @Mock
    private MeetMapper meetMapper;
    @Mock
    private ProjectService projectService;
    @Mock
    private MeetRepository meetRepository;
    @Captor
    ArgumentCaptor<Meet> meetArgumentCaptor;
    @Captor
    ArgumentCaptor<Long> meetIdArgumentCaptor;
    @InjectMocks
    private MeetService meetService;

    private final Long userId = 1L;
    private final Long projectId = 1L;
    private final Long meetId = 1L;
    private final Meet firstMeet = new Meet();
    private final Meet secondMeet = new Meet();
    private final Meet thirdMeet = new Meet();
    private final Meet testMeet = new Meet();
    private final Project project = new Project();
    private final List<Meet> meets = new ArrayList<>();

    @BeforeEach
    void init() {
        testMeet.setTitle("first title");
        testMeet.setDescription("first description");
        testMeet.setStatus(MeetStatus.PENDING);
        testMeet.setUserIds(List.of(1L, 2L));

        project.setId(1L);
        project.setMeets(meets);

        firstMeet.setId(1L);
        firstMeet.setProject(project);
        firstMeet.setStatus(MeetStatus.PENDING);
        firstMeet.setTitle("first title");
        firstMeet.setDescription("first description");
        firstMeet.setUserIds(List.of(1L, 2L));
        firstMeet.setCreatorId(1L);
        firstMeet.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 0, 0));

        secondMeet.setId(2L);
        secondMeet.setProject(project);
        secondMeet.setStatus(MeetStatus.PENDING);
        secondMeet.setTitle("second title");
        secondMeet.setDescription("second description");
        secondMeet.setUserIds(List.of(3L, 4L));
        secondMeet.setCreatorId(1L);
        secondMeet.setCreatedAt(LocalDateTime.of(2024, 2, 1, 0, 0, 0));

        thirdMeet.setId(3L);
        thirdMeet.setProject(project);
        thirdMeet.setStatus(MeetStatus.PENDING);
        thirdMeet.setTitle("third title");
        thirdMeet.setDescription("third description");
        thirdMeet.setUserIds(List.of(5L, 6L));
        thirdMeet.setCreatorId(1L);
        thirdMeet.setCreatedAt(LocalDateTime.of(2024, 3, 1, 0, 0, 0));

        meets.add(firstMeet);
        meets.add(secondMeet);
        meets.add(thirdMeet);
    }

    @Test
    void testCreateMeet() {
        when(meetRepository.save(testMeet)).thenReturn(firstMeet);

        var result = meetService.createMeet(userId, projectId, testMeet);

        assertEquals(firstMeet.getTitle(), result.getTitle());
        assertEquals(firstMeet.getDescription(), result.getDescription());
        assertEquals(firstMeet.getProject(), result.getProject());
        assertEquals(userId, result.getCreatorId());
        assertEquals(firstMeet.getStatus(), result.getStatus());
        assertTrue(firstMeet.getUserIds().containsAll(result.getUserIds()));
        assertTrue(result.getUserIds().containsAll(firstMeet.getUserIds()));
    }

    @Test
    void testCreateMeetThrowsProjectNotFoundException() {
        doThrow(EntityNotFoundException.class).when(projectService).getProjectById(projectId);
        assertThrows(EntityNotFoundException.class, () -> meetService.createMeet(userId, projectId, testMeet));
    }

    @Test
    void testCreateMeetThrowsFeignException() {
        doThrow(FeignException.class).when(meetValidator).validateUser(userId);
        assertThrows(FeignException.class, () -> meetService.createMeet(userId, projectId, testMeet));
    }

    @Test
    void testCreateMeetThrowsMeetValidationException() {
        doThrow(MeetValidationException.class).when(meetValidator).validateParticipants(testMeet.getUserIds());
        assertThrows(MeetValidationException.class, () -> meetService.createMeet(userId, projectId, testMeet));
    }

    @Test
    void testUpdateMeet() {
        Meet updatedMeet = new Meet();
        updatedMeet.setId(secondMeet.getId());
        updatedMeet.setTitle(testMeet.getTitle());
        updatedMeet.setDescription(testMeet.getDescription());
        updatedMeet.setCreatorId(secondMeet.getCreatorId());
        updatedMeet.setStatus(testMeet.getStatus());
        updatedMeet.setUserIds(testMeet.getUserIds());

        when(meetRepository.findById(secondMeet.getId())).thenReturn(Optional.of(secondMeet));
        when(meetMapper.updateEntity(testMeet, secondMeet)).thenReturn(updatedMeet);
        when(meetRepository.save(updatedMeet)).thenReturn(updatedMeet);

        var result = meetService.updateMeet(userId, secondMeet.getId(), testMeet);

        assertEquals(testMeet.getTitle(), result.getTitle());
        assertEquals(testMeet.getDescription(), result.getDescription());
        assertEquals(testMeet.getStatus(), result.getStatus());
        assertEquals(userId, result.getCreatorId());
        assertEquals(secondMeet.getId(), result.getId());
    }

    @Test
    void testUpdateMeetThrowsNotFoundException() {
        when(meetRepository.findById(meetId)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> meetService.updateMeet(userId, meetId, testMeet));
    }

    @Test
    void testUpdateMeetThrowsMeetValidationExceptionOnValidateEditPermission() {
        when(meetRepository.findById(meetId)).thenReturn(Optional.of(firstMeet));
        doThrow(MeetValidationException.class).when(meetValidator).validateEditPermission(userId, firstMeet.getCreatorId());
        assertThrows(MeetValidationException.class, () -> meetService.updateMeet(userId, meetId, testMeet));
    }

    @Test
    void testUpdateMeetThrowsMeetValidationExceptionOn() {
        when(meetRepository.findById(meetId)).thenReturn(Optional.of(firstMeet));
        doThrow(MeetValidationException.class).when(meetValidator).validateParticipants(testMeet.getUserIds());
        assertThrows(MeetValidationException.class, () -> meetService.updateMeet(userId, meetId, testMeet));
    }

    @Test
    void testGetMeetById() {
        when(meetRepository.findById(meetId)).thenReturn(Optional.of(firstMeet));
        var result = meetService.getMeetById(userId, meetId);
        assertEquals(result.getId(), firstMeet.getId());
    }

    @Test
    void testGetMeetByIdThrowsEntityNotFoundException() {
        when(meetRepository.findById(meetId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> meetService.getMeetById(userId, meetId));
    }

    @Test
    void testGetMeetsByProjectId() {
        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(meetRepository.findAllByProject(project)).thenReturn(meets);

        var result = meetService.getMeetsByProjectId(userId, projectId);

        assertTrue(result.containsAll(project.getMeets()));
        assertTrue(project.getMeets().containsAll(result));
    }

    @Test
    void testGetMeetsByProjectIdThrowsEntityNotFoundException() {
        when(projectService.getProjectById(projectId)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(projectId));
    }

    @Test
    void testCancelMeet() {
        when(meetRepository.findById(meetId)).thenReturn(Optional.of(firstMeet));

        meetService.cancelMeet(userId, meetId);

        verify(meetRepository).save(meetArgumentCaptor.capture());
        assertEquals(MeetStatus.CANCELLED, meetArgumentCaptor.getValue().getStatus());
    }

    @Test
    void testDeleteMeet() {
        when(meetRepository.findById(meetId)).thenReturn(Optional.of(firstMeet));

        meetService.deleteMeet(userId, meetId);

        verify(meetRepository).deleteById(meetIdArgumentCaptor.capture());
        assertEquals(meetId, meetIdArgumentCaptor.getValue());
    }
}