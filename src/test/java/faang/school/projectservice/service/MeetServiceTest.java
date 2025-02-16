package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.filter.meet.MeetFilter;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.validation.MeetValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class MeetServiceTest {
    @Mock
    private MeetRepository meetRepository;

    @Mock
    private MeetValidation meetValidation;

    @Mock
    private MeetMapper meetMapper;

    @Mock
    private UserContext userContext;

    private List<MeetFilter> filters;

    @InjectMocks
    private MeetService meetService;

    @Mock
    private MeetFilter firstFilter;

    @Mock
    private MeetFilter secondFilter;

    public MeetDto meetDto;

    public Meet meetEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filters = List.of(firstFilter, secondFilter);
        meetService = new MeetService(meetRepository, meetValidation, meetMapper, userContext, filters);

        meetDto = new MeetDto();
        meetEntity = new Meet();
    }

    @Test
    public void testCreateMeet() {
        doNothing().when(meetValidation).validationMeet(meetDto);
        when(meetMapper.toEntity(meetDto)).thenReturn(meetEntity);
        when(meetRepository.save(meetEntity)).thenReturn(meetEntity);
        when(meetMapper.toDto(meetEntity)).thenReturn(meetDto);

        MeetDto result = meetService.createMeet(meetDto);

        verify(meetValidation, times(1)).validationMeet(meetDto);
        verify(meetMapper, times(1)).toEntity(meetDto);
        verify(meetRepository, times(1)).save(meetEntity);
        verify(meetMapper, times(1)).toDto(meetEntity);

        assertNotNull(result);
    }

    @Test
    public void testUpdateMeet() {
        doNothing().when(meetValidation).meetExists(eq(0L));
        doNothing().when(meetValidation).permissionCheck(eq(0L), isNull());
        when(meetMapper.toEntity(meetDto)).thenReturn(meetEntity);
        when(meetRepository.save(meetEntity)).thenReturn(meetEntity);
        when(meetMapper.toDto(meetEntity)).thenReturn(meetDto);


        MeetDto result = meetService.updateMeet(meetDto);

        verify(meetValidation, times(1)).meetExists(eq(0L));
        verify(meetValidation, times(1)).permissionCheck(eq(0L), isNull());
        verify(meetMapper, times(1)).toEntity(meetDto);
        verify(meetRepository, times(1)).save(meetEntity);
        verify(meetMapper, times(1)).toDto(meetEntity);


        assertNotNull(result);
    }

}
