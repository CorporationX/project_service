package faang.school.projectservice.dto.moment;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class MomentDto {
    private Long id;
    private String name;
    private Date date;
    private Long projectId;
    private List<Long> partnerProjectIds;
}