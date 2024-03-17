package faang.school.projectservice.controller;

import faang.school.projectservice.controller.validation.InternshipValidator;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/projects")
public class InternshipController {
    private final InternshipValidator internshipValidator;
    private final InternshipService internshipService;

    @PostMapping("/create")
    public InternshipDto create (@RequestBody InternshipDto internshipDto){
        internshipValidator.validateCreation(internshipDto);
        return internshipService.create(internshipDto);
    }

    @PutMapping("/update}")
    public InternshipDto update (@RequestBody InternshipDto internshipDto){
        internshipValidator.validateUpdating(internshipDto);
        return internshipService.update(internshipDto);
    }

    @GetMapping("/get")
    public List<InternshipDto> getAllInternships(){
        return internshipService.getAllInternships();
    }

    @PostMapping("/filters")
    public List<InternshipDto> getFilteredInternships(@RequestBody InternshipFilterDto filters){
        return internshipService.getFilteredInternships(filters);
    }
}
