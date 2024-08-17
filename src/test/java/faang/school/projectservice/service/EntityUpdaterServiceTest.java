package faang.school.projectservice.service;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.model.meet.Meet;
import faang.school.projectservice.model.meet.MeetStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityUpdaterServiceTest {

    private EntityUpdaterService entityUpdaterService;
    private MeetDto meetDto;
    private Meet meet;

    @BeforeEach
    public void setUp() {
        entityUpdaterService = new EntityUpdaterService();
        meetDto = MeetDto.builder()
                .id(1L)
                .title("title")
                .description("description")
                .location("location")
                .status(MeetStatus.ACTIVE)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        meet = Meet.builder()
                .build();
    }

    @Test
    void updateNonNullFields() {
        entityUpdaterService.updateNonNullFields(meetDto, meet);
        assertEquals(meetDto.getId(), meet.getId());
        assertEquals(meetDto.getTitle(), meet.getTitle());
        assertEquals(meetDto.getDescription(), meet.getDescription());
        assertEquals(meetDto.getLocation(), meet.getLocation());
        assertEquals(meetDto.getStatus(), meet.getStatus());
        assertEquals(meetDto.getStartDate(), meet.getStartDate());
        assertEquals(meetDto.getEndDate(), meet.getEndDate());
    }
}