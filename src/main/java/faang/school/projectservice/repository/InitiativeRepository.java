package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.InitiativeJpaRepository;
import faang.school.projectservice.model.initiative.Initiative;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InitiativeRepository {
    private final InitiativeJpaRepository jpaRepository;

    public Initiative getById(Long id) {
        return jpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Initiative not found by id=" + id)
        );
    }

    public List<Initiative> findAll() {
        return jpaRepository.findAll();
    }

    public List<Initiative> findAllByIds(List<Long> ids) {
        return jpaRepository.findAllById(ids);
    }

    public Initiative save(Initiative initiative){
        return jpaRepository.save(initiative);
    }
}
