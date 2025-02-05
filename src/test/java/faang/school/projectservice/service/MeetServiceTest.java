package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.meet.CreateMeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.MeetMapperImpl;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MeetRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeetServiceTest {
    @Mock
    private MeetRepository meetRepository;
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
        createMeetDto.setCreatorId(1L);
        createMeetDto.setProjectId(1L);

        updateMeetDto = new UpdateMeetDto();
        updateMeetDto.setId(1L);

        meet = new Meet();
        meet.setId(1L);
        meet.setStatus(MeetStatus.PENDING);
    }

    @Test
    void createMeet_ShouldThrowExceptionWhenCreatorNotExists() {
        // Given
        when(userServiceClient.getUser(anyLong())).thenThrow(mock(FeignException.BadRequest.class));

        // When & Then
        assertThrows(DataValidationException.class, () -> meetService.createMeet(createMeetDto));
    }

    @Test
    void createMeet_ShouldReturnMeetResponseDto() {
        // Given

        when(userServiceClient.getUser(anyLong())).thenReturn(new UserDto(1L, "", ""));
        when(projectService.findEntityById(createMeetDto.getProjectId())).thenReturn(new Project());
        when(meetRepository.save(any(Meet.class))).thenReturn(meet);

        // When
        MeetResponseDto responseDto = meetService.createMeet(createMeetDto);

        // Then
        assertEquals(MeetStatus.PENDING, responseDto.getStatus());
        verify(meetRepository, times(1)).save(any(Meet.class));
    }

    @Test
    void findByFilter_ShouldReturnListOfMeetResponseDtoWhenValidFilter() {
        // Given
        MeetFilterDto filter = new MeetFilterDto();
        when(meetRepository.findByFilter(anyLong(), any(), any())).thenReturn(Stream.of(meet));

        // When
        List<MeetResponseDto> responseDtos = meetService.findProjectMeetsByFilter(1L, filter);

        // Then
        assertFalse(responseDtos.isEmpty());
        verify(meetRepository, times(1)).findByFilter(anyLong(), any(), any());
    }

    @Test
    void findById_ShouldThrowExceptionWhenMeetNotFound() {
        // Given
        when(meetRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> meetService.findById(1L));
    }

    @Test
    void findById_ShouldReturnMeetResponseDto() {
        // Given
        when(meetRepository.findById(1L)).thenReturn(Optional.of(meet));

        // When
        meetService.findById(1L);

        // Then
        verify(meetRepository, times(1)).findById(1L);
    }

    @Test
    void updateMeet_ShouldThrowExceptionWhenMeetNotFound() {
        // Given
        when(meetRepository.findById(updateMeetDto.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> meetService.updateMeet(updateMeetDto));
    }

    @Test
    void updateMeet_ShouldReturnMeetResponseDto() {
        // Given
        when(meetRepository.findById(anyLong())).thenReturn(Optional.of(meet));
        when(meetRepository.save(any(Meet.class))).thenReturn(meet);

        // When
        meetService.updateMeet(updateMeetDto);

        // Then
        verify(meetRepository, times(1)).save(any(Meet.class));
    }

    @Test
    void cancelMeet_ShouldThrowExceptionWhenMeetNotFound() {
        // Given
        when(meetRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> meetService.cancelMeet(1L));
    }

    @Test
    void cancelMeet_ShouldReturnMeetResponseDtoWhenMeetFound() {
        // Given
        when(meetRepository.findById(1L)).thenReturn(Optional.of(meet));
        when(meetRepository.save(any(Meet.class))).thenReturn(meet);

        // When
        MeetResponseDto responseDto = meetService.cancelMeet(1L);

        // Then
        assertEquals(MeetStatus.CANCELLED, responseDto.getStatus());
        verify(meetRepository, times(1)).save(any(Meet.class));
    }

    @Test
    void deleteMeet_ShouldCallDeleteById() {
        // Given
        doNothing().when(meetRepository).deleteById(1L);

        // When
        meetService.deleteMeet(1L);

        // Then
        verify(meetRepository, times(1)).deleteById(1L);
    }
}
