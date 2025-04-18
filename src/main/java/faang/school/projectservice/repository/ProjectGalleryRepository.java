package faang.school.projectservice.repository;

import faang.school.projectservice.model.ProjectGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectGalleryRepository extends JpaRepository<ProjectGallery, Integer> {
}
