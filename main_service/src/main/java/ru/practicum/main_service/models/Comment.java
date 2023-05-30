package ru.practicum.main_service.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "text")
    private String text;
    @ManyToOne
    @JoinColumn(name = "event", referencedColumnName = "id")
    private Event event;
    @ManyToOne
    @JoinColumn(name = "author", referencedColumnName = "id")
    private User author;
    @Column(name = "modified")
    private Boolean modified = false;
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    @Column(name = "rating")
    private Long rating = 0L;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Comment parentComment;

    @Column(name = "created")
    private LocalDateTime created;

    @Transient
    public void increaseRating() {
        this.rating++;
    }

    @Transient
    public void decreaseRating() {
        this.rating--;
    }
}
