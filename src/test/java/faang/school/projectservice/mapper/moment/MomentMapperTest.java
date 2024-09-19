package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MomentMapperTest {

    private static final Long PROJECT_ID_1 = 1L;
    private static final Long PROJECT_ID_2 = 2L;
    private static final Long MOMENT_ID = 1L;
    private static final String MOMENT_NAME = "Moment Name";
    private static final String MOMENT_DESCRIPTION = "Moment Description";
    private static final LocalDateTime MOMENT_DATE = LocalDateTime.of(2024, 9, 10, 15, 0);

    private MomentMapper momentMapper;

    private Moment moment;
    private MomentDto momentDto;

    @BeforeEach
    void setUp() {
        momentMapper = Mappers.getMapper(MomentMapper.class);

        Project project1 = new Project();
        project1.setId(PROJECT_ID_1);

        Project project2 = new Project();
        project2.setId(PROJECT_ID_2);

        moment = new Moment();
        moment.setId(MOMENT_ID);
        moment.setName(MOMENT_NAME);
        moment.setDescription(MOMENT_DESCRIPTION);
        moment.setDate(MOMENT_DATE);
        moment.setProjects(List.of(project1, project2));

        momentDto = MomentDto.builder()
                .id(MOMENT_ID)
                .name(MOMENT_NAME)
                .projectIds(List.of(PROJECT_ID_1, PROJECT_ID_2))
                .date(MOMENT_DATE)
                .build();
    }

    @Nested
    @DisplayName("Test toDto method")
    class ToDtoTests {

        @Test
        @DisplayName("When mapping Moment to MomentDto all fields should be correctly mapped")
        void whenMappingMomentToDtoThenFieldsShouldBeMappedCorrectly() {
            MomentDto result = momentMapper.toDto(moment);

            assertEquals(moment.getId(), result.getId());
            assertEquals(moment.getName(), result.getName());
            assertEquals(moment.getDate(), result.getDate());
            assertEquals(List.of(PROJECT_ID_1, PROJECT_ID_2), result.getProjectIds());
        }
    }

    @Nested
    @DisplayName("Test toEntity method")
    class ToEntityTests {

        @Test
        @DisplayName("When mapping MomentDto to Moment all fields should be correctly mapped")
        void whenMappingDtoToMomentThenFieldsShouldBeMappedCorrectly() {
            Moment result = momentMapper.toEntity(momentDto);

            assertEquals(momentDto.getId(), result.getId());
            assertEquals(momentDto.getName(), result.getName());
            assertEquals(momentDto.getDate(), result.getDate());
        }
    }

    @Nested
    @DisplayName("Test toDtoList method")
    class ToDtoListTests {

        @Test
        @DisplayName("When mapping a list of Moments to MomentDtoList the list should be correctly mapped")
        void whenMappingListOfMomentsThenDtoListShouldBeMappedCorrectly() {
            List<MomentDto> result = momentMapper.toDtoList(List.of(moment));

            assertEquals(1, result.size());
            MomentDto mappedDto = result.get(0);
            assertEquals(moment.getId(), mappedDto.getId());
            assertEquals(moment.getName(), mappedDto.getName());
            assertEquals(moment.getDate(), mappedDto.getDate());
            assertEquals(List.of(PROJECT_ID_1, PROJECT_ID_2), mappedDto.getProjectIds());
        }
    }
}
