package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;

    public void create(InternshipDto internshipDto) throws DataFormatException {
        validateInternship(internshipDto);
        internshipDto.setStatus(InternshipStatus.IN_PROGRESS);

        Internship internship = InternshipDto.dtoTo(internshipDto);
        internshipRepository.save(internship);
    }

    public List<InternshipDto> getInternships() {
        List<Internship> internships = internshipRepository.findAll();
        return internships.stream().map(InternshipDto::toDto).toList();
    }

    public InternshipDto getInternshipByID(long internshipId) throws DataFormatException {
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

    public void update(InternshipDto internshipDto, Map<Long, TeamRole> newRoles) throws DataFormatException {
        validateInternship(internshipDto);
        exitInternship(internshipDto.getId());

        if (LocalDateTime.now().getMonth().getValue() >= internshipDto.getEndDate().getMonth().getValue() ||
                internshipDto.getProject().getTasks().isEmpty()) {
            internshipDto.setStatus(InternshipStatus.COMPLETED);
            Internship internship = finished(internshipDto, newRoles);
            internshipRepository.save(internship);
        }

    }

    public Internship finished(InternshipDto internshipDto, Map<Long, TeamRole> newRoles) {
        Internship internship = InternshipDto.dtoTo(internshipDto);

        if (internship.getProject().getTasks().isEmpty()) {
            internship.getInterns().forEach(i -> {
                i.setRoles(List.of(newRoles.get(i.getId())));
            });
        } else {
            internship.setInterns(null);
        }
        return internship;
    }

    public void validateInternship(InternshipDto internship) throws DataFormatException {
        if (internship == null || internship.getProject()==null ||
                internship.getMentorId()==null || internship.getStartDate()==null) {
            throw new NullPointerException("invalid internship");
        }
        if (internship.getInterns().isEmpty()) {
            throw new IllegalArgumentException("interns is empty");
        }
        if (internship.getStartDate().getMonth().getValue() > internship.getEndDate().getMonth().getValue()) {
            throw new DataFormatException("invalid date");
        }
        if (Period.between(internship.getStartDate().toLocalDate(), internship.getEndDate().toLocalDate()).getMonths() > 3) {
            throw new DataFormatException("period more than 3 months");
        }
    }

    public void exitInternship(long internshipId) throws DataFormatException {
        if (!internshipRepository.existsById(internshipId)) {
            throw new DataFormatException(String.format("Internship with id %s not found",  internshipId));
        }
    }

}
