package faang.school.projectservice.service.moment;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;

    public void addMomentByName(Project project, String momentName) {
        Optional<Moment> moment = findMomentByName(momentName);
        if (moment.isEmpty()) {
            createMoment(project, momentName);
        } else {
            updateMoment(moment, project);
        }
    }

    private void updateMoment(Optional<Moment> moment, Project subProject) {
    }

    private void createMoment(Project subProject, String momentName) {
    }

    private Optional<Moment> findMomentByName(String name) {
        Moment moment = new Moment();
        moment.setName(name);
        Example<Moment> momentExample = Example.of(moment);
        return momentRepository.findOne(momentExample);
    }
}
