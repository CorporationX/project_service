package faang.school.projectservice.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
public class ResourceDto {
    private String name;
    private String key;
    private BigInteger size;
}
