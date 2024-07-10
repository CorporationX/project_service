package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.zip.DataFormatException;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;

    public void create(InternshipDto internshipDto) throws DataFormatException {
        validateInternship(internshipDto);
        LocalDateTime startTime = LocalDateTime.now();
        internshipDto.setStatus(InternshipStatus.IN_PROGRESS);

        Internship internship = new Internship();
        internship.setCreatedAt(startTime);
        internshipRepository.save(internship);
    }

    public List<InternshipDto> getInternships() {
        List<Internship> internships = internshipRepository.findAll();
        return internships.stream().map(InternshipDto::toDto).toList();
    }

    public InternshipDto getInternshipID(long internshipId) throws DataFormatException {
        exitInternship(internshipId);
        Internship internship = internshipRepository.findById(internshipId).orElseThrow();
        return InternshipDto.toDto(internship);
    }

    public List<InternshipDto> getInternshipStatus(InternshipStatus internshipStatus) {
        List<Internship> internships = internshipRepository.findAll();
        return internships.stream()
                .filter(i -> i.getStatus().equals(internshipStatus))
                .map(InternshipDto::toDto).toList();
    }

    public void update(InternshipDto internshipDto) throws DataFormatException {
        validateInternship(internshipDto);
        exitInternship(internshipDto.getId());

        if (Period.between(internshipDto.getStartDate().toLocalDate(), LocalDateTime.now().toLocalDate()).getMonths() >= 3) {
            internshipDto.setStatus(InternshipStatus.COMPLETED);
            finished(internshipDto);
            Internship internship = InternshipDto.dtoTo(internshipDto);
            internship.setEndDate(LocalDateTime.now());
            internshipRepository.save(internship);
        }

    }

    public void finished(InternshipDto internship) {
        if (internship.getProject().getTasks().isEmpty()) {
            internship.getInterns().forEach(i -> i.setRoles(List.of(TeamRole.DEVELOPER)));
        } else {
            internship.setInterns(null);
        }

    }

    public void validateInternship(InternshipDto internship) throws DataFormatException {
        if (internship.getProject()==null || internship.getMentorId()==null
                || internship.getStartDate()==null) {
            throw new NullPointerException("invalid internship");
        }
        if (internship.getInterns().isEmpty()) {
            throw new DataFormatException("interns is empty");
        }
    }

    public void exitInternship(long internshipId) throws DataFormatException {
        if (!internshipRepository.existsById(internshipId)) {
            throw new DataFormatException(String.format("Internship with id %s not found",  internshipId));
        }
    }

}
