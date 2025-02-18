package faang.school.projectservice.repository.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.List;

public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void deleteGalleryByKeys(List<String> keys) {
        String hql = "DELETE FROM project_gallery WHERE file_key IN :keys";
        Query query = entityManager.createNativeQuery(hql);
        query.setParameter("keys", keys);
        query.executeUpdate();
    }
}
