package faang.school.projectservice.dto.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Data
public class StageDto {
    private final long id;
    private final long projectId;
    private final Map<Long, Integer> rolesMap;//ключ - id роли, значение - нужное количество человек
    private List<Long> executorsIds;
}
