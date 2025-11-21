package faang.school.projectservice.dto.simple;

import faang.school.projectservice.model.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceSimpleDto { private Long id; private String name; private ResourceType type; }
