package faang.school.projectservice.controller.internship;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.service.internship.InternshipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class InternshipControllerTest {
    @Mock
    private InternshipService service;
    @InjectMocks
    private InternshipController controller;

    @Test
    public void createNullInternshipTest() {
        DataValidationException exception = assertThrows(DataValidationException.class, () -> controller.createInternship(null));

        assertEquals(exception.getMessage(), "Internship is null!");
    }

    @Test
    public void createInternshipWithNullNameTest() {
        DataValidationException exception = assertThrows(DataValidationException.class, () -> controller.createInternship(new Internship()));

        assertEquals(exception.getMessage(), "Internship name can not be blank or null!");
    }

    @Test
    public void createInternshipWithBlankNameTest() {
        DataValidationException exception = assertThrows(DataValidationException.class, () -> controller.createInternship(Internship.builder().name("").build()));

        assertEquals(exception.getMessage(), "Internship name can not be blank or null!");
    }

    @Test
    public void createInternshipNullProjectTest() {
        DataValidationException exception = assertThrows(DataValidationException.class, () -> controller.createInternship(Internship.builder().name("A").build()));

        assertEquals(exception.getMessage(), "Internship relation project error!");
    }
}
