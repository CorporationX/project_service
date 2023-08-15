package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.repository.MomentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {

    @InjectMocks
    private MomentService momentService;
    @Mock
    private MomentRepository momentRepository;
    private MomentDto momentDto;

    @BeforeEach
    void init() {
        momentDto = MomentDto.builder()
                .id(1L)
                .name("moment")
                .build();
    }

    public void createMomentTestValid() {

    }
}
