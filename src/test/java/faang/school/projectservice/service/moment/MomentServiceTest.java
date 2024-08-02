package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class MomentServiceTest {
    @SpyBean
    private MomentRepository momentRepository;
    @MockBean
    private ProjectMapper projectMapper;
    @SpyBean
    private MomentService momentService;

    private Project project = new Project();
    private ProjectDto projectDto = new ProjectDto();
    private String momentName = "All subprojects finished";

    @BeforeEach
    void setUp() {
        lenient().when(projectMapper.toDto(project)).thenReturn(projectDto);
    }

    @Test
    public void testAddMomentByNameWithExistingMoment() {
        momentService.addMomentByName(project, momentName);
        verify(momentService, times(1)).updateMoment(any(), any());
        verify(momentService, times(0)).createMoment(any(), any());
    }

    @Test
    public void testAddMomentByNameWithoutExistingMoment() {
        momentName = "something not existing";
        momentService.addMomentByName(project, momentName);
        verify(momentService, times(0)).updateMoment(any(), any());
        verify(momentService, times(1)).createMoment(any(), any());
    }

    @Test
    public void testFindMomentByName() {
        if (momentService.findMomentByName(momentName).isEmpty()) {
            Moment moment = new Moment();
            moment.setName(momentName);
            moment.setDate(LocalDateTime.now());
            momentRepository.save(moment);
        }
        Optional<Moment> result = momentService.findMomentByName(momentName);

        assertTrue(result.isPresent());
        assertEquals(momentName, result.get().getName());
    }
}