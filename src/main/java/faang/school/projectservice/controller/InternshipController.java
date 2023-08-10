package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipFilterDto;
import faang.school.projectservice.dto.client.InternshipUpdateDto;
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

    public InternshipDto updateInternship(InternshipUpdateDto internshipUpdateDto, Long idInternship) {
        if (internshipUpdateCommonValidation(internshipUpdateDto)) {
            throw new InternshipValidationException("Updated validation parameters did not passed!");
        }
        internshipValidationId(idInternship);
        return internshipService.updateInternship(internshipUpdateDto, idInternship);
    }

    public InternshipDto updateInternBeforeInternshipEnd(Long idInternship, Long internsId) {
        internshipValidationId(internsId);
        internshipValidationId(idInternship);
        return internshipService.updateInternBeforeInternshipEnd(idInternship,internsId);
    }


    public InternshipDto deleteIntern(Long idInternship, Long internsId) {
        internshipValidationId(internsId);
        internshipValidationId(idInternship);
        return internshipService.deleteIntern(idInternship,internsId);
    }

    public List<InternshipDto> gettingAllInternshipsAccordingToFilters(InternshipFilterDto internshipFilterDto) {
        return internshipService.gettingAllInternshipsAccordingToFilters(internshipFilterDto);
    }

    public List<InternshipDto> gettingAllInternships() {
        return internshipService.gettingAllInternships();
    }

    public InternshipDto getInternshipById(Long id) {
        return internshipService.getInternshipById(id);
    }


    //ID проверяем отдельно, тк он приходит позже
    private void internshipValidationId(Long id) {
        if (id == null || id < 1) {
            throw new InternshipValidationException("Invalid id");
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

    private boolean internshipUpdateCommonValidation(InternshipUpdateDto internshipUpdateDto) {
        return internshipUpdateDto.getProjectId() == null &&
                internshipUpdateDto.getMentorId() == null &&
                internshipUpdateDto.getEndDate() == null &&
                internshipUpdateDto.getStatus() == null &&
                internshipUpdateDto.getDescription() == null &&
                internshipUpdateDto.getName() == null &&
                internshipUpdateDto.getUpdatedAt() == null &&
                internshipUpdateDto.getUpdatedBy() == null &&
                internshipUpdateDto.getScheduleId() == null;
    }
}
