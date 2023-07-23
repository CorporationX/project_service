package faang.school.projectservice.contoller.moment;


import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.MomentService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MomentControllerTest {
    @InjectMocks
    private MomentController momentController;

    @Mock
    private MomentService momentService;

    @Mock
    private MomentRepository momentRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Test
    public void testCreateMoment_ValidMomentDto_ReturnsCreatedMomentDto() {
        MomentDto validMomentDto = createMomentDto();
        when(momentService.create(any(MomentDto.class))).thenReturn(validMomentDto);

        ResponseEntity<MomentDto> response = momentController.create(validMomentDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(validMomentDto.getId(), response.getBody().getId());
        assertEquals(validMomentDto.getName(), response.getBody().getName());
    }

    @Test
    public void testCreateMoment_InvalidMomentDto_ReturnsBadRequest() {
        MomentDto invalidMomentDto = createMomentDto();
        invalidMomentDto.setId(null);

        ResponseEntity<MomentDto> response = momentController.create(invalidMomentDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    private MomentDto createMomentDto() {
        MomentDto momentDto = new MomentDto();
        momentDto.setId(1L);
        momentDto.setName("Test Moment");
        momentDto.setDescription("Test Description");
        momentDto.setDate(LocalDateTime.now());
        momentDto.setProjects(Collections.singletonList(createProjectDto()));
        momentDto.setUserIds(createUserIds());
        momentDto.setCreatedBy(1L);
        return momentDto;
    }

    private ProjectDto createProjectDto() {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setStatus(ProjectStatus.IN_PROGRESS);
        return projectDto;
    }

    private List<Long> createUserIds() {
        return IntStream.rangeClosed(1, 5).mapToLong(i -> i).boxed().toList();
    }
}