package faang.school.projectservice.mappper;

import faang.school.projectservice.dto.project.ResourceDto;
import faang.school.projectservice.mapper.ResourceMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ResourceMapperTest {
    @Spy
    private ResourceMapperImpl resourceMapper;

    private Resource resource;

    private ResourceDto resourceDto;

    @BeforeEach
    void setUp() {
        resource = Resource.builder().id(1L).project(Project.builder().id(1L).build()).build();
        resourceDto = ResourceDto.builder().id(1L).projectId(1L).build();
    }

    @Test
    void toDto() {
        ResourceDto result = resourceMapper.toDto(resource);
        assertEquals(resourceDto, result);
    }

    @Test
    void toEntity() {
        Resource result = resourceMapper.toEntity(resourceDto);
        assertEquals(resource, result);
    }

    @Test
    void update() {
        resourceDto.setProjectId(1L);
        resourceMapper.update(resourceDto, resource);
        Resource result = Resource.builder()
                .id(1L)
                .project(Project.builder().id(1L).build())
                .build();
        assertEquals(result, resource);
    }
}
