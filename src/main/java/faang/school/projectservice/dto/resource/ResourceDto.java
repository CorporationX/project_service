package faang.school.projectservice.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceDto {
    private Long id;
    private String name;
    private String key;
    private BigInteger size;
}
