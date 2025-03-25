package faang.school.projectservice.repository;

import faang.school.projectservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Optional<Resource> findByKey(String key);
}
