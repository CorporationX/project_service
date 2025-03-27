package faang.school.projectservice.repository;

import faang.school.projectservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

}
