package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MomentServiceTest {
    @InjectMocks
    private MomentService momentService;
    @Mock
    private MomentRepository momentRepository;
    @Mock
    private MomentMapper momentMapper;
    @Mock
    private ProjectService projectService;
    private Moment moment = new Moment();
    private Project project = new Project();
    private MomentDto momentDto = new MomentDto();
    private Long rightId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rightId = 1L;
    }

    @Test
    public void testCreateMoment() {
        momentDto.setProjectId(rightId);

        Mockito.when(momentMapper.toMoment(momentDto))
                .thenReturn(moment);
        Mockito.when(projectService.getProjectById(momentDto.getProjectId()))
                .thenReturn(project);

        momentService.createMoment(momentDto);

        assertTrue(moment.getCreatedAt().isBefore(LocalDateTime.now()));
        assertEquals(1, moment.getProjects().size());

        Mockito.verify(momentMapper, Mockito.times(1))
                .toMoment(momentDto);
        Mockito.verify(projectService, Mockito.times(1))
                .getProjectById(momentDto.getProjectId());
        Mockito.verify(momentRepository, Mockito.times(1))
                .save(moment);
    }

}