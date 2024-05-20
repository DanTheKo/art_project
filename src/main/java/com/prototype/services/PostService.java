package com.prototype.services;

import com.prototype.entities.Post;
import com.prototype.entities.User;
import com.prototype.repositories.PostRepository;
import com.prototype.specifications.PostSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private final PostRepository repository;

    @Autowired
    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Post getById(long id) {
        return repository.findById(id).orElse(null);
    }


    public List<Post> getAllPosts() {
        return repository.findAll();
    }

    @Transactional
    public List<Post> getAllPostsUser(User  user) {
        Specification<Post> specification;
        if(!user.getAuthority().getAuthority().equals("ROLE_ADMIN")){
            specification = Specification
                    .where(PostSpecifications.hasUser(user));
            return repository.findAll(specification);
        }
        else{
            return repository.findAll();
        }
    }

    public Page<Post> getAllPostsUser( Pageable pageable) {
            return repository.findAll(pageable);
    }
    public List<Post> FilterAndGetAllPosts(String text, LocalDate startDate, LocalDate endDate, User user){
        Specification<Post> specification;
        if(!user.getAuthority().getAuthority().equals("ROLE_ADMIN")){
            specification = Specification
                    .where(PostSpecifications.hasText(text))
                    .and(PostSpecifications.afterDate(startDate))
                    .and(PostSpecifications.beforeDate(endDate))
                    .and(PostSpecifications.hasUser(user));
        }
        else{
            specification = Specification
                    .where(PostSpecifications.hasText(text))
                    .and(PostSpecifications.afterDate(startDate))
                    .and(PostSpecifications.beforeDate(endDate));
        }
        return repository.findAll(specification);

//        Specification<Post> specification = Specification
//                .where(PostSpecifications.hasText(text))
//                .and(PostSpecifications.afterDate(startDate))
//                .and(PostSpecifications.beforeDate(endDate));
//        if(!user.getAuthority().getAuthority().equals("ROLE_ADMIN")){
//            specification.and(PostSpecifications.hasUser(user));
//        }
//        return repository.findAll(specification, pageable);
    }

//    public List<Post> getTopPosts() {
//        Pageable topPageable = PageRequest.of(0, 3, Sort.by(Sort.Order.desc("views")));
//        Page<Post> topPostsPages = repository.findAll(topPageable);
//        return topPostsPages.getContent();
//    }

    @Transactional
    public void add(Post post) {
        repository.save(post);
    }
    public void delete(Post post) {
        repository.delete(post);
    }

    public Post getPostById(Long id) {
        return repository.findById(id).orElse(null);
    }
    public void update(Long id, Post updated) {
        Post post = getPostById(id);
        if (post != null) {
            post.setPostText(updated.getPostText());
            post.setCreatedAt(updated.getCreatedAt());
            post.setImages(updated.getImages());
            repository.save(post);
        }
    }
    public void incrementViews(Post post){
        post.setViews(post.getViews() + 1);
        repository.save(post);
    }
}