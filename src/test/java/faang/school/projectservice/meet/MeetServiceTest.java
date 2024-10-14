package faang.school.projectservice.meet;

import faang.school.projectservice.config.app.AppConfig;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.dto.CalendarEventDto;
import faang.school.projectservice.model.dto.ZonedDateTimeDto;
import faang.school.projectservice.model.dto.MeetDto;
import faang.school.projectservice.model.dto.MeetFilterDto;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.mapper.calendar.CalendarMeetDtoEventDtoMapper;
import faang.school.projectservice.model.entity.Meet;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.GoogleCalendarService;
import faang.school.projectservice.service.impl.MeetServiceImpl;
import faang.school.projectservice.validator.MeetValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MeetServiceTest {

    @InjectMocks
    private MeetServiceImpl meetService;

    @Mock
    private MeetRepository meetRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private GoogleCalendarService googleCalendarService;

    @Mock
    private MeetMapper meetMapper;

    @Mock
    private CalendarMeetDtoEventDtoMapper calendarMeetDtoEventDtoMapper;

    @Mock
    private MeetValidator meetValidator;

    @Mock
    private UserContext userContext;

    @Mock
    private AppConfig appConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateMeet() throws GeneralSecurityException, IOException {
        MeetDto meetDto = new MeetDto();
        meetDto.setCreatorId(1L);
        meetDto.setProjectId(1L);
        meetDto.setStartDate(new ZonedDateTimeDto(LocalDateTime.now(), "UTC"));
        meetDto.setEndDate(new ZonedDateTimeDto(LocalDateTime.now().plusHours(1), "UTC"));

        Project project = new Project();

        when(projectRepository.findById(meetDto.getProjectId())).thenReturn(project);
        when(googleCalendarService.createEvent(any())).thenReturn(new CalendarEventDto());
        when(meetRepository.save(any(Meet.class))).thenReturn(new Meet());
        when(userContext.getUserId()).thenReturn(1L);
        when(appConfig.getTimeZone()).thenReturn("Europe/Moscow");

        meetService.create(meetDto);

        verify(meetValidator).validateUser(anyLong());
        verify(meetValidator).validateUserIsCreator(anyLong(), anyLong());
        verify(meetRepository).save(any(Meet.class));
    }

    @Test
    void testUpdate_WhenAllFieldsTheSame() throws GeneralSecurityException, IOException {
        long meetId = 1L;
        MeetDto meetDto = new MeetDto();
        meetDto.setId(meetId);
        meetDto.setCreatorId(1L);
        meetDto.setTitle("Title");

        Meet meet = new Meet();
        meet.setId(meetId);
        meet.setCreatorId(1L);
        meet.setTitle("Title");
        when(meetRepository.findById(meetId)).thenReturn(Optional.of(meet));
        when(meetMapper.toDto(meet)).thenReturn(meetDto);
        when(userContext.getUserId()).thenReturn(1L);

        meetService.update(meetId, meetDto);

        verify(meetValidator).validateUser(anyLong());
        verify(meetValidator).validateUserIsCreator(anyLong(), anyLong());
        verify(meetValidator).validateIdFromPath(anyLong(), anyLong());

        verify(googleCalendarService, never()).update(any());
        verify(meetRepository, never()).save(any(Meet.class));
    }

    @Test
    void testUpdate_WhenAllFieldsDifferent() throws GeneralSecurityException, IOException {
        long meetId = 1L;
        MeetDto meetDto = new MeetDto();
        meetDto.setId(meetId);
        meetDto.setCreatorId(1L);
        meetDto.setTitle("New title");
        meetDto.setStartDate(new ZonedDateTimeDto(LocalDateTime.now(), "UTC"));
        meetDto.setEndDate(new ZonedDateTimeDto(LocalDateTime.now().plusHours(1), "UTC"));

        Meet meet = new Meet();
        meet.setId(meetId);
        meet.setCreatorId(1L);
        meet.setTitle("Old title");
        meetDto.setStartDate(new ZonedDateTimeDto(LocalDateTime.now(), "UTC"));
        meetDto.setEndDate(new ZonedDateTimeDto(LocalDateTime.now().plusHours(1), "UTC"));
        MeetDto oldMeetDto = new MeetDto();
        oldMeetDto.setId(meetId);
        oldMeetDto.setCreatorId(1L);
        oldMeetDto.setTitle("Old title");
        when(meetRepository.findById(meetId)).thenReturn(Optional.of(meet));
        when(meetMapper.toDto(meet)).thenReturn(oldMeetDto);
        when(userContext.getUserId()).thenReturn(1L);

        meetService.update(meetId, meetDto);

        verify(meetValidator).validateUser(anyLong());
        verify(meetValidator).validateUserIsCreator(anyLong(), anyLong());
        verify(meetValidator).validateIdFromPath(anyLong(), anyLong());

        verify(googleCalendarService).update(any());
        verify(meetRepository).save(any(Meet.class));
    }

    @Test
    void testDelete() throws GeneralSecurityException, IOException {
        long meetId = 1L;
        Meet meet = new Meet();
        meet.setCreatorId(1L);

        when(meetRepository.findById(meetId)).thenReturn(Optional.of(meet));
        when(userContext.getUserId()).thenReturn(1L);

        meetService.delete(meetId);

        verify(meetValidator).validateUser(anyLong());
        verify(meetValidator).validateUserIsCreator(anyLong(), anyLong());
        verify(googleCalendarService).delete(meet.getCalendarEventId());
        verify(meetRepository).delete(meet);
    }

    @Test
    void testDelete_WhenMeetNotFound() {
        long meetId = 1L;
        Meet meet = new Meet();
        meet.setCreatorId(1L);

        when(meetRepository.findById(meetId)).thenReturn(Optional.empty());
        when(userContext.getUserId()).thenReturn(1L);

        assertThrows(NoSuchElementException.class, () -> meetService.delete(meetId));
    }

    @Test
    void testGetById() {
        long meetId = 1L;
        Meet meet = new Meet();
        MeetDto meetDto = new MeetDto();

        when(meetRepository.findById(meetId)).thenReturn(Optional.of(meet));
        when(meetMapper.toDto(meet)).thenReturn(meetDto);
        when(userContext.getUserId()).thenReturn(1L);

        MeetDto result = meetService.getById(meetId);

        assertEquals(meetDto, result);
        verify(meetValidator).validateUser(anyLong());
        verify(meetRepository).findById(meetId);
    }

    @Test
    void testGetById_WhenMeetNotFound() {
        long meetId = 1L;
        Meet meet = new Meet();
        MeetDto meetDto = new MeetDto();

        when(meetRepository.findById(meetId)).thenReturn(Optional.empty());
        when(meetMapper.toDto(meet)).thenReturn(meetDto);
        when(userContext.getUserId()).thenReturn(1L);

        assertThrows(NoSuchElementException.class, () -> meetService.getById(meetId));
    }

    @Test
    void testGetAll() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Meet> meets = Collections.singletonList(new Meet());
        Page<Meet> meetPage = new PageImpl<>(meets, pageable, meets.size());

        when(meetRepository.findAllWithLimitAndOffset(anyInt(), anyInt())).thenReturn(meets);
        when(meetMapper.toDtoList(meets)).thenReturn(Collections.singletonList(new MeetDto()));
        when(userContext.getUserId()).thenReturn(1L);

        Page<MeetDto> result = meetService.getAll(pageable);

        assertNotNull(result);
        verify(meetValidator).validateUser(anyLong());
        verify(meetRepository).findAllWithLimitAndOffset(anyInt(), anyInt());
    }

    @Test
    void testUpdate_WhenMeetNotFound() {
        long meetId = 1L;
        MeetDto meetDto = new MeetDto();
        meetDto.setId(meetId);
        meetDto.setCreatorId(1L);
        meetDto.setTitle("New title");

        Meet meet = new Meet();
        meet.setId(meetId);
        meet.setCreatorId(1L);
        meet.setTitle("Old title");
        MeetDto oldMeetDto = new MeetDto();
        oldMeetDto.setId(meetId);
        oldMeetDto.setCreatorId(1L);
        oldMeetDto.setTitle("Old title");
        when(meetRepository.findById(meetId)).thenReturn(Optional.empty());
        when(meetMapper.toDto(meet)).thenReturn(oldMeetDto);
        when(userContext.getUserId()).thenReturn(1L);

        assertThrows(NoSuchElementException.class, () -> meetService.update(meetId, meetDto));
    }

    @Test
    void getByFilterSuccess() {
        MeetFilterDto filterDto = new MeetFilterDto();
        filterDto.setTitle("Meeting");
        filterDto.setStartDateAfter(LocalDateTime.of(2024, 9, 27, 0, 0));

        Meet meet = new Meet();
        List<Meet> meetList = Collections.singletonList(meet);
        List<MeetDto> meetDtoList = Collections.singletonList(new MeetDto());

        when(meetRepository.findAll(any(Specification.class))).thenReturn(meetList);
        when(meetMapper.toDtoList(meetList)).thenReturn(meetDtoList);
        when(userContext.getUserId()).thenReturn(1L);

        List<MeetDto> result = meetService.getByFilter(filterDto);

        verify(meetRepository).findAll(any(Specification.class));

        assertEquals(meetDtoList, result);
    }

    @Test
    void getByFilterWithNoCriteria() {
        MeetFilterDto filterDto = new MeetFilterDto();

        Meet meet = new Meet();
        List<Meet> meetList = Collections.singletonList(meet);
        List<MeetDto> meetDtoList = Collections.singletonList(new MeetDto());

        when(meetRepository.findAll(any(Specification.class))).thenReturn(meetList);
        when(meetMapper.toDtoList(meetList)).thenReturn(meetDtoList);
        when(userContext.getUserId()).thenReturn(1L);

        List<MeetDto> result = meetService.getByFilter(filterDto);

        verify(meetRepository).findAll(any(Specification.class));

        assertEquals(meetDtoList, result);
    }

    @Test
    void getByFilterWithMultipleCriteria() {
        MeetFilterDto filterDto = new MeetFilterDto();
        filterDto.setTitle("Team Sync");
        filterDto.setStartDateAfter(LocalDateTime.of(2024, 9, 1, 0, 0));
        filterDto.setStartDateBefore(LocalDateTime.of(2024, 10, 1, 0, 0));

        Meet meet = new Meet();
        List<Meet> meetList = Collections.singletonList(meet);
        List<MeetDto> meetDtoList = Collections.singletonList(new MeetDto());

        when(meetRepository.findAll(any(Specification.class))).thenReturn(meetList);
        when(meetMapper.toDtoList(meetList)).thenReturn(meetDtoList);
        when(userContext.getUserId()).thenReturn(1L);

        List<MeetDto> result = meetService.getByFilter(filterDto);

        verify(meetRepository).findAll(any(Specification.class));

        assertEquals(meetDtoList, result);
    }

}
