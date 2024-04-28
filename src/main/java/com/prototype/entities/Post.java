package com.prototype.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
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
    @Column(name  = "post_text")
    private String postText;

    @Getter
    @Column(name  = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "views")
    private Integer views;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Image> images;

    @ManyToOne
    @JoinColumn(name = "username")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;




}
