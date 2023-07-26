package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;

    private void validateListOfInternsAndThereIsMentor(InternshipDto internshipDto) {
        if (internshipDto.getInterns() == null) {
            throw new DataValidationException("Can't create an internship without interns");
        }
        if (internshipDto.getEndDate().plusMonths(3).isAfter(internshipDto.getStartDate())) {
            throw new DataValidationException("Internship cannot last more than 3 months");
        }
        if (internshipDto.getMentorId() == null) {
            throw new DataValidationException("There is not mentor for interns!");
        }
    }

    public InternshipDto saveNewInternship(InternshipDto internshipDto) {
        validateListOfInternsAndThereIsMentor(internshipDto);
        Internship internship = internshipRepository.save(internshipMapper.toEntity(internshipDto));
        return internshipMapper.toDto(internship);
    }
}