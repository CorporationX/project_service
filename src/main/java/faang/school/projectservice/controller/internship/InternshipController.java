package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.service.internship.InternshipService;
import faang.school.projectservice.validator.internship.InternshipControllerValidator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
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
    public InternshipDto createInternship(@NotNull @RequestBody InternshipDto internshipDto) {
        internshipValidator.checkDataBeforeCreate(internshipDto);
        internshipService.createInternship(internshipDto);
        return internshipDto;
    }
    @PutMapping
    public InternshipDto updateInternship(@NotNull @RequestBody InternshipDto internshipDto) {
        internshipValidator.checkDataBeforeUpdate(internshipDto);
        return internshipService.updateInternship(internshipDto);
    }

    @GetMapping("/filter")
    public List<InternshipDto> getInternshipsWithFilters(InternshipFilterDto filters) {
        return getInternshipsWithFilters(filters);
    }
    @GetMapping
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }

    @GetMapping("/{id}")
    public InternshipDto getInternship(Long id) {
        internshipValidator.checkDataBeforeGetInternship(id);
        return internshipService.getInternship(id);
    }
}
