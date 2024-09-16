package faang.school.projectservice.controller;

import faang.school.projectservice.dto.intership.InternshipDto;
import faang.school.projectservice.dto.intership.InternshipFilterDto;
import faang.school.projectservice.dto.intership.TeamMemberDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.service.internship.InternshipServiceImpl;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(SpringExtension.class)
class InternshipControllerTest {

    @InjectMocks
    InternshipController internshipController;

    @Mock
    InternshipServiceImpl internshipService;

    @Test
    void testCreateInternship() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(2);

        InternshipDto internshipDto = new InternshipDto(1L, 1L, new TeamMemberDto(1L, 4L),
                List.of(), startDate, endDate, InternshipStatus.IN_PROGRESS, 999L, "name","description");

        internshipController.createInternship(internshipDto);

        Mockito.verify(internshipService).create(internshipDto);
    }

    @Test
    void testNullCreateInternship() {
        InternshipDto internshipDto = new InternshipDto(1L, null, new TeamMemberDto(1L, 4L),
                List.of(), LocalDateTime.now(), LocalDateTime.now(), InternshipStatus.IN_PROGRESS, 999L, "name","description");
        Assert.assertThrows(DataValidationException.class, () -> internshipController
                .createInternship(internshipDto));
    }

    @Test
    void testUpdate() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(2);

        InternshipDto internshipDto = new InternshipDto(1L, 1L, new TeamMemberDto(1L, 4L),
                List.of(), startDate, endDate, InternshipStatus.IN_PROGRESS, 999L, "name","description");

        internshipController.update(internshipDto);

        Mockito.verify(internshipService).update(internshipDto);
    }

    @Test
    void testGetInternshipByFilter() {
        InternshipFilterDto internshipFilterDto = new InternshipFilterDto();

        internshipController.getInternshipByFilter(internshipFilterDto);

        Mockito.verify(internshipService).getInternshipByFilter(internshipFilterDto);
    }

    @Test
    void testGetAllInternships() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(2);

        InternshipDto internshipDto = new InternshipDto(1L, 1L, new TeamMemberDto(1L, 4L),
                List.of(), startDate, endDate, InternshipStatus.IN_PROGRESS, 999L, "name","description");

        internshipController.getAllInternships(internshipDto);

        Mockito.verify(internshipService).getAllInternships(internshipDto);
    }

    @Test
    void testGetInternshipById() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(2);

        InternshipDto internshipDto = new InternshipDto(1L, 1L, new TeamMemberDto(1L, 4L),
                List.of(), startDate, endDate, InternshipStatus.IN_PROGRESS, 999L, "name","description");

        internshipController.getInternshipById(internshipDto);

        Mockito.verify(internshipService).getInternshipById(internshipDto);
    }
}