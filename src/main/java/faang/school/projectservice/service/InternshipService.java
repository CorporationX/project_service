package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Period;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final TeamMemberRepository teamMemberRepository;


    private boolean internshipValidation(InternshipDto internshipDto) {
        return internshipDto.getProjectId() != null &&
                internshipDto.getProjectId() < 2 &&
                internshipDto.getInternsId().size() > 0 &&
                Period.between(internshipDto.getStartDate().toLocalDate(),
                        internshipDto.getEndDate().toLocalDate()).getMonths() < 3 &&
        (teamMemberJpaRepository.findByUserIdAndProjectId(internshipDto.getMentorId(), internshipDto.getProjectId()) != null);
    }

    private updateInternship (InternshipDto internshipDto) {
        if (internshipValidation(internshipDto)) {
            teamMemberRepository.updateInternship(internshipDto);

            internshipDto.getId() == internshipDto.getProjectId().getPerformerUserId;
        }
    }






}

//❤️