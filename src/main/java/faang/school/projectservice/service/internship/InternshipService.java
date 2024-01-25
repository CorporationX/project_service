package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;

    public InternshipDto createInternship(InternshipDto internshipDto) {
        if (!checkInternshipDto(internshipDto))
            throw new IllegalArgumentException("Internship cannot be created");
        if(internshipRepository.existsById(internshipDto.getId()))
            throw new IllegalArgumentException("Internship with this id " + internshipDto.getId() + " already exists");
        Internship createdInternship = internshipRepository.save(internshipMapper.toEntity(internshipDto));
        return internshipMapper.toInternshipDto(createdInternship);
    }

    private boolean checkInternshipDto(InternshipDto internshipDto) {
        Project project = projectRepository.getProjectById(internshipDto.getProject().getId());
        TeamMember mentor = teamMemberRepository.findById(internshipDto.getMentorId().getId());
        List<TeamMember> interns = internshipDto.getInterns();
        if (interns == null || interns.isEmpty())
            throw new IllegalArgumentException("Interns list cannot be empty");
        if (internshipDto.getStartDate().isAfter(internshipDto.getEndDate()))
            throw new IllegalArgumentException("Incorrect dates have been entered");
        Duration duration = Duration.between(internshipDto.getStartDate(), internshipDto.getEndDate());
        if (duration.toDays() > 91)
            throw new IllegalArgumentException("Internship duration cannot exceed 91 days");
        return true;
    }


}
