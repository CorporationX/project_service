package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.MomentFilterDto;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class MomentFilterMapperTest {
    private MomentFilterMapper mapper = Mappers.getMapper(MomentFilterMapper.class);
    private MomentFilterDto filterDto;
    private MomentDto dto;

    @BeforeEach
    void setUp() {
        filterDto = new MomentFilterDto(1L, "name", LocalDateTime.of(2000, 1, 1, 0, 0));
        dto = new MomentDto(1L, "name", Collections.emptyList(), Collections.emptyList(), LocalDateTime.of(2000, 1, 1, 0, 0));
    }

    @Test
    public void toMomentDtoTest() {
        MomentDto actualDto = mapper.toMomentDto(filterDto);
        Assertions.assertEquals(dto, actualDto);
    }
}
