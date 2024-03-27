package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Transactional
public class InternshipUpdateLogicService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final Promotion promotion;
    private final TaskPerformance taskPerformance;

    public InternshipDto updateLogic(InternshipDto internshipDto) {
        Optional<Internship> optional = internshipRepository.findById(internshipDto.getId());
        Internship internship = optional.orElseThrow(EntityNotFoundException::new);

        Map<Boolean, List<Long>> internsPerformance = taskPerformance.partitionByStatusDone(internship);

        List<Long> failedInternsByUserId = internsPerformance.get(false);
        List<Long> succeededInternsByUserId = internsPerformance.get(true);


        List<TeamMember> promotedInterns = promotion.promoteSucceededInterns(internship, succeededInternsByUserId);
        List<TeamMember> demotedAndDeletedFromProjectInterns = promotion.demoteFailedInterns(internship, failedInternsByUserId);

        internship.getInterns().clear();

        return internshipMapper.toDto(internship);
    }
}
