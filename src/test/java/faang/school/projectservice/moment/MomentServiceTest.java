package faang.school.projectservice.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.MomentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class MomentServiceTest {
    @Mock
    private MomentRepository momentRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MomentMapper momentMapper;

    @InjectMocks
    private MomentService momentService;

    private MomentDto momentDto;
    private Moment moment;
    private Project project;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        project = new Project();
        project.setId(1L);
        project.setStatus(ProjectStatus.IN_PROGRESS);

        momentDto = new MomentDto();
        momentDto.setName("Important Moment");
        momentDto.setDescription("This is an important moment");
        momentDto.setDate(LocalDateTime.now());
        momentDto.setProjectIds(Arrays.asList(1L));

        moment = new Moment();
        moment.setId(1L);
        moment.setName("Important Moment");
    }

    @Test
    void createMoment_success() {
        when(projectRepository.findAllByIds(any())).thenReturn(Arrays.asList(project));
        when(momentMapper.toEntity(any())).thenReturn(moment);
        when(momentRepository.save(any())).thenReturn(moment);
        when(momentMapper.toDto(any())).thenReturn(momentDto);

       momentService.createMoment(momentDto);
    }

    @Test
    void createMoment_withoutName_throwsException() {
        momentDto.setName(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            momentService.createMoment(momentDto);
        });

        assertEquals("Moment must have a name", exception.getMessage());
    }

    @Test
    void updateMoment_success() {
        when(momentRepository.findById(anyLong())).thenReturn(Optional.of(moment));
        when(projectRepository.findAllByIds(any())).thenReturn(Arrays.asList(project));
        when(momentMapper.toDto(any())).thenReturn(momentDto);

        MomentDto result = momentService.updateMoment(1L, momentDto);

        assertNotNull(result);
        assertEquals("Important Moment", result.getName());
    }

    @Test
    void updateMoment_withNonExistingMoment_throwsException() {
        when(momentRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            momentService.updateMoment(1L, momentDto);
        });

        assertEquals("Moment not found", exception.getMessage());
    }
}
