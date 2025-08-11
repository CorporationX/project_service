package faang.school.projectservice.model.stage.enums;

/**
 * DeleteStrategy — перечисление определяющее стратегии удаления этапов.
 *
 * @author bozya
 * @since 06.08.2025
 */
public enum DeleteStrategy {
    /**
     * Удалить этап вместе со всеми задачами
     */
    CASCADE,
    /**
     * Закрыть все задачи и удалить этап
     */
    CLOSE,
    /**
     * Перенести задачи в другой этап и удалить текущий
     */
    MOVE
}