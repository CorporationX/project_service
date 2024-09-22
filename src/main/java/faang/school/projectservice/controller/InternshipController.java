package faang.school.projectservice.controller;

import faang.school.projectservice.dto.intership.InternshipDto;
import faang.school.projectservice.dto.intership.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.internship.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.time.Period;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipServiceImpl;

    public InternshipDto createInternship(InternshipDto internshipDto) {
        validateToStart(internshipDto);
        return internshipServiceImpl.create(internshipDto);
    }

    public InternshipDto update(InternshipDto internshipDto) {
        validateToStart(internshipDto);
        return internshipServiceImpl.update(internshipDto);
    }

    public List<InternshipDto> getInternshipByFilter(InternshipFilterDto filters) {
        return internshipServiceImpl.getInternshipByFilter(filters);
    }

    public List<InternshipDto> getAllInternships(InternshipDto internshipDto) {
        return internshipServiceImpl.getAllInternships(internshipDto);
    }

    public InternshipDto getInternshipById(InternshipDto internshipDto) {
        return internshipServiceImpl.getInternshipById(internshipDto);
    }

    private void validateToStart(InternshipDto internshipDto) {
        Period period = Period.between(internshipDto.getStartDate().toLocalDate(), internshipDto.getEndDate().toLocalDate());
        long resultTime = period.getMonths();
        if(internshipDto.getProjectId() == null || internshipDto.getInterns() == null ||
                internshipDto.getInterns().isEmpty() || resultTime > 3) {
            throw new DataValidationException("Валидация создания провалилась");
        }
    }
}
