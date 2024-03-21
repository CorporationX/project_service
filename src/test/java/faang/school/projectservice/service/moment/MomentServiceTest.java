package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.mapper.moment.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.moment.ValidatorMoment;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private MomentDto moment;
    @BeforeEach
    void entity(){
        Long id = 1L;
        momentDto = MomentDto.builder()
                .id(10L)
                .name("testMomentDto")
                .projectIds(Arrays.asList(1L, 2L))
                .userIds(Arrays.asList(3L,4L))
                .build();
        moment = MomentDto.builder()
                .id(9L)
                .name("testMoment")
                .projectIds(Arrays.asList(5L,6L))
                .userIds(Arrays.asList(7L,8L))
                .build();
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
    public void testUpdateUsers(){

    }


    @Test
    public void testUpdateMoment(){
        when(momentService.updateUsers(momentDto)).thenReturn(momentDto);
        assertThrows(EntityNotFoundException.class, () -> momentService.updateUsers(momentDto));
        when(momentService.updateProjects(momentDto)).thenReturn(momentDto);

        momentService.updateMoment(momentDto);

        verify(momentService).updateUsers(any(MomentDto.class));
        verify(momentService).updateProjects(any(MomentDto.class));
    }
}
