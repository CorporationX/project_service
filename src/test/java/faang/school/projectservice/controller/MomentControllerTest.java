package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.validator.MomentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MomentControllerTest {
    private MockMvc mockMvc;
    @InjectMocks
    private MomentController momentController;
    @Mock
    private MomentService momentService;
    @Mock
    private MomentValidator momentValidator;

    @BeforeEach
    private void init() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(momentController)
                .build();
    }

    @Test
    public void createMomentTestNullMomentThrowException() {
        Mockito.doThrow(new DataValidationException("Moment can't be null")).when(momentValidator).validateMoment(null);
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
