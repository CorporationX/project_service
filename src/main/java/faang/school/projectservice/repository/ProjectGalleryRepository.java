package faang.school.projectservice.repository;

import faang.school.projectservice.model.ProjectGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectGalleryRepository extends JpaRepository<ProjectGallery, Integer> {
    int countByProjectId(Long project_id);
    List<ProjectGallery> findByProjectId(Long project_id);
}
