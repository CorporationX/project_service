package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.service.internship.InternshipService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internship")
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping("/create")
    public InternshipDto createInternship(@NotNull @RequestBody InternshipDto internshipDto) {
        return internshipService.createInternship(internshipDto);
    }

    @PutMapping("/intern/{id}/add/{internshipId}")
    public InternshipDto addNewIntern(@PathVariable long internshipId, @PathVariable long id) {
        return internshipService.addNewIntern(internshipId, id);
    }

    @PutMapping("/intern/{id}/finish/{internshipId}")
    public InternshipDto finishInterPrematurely(@PathVariable long internshipId, @PathVariable long id) {
        return internshipService.finishInterPrematurely(internshipId, id);
    }

    @DeleteMapping("intern/delete")
    public InternshipDto removeInterPrematurely(InternshipDto internshipDto, TeamMemberDto teamMemberDto) {
        return internshipService.removeInterPrematurely(internshipDto, teamMemberDto);
    }

    @PutMapping("/update")
    public InternshipDto updateInternship(InternshipDto internshipDto) {
        return internshipService.updateInternship(internshipDto);
    }

    @PutMapping("/update/end")
    public InternshipDto updateInternshipAfterEndDate(long idInternshipDto) {
        if (idInternshipDto < 1)
            throw new DataValidationException("Invalid id");
        return internshipService.updateInternshipAfterEndDate(idInternshipDto);
    }
@GetMapping
    public List<InternshipDto> getInternshipByFilter(@RequestBody InternshipFilterDto filter) {
        return internshipService.getInternshipByFilter(filter);
    }
}
