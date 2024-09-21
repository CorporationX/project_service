package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.service.internship.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internships")
public class InternshipController {

    private final InternshipService internshipService;

    @PostMapping("/create")
    @ResponseBody
    public InternshipDto createInternship(@RequestBody InternshipDto internshipDto) {
        return internshipService.create(internshipDto);
    }

    @PostMapping("/update")
    @ResponseBody
    public InternshipDto updateInternship(@RequestBody InternshipDto internshipDto) {
        return internshipService.update(internshipDto);
    }

    @PostMapping("/filter")
    @ResponseBody
    public List<InternshipDto> filterInternship(@RequestBody InternshipFilterDto filters) {
        return internshipService.getFilteredInternship(filters);
    }

    @GetMapping
    @ResponseBody
    public List<InternshipDto> getInternships() {
        return internshipService.getAllInternship();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public InternshipDto getInternship(@PathVariable Long id) {
        return internshipService.getInternship(id);
    }
}
