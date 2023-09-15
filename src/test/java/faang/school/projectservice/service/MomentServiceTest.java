package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.moment.MomentService;
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

}
