package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

List<Resource> findByProjectId(Long id);
}
