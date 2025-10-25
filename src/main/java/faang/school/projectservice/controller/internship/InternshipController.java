package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.service.internship.InternshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/internships")
public class InternshipController {

    private final InternshipService internshipService;

    @PostMapping
    public ResponseEntity<InternshipDto> create(@RequestBody @Valid CreateInternshipDto createInternshipDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(internshipService.create(createInternshipDto));
    }

    @PatchMapping("/{internshipId}")
    public ResponseEntity<InternshipDto> update(@PathVariable long internshipId, @RequestBody @Valid UpdateInternshipDto updateInternshipDto) {
        return ResponseEntity.status(HttpStatus.OK).body(internshipService.update(internshipId, updateInternshipDto));
    }

    @GetMapping
    public ResponseEntity<List<InternshipDto>> getByFilters(@ModelAttribute InternshipFilterDto internshipFilterDto) {
        return ResponseEntity.status(HttpStatus.OK).body(internshipService.getByFilters(internshipFilterDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternshipDto> getById(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(internshipService.getById(id));
    }
}
