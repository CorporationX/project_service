package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResourceMapperTest {

    @Spy
    private ResourceMapper resourceMapper = new ResourceMapperImpl();

    @Test
    void testToDto() {
        Project project = Project.builder().id(1L).build();
        Resource resource = Resource.builder()
                .id(1L)
                .name("test.txt")
                .size(BigInteger.valueOf(100))
                .type(ResourceType.TEXT)
                .status(ResourceStatus.ACTIVE)
                .project(project)
                .build();

        ResourceDto dto = resourceMapper.toDto(resource);

        assertNotNull(dto);
        assertEquals(resource.getId(), dto.getId());
        assertEquals(resource.getName(), dto.getName());
        assertEquals(resource.getSize(), dto.getSize());
        assertEquals(resource.getType(), dto.getType());
        assertEquals(resource.getStatus(), dto.getStatus());
        assertEquals(project.getId(), dto.getProjectId());
    }

    @Test
    void testToEntity() {
        ResourceDto dto = ResourceDto.builder()
                .id(1L)
                .name("test.txt")
                .size(BigInteger.valueOf(100))
                .projectId(1L)
                .build();

        Resource entity = resourceMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getSize(), entity.getSize());
        assertNotNull(entity.getProject());
        assertEquals(dto.getProjectId(), entity.getProject().getId());
    }
}