package faang.school.projectservice.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.MomentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MomentServiceImplTest {
    @InjectMocks
    private MomentServiceImpl momentService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MomentRepository momentRepository;

    @Spy
    private MomentMapper momentMapper = Mappers.getMapper(MomentMapper.class);

    private MomentDto momentDto;
    private Moment moment;
    private Project project;

    @BeforeEach
    public void setUp() {
        momentDto = new MomentDto();
        momentDto.setName("Test moment");
        momentDto.setDescription("Test description");
        String dateString = "2024-10-01";
        LocalDateTime dateTime = LocalDateTime.parse(dateString + "T00:00:00");
        momentDto.setProjectIds(List.of(1L));

        project = new Project();
        project.setId(1L);
        project.setName("Open Project");
        project.setStatus(ProjectStatus.CREATED);

        moment = new Moment();
        moment.setId(1L);
        moment.setName(momentDto.getName());
        moment.setProjects(Collections.singletonList(project));
    }

    @Test
    public void createMoment_success() {
        when(projectRepository.findAllByIds(momentDto.getProjectIds())).thenReturn(Collections.singletonList(project));
        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        when(momentRepository.save(any(Moment.class))).thenReturn(moment);
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        MomentDto createdMomentDto = momentService.createMoment(momentDto);

        assertEquals(momentDto.getName(), createdMomentDto.getName());
        assertEquals(momentDto.getDescription(), createdMomentDto.getDescription());

        verify(momentRepository).save(any(Moment.class));
    }

    @Test
    public void updateMoment_success() {
        when(momentRepository.findById(1L)).thenReturn(Optional.of(moment));
        when(projectRepository.findAllByIds(momentDto.getProjectIds())).thenReturn(Collections.singletonList(project));
        when(momentRepository.save(any(Moment.class))).thenReturn(moment);

        MomentDto updatedMomentDto = momentService.updateMoment(1L, momentDto);

        assertEquals(momentDto.getName(), updatedMomentDto.getName());
        assertEquals(momentDto.getDescription(), updatedMomentDto.getDescription());
        assertEquals(momentDto.getDate(), updatedMomentDto.getDate());
        assertEquals(momentDto.getProjectIds(), updatedMomentDto.getProjectIds());

        verify(momentRepository).save(any(Moment.class));
    }

    @Test
    public void getMomentById_found() {
        when(momentRepository.findById(1L)).thenReturn(Optional.of(moment));
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        MomentDto foundMoment = momentService.getMomentById(1L);

        assertEquals(momentDto.getId(), foundMoment.getId());
        assertEquals(momentDto.getName(), foundMoment.getName());
    }
}
