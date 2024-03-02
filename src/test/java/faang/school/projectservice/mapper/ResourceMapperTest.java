package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ResourceMapperTest {
    @Spy
    private ResourceMapper mapper = new ResourceMapperImpl();
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
    public void shouldMapResourceToResourceDto() {
        Project project = new Project();
        project.setId(1L);
        Resource resource = new Resource();
        resource.setProject(project);

        ResourceDto resourceDto = mapper.toDto(resource);

        assertEquals(resource.getProject().getId(), resourceDto.getProjectId());
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