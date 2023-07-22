package faang.school.projectservice;

import faang.school.projectservice.controller.InternshipController;
import faang.school.projectservice.service.InternshipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class InternshipControllerTest {
    @Mock
    private InternshipService internshipService;

    @InjectMocks
    private InternshipController internshipController;

//    @Test
//    public void ProjectForInternshipThrowsException() {
//        assertThrows(DataFormatException.class,
//                () -> internshipController.saveNewInternship())
//    }
}
