package faang.school.projectservice.controller;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.dto.InternshipFilterDto;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.validator.InternshipValidator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internship")
@Validated
public class InternshipController {

    private final InternshipService internshipService;
    private static final String ID_ERR_MESSAGE = "Internship id must be more then 0";

    @PostMapping
    public InternshipDto create(@Valid @RequestBody InternshipDto internshipDto) {
        return internshipService.create(internshipDto);
    }

    @PutMapping("/{id}")
    public InternshipDto update(@Valid @RequestBody InternshipDto internshipDto,
                                @PathVariable @Min(value = 1, message = ID_ERR_MESSAGE) long id) {
        return internshipService.update(internshipDto, id);
    }

    @GetMapping("/filter")
    public List<InternshipDto> findAllWithFilter(@Valid @RequestBody InternshipFilterDto internshipFilterDto) {
        return internshipService.findAllWithFilter(internshipFilterDto);
    }

    @GetMapping
    public List<InternshipDto> findAll() {
        return internshipService.findAll();
    }

    @GetMapping("/{id}")
    public InternshipDto findById(@PathVariable @Min(value = 1, message = ID_ERR_MESSAGE) long id) {
        return internshipService.findById(id);
    }
}
