package faang.school.projectservice.service.meet;

import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetRequestDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.filter.meet.MeetFilter;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.meet.MeetMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.meet.Meet;
import faang.school.projectservice.model.meet.MeetStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.meet.MeetValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetServiceImplTest {

    @Mock
    private MeetRepository meetRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MeetMapper meetMapper;

    private List<MeetFilter> meetFilters;

    @Mock
    private MeetValidator meetValidator;

    @InjectMocks
    private MeetServiceImpl meetService;

    private MeetRequestDto meetRequestDto;
    private Meet meet;
    private MeetResponseDto result;

    @BeforeEach
    void setUp() {
        MeetFilter meetFilter = Mockito.mock(MeetFilter.class);
        meetFilters = List.of(meetFilter);
        meetService = new MeetServiceImpl(meetRepository, projectRepository, meetMapper, meetValidator, meetFilters);

        meetRequestDto = MeetRequestDto.builder()
                .title("Team Meeting")
                .description("Discuss project updates")
                .status(MeetStatus.PENDING)
                .projectId(1L)
                .build();

        meet = Meet.builder()
                .id(1L)
                .title("Team Meeting")
                .description("Discuss project updates")
                .status(MeetStatus.PENDING)
                .creatorId(1L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void create_shouldSaveMeetAndReturnDto() {
        // given
        when(meetMapper.toEntity(any(MeetRequestDto.class))).thenReturn(meet);
        when(meetRepository.save(any(Meet.class))).thenReturn(meet);
        when(meetMapper.toDto(any(Meet.class))).thenReturn(MeetResponseDto.builder().build());
        // when
        result = meetService.create(1L, meetRequestDto);
        // then
        verify(meetRepository).save(meet);
        assertThat(result).isNotNull();
    }

    @Test
    void update_shouldUpdateMeetAndReturnDto() {
        // given
        when(meetRepository.findById(any())).thenReturn(Optional.of(meet));
        when(meetRepository.save(any(Meet.class))).thenReturn(meet);
        when(meetMapper.toDto(any(Meet.class))).thenReturn(MeetResponseDto.builder().build());
        // when
        result = meetService.update(1L, meetRequestDto);
        // then
        verify(meetValidator).validateMeetToUpdate(meet, 1L);
        verify(meetRepository).save(meet);
        assertThat(result).isNotNull();
    }

    @Test
    void delete_shouldDeleteMeet_whenCreatorIdMatches() {
        // given
        when(meetRepository.findById(anyLong())).thenReturn(Optional.of(meet));
        // when
        meetService.delete(1L, 1L);
        // then
        verify(meetValidator).validateMeetToDelete(meet, 1L);
        verify(meetRepository).delete(meet);
    }

    @Test
    void delete_shouldThrowException_whenMeetNotFound() {
        // given
        when(meetRepository.findById(anyLong())).thenReturn(Optional.empty());
        // when/then
        assertThrows(EntityNotFoundException.class, () -> meetService.delete(1L, 1L));
    }

    @Test
    void findAllByProjectIdFilter_shouldReturnFilteredMeets() {
        // given
        var project = Project.builder().id(1L)
                .meets(List.of(meet))
                .build();
        var filter = MeetFilterDto.builder().build();
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(meetFilters.get(0).isApplicable(filter)).thenReturn(true);
        when(meetFilters.get(0).apply(any(), any())).thenAnswer(invocation -> Stream.of(meet));
        // when
        var result = meetService.findAllByProjectIdFilter(1L, filter);
        // then
        assertThat(result).hasSize(1);
        verify(meetMapper).toDto(meet);
    }

    @Test
    void findById_shouldReturnMeetResponseDto() {
        // given
        when(meetRepository.findById(anyLong())).thenReturn(Optional.of(meet));
        when(meetMapper.toDto(any(Meet.class))).thenReturn(MeetResponseDto.builder().build());
        // when
        result = meetService.findById(1L);
        // then
        assertThat(result).isNotNull();
        verify(meetMapper).toDto(meet);
    }

    @Test
    void findById_shouldThrowException_whenMeetNotFound() {
        // given
        when(meetRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when/then
        assertThrows(EntityNotFoundException.class, () -> meetService.findById(1L));
    }
}