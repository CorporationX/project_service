package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.audit.AuditorAwareImpl;
import faang.school.projectservice.dto.meet.CreateMeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.mapper.MeetMapperImpl;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.validator.UserValidator;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeetServiceTest {

    @Mock
    private MeetRepository meetRepository;
    @Mock
    private UserValidator userValidator;
    @Mock
    private AuditorAwareImpl auditorAware;
    @Spy
    private MeetMapperImpl meetMapper;
    @Mock
    private ProjectService projectService;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private MeetService meetService;

    private CreateMeetDto createMeetDto;
    private UpdateMeetDto updateMeetDto;
    private Meet meet;

    @BeforeEach
    void setUp() {
        createMeetDto = new CreateMeetDto();
        createMeetDto.setProjectId(1L);

        updateMeetDto = new UpdateMeetDto();
        updateMeetDto.setId(1L);

        meet = new Meet();
        meet.setId(1L);
        meet.setStatus(MeetStatus.PENDING);
        meet.setStartsAt(LocalDateTime.now());
        meet.setProject(new Project());
    }

    @Test
    void createMeetShouldReturnMeetResponseDto() {

        when(auditorAware.getCurrentAuditor()).thenReturn(Optional.of(1L));
        when(projectService.getProjectById(createMeetDto.getProjectId())).thenReturn(new Project());
        when(meetRepository.save(any(Meet.class))).thenReturn(meet);
        MeetResponseDto responseDto = meetService.createMeet(createMeetDto);
        assertEquals(MeetStatus.PENDING, responseDto.getStatus());
        verify(meetRepository, times(1)).save(any(Meet.class));
    }

    @Test
    void findByFilterShouldReturnListOfMeetResponseDtoWhenValidFilter() {

        MeetFilterDto filter = new MeetFilterDto();
        when(meetRepository.findByFilter(anyLong(), any(), any())).thenReturn(Stream.of(meet));
        List<MeetResponseDto> responseDtos = meetService.findProjectMeetsByFilter(1L, filter);
        assertFalse(responseDtos.isEmpty());
        verify(meetRepository, times(1)).findByFilter(anyLong(), any(), any());
    }

    @Test
    void findByIdShouldThrowExceptionWhenMeetNotFound() {

        when(meetRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> meetService.findById(1L));
    }

    @Test
    void findByIdShouldReturnMeetResponseDto() {

        when(meetRepository.findById(1L)).thenReturn(Optional.of(meet));
        meetService.findById(1L);
        verify(meetRepository, times(1)).findById(1L);
    }

    @Test
    void updateMeetShouldThrowExceptionWhenMeetNotFound() {

        when(meetRepository.findById(updateMeetDto.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> meetService.updateMeet(updateMeetDto));
    }

    @Test
    void updateMeetShouldReturnMeetResponseDto() {

        when(meetRepository.findById(anyLong())).thenReturn(Optional.of(meet));
        when(meetRepository.save(any(Meet.class))).thenReturn(meet);
        meetService.updateMeet(updateMeetDto);
        verify(meetRepository, times(1)).save(any(Meet.class));
    }

    @Test
    void cancelMeetShouldThrowExceptionWhenMeetNotFound() {

        when(meetRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> meetService.cancelMeet(1L));
    }

    @Test
    void cancelMeetShouldReturnMeetResponseDtoWhenMeetFound() {

        when(meetRepository.findById(1L)).thenReturn(Optional.of(meet));
        when(meetRepository.save(any(Meet.class))).thenReturn(meet);
        MeetResponseDto responseDto = meetService.cancelMeet(1L);
        assertEquals(MeetStatus.CANCELLED, responseDto.getStatus());
        verify(meetRepository, times(1)).save(any(Meet.class));
    }

    @Test
    void deleteMeetShouldCallDeleteById() {

        when(meetRepository.findById(1L)).thenReturn(Optional.of(new Meet()));
        meetService.deleteMeet(1L);
        verify(meetRepository, times(1)).delete(any(Meet.class));
    }
}
