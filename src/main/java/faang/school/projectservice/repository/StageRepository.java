package faang.school.projectservice.repository;

import faang.school.projectservice.model.stage.Stage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StageRepository extends JpaRepository<Stage, Long> {
}