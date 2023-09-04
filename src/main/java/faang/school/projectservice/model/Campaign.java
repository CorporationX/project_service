package faang.school.projectservice.model;

import faang.school.projectservice.dto.client.Currency;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "campaign")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 128, nullable = false)
    private String title;

    @Column(name = "description", length = 4096)
    private String description;

    @Column(name = "goal")
    private BigDecimal goal;

    @Column(name = "amount_raised")
    private BigDecimal amountRaised;

    @Enumerated(EnumType.STRING)
    private CampaignStatus status;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    private Long createdBy;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private Long updatedBy;
}
