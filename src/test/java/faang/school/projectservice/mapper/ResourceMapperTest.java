package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResourceMapperTest {
    @Spy
    private ResourceMapper mapper = new ResourceMapperImpl();

    @Test
    public void shouldMapResourceToResourceDto() {
        Project project = new Project();
        project.setId(1L);
        Resource resource = new Resource();
        resource.setProject(project);

        ResourceDto resourceDto = mapper.toDto(resource);

        assertEquals(resource.getProject().getId(), resourceDto.getProjectId());
    }
}