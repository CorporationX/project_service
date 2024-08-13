package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {
    @Mock
    private MomentRepository momentRepository;
    @Mock
    private MomentMapper momentMapper;

    @InjectMocks
    private MomentService momentService;

    MomentDto momentDto;
    Moment moment;
    long momentId = 3L;
    long subProjectId = 2L;
    long subProjectIdOne = 1L;
    long userId = 1L;

    @BeforeEach
    void setUp() {
        momentDto = MomentDto.builder()
                .id(momentId)
                .name("Выполнены все подпроекты")
                .projectIds(List.of(subProjectId, subProjectIdOne))
                .userIds(List.of(userId))
                .build();
        moment = Moment.builder()
                .id(momentId)
                .name("Выполнены все подпроекты")
                .build();
    }

    @Nested
    @DisplayName("testAddMoment")
    class testAddMoment {

        @Test
        void testMomentDtoToEntity() {
            when(momentMapper.toEntity(momentDto)).thenReturn(moment);
            momentService.addMoment(momentDto);
            verify(momentMapper).toEntity(momentDto);
        }

        @Test
        void testSave() {
            when(momentMapper.toEntity(momentDto)).thenReturn(moment);
            when(momentRepository.save(moment)).thenReturn(moment);
            momentService.addMoment(momentDto);
            verify(momentRepository).save(moment);
        }

        @Test
        void testToDto() {
            when(momentMapper.toEntity(momentDto)).thenReturn(moment);
            when(momentRepository.save(moment)).thenReturn(moment);
            momentService.addMoment(momentDto);
            verify(momentMapper).toDto(moment);
        }
    }
}