package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceTest {

    @Test
    void saveProject() {
        ProjectDto projectDto = new ProjectDto( "name", "description", 1L);
        assertNull(projectDto.getStatus());

    }
}