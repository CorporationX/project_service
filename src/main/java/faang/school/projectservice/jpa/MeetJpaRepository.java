package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Meet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetJpaRepository extends JpaRepository<Meet, Long> {

    List<Meet> findAllByProjectId(Long projectId);
}
