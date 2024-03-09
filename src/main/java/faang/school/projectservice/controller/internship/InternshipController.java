package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.internship.InternshipService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internship")
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping
    public InternshipDto createInternship(@Validated @RequestBody InternshipDto internshipDto) {
        return internshipService.createInternship(internshipDto);
    }

    @PutMapping("/intern/{id}/add/{internshipId}")
    public InternshipDto addNewIntern(@PathVariable @Positive(message = "Id must be greater than zero") long internshipId,
                                      @PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return internshipService.addNewIntern(internshipId, id);
    }

    @PutMapping("/intern/{id}/finish/{internshipId}")
    public InternshipDto finishInternPrematurely(@PathVariable @Positive(message = "Id must be greater than zero") long internshipId,
                                                 @PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return internshipService.finishInternPrematurely(internshipId, id);
    }

    @DeleteMapping("intern/{id}/delete/{internshipId}")
    public InternshipDto removeInternPrematurely(@PathVariable @Positive(message = "Id must be greater than zero") long internshipId,
                                                 @PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return internshipService.removeInternPrematurely(internshipId, id);
    }

    @PutMapping
    public InternshipDto updateInternship(@NotNull @RequestBody InternshipDto internshipDto) {
        return internshipService.updateInternship(internshipDto);
    }

    @PutMapping("/update/end/{internshipId}")
    public InternshipDto updateInternshipAfterEndDate(@PathVariable @Positive(message = "Id must be greater than zero") long internshipId) {
        return internshipService.updateInternshipAfterEndDate(internshipId);
    }

    @GetMapping("/filter/status")
    public List<InternshipDto> getInternshipByStatus(@RequestBody InternshipFilterDto filter) {
        return internshipService.getInternshipByStatus(filter);
    }

    @GetMapping("/filter/{role}")
    public List<InternshipDto> getInternshipByRole(InternshipFilterDto id, @PathVariable TeamRole role) {
        return internshipService.getInternshipByRole(id, role);
    }

    @GetMapping("/all")
    public List<InternshipDto> getAllInternship() {
        return internshipService.getAllInternship();
    }

    @GetMapping("/{id}")
    public InternshipDto getById(@PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return internshipService.getDtoById(id);
    }
}
