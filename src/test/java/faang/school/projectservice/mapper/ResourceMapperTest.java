package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResourceMapperTest {
    @Spy
    private ResourceMapperImpl resourceMapper;
    private Resource resource;
    private ResourceDto resourceDto;
    private Project project;

    @BeforeEach
    void setUp() {
        resourceDto = ResourceDto
                .builder()
                .id(1L)
                .name("test")
                .key("test")
                .size(BigInteger.valueOf(1))
                .build();
        resource = Resource
                .builder()
                .id(1L)
                .name("test")
                .key("test")
                .size(BigInteger.valueOf(1))
                .build();
    }

    @Test
    void toEntity() {
        assertEquals(resource, resourceMapper.toEntity(resourceDto));
    }

    @Test
    void toDto() {
        assertEquals(resourceDto, resourceMapper.toDto(resource));
    }
}