package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.validator.MomentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {

    @InjectMocks
    private MomentService momentService;
    @Mock
    private MomentRepository momentRepository;
    @Spy
    private MomentMapper momentMapper;
    @Mock
    private MomentValidator momentValidator;
    @Mock
    private ProjectService projectService;
    private MomentDto momentDto;

    @BeforeEach
    void init() {
        momentDto = MomentDto.builder()
                .id(1L)
                .name("moment")
                .projectIds(List.of(1L, 2L))
                .date(LocalDateTime.now())
                .build();
    }

    @Test
    public void createMomentTestValid() {
        Mockito.doNothing().when(momentValidator).checkIsProjectClosed(momentDto.getProjectIds());

        Moment moment = momentMapper.toEntity(momentDto);
        momentService.createMoment(momentDto);

        Mockito.verify(projectService, Mockito.times(1))
                .getProjectsById(Mockito.anyList());
        Mockito.verify(momentRepository, Mockito.times(1))
                .save(moment);
    }
}
