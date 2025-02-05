package faang.school.projectservice.service.moment;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {
    @Mock
    private MomentRepository repository;
    @InjectMocks
    private MomentService service;

    @Test
    void create() {
        Moment expected = new Moment();
        Moment actual = service.create();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getAllByProjectId() {
        List<Moment> expected = List.of(
                new Moment(),
                new Moment(),
                new Moment()
        );
        Mockito.when(repository.findAllByProjectId(1L)).thenReturn(expected);
        List<Moment> actual = service.getAllByProjectId(1L);
        Assertions.assertEquals(expected, actual);
    }
}