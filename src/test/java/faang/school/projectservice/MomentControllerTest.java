package faang.school.projectservice;

import faang.school.projectservice.controller.MomentController;
import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.service.MomentServiceImpl;
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
    private MomentServiceImpl momentServiceImpl;
    @InjectMocks
    private MomentController momentController;

    @Test
    public void createMoment() {
        MomentDto momentDto = new MomentDto();

        momentController.createMoment(momentDto);

        verify(momentServiceImpl, times(1)).createMoment(momentDto);
    }

    @Test
    public void getMoment() {
        long momentId = 1L;

        momentController.getMoment(momentId);

        verify(momentServiceImpl, times(1)).getMomentById(momentId);
    }

    @Test
    public void updateMoment() {
        long momentId = 1L;
        java.util.List<Long> addedProjectIds = List.of(1L, 2L);
        List<Long> addedUserIds = List.of(1L, 2L);

        momentController.updateMoment(momentId, addedProjectIds, addedUserIds);

        verify(momentServiceImpl, times(1)).updateMoment(momentId, addedProjectIds, addedUserIds);
    }

    @Test
    public void getAllMoments() {
        momentServiceImpl.getAllMoments();

        verify(momentServiceImpl, times(1)).getAllMoments();
    }

    @Test
    public void getAllProjectMomentsByDate() {
        Long projectId = 1L;
        LocalDateTime month = LocalDateTime.of(2020, 1, 1, 1, 1);

        momentServiceImpl.getAllProjectMomentsByDate(projectId, month);

        verify(momentServiceImpl, times(1)).getAllProjectMomentsByDate(projectId, month);
    }
}
