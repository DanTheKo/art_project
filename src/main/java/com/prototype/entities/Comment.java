package com.prototype.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {
    public Comment(String text, LocalDateTime createdAt) {
        this.text = text;
        this.createdAt = createdAt;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name  = "comment_id")
    private long id;

    @Getter
    @Column(name  = "comment_text", columnDefinition="TEXT")
    private String text;

    @Getter
    @Column(name  = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "username")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
    public String getCreatedAtConverted() {
        try {
            return "Опубликовано " + createdAt.format(DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy"));
        }
        catch (Exception e){
            return "Ошибка";
        }

    }






}
