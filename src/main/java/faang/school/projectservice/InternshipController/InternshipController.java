package faang.school.projectservice.InternshipController;

import faang.school.projectservice.InternshipService.InternshipService;
import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipFilterDto;
import faang.school.projectservice.validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    @PostMapping("/saveInternship")
    public InternshipDto saveNewInternship(@RequestBody InternshipDto internshipDto) {
        InternshipValidator.validateControllerInternship(internshipDto);
        return internshipService.saveNewInternship(internshipDto);
    }

    @PutMapping("/updateInternship")
    public InternshipDto updateInternship(InternshipDto internshipDto, long id) {
        InternshipValidator.validateControllerInternship(internshipDto);
        return internshipService.updateInternship(internshipDto, id);
    }

    @GetMapping("/findInternshipsWithFilter")
    public List<InternshipDto> findInternshipsWithFilter(long projectId, InternshipFilterDto filterDto) {
        return internshipService.findInternshipsWithFilter(projectId, filterDto);
    }

    @GetMapping("/getAllInternship")
    public List<InternshipDto> getAllInternships() {
        return internshipService.getAllInternships();
    }
}