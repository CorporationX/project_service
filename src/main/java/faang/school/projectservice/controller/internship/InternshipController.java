package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.ResponseInternshipDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.service.internship.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internship")
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping()
    public ResponseInternshipDto create(@RequestBody CreateInternshipDto dto) {
        return internshipService.create(dto);
    }

    @PatchMapping()
    public ResponseInternshipDto update(@RequestBody UpdateInternshipDto dto) {
        return internshipService.update(dto);
    }

    @PostMapping("/byFilter")
    public List<ResponseInternshipDto> getAllByFilter(@RequestBody InternshipFilterDto internshipFilterDto) {
        return internshipService.findByFilter(internshipFilterDto);
    }

    @GetMapping()
    public List<ResponseInternshipDto> getAll() {
        return internshipService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseInternshipDto getById(@PathVariable Long id) {
        return internshipService.findById(id);
    }
}
