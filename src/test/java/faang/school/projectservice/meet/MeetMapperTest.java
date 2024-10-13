package faang.school.projectservice.meet;

import faang.school.projectservice.config.app.AppConfig;
import faang.school.projectservice.model.dto.MeetDto;
import faang.school.projectservice.mapper.MeetMapperImpl;
import faang.school.projectservice.model.entity.Meet;
import faang.school.projectservice.model.enums.MeetStatus;
import faang.school.projectservice.model.entity.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class MeetMapperTest {

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private MeetMapperImpl meetMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(appConfig.getTimeZone()).thenReturn("UTC");
    }

    @Test
    void testToDto_ShouldMapMeetToMeetDto() {
        Meet meet = new Meet();
        meet.setId(1L);
        meet.setTitle("Test Title");
        meet.setDescription("Test Description");
        meet.setStartDate(LocalDateTime.of(2023, 9, 27, 10, 0));
        meet.setEndDate(LocalDateTime.of(2023, 9, 27, 12, 0));
        meet.setStatus(MeetStatus.TENTATIVE);
        meet.setCreatorId(100L);
        meet.setCalendarEventId("event id");

        Project project = new Project();
        project.setId(200L);
        meet.setProject(project);

        meet.setUserIds(Arrays.asList(1L, 2L, 3L));

        MeetDto meetDto = meetMapper.toDto(meet);

        assertNotNull(meetDto);
        assertEquals(1L, meetDto.getId());
        assertEquals("Test Title", meetDto.getTitle());
        assertEquals("Test Description", meetDto.getDescription());
        assertEquals(LocalDateTime.of(2023, 9, 27, 10, 0), meetDto.getStartDate().getLocalDateTime());
        assertEquals(LocalDateTime.of(2023, 9, 27, 12, 0), meetDto.getEndDate().getLocalDateTime());
        assertEquals(MeetStatus.TENTATIVE, meetDto.getStatus());
        assertEquals(100L, meetDto.getCreatorId());
        assertEquals("event id", meetDto.getCalendarEventId());
        assertEquals(200L, meetDto.getProjectId());
        assertEquals(Arrays.asList(1L, 2L, 3L), meetDto.getUserIds());
    }

    @Test
    void testToDtoList_ShouldMapListOfMeetToListOfMeetDto() {
        Meet meet1 = new Meet();
        meet1.setId(1L);
        meet1.setTitle("Test Title 1");
        meet1.setDescription("Test Description 1");
        meet1.setStartDate(LocalDateTime.of(2023, 9, 27, 10, 0));
        meet1.setEndDate(LocalDateTime.of(2023, 9, 27, 12, 0));
        meet1.setStatus(MeetStatus.TENTATIVE);
        meet1.setCreatorId(100L);
        meet1.setCalendarEventId("event id 1");

        Project project1 = new Project();
        project1.setId(200L);
        meet1.setProject(project1);
        meet1.setUserIds(Arrays.asList(1L, 2L));

        Meet meet2 = new Meet();
        meet2.setId(2L);
        meet2.setTitle("Test Title 2");
        meet2.setDescription("Test Description 2");
        meet2.setStartDate(LocalDateTime.of(2023, 9, 28, 10, 0));
        meet2.setEndDate(LocalDateTime.of(2023, 9, 28, 12, 0));
        meet2.setStatus(MeetStatus.CONFIRMED);
        meet2.setCreatorId(101L);
        meet2.setCalendarEventId("event id 2");

        Project project2 = new Project();
        project2.setId(201L);
        meet2.setProject(project2);
        meet2.setUserIds(Arrays.asList(3L, 4L));

        List<Meet> meets = Arrays.asList(meet1, meet2);

        List<MeetDto> meetDtos = meetMapper.toDtoList(meets);

        assertNotNull(meetDtos);
        assertEquals(2, meetDtos.size());

        MeetDto meetDto1 = meetDtos.get(0);
        assertEquals(1L, meetDto1.getId());
        assertEquals("Test Title 1", meetDto1.getTitle());
        assertEquals("Test Description 1", meetDto1.getDescription());
        assertEquals(LocalDateTime.of(2023, 9, 27, 10, 0), meetDto1.getStartDate().getLocalDateTime());
        assertEquals(LocalDateTime.of(2023, 9, 27, 12, 0), meetDto1.getEndDate().getLocalDateTime());
        assertEquals(MeetStatus.TENTATIVE, meetDto1.getStatus());
        assertEquals(100L, meetDto1.getCreatorId());
        assertEquals("event id 1", meetDto1.getCalendarEventId());
        assertEquals(200L, meetDto1.getProjectId());
        assertEquals(Arrays.asList(1L, 2L), meetDto1.getUserIds());

        MeetDto meetDto2 = meetDtos.get(1);
        assertEquals(2L, meetDto2.getId());
        assertEquals("Test Title 2", meetDto2.getTitle());
        assertEquals("Test Description 2", meetDto2.getDescription());
        assertEquals(LocalDateTime.of(2023, 9, 28, 10, 0), meetDto2.getStartDate().getLocalDateTime());
        assertEquals(LocalDateTime.of(2023, 9, 28, 12, 0), meetDto2.getEndDate().getLocalDateTime());
        assertEquals(MeetStatus.CONFIRMED, meetDto2.getStatus());
        assertEquals(101L, meetDto2.getCreatorId());
        assertEquals("event id 2", meetDto2.getCalendarEventId());
        assertEquals(201L, meetDto2.getProjectId());
        assertEquals(Arrays.asList(3L, 4L), meetDto2.getUserIds());
    }
}
