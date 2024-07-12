package faang.school.projectservice.validator;


import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.MomentJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MomentValidator {
    private final MomentJpaRepository momentRepository;
    private final ProjectRepository projectRepository;

    public void existsMoment(long momentId) {
        if (!momentRepository.existsById(momentId)) {
            throw new EntityNotFoundException("Moment not exists!");
        }
    }

    public void validateMoment(MomentDto momentDto) {
        if (Objects.nonNull(momentDto.getProjectsId()) && !momentDto.getProjectsId().isEmpty()) {
            List<Project> projects = projectRepository.findAllByIds(momentDto.getProjectsId());
            if (projects.size() != momentDto.getProjectsId().size()) {
                throw new DataValidationException("Project does not exist.");
            }
            for (Project project : projects) {
                ProjectStatus status = project.getStatus();
                if (ProjectStatus.COMPLETED.equals(status) ||
                        ProjectStatus.CANCELLED.equals(status)) {
                    throw new DataValidationException("Project can`t be completed or canceled.");
                }

            }
        } else {
            throw new DataValidationException("Projects empty.");
        }
    }
}
