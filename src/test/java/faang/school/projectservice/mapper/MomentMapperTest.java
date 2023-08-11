package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MomentMapperTest {
    @Spy
    private MomentMapperImpl momentMapper;
    private MomentDto momentDtoExpected;
    private Moment momentExpected;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        momentExpected = Moment.builder()
                .id(1L)
                .name("name")
                .description("description")
                .date(now)
                .projects(List.of(Project.builder().id(1L).build()))
                .userIds(List.of(1L))
                .imageId("imageId")
                .build();

        momentDtoExpected = MomentDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .date(now)
                .projectIds(List.of(1L))
                .teamMemberIds(List.of(1L))
                .imageId("imageId")
                .build();
    }

    @Test
    void toDto_shouldProperlyMapAllFields() {
        MomentDto actual = momentMapper.toDto(momentExpected);
        assertEquals(momentDtoExpected, actual);
    }

    @Test
    void toEntity() {
        Moment actual = momentMapper.toEntity(momentDtoExpected);
        assertEquals(momentExpected, actual);
    }
}