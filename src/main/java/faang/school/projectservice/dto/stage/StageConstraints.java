package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.stage.Stage;

/**
 * StageConstraints — класс содержащий константы и ограничения для класса {@link Stage}.
 *
 * @author bozya
 * @since 01.08.2025
 */
public class StageConstraints {
    public static final int MAX_SIZE_STRING = 512;
    public static final String MESSAGE_SIZE_INVALID = "Message size should be at most" + MAX_SIZE_STRING + "characters";
    public static final int MIN_SIZE_EXECUTORS = 1;
    public static final String MESSAGE_SIZE_EXECUTORS_INVALID = "Executors count should be more than" + MIN_SIZE_EXECUTORS;

    /**
     * {@link StageRolesDto}
     */
    public static final int MIN_STAGE_ROLE_COUNT = 1;
    public static final String MESSAGE_MIN_STAGE_ROLE_COUNT = "Stage role count should be more than" + MIN_STAGE_ROLE_COUNT;
}