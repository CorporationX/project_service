package faang.school.projectservice.service.utilservice;

import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.exception.NotFoundException;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.moment.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentUtilService {

    private final MomentRepository momentRepository;

    public Moment getById(long id) {
        return momentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.MOMENT_NOT_EXIST));
    }

    public List<Moment> getAll() {
        return momentRepository.findAll();
    }

    public List<Moment> findAllByProjectId(long projectId) {
        return momentRepository.findAllByProjectId(projectId);
    }

    public List<Moment> findAllByProjectIdAndDateBetween(long projectId, LocalDateTime start, LocalDateTime endExclusive) {
        return momentRepository.findAllByProjectIdAndDateBetween(projectId, start, endExclusive);
    }

    public Moment save(Moment moment) {
        return momentRepository.save(moment);
    }
}
