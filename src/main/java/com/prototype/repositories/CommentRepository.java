package com.prototype.repositories;

import com.prototype.entities.Comment;
import com.prototype.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    Comment getCommentById(Long id);
}