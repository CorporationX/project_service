package faang.school.projectservice.service;

import faang.school.projectservice.model.entity.Stage;
import faang.school.projectservice.model.entity.StageRoles;

import java.util.List;

public interface StageRolesService {
    void saveAll(List<StageRoles> stageRolesList);

    void getExecutorsForRole(Stage stage, StageRoles stageRoles);
}
