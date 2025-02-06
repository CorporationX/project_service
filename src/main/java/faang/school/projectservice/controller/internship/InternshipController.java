package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.service.internship.InternshipService;
import faang.school.projectservice.validator.internship.InternshipControllerValidator;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internship")
@Tag(name = "Internship", description = "All methods for internship")
public class InternshipController {
    private final InternshipService internshipService;
    private final InternshipControllerValidator internshipValidator;

    @GetMapping("/health")
    public String checkHealth() {
        return "Application is running!";
    }

    @PostMapping
    @Operation(summary = "Create internship", description = "The method allows you to create an internship.")
    public InternshipDto createInternship(@NotNull @RequestBody InternshipDto internshipDto) {
        internshipValidator.checkDataBeforeCreate(internshipDto);
        internshipService.createInternship(internshipDto);
        return internshipDto;
    }

    @PutMapping
    @Operation(summary = "Update internship", description = "The method allows you to update an internship.")
    public InternshipDto updateInternship(@NotNull @RequestBody InternshipUpdateDto internshipUpdateDto) {
        internshipValidator.checkDataBeforeUpdate(internshipUpdateDto);
        return internshipService.updateInternship(internshipUpdateDto);
    }

    @GetMapping("/filter")
    @Operation(summary = "Get internships with filters", description = "The method allows you to get internships " +
            "by filters.")
    public List<InternshipDto> getProjectInternshipsWithFilters(InternshipFilterDto filters) {
        return internshipService.getInternshipsWithFilters(filters);
    }

    @GetMapping
    @Operation(summary = "Get all internships from database", description = "The method allows you to get all" +
            " internships from database.")
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get internship by id", description = "The method allows you to get the internship by id.")
    public InternshipDto getInternship(Long id) {
        internshipValidator.checkDataBeforeGetInternship(id);
        return internshipService.getInternship(id);
    }
}
