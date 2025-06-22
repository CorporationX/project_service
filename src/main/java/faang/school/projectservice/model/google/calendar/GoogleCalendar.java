//package faang.school.projectservice.model.google.calendar;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.OneToMany;
//import jakarta.persistence.Table;
//import jakarta.persistence.Temporal;
//import jakarta.persistence.TemporalType;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Data
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//@Table(name = "google_calendar")
//public class GoogleCalendar {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "calendar_id", unique = true, nullable = false)
//    private String calendarId; // Unique identifier for the Google Calendar
//
//    @Column(name = "project_id", nullable = false)
//    private Long projectId; // ID of the project this calendar belongs to
//
//    @Column(name = "title", nullable = false)
//    private String title;
//
//    @CreationTimestamp
//    @Temporal(TemporalType.TIMESTAMP)
//    private LocalDateTime createdAt;
//
//    @UpdateTimestamp
//    @Temporal(TemporalType.TIMESTAMP)
//    private LocalDateTime updatedAt;
//
//    @OneToMany(mappedBy = "calendar")
//    private List<GoogleCalendarEvent> events; // List of events associated with this calendar
//}
