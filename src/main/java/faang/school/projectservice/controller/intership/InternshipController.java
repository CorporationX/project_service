package faang.school.projectservice.controller.intership;

import faang.school.projectservice.model.dto.internship.InternshipDto;
import faang.school.projectservice.model.dto.internship.InternshipFilterDto;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.validator.groups.CreateGroup;
import faang.school.projectservice.validator.groups.UpdateGroup;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/internships")
@RequiredArgsConstructor
@Validated
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InternshipDto createInternship(@RequestBody @Validated(CreateGroup.class) @NotNull InternshipDto internshipDto) {
        return internshipService.createInternship(internshipDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InternshipDto updateInternship(@PathVariable @Positive long id,
                                          @RequestBody @Validated(UpdateGroup.class) @NotNull InternshipDto internshipDto) {
        return internshipService.updateInternship(id, internshipDto);
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public List<InternshipDto> getAllInternshipsByFilter(@NotNull InternshipFilterDto filters) {
        return internshipService.getAllInternshipsByFilter(filters);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InternshipDto getInternshipById(@PathVariable @Positive long id) {
        return internshipService.getInternshipById(id);
    }
}
