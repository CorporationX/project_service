package faang.school.projectservice.filter;

import faang.school.projectservice.model.Task;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KeywordFilter implements TaskFilter {

    private final String keyword;

    @Override
    public boolean test(Task task) {
        if (keyword == null || keyword.isEmpty()) {
            return true;
        }
        String lowerKeyword = keyword.toLowerCase();
        return (task.getName() != null && task.getName().toLowerCase().contains(lowerKeyword))
                || (task.getDescription() != null && task.getDescription().toLowerCase().contains(lowerKeyword));
    }
}