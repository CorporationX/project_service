package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.moment.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.filters.MomentFilter;
import faang.school.projectservice.validator.moment.ValidatorMoment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {

    @Spy
    private MomentMapperImpl momentMapper;
    @Mock
    private ValidatorMoment validatorMoment;
    @Mock
    private MomentRepository momentRepository;
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private MomentService momentService;
    private MomentDto momentDto;
    private Moment moment;


    @BeforeEach
    void entity(){
        Project project = Project.builder()
                .id(5L)
                .build();
        Project project1 = Project.builder()
                .id(6L)
                .build();
        Project project2 = Project.builder()
                .id(66L)
                .build();
        Project project3 = Project.builder()
                .id(67L)
                .build();
        MomentDto momentDto = MomentDto.builder()
                .id(10L)
                .name("testMomentDto")
                .projectIds(Arrays.asList(1L, 2L))
                .userIds(Arrays.asList(3L,4L))
                .build();
        Moment moment = Moment.builder()
                .id(9L)
                .name("testMoment")
                .projects(Arrays.asList(project,project1))
                .userIds(Arrays.asList(7L,8L))
                .build();

        List<Project> newProjects = Arrays.asList(project2,project3);
        List<Long> newProjectIds = Arrays.asList(11L,12L,13L);
        List<Long> oldProjectIds = Arrays.asList(111L,2L,1L);
        List<Long> oldUserIds = Arrays.asList(15L,20L,30L);
        List<Long> newUserIds = Arrays.asList(14L,21L,31L);
        MomentFilter momentFiltersMock = Mockito.mock(MomentFilter.class);
        List<MomentFilter> momentFilters = List.of(momentFiltersMock);
    }

    @Test
    public void testCreateMoment() {

        momentService.createMoment(momentDto);

        verify(validatorMoment).ValidatorMomentName(any(MomentDto.class));
        verify(validatorMoment).ValidatorOpenProject(any(MomentDto.class));
        verify(validatorMoment).ValidatorMomentProject(any(MomentDto.class));
        verify(momentMapper).toEntity(momentDto);
        verify(momentRepository).save(any(Moment.class));
    }


    @Test
    public void testUpdateMoment() {
        when(momentRepository.findById(momentDto.getId())).thenReturn(Optional.ofNullable(moment));
        when(momentRepository.save(moment)).thenReturn(moment);

        momentService.updateMoment(momentDto);

        verify(momentRepository).save(any(MomentDto.class));
    }

    @Test
    public void getAllMomentsByDateAndProjectTest(){

    }

}
