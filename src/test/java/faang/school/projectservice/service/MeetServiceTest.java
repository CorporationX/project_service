package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.meet.MeetCreateRequest;
import faang.school.projectservice.dto.meet.MeetFilterRequest;
import faang.school.projectservice.dto.meet.MeetResponse;
import faang.school.projectservice.dto.meet.MeetUpdateRequest;
import faang.school.projectservice.exception.MeetingOwnershipRequiredException;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.service.filter.meet.MeetFilter;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetServiceTest {
    @Mock
    private UserValidator userValidator;

    @Mock
    private ProjectValidator projectValidator;

    @Spy
    private MeetMapper meetMapper = Mappers.getMapper(MeetMapper.class);

    @Mock
    private MeetRepository meetRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private List<MeetFilter> meetFilters = new ArrayList<>();

    @InjectMocks
    private MeetService meetService;

    @Captor
    private ArgumentCaptor<Meet> meetCaptor;

    @Test
    void createMeet_Success() {
        long meetId = 100;
        long projectId = 10;
        long creatorId = 1;
        Meet savedMeet = new Meet();
        savedMeet.setId(meetId);
        savedMeet.setCreatorId(creatorId);
        savedMeet.setProject(Project.builder().id(projectId).build());

        willDoNothing().given(userValidator).validateUser(creatorId);
        willDoNothing().given(userValidator).validateUsers(List.of(2L, 3L));
        willDoNothing().given(projectValidator).validateProject(projectId);
        when(meetRepository.save(any(Meet.class))).thenReturn(savedMeet);

        MeetCreateRequest request = MeetCreateRequest.builder()
                .creatorId(creatorId)
                .title("title")
                .description("description")
                .projectId(projectId)
                .userIds(List.of(2L, 3L))
                .build();

        MeetResponse response = meetService.createMeet(request);
        verify(meetRepository).save(meetCaptor.capture());
        assertEquals(meetId, response.id());

        Meet captured = meetCaptor.getValue();
        assertEquals(1L, captured.getCreatorId());
        assertEquals(projectId, captured.getProject().getId());
    }

    @Test
    void createMeet_WithNullUsers() {
        MeetCreateRequest request = MeetCreateRequest.builder()
                .creatorId(1L)
                .title("title")
                .description("description")
                .projectId(10L)
                .userIds(null)
                .build();

        Meet savedMeet = new Meet();
        savedMeet.setId(101L);

        when(meetRepository.save(any(Meet.class))).thenReturn(savedMeet);

        MeetResponse response = meetService.createMeet(request);

        verify(meetRepository).save(any(Meet.class));
        assertEquals(101L, response.id());
    }

    @Test
    void updateMeet_Success() {
        long meetId = 100;
        long userId = 1;
        String oldDesc = "old desc";
        Meet existingMeet = new Meet();
        existingMeet.setId(meetId);
        existingMeet.setCreatorId(userId);
        existingMeet.setTitle(oldDesc);

        when(meetRepository.findById(meetId)).thenReturn(Optional.of(existingMeet));

        String newDesc = "new desc";
        String newTitle = "new title";
        MeetUpdateRequest request = MeetUpdateRequest.builder()
                .meetId(meetId)
                .title(newTitle)
                .description(newDesc)
                .userId(userId)
                .build();

        MeetResponse response = meetService.updateMeet(request);

        assertEquals(newTitle, response.title());
        assertEquals(newDesc, response.description());
    }

    @Test
    void updateMeet_NotOwnerThrowsException() {
        long meetId = 100;
        long creatorId = 1;
        Meet existingMeet = new Meet();
        existingMeet.setId(meetId);
        existingMeet.setCreatorId(creatorId);

        when(meetRepository.findById(meetId)).thenReturn(Optional.of(existingMeet));

        long userId = 2;
        MeetUpdateRequest request = MeetUpdateRequest.builder()
                .meetId(meetId)
                .title("new title")
                .description("new description")
                .userId(userId)
                .build();

        assertThrows(MeetingOwnershipRequiredException.class, () -> meetService.updateMeet(request));
    }

    @Test
    void deleteMeet_Success() {
        long meetId = 100;
        long userId = 1;
        when(meetRepository.existsById(meetId)).thenReturn(true);
        when(meetRepository.isUserOwnerMeet(meetId, userId)).thenReturn(true);

        meetService.deleteMeet(meetId, userId);

        verify(meetRepository).deleteById(meetId);
    }

    @Test
    void deleteMeet_NotFoundThrowsException() {
        long meetId = 999;
        when(meetRepository.existsById(meetId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> meetService.deleteMeet(meetId, 1L));
        verify(meetRepository, never()).deleteById(any());
    }

    @Test
    void deleteMeetNotOwnerThrowsException() {
        long meetId = 100;
        long userId = 1;
        when(meetRepository.existsById(meetId)).thenReturn(true);
        when(meetRepository.isUserOwnerMeet(meetId, userId)).thenReturn(false);

        assertThrows(MeetingOwnershipRequiredException.class, () -> meetService.deleteMeet(meetId, userId));
        verify(meetRepository, never()).deleteById(any());
    }

    @Test
    void getMeetById_Success() {
        long meetId = 100;
        Meet meet = new Meet();
        meet.setId(meetId);

        when(meetRepository.findById(meetId)).thenReturn(Optional.of(meet));

        MeetResponse response = meetService.getMeetById(meetId);

        assertNotNull(response);
        assertEquals(meetId, response.id());
    }

    @Test
    void getMeetById_NotFoundThrowsException() {
        long meetId = 999;
        when(meetRepository.findById(meetId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> meetService.getMeetById(meetId));
    }

    @Test
    void getMeetsByFilter() {
        long projectId = 10;
        long meetId1 = 1;
        long meetId2 = 2;

        Meet meet1 = new Meet();
        meet1.setId(meetId1);
        meet1.setProject(Project.builder().id(projectId).build());

        Meet meet2 = new Meet();
        meet2.setId(meetId2);
        meet2.setProject(Project.builder().id(projectId).build());

        when(meetRepository.findByProjectId(projectId))
                .thenReturn(Stream.of(meet1, meet2));

        MeetFilterRequest filterRequest = new MeetFilterRequest(null, projectId, null);

        List<MeetResponse> responses = meetService.getMeetsByFilter(filterRequest);

        assertEquals(2, responses.size());
        assertEquals(meetId1, responses.get(0).id());
        assertEquals(meetId2, responses.get(1).id());
    }

    @Test
    void testGetMeetsByFilterWithFilters() {
        long projectId = 10;
        long meetId1 = 1;
        Meet meet1 = new Meet();
        meet1.setId(meetId1);
        meet1.setProject(Project.builder().id(projectId).build());
        meet1.setTitle("Тема1");

        Meet meet2 = new Meet();
        meet2.setId(2L);
        meet2.setProject(Project.builder().id(projectId).build());
        meet2.setTitle("Тема2");

        when(meetRepository.findByProjectId(projectId))
                .thenReturn(Stream.of(meet1, meet2));

        MeetFilterRequest filterRequest = MeetFilterRequest.builder().projectId(projectId).title("Тема").build();

        // Допустим, у нас есть один мок-фильтр, который пропускает только meet1
        MeetFilter filterMock = mock(MeetFilter.class);
        when(filterMock.filter(any(Stream.class), eq(filterRequest)))
                .thenAnswer(invocation -> {
                    Stream<Meet> streamArg = invocation.getArgument(0);
                    return streamArg.filter(m -> m.getTitle().equals(meet1.getTitle()));
                });
        List<MeetFilter> filters = new ArrayList<>();
        filters.add(filterMock);


        when(meetFilters.iterator()).thenReturn(filters.iterator());

        List<MeetResponse> responses = meetService.getMeetsByFilter(filterRequest);
        assertEquals(1, responses.size());
        assertEquals(meetId1, responses.get(0).id());
    }

    @Test
    void testGetMeetsByProjectId() {
        long projectId = 10;
        long meetId1 = 1;
        Meet meet1 = new Meet();
        meet1.setId(meetId1);
        meet1.setProject(Project.builder().id(projectId).build());

        when(meetRepository.findByProjectId(projectId))
                .thenReturn(Stream.of(meet1));

        List<MeetResponse> responses = meetService.getMeetsByProjectId(projectId);

        assertEquals(1, responses.size());
        assertEquals(meetId1, responses.get(0).id());
    }

}