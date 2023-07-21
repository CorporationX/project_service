package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Period;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;

    public InternshipDto internshipCreation(InternshipDto internshipDto) {
        if (internshipDto.getProjectId() == null ||
                internshipDto.getProjectId() > 1 &&
                        internshipDto.getProjectId() < 1
        ) {
            throw new IllegalArgumentException("Invalid project id");
        }
        if (internshipDto.getInternsId().size() == 0) {
            throw new IllegalArgumentException("Empty list of interns");
        }
        if (Period.between(internshipDto.getStartDate().toLocalDate(),
                internshipDto.getEndDate().toLocalDate()).getMonths() > 3) {
            throw new IllegalArgumentException("Invalid internship period");
        }
        if (teamMemberJpaRepository.findByUserIdAndProjectId(internshipDto.getMentorId(),
                internshipDto.getProjectId()) == null) {
            throw new IllegalArgumentException("Invalid mentor");
        }
        return internshipMapper.toInternshipDto(internshipRepository.save(internshipMapper.toInternship(internshipDto)));
    }

    public InternshipDto internshipUpdate(InternshipDto internshipDto) {
        if (internshipValidation(internshipDto)) {
            teamMemberRepository.updateInternship(internshipDto);
            internshipDto.getId() == internshipDto.getProjectId().getPerformerUserId;
        }
    }

    //private boolean internshipValidation(InternshipDto internshipDto) {
//        return internshipDto.getProjectId() != null &&
//                internshipDto.getProjectId() < 2 &&
//                internshipDto.getInternsId().size() > 0 &&
//                Period.between(internshipDto.getStartDate().toLocalDate(),
//                        internshipDto.getEndDate().toLocalDate()).getMonths() < 3 &&
//        (teamMemberJpaRepository.findByUserIdAndProjectId(internshipDto.getMentorId(), internshipDto.getProjectId()) != null);
//    }


}

//❤️