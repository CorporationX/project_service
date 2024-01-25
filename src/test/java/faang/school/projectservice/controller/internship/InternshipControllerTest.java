package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.service.internship.InternshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class InternshipControllerTest {
    @InjectMocks
    private InternshipController internshipController;
    @Mock
    private InternshipService internshipService;
    private InternshipDto internshipDto = new InternshipDto();


    @Test
    void testCreateInternshipSuccessful() {
        internshipDto.setId(123L);
        internshipController.createInternship(internshipDto);
        Mockito.verify(internshipService).createInternship(internshipDto);
    }

    @Test
    void testCreateInternshipWithNullInternshipDto() {
        assertThrows(IllegalArgumentException.class, () -> internshipController.createInternship(internshipDto));
    }

    @Test
    void testCreateInternshipWithNullId() {
        internshipDto.setId(null);
        assertThrows(IllegalArgumentException.class, () -> internshipController.createInternship(internshipDto));
    }

    @Test
    void testCreateInternshipWithInvalidId() {
        internshipDto.setId(-1L);
        assertThrows(IllegalArgumentException.class, () -> internshipController.createInternship(internshipDto));
    }
}