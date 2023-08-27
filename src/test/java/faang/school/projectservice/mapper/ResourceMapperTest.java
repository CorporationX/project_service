package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResourceMapperTest {

    @Spy
    private ResourceMapperImpl mapper;

    private Resource resource;

    private ResourceDto resourceDto;

    @BeforeEach
    void setUp() {
        resource = Resource.builder().id(1L).project(Project.builder().id(1L).build()).build();

        resourceDto = ResourceDto.builder().id(1L).projectId(1L).build();
    }

    @Test
    void toDto() {
        ResourceDto actual = mapper.toDto(resource);

        assertEquals(resourceDto, actual);
    }

    @Test
    void toEntity() {
        Resource actual = mapper.toEntity(resourceDto);

        assertEquals(resource, actual);
    }

    @Test
    void update() {
        resourceDto.setProjectId(3L);
        mapper.update(resourceDto, resource);
        Resource expected = Resource.builder()
                .id(1L)
                .project(Project.builder()
                        .id(3L)
                        .build())
                .build();

        assertEquals(expected, resource);
    }
}