package faang.school.projectservice.service;

import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.zip.DataFormatException;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;

    public void create(Internship internship) throws DataFormatException {
        validate(internship);
        internship.setCreatedAt(LocalDateTime.now());
        internship.setStatus(InternshipStatus.IN_PROGRESS);
        internshipRepository.save(internship);
    }

    public void validate(Internship internship) throws DataFormatException {
        if (internship.getProject()==null || internship.getMentorId()==null) {
            throw new NullPointerException("project or mentor is null");
        }
        if (internship.getInterns().isEmpty()) {
            throw new DataFormatException("interns is empty");
        }
    }
}
