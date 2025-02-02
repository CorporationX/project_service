package faang.school.projectservice.service.validator;

import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InternshipValidator {
    private static final long MAX_DURATION = 92;

    public void internshipCreateValidate(Internship internship) {
        internshipDateValidation(internship.getStartDate(), internship.getEndDate());

        Project project = internship.getProject();
        TeamMember mentor = internship.getMentor();
        List<TeamMember> interns = internship.getInterns();

        if (!mentor.getTeam().getProject().equals(project)) {
            throw new BusinessException("Ментор с id:" + mentor.getId() + " не участвует в проекте!!!");
        }
        for (TeamMember member : interns) {
            if (!member.getTeam().getProject().equals(project)) {
                throw new BusinessException("Стажер с id:" + member.getId() + " не участвует в проекте!!!");
            }
        }
    }

    public void internshipUpdateValidation(Internship internship, InternshipUpdateDto updateDto) {
        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            throw new IllegalArgumentException("Стажировка окончена!!!");
        }
        if (internship.getStartDate().isBefore(LocalDateTime.now())
                && isInternsListNotEqualNotEmpty(internship, updateDto)) {
            throw new BusinessException("После начала стажировки нельзя менять список стажеров");
        }
    }

    public boolean isInternsListNotEqualNotEmpty(Internship internship, InternshipUpdateDto updateDto) {
        if (updateDto.getInternsId() != null && !updateDto.getInternsId().isEmpty()) {
            Set<Long> oldInternsIds = internship.getInterns().stream()
                    .map(TeamMember::getId)
                    .collect(Collectors.toSet());
            Set<Long> newInternsIds = new HashSet<>(updateDto.getInternsId());
            return !oldInternsIds.equals(newInternsIds);
        }
        return false;
    }

    public boolean internValidation(TeamMember intern, List<Task> tasks) {
        return tasks.stream()
                .filter(task -> task.getPerformerUserId().equals(intern.getId()))
                .allMatch(task -> task.getStatus().equals(InternshipStatus.COMPLETED));
    }

    public void internshipDateValidation(LocalDateTime startDate, LocalDateTime endDate) {
        if (ChronoUnit.DAYS.between(startDate, endDate) > MAX_DURATION) {
            throw new BusinessException("Стажировка длится слишком долго!!!.\nМаксимальное кол-во дней " + MAX_DURATION);
        }
        if (startDate.isAfter(endDate) || startDate.isEqual(endDate)) {
            throw new IllegalArgumentException("Дата окончания не может быть раньше или равна дате начала!!!");
        }
    }
}
