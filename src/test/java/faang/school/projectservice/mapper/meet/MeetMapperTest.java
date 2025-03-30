package faang.school.projectservice.mapper.meet;

import faang.school.projectservice.dto.meet.MeetCreateDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.dto.meet.MeetUpdateDto;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class MeetMapperTest {
    private final MeetMapper meetMapper = Mappers.getMapper(MeetMapper.class);
    private Meet meet;
    private MeetCreateDto meetCreateDto;
    private MeetUpdateDto meetUpdateDto;
    private final LocalDateTime startsAt = LocalDateTime.parse("2025-04-05T11:00:00");
    private final LocalDateTime createdAt = LocalDateTime.parse("2025-04-01T10:00:00");


    @BeforeEach
    void setUp() {
        long projectId = 1L;
        Project project = Project.builder()
                .id(projectId)
                .build();
        long meetId = 1L;
        String title = "title";
        String description = "description";
        meet = Meet.builder()
                .id(meetId)
                .title(title)
                .description(description)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .startsAt(startsAt)
                .project(project)
                .build();
        meetCreateDto = MeetCreateDto.builder()
                .title(title)
                .description(description)
                .startsAt(startsAt)
                .projectId(projectId)
                .build();
        meetUpdateDto = MeetUpdateDto.builder()
                .title(title.repeat(2))
                .description(description.repeat(2))
                .startsAt(startsAt.plusDays(1))
                .build();
    }

    @Test
    void testMapToEntity() {
        Meet result = meetMapper.toEntity(meetCreateDto);

        assertNotNull(result);
        assertEquals(meetCreateDto.getStartsAt(), result.getStartsAt());
        assertEquals(meetCreateDto.getDescription(), result.getDescription());
        assertEquals(meetCreateDto.getTitle(), result.getTitle());

    }

    @Test
    void testMapToDto() {
        MeetResponseDto result = meetMapper.toDto(meet);

        assertNotNull(result);
        assertEquals(meet.getId(), result.getId());
        assertEquals(meet.getTitle(), result.getTitle());
        assertEquals(meet.getStartsAt(), result.getStartsAt());
    }

    @Test
    void testMapListToListDtos() {
        List<MeetResponseDto> result = meetMapper.toDto(List.of(meet));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(meet.getId(), result.get(0).getId());
        assertEquals(meet.getTitle(), result.get(0).getTitle());
        assertEquals(meet.getStartsAt(), result.get(0).getStartsAt());
    }

    @Test
    void testUpdateMeetFromDto() {
        meetMapper.updateMeetFromDto(meetUpdateDto, meet);

        assertNotNull(meet);
        assertEquals(meetUpdateDto.getTitle(), meet.getTitle());
        assertEquals(meetUpdateDto.getDescription(), meet.getDescription());
        assertEquals(meetUpdateDto.getStartsAt(), meet.getStartsAt());
    }
}