package faang.school.projectservice.repository.adapter.moment;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MomentRepositoryAdapter {
    private final MomentRepository momentRepository;

    public Moment getMomentById(Long id) {
        return momentRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Entity not found with id: " + id));
    }

    public Moment save(Moment moment) {
        return momentRepository.save(moment);
    }

    public List<Moment> getAllMoments() {
        return momentRepository.findAll();
    }

}
