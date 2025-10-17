package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.service.InternshipService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class InternshipController {

private final InternshipService internshipService;

public InternshipDto createInternship(@NonNull Long projectId, CreateInternshipDto internshipDto) {

    return internshipService.createInternship(projectId, internshipDto);
}

public InternshipDto updateInternship(@NonNull Long internshipId, UpdateInternshipDto internshipDto) {
    return internshipService.updateInternship(internshipId, internshipDto);
}

public InternshipDto getByFilter() {
    return null;
}

public InternshipDto getById(@NonNull Long internshipId) {
    return internshipService.findById(internshipId);
}

public List<InternshipDto> getAllInternships() {
    return new ArrayList<>();
}

}
