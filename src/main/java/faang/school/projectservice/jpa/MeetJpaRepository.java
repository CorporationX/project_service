package faang.school.projectservice.jpa;

import faang.school.projectservice.model.meet.Meet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetJpaRepository extends JpaRepository<Meet, Long> {
}
