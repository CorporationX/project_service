package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipFilterDto;
import faang.school.projectservice.exceptions.InternshipValidationException;
import faang.school.projectservice.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;

    public InternshipDto internshipCreation(InternshipDto internshipDto) {
        if (internshipCommonValidation(internshipDto)) {
            throw new InternshipValidationException("Validation parameters did not passed!");
        }
        return internshipService.internshipCreation(internshipDto);
    }


    public List<InternshipDto> gettingAllInternships() {
        return internshipService.gettingAllInternships();
    }
    
    public InternshipDto getInternshipById(Long id) {
        return internshipService.getInternshipById(id);
    }

//    public InternshipDto internshipUpdate(Long id, InternshipDto internshipDto) {
//        if (internshipCommonValidation(internshipDto)) {
//            throw new InternshipValidationException("Validation parameters did not passed!");
//        }
//        internshipValidationId(id);
//        return internshipService.internshipUpdate(internshipDto);
//    }

    //ID проверяем отдельно, тк он приходит позже
    private void internshipValidationId(Long id) {
        if (id == null || id < 1) {
            throw new InternshipValidationException("Invalid id");
        }
    }

    public  List<InternshipDto> gettingAllInternshipsAccordingToFilters (InternshipFilterDto internshipFilterDto) {
        return internshipService.gettingAllInternshipsAccordingToFilters(internshipFilterDto);
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
