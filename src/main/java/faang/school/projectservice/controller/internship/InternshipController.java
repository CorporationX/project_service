package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.service.internship.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    public InternshipDto createInternship(InternshipDto internshipDto) {
        checkValidData(internshipDto);
        return internshipService.createInternship(internshipDto);
    }

    public InternshipDto addNewInterns(InternshipDto internshipDto, TeamMemberDto teamMemberDto) {
        checkValidData(internshipDto);
        checkValidData(teamMemberDto);
        return internshipService.addNewInterns(internshipDto, teamMemberDto);
    }

    public InternshipDto finishInterPrematurely(InternshipDto internshipDto, TeamMemberDto teamMemberDto) {
        checkValidData(internshipDto);
        checkValidData(teamMemberDto);
        return internshipService.finishInterPrematurely(internshipDto, teamMemberDto);
    }

    public InternshipDto removeInterPrematurely(InternshipDto internshipDto, TeamMemberDto teamMemberDto) {
        checkValidData(internshipDto);
        checkValidData(teamMemberDto);
        return internshipService.removeInterPrematurely(internshipDto, teamMemberDto);
    }

    public InternshipDto updateInternship(InternshipDto internshipDto) {
        checkValidData(internshipDto);
        return internshipService.updateInternship(internshipDto);
    }

    public InternshipDto updateInternshipAfterEndDate(long idInternshipDto) {
        checkValidData(idInternshipDto);
        return internshipService.updateInternshipAfterEndDate(idInternshipDto);
    }
    public List<InternshipDto> getInternshipByFilter(InternshipFilterDto filter) {
        checkValidData(filter);
        return internshipService.getInternshipByFilter(filter);
    }
    private <T> void checkValidData(T data) {
        if (data == null)
            throw new IllegalArgumentException("The object has not been created");
    }
}
