package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.filter.internship.InternshipFilterDto;
import faang.school.projectservice.service.internship.InternshipService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/internship")
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping
    public InternshipDto create(@RequestBody @Valid @NotNull InternshipDto internship) {
        return internshipService.create(internship);
    }

    @PutMapping("/update")
    public void updateInternship(@RequestBody @Valid @NotNull InternshipDto internshipDto) {
        internshipService.updateInternship(internshipDto);
    }

    @GetMapping("/status")
    public List<InternshipDto> getAllInternshipByStatus(@RequestParam @NotNull Long projectId,
                                                        @RequestBody @Valid InternshipFilterDto filters) {
        return internshipService.getAllInternshipByStatus(projectId, filters);
    }

    @GetMapping(("/allinternship"))
    public List<InternshipDto> getAllInternship() {
        return internshipService.getAllInternship();
    }

    @GetMapping(("/{id}"))
    public InternshipDto getInternshipById(@PathVariable @NotNull Long id) {
        return internshipService.getInternshipById(id);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRunTimeException(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + exception.getMessage());
    }
}
