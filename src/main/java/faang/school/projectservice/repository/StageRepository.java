package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.model.stage.Stage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {

    @Override
    Optional<Stage> findById(Long aLong);
}
