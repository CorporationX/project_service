package faang.school.projectservice.controller.internship;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.internship.InternshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InternshipControllerTest {
    @InjectMocks
    private InternshipController internshipController;
    @Mock
    private InternshipService internshipService;
    @Mock
    private UserContext userContext;
    private InternshipDto internshipDto;
    private TeamMemberDto teamMemberDto;
    private InternshipFilterDto filter;
    private TeamRole teamRole;
    private long INTERNSHIP_ID = 5;


    @BeforeEach
    void setUp() {
        internshipDto = InternshipDto.builder()
                .id(1L)
                .build();
        teamMemberDto = TeamMemberDto.builder()
                .id(100L)
                .build();
        filter = new InternshipFilterDto();
    }

    @Test
    void testCreateInternshipSuccessful() {
        Mockito.when(userContext.getUserId()).thenReturn(1L);
        internshipController.createInternship(internshipDto);
        Mockito.verify(internshipService).createInternship(internshipDto);
    }

    @Test
    void testAddNewInternsSuccessful() {
        internshipController.addNewIntern(internshipDto.getId(), teamMemberDto.getId());
        Mockito.verify(internshipService).addNewIntern(internshipDto.getId(), teamMemberDto.getId());
    }

    @Test
    void testFinishInterPrematurelySuccessful() {
        internshipController.finishInternPrematurely(internshipDto.getId(), teamMemberDto.getId());
        Mockito.verify(internshipService).finishInternPrematurely(internshipDto.getId(), teamMemberDto.getId());
    }

    @Test
    void removeInterPrematurelySuccessful() {
        internshipController.removeInternPrematurely(internshipDto.getId(), teamMemberDto.getId());
        Mockito.verify(internshipService).removeInternPrematurely(internshipDto.getId(), teamMemberDto.getId());
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
    void testGetInternshipByStatusSuccessful() {
        internshipController.getInternshipByStatus(filter);
        Mockito.verify(internshipService).getInternshipByStatus(filter);
    }

    @Test
    void testGetInternshipByRoleSuccessful() {
        internshipController.getInternshipByRole(filter, teamRole);
        Mockito.verify(internshipService).getInternshipByRole(filter, teamRole);
    }

    @Test
    void testGetAllInternshipSuccessful() {
        internshipController.getAllInternship();
        Mockito.verify(internshipService).getAllInternship();
    }

    @Test
    void testGetById() {
        internshipController.getById(INTERNSHIP_ID);
        Mockito.verify(internshipService).getDtoById(INTERNSHIP_ID);
    }
}