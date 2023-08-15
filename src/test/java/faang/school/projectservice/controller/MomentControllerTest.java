package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.MomentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MomentControllerTest {
    @InjectMocks
    private MomentController momentController;
    @Mock
    private MomentService momentService;

    @Test
    public void createMomentTestNullMomentThrowException() {
        Exception exception = assertThrows(DataValidationException.class,
                () -> momentController.createMoment(null));
        assertEquals("create null moment exception message",
                "Moment can't be null",
                exception.getMessage()
        );
    }

    @Test
    public void createMomentTestEmptyMomentThrowException() {
        MomentDto emptyMomentDto = MomentDto.builder().build();

        Exception exception = assertThrows(DataValidationException.class,
                () -> momentController.createMoment(emptyMomentDto));
        assertEquals("create empty moment exception message",
                "Moment name can't be null or blank",
                exception.getMessage()
        );
    }

    @Test
    public void createMomentTestValid() {
        MomentDto momentDto = MomentDto.builder().id(1L).name("birthday").build();

        momentController.createMoment(momentDto);
        Mockito.verify(momentService, Mockito.times(1)).createMoment(momentDto);
    }
}
