package faang.school.projectservice.mapper.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResourceMapperTest {
    private ResourceMapper resourceMapper;

    @BeforeEach
    void setUp() {
        resourceMapper = Mappers.getMapper(ResourceMapper.class);
    }

    @Test
    void testToDto() {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setName("Test Resource");
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());

        TeamMember createdBy = new TeamMember();
        createdBy.setId(10L);
        resource.setCreatedBy(createdBy);

        TeamMember updatedBy = new TeamMember();
        updatedBy.setId(20L);
        resource.setUpdatedBy(updatedBy);

        ResourceDto resourceDto = resourceMapper.toDto(resource);

        assertEquals(resource.getId(), resourceDto.getId());
        assertEquals(resource.getName(), resourceDto.getName());
        assertEquals(resource.getStatus(), resourceDto.getStatus());
        assertEquals(resource.getCreatedAt(), resourceDto.getCreatedAt());
        assertEquals(resource.getUpdatedAt(), resourceDto.getUpdatedAt());
        assertEquals(resource.getCreatedBy().getId(), resourceDto.getCreatedById());
        assertEquals(resource.getUpdatedBy().getId(), resourceDto.getUpdatedById());
    }

    @Test
    void testToEntity() {
        ResourceDto resourceDto = new ResourceDto();
        resourceDto.setId(1L);
        resourceDto.setName("Test Resource");
        resourceDto.setStatus(ResourceStatus.ACTIVE);
        resourceDto.setCreatedAt(LocalDateTime.now());
        resourceDto.setUpdatedAt(LocalDateTime.now());
        resourceDto.setCreatedById(10L);
        resourceDto.setUpdatedById(20L);

        Resource resource = resourceMapper.toEntity(resourceDto);

        assertEquals(resourceDto.getId(), resource.getId());
        assertEquals(resourceDto.getName(), resource.getName());
        assertEquals(resourceDto.getStatus(), resource.getStatus());
        assertEquals(resourceDto.getCreatedAt(), resource.getCreatedAt());
        assertEquals(resourceDto.getUpdatedAt(), resource.getUpdatedAt());
    }
}
