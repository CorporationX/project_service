package faang.school.projectservice.service;

import faang.school.projectservice.dto.MeetDto;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.repository.MeetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetServiceTest {
    @Mock
    private MeetRepository meetRepository;

    @Spy
    private MeetMapper meetMapper;

    @InjectMocks
    private MeetService meetService;

    private Meet meet;
    private MeetDto meetDto;

    @BeforeEach
    void setUp() {
        meet = new Meet();
        meet.setId(1L);
        meet.setTitle("Test Meet");
        meet.setDescription("Test Description");
        meet.setStatus(MeetStatus.PENDING);
        meet.setCreatorId(1L);
        meet.setStartsAt(LocalDateTime.now());

        meetDto = MeetDto.builder()
                .id(1L)
                .title("Test Meet")
                .description("Test Description")
                .status(MeetStatus.PENDING)
                .creatorId(1L)
                .projectId(1L)
                .startsAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllMeets_ShouldReturnListOfMeetDto() {
        when(meetRepository.findAll()).thenReturn(List.of(meet));
        when(meetMapper.toDto(meet)).thenReturn(meetDto);

        var result = meetService.getAllMeets();

        assertEquals(1, result.size());
        assertEquals(meetDto, result.get(0));
    }

    @Test
    void getMeetById_ShouldReturnMeetDto() {
        when(meetRepository.findById(1L)).thenReturn(Optional.of(meet));
        when(meetMapper.toDto(meet)).thenReturn(meetDto);

        var result = meetService.getMeetById(1L);

        assertNotNull(result);
        assertEquals(meetDto, result);
    }

    @Test
    void createMeet_ShouldReturnMeetDto() {
        when(meetRepository.save(any(Meet.class))).thenReturn(meet);
        when(meetMapper.toEntity(any(MeetDto.class))).thenReturn(meet);
        when(meetMapper.toDto(meet)).thenReturn(meetDto);

        var result = meetService.createMeet(meetDto);

        assertNotNull(result);
        assertEquals(meetDto, result);
    }

    @Test
    void updateMeet_ShouldReturnUpdatedMeetDto() {
        when(meetRepository.findById(1L)).thenReturn(Optional.of(meet));
        when(meetRepository.save(any(Meet.class))).thenReturn(meet);
        when(meetMapper.toDto(meet)).thenReturn(meetDto);

        var result = meetService.updateMeet(1L, meetDto);

        assertNotNull(result);
        assertEquals(meetDto, result);
    }

    @Test
    void cancelMeet_ShouldReturnCancelledMeetDto() {
        meetDto = MeetDto.builder().status(MeetStatus.CANCELLED).build();
        when(meetRepository.findById(1L)).thenReturn(Optional.of(meet));
        when(meetRepository.save(any(Meet.class))).thenReturn(meet);
        when(meetMapper.toDto(meet)).thenReturn(meetDto);

        var result = meetService.cancelMeet(1L);

        assertNotNull(result);
        assertEquals(MeetStatus.CANCELLED.name(), String.valueOf(result.status()));
    }

    @Test
    void deleteMeet_ShouldDeleteMeet() {
        doNothing().when(meetRepository).deleteById(1L);

        meetService.deleteMeet(1L);

        verify(meetRepository, times(1)).deleteById(1L);
    }
}
