package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.moment.MomentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MomentControllerTest {
    @Mock
    private MomentServiceImpl momentService;
    @InjectMocks
    private MomentController momentController;

    private MomentDto momentDto;
    private long id;
    private MomentFilterDto filterDto;

    @BeforeEach
    void setUp() {
        momentDto = MomentDto.builder().build();
        id = 1;
        filterDto = MomentFilterDto.builder().build();
    }

    @Test
    void createMoment() {
        momentController.createMoment(momentDto);

        verify(momentService).createMoment(momentDto);
    }

    @Test
    void updateMomentByProjects() {
        momentController.updateMomentByProjects(momentDto);

        verify(momentService).updateMomentByProjects(momentDto);
    }

    @Test
    void updateMomentByUser() {
        momentController.updateMomentByUser(id, momentDto);

        verify(momentService).updateMomentByUser(id, momentDto);
    }

    @Test
    void getMomentsByFilters() {
        momentController.getMomentsByFilters(id, filterDto);

        verify(momentService).getMomentsByFilters(id, filterDto);
    }

    @Test
    void getAllMoments() {
        momentController.getAllMoments();

        verify(momentService).getAllMoments();
    }

    @Test
    void getMomentById() {
        momentController.getMomentById(id);

        verify(momentService).getMomentById(id);
    }
}