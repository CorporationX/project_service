package faang.school.projectservice.service.meet;

import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetServiceTest {

    @InjectMocks
    private MeetService meetService;

    @Mock
    private MeetRepository repository;

    @Mock
    private ProjectService projectService;
    private Long projectId;
    private Long creatorId;
    private Meet testMeet;
    private Project project;
    private Long meetId;

    @BeforeEach
    void setUp() {
        projectId = 1L;
        testMeet = new Meet();
        testMeet.setCreatorId(1L);
        testMeet.setTitle("Test Meet");
        project = new Project();
        project.setId(projectId);
        creatorId = 1L;
        meetId = 1L;
    }

    @Test
    void testCreateMeet() {
        when(projectService.findProjectById(projectId)).thenReturn(project);
        when(repository.save(any(Meet.class))).thenReturn(testMeet);

        Meet createdMeet = meetService.create(projectId, testMeet);

        assertNotNull(createdMeet);
        assertEquals(testMeet.getTitle(), createdMeet.getTitle());
        assertEquals(project, createdMeet.getProject());
        verify(repository, times(1)).save(any(Meet.class));
    }

    @Test
    void testUpdateMeet() {
        testMeet.setTitle("Updated Meet");
        Meet existingMeet = new Meet();
        existingMeet.setId(meetId);
        existingMeet.setCreatorId(creatorId);
        existingMeet.setTitle("Original Meet");

        when(repository.findByIdOrThrow(meetId)).thenReturn(existingMeet);
        when(projectService.findProjectById(projectId)).thenReturn(project);
        when(repository.save(any(Meet.class))).thenReturn(testMeet);

        Meet updatedMeet = meetService.update(meetId, projectId, testMeet);

        assertNotNull(updatedMeet);
        assertEquals(testMeet.getTitle(), updatedMeet.getTitle());
        assertEquals(project, updatedMeet.getProject());
        verify(repository, times(1)).save(any(Meet.class));
    }

    @Test
    void testUpdateMeetWrongCreator() {
        long otherCreatorId = 2L;
        testMeet.setCreatorId(otherCreatorId);
        Meet existingMeet = new Meet();
        existingMeet.setId(meetId);
        existingMeet.setCreatorId(creatorId);

        when(repository.findByIdOrThrow(meetId)).thenReturn(existingMeet);

        assertThrows(IllegalArgumentException.class, () -> meetService.update(meetId, projectId, testMeet));
        verify(repository, never()).save(any(Meet.class));
    }

    @Test
    void testCancelMeet() {
        testMeet.setStatus(MeetStatus.PENDING);

        when(repository.findByIdOrThrow(meetId)).thenReturn(testMeet);
        when(repository.save(any(Meet.class))).thenReturn(testMeet);

        meetService.cancelMeet(meetId, creatorId);

        assertEquals(MeetStatus.CANCELLED, testMeet.getStatus());
        verify(repository, times(1)).save(any(Meet.class));
    }

    @Test
    void testDeleteMeet() {
        when(repository.findByIdOrThrow(meetId)).thenReturn(testMeet);

        meetService.deleteMeet(meetId, creatorId);

        verify(repository, times(1)).deleteById(meetId);
    }


    @Test
    void testDeleteMeetWrongCreator() {
        Long otherCreatorId = 2L;

        when(repository.findByIdOrThrow(meetId)).thenReturn(testMeet);

        assertThrows(IllegalArgumentException.class, () -> meetService.deleteMeet(meetId, otherCreatorId));
        verify(repository, never()).deleteById(meetId);
    }

    @Test
    void testGetById() {
        when(repository.findByIdOrThrow(meetId)).thenReturn(testMeet);

        Meet retrievedMeet = meetService.getById(meetId);

        assertEquals(testMeet, retrievedMeet);
    }

    @Test
    void testFindAll() {
        List<Meet> meets = List.of(new Meet(), new Meet());
        when(repository.findAll()).thenReturn(meets);

        List<Meet> retrievedMeets = meetService.findAll();

        assertEquals(meets, retrievedMeets);
    }
}