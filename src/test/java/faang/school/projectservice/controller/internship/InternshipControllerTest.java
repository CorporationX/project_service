package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.service.internship.InternshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class InternshipControllerTest {
    @InjectMocks
    private InternshipController internshipController;
    @Mock
    private InternshipService internshipService;
    private InternshipDto internshipDto;
    private InternshipDto internshipDtoNull;
    private TeamMemberDto teamMemberDto;
    private InternshipFilterDto filter;
    private long INTERNSHIP_ID = 5;
    private long NO_VALID_ID = -5;

    @BeforeEach
    void setUp() {
        internshipDto = InternshipDto.builder().build();
        teamMemberDto = TeamMemberDto.builder().build();
        filter = new InternshipFilterDto();
    }

    @Test
    void testCreateInternshipSuccessful() {
        internshipDto.setId(123L);
        internshipController.createInternship(internshipDto);
        Mockito.verify(internshipService).createInternship(internshipDto);
    }


    @Test
    void testCreateInternshipWithNullInternshipDto() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipController.createInternship(internshipDtoNull));
        assertEquals(exception.getMessage(), "The object has not been created");
    }

    @Test
    void testAddNewInternsSuccessful() {
        internshipController.addNewInterns(internshipDto, teamMemberDto);
        Mockito.verify(internshipService).addNewInterns(internshipDto, teamMemberDto);
    }

    @Test
    void testFinishInterPrematurelySuccessful() {
        internshipController.finishInterPrematurely(internshipDto, teamMemberDto);
        Mockito.verify(internshipService).finishInterPrematurely(internshipDto, teamMemberDto);
    }

    @Test
    void removeInterPrematurelySuccessful() {
        internshipController.removeInterPrematurely(internshipDto, teamMemberDto);
        Mockito.verify(internshipService).removeInterPrematurely(internshipDto, teamMemberDto);
    }

    @Test
    void testUpdateInternshipSuccessful() {
        internshipController.updateInternship(internshipDto);
        Mockito.verify(internshipService).updateInternship(internshipDto);
    }

    @Test
    void testUpdateInternshipAfterEndDateSuccessful() {
        internshipController.updateInternshipAfterEndDate(INTERNSHIP_ID);
        Mockito.verify(internshipService).updateInternshipAfterEndDate(INTERNSHIP_ID);
    }

    @Test
    void testUpdateInternshipAfterEndDateFailed() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> internshipController.updateInternshipAfterEndDate(NO_VALID_ID));
        assertEquals(exception.getMessage(), "Invalid id");
    }

    @Test
    void testGetInternshipByFilterSuccessful() {
        internshipController.getInternshipByFilter(filter);
        Mockito.verify(internshipService).getInternshipByFilter(filter);
    }
}