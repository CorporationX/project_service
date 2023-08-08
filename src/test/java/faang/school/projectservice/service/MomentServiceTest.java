package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.validator.MomentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {
    @Mock
    private MomentRepository repository;
    @Mock
    private MomentMapper mapper;
    @Mock
    private MomentValidator validator;
    @InjectMocks
    private MomentService service;
    private MomentDto momentDto;
    private Moment moment;

    @BeforeEach
    void setUp() {
        momentDto = MomentDto.builder()
                .name("Name")
                .description("Description")
                .projectIds(List.of(1L))
                .userIds(List.of(1L))
                .imageId("imageId")
                .build();

        moment = Moment.builder()
                .id(1L)
                .name("Name")
                .description("Description")
                .projects(List.of(Project.builder().id(1L).build()))
                .userIds(List.of(1L))
                .imageId("imageId")
                .build();

        when(repository.save(Mockito.any())).thenReturn(moment);
    }

    @Test
    void create_shouldInvokeValidatorValidateMomentProjectsMethod() {
        service.create(momentDto);
        verify(validator).validateMomentProjects(momentDto);
    }

    @Test
    void create_shouldInvokeRepositorySaveMethod() {
        service.create(momentDto);
        verify(repository).save(mapper.toEntity(momentDto));
    }

    @Test
    void create_shouldInvokeMapperBothMethods() {
        service.create(momentDto);
        verify(mapper).toEntity(momentDto);
        verify(mapper).toDto(moment);
    }
}