package faang.school.projectservice.filter;

/**
 * Используется для фильтров этапов
 * проекта по статусу задач (например, отобрать только те этапы,
 * в которых есть задачи(ANY_TASK) определенного типа)
 */
public enum TaskFilterEnum {
    ANY, ALL, NOTHING
}
