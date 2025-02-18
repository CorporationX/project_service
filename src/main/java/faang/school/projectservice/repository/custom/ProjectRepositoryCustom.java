package faang.school.projectservice.repository.custom;

import java.util.List;

public interface ProjectRepositoryCustom {
    void deleteGalleryByKeys(List<String> keys);
}
