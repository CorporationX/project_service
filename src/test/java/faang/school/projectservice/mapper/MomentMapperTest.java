package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MomentMapperTest {

    private MomentMapperImpl mapper;
    private Moment moment;
    private MomentDto momentDto;
    private List<Long> projectIds;
    private List<Long> userIds;

    @BeforeEach
    public void setUp() {
        mapper = new MomentMapperImpl();
        projectIds = new ArrayList<>();
        projectIds.add(1L);
        userIds = new ArrayList<>();
        userIds.add(2L);
        momentDto = MomentDto.builder().name("test")
                .date(LocalDateTime.of(2021, 1, 1, 0, 0))
                .id(1L).projectIds(projectIds).userIds(userIds).build();
        moment = Moment.builder().name("test")
                .date(LocalDateTime.of(2021, 1, 1, 0, 0))
                .id(1L).userIds(userIds).build();
    }

    @Test
    public void testEntityToDto() {
        Moment actualMoment = mapper.toEntity(momentDto);
        Assertions.assertEquals(actualMoment, moment);
    }

    @Test
    public void testDtoToEntity() {
        momentDto.setProjectIds(null);
        MomentDto actualMomentDto = mapper.toDto(moment);
        Assertions.assertEquals(actualMomentDto, momentDto);
    }

    @Test
    public void testUpdateEntity() {
        momentDto.setDate(LocalDateTime.of(2022, 1, 1, 0, 0));
        momentDto.setName("newName");
        momentDto.setUserIds(List.of(2L, 3L));
        Moment updatedMoment = mapper.update(moment, momentDto);
        Assertions.assertEquals(updatedMoment.getName(), "newName");
        Assertions.assertEquals(updatedMoment.getDate(), LocalDateTime.of(2022, 1, 1, 0, 0));
        Assertions.assertEquals(updatedMoment.getUserIds(), List.of(2L, 3L));
    }

}
