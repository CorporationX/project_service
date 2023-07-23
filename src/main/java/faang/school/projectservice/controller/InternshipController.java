package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;

    public InternshipDto internshipCreation(InternshipDto internshipDto) {
        if (internshipCommonValidation(internshipDto)) {
            throw new IllegalArgumentException("Validation parameters did not passed!");
        }
        return internshipService.internshipCreation(internshipDto);
    }

    public InternshipDto internshipUpdate(Long id, InternshipDto internshipDto) {
        if (internshipCommonValidation(internshipDto)) {
            throw new IllegalArgumentException("Validation parameters did not passed!");
        }
        internshipValidationId(id);
        return internshipService.internshipUpdate(internshipDto);
    }

    //ID проверяем отдельно, тк он приходит позже
    private void internshipValidationId(Long id) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("Invalid id");
        }
    }

    private boolean internshipCommonValidation(InternshipDto internshipDto) {
        return internshipDto.getName() == null &&
                internshipDto.getStartDate() == null &&
                //internshipDto.getEndDate() == null &&
                //internshipDto.getDescription() == null &&
                internshipDto.getMentorId() == null &&
                //internshipDto.getInternsId() == null &&
                internshipDto.getProjectId() == null;
                //internshipDto.getStatus() == null &&
    }
}
