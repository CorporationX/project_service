package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MomentMapperTest {
    @Spy
    private MomentMapper mapper = Mappers.getMapper(MomentMapper.class);

    private Moment moment;
    private MomentDto dto;

    @BeforeEach
    void init() {
        List<Project> projects = List.of(
                Project.builder().id(1L).build(),
                Project.builder().id(2L).build(),
                Project.builder().id(3L).build()
        );

        List<Long> projectIds = List.of(1L, 2L, 3L);

        List<Long> userIds = List.of(4L, 5L, 6L);

        moment = Moment.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .projects(projects)
                .userIds(userIds)
                .imageId("1234")
                .build();

        dto = MomentDto.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .projectIds(projectIds)
                .userIds(userIds)
                .imageId("1234")
                .build();
    }

    @Test
    void toEntity() {
        Moment actual = mapper.toEntity(dto);
        assertEquals(moment, actual);
    }

    @Test
    void toDto() {
        MomentDto actual = mapper.toDto(moment);
        assertEquals(dto, actual);
    }
}