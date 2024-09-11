package faang.school.projectservice;

import faang.school.projectservice.controller.MomentController;
import faang.school.projectservice.service.MomentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MomentControllerTest {
    @Mock
    private MomentService momentService;
    @InjectMocks
    private MomentController momentController;

    @Test
    public void createMoment() {
        long momentId = 1L;

        momentController.createMoment(momentId);

        verify(momentService,times(1)).createMoment(momentId);
    }

    @Test
    public void getMoment() {
        long momentId = 1L;

        momentController.getMoment(momentId);

        verify(momentService,times(1)).getMomentById(momentId);
    }

    @Test
    public void updateMoment() {
        long momentId = 1L;
        java.util.List<Long> addedProjectIds = List.of(1L, 2L);
        List<Long> addedUserIds = List.of(1L, 2L);

        momentController.updateMoment(momentId, addedProjectIds, addedUserIds);

        verify(momentService,times(1)).updateMoment(momentId, addedProjectIds, addedUserIds);
    }

    @Test
    public void getAllMoments() {
       momentService.getAllMoments();

       verify(momentService,times(1)).getAllMoments();
    }

    @Test
    public void getAllProjectMomentsByDate() {
        Long projectId = 1L;
        LocalDateTime month = LocalDateTime.of(2020, 1, 1, 1, 1);

        momentService.getAllProjectMomentsByDate(projectId, month);

        verify(momentService,times(1)).getAllProjectMomentsByDate(projectId, month);
    }
}
