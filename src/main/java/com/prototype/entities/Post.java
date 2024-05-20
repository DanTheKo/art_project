package com.prototype.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "post")
public class Post {
    public Post(String post_text, LocalDateTime createdAt) {
        this.postText = post_text;
        this.createdAt = createdAt;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name  = "post_id")
    private long postId;

    @Getter
    @Column(name  = "post_text", columnDefinition="TEXT")
    private String postText;

    @Getter
    @Column(name  = "created_at")
    private LocalDateTime createdAt;

    public String getCreatedAtConverted() {
        try {
            return "Опубликовано " + createdAt.format(DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy"));
        }
        catch (Exception e){
            return "Ошибка";
        }

    }

    @Column(name = "views")
    private Integer views;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Image> images;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "username")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
}
