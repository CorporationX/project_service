package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.mapper.moment.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.validator.moment.ValidatorMoment;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {

    @Spy
    private MomentMapperImpl momentMapper;
    @Mock
    private ValidatorMoment validatorMoment;
    @Mock
    private MomentRepository momentRepository;
    @InjectMocks
    private MomentService momentService;

    private MomentDto momentDto;

    @Test
    public void testCreateMoment() {
        momentDto = MomentDto.builder()
                .name("test")
                .projectIds(Arrays.asList(1L, 2L))
                .build();

        momentService.createMoment(momentDto);

        verify(validatorMoment).ValidatorMomentName(any(MomentDto.class));
        verify(validatorMoment).ValidatorOpenProject(any(MomentDto.class));
        verify(validatorMoment).ValidatorMomentProject(any(MomentDto.class));
        verify(momentMapper).toEntity(momentDto);
        verify(momentRepository).save(any(Moment.class));
    }
}
