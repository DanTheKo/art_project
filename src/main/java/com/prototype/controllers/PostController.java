package com.prototype.controllers;

import com.prototype.entities.*;
import com.prototype.services.CommentService;
import com.prototype.services.ImageService;
import com.prototype.services.PostService;
import com.prototype.services.UserService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class PostController {

    private PostService postService;
    private UserService userService;
    private ImageService imageService;
    private CommentService commentService;
    private UserDetailsService userDetailsService;
    @Autowired
    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    @Autowired
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/")
    @Transactional(readOnly = true)
    public String showPostsList( Principal principal, Model model, @RequestParam(defaultValue = "0") int page) {
        Page<Post> postPage = postService.getAllPostsUser(PageRequest.of(page, 5).withSort( Sort.by(Sort.Direction.DESC,"postId")));
        List<Post> list = postPage.toList();
        model.addAttribute("posts", list);
//        model.addAttribute("filterUrl", "/");
//        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("page", page);
        return "posts";
    }
    @GetMapping("/posts")
    public String redirect() {
        return "redirect:/";
    }
//    @GetMapping("/posts/filter")
//    @Transactional
//    public String filterPost(Principal principal, Model model,
//                             @RequestParam(value = "text", required = false) String text,
//                             @RequestParam(value = "start", required = false) LocalDate startDate,
//                             @RequestParam(value = "end", required = false) LocalDate endDate){
//
//        if (principal != null) {
//            List<Post> list = postService.getAllPosts();
//            model.addAttribute("posts", list);
//            model.addAttribute("text", text);
//            model.addAttribute("start", startDate);
//            model.addAttribute("end", endDate);
//        }
//        return "posts";
//    }
    @GetMapping("/posts/login")
    @Transactional(readOnly = true)
    public String login(Principal principal, Model model) {
        return "/login";
    }
    @GetMapping("/posts/registration")
    @Transactional(readOnly = true)
    public String register(Principal principal, Model model) {
        model.addAttribute("user", new User());
        return "/registration";
    }
    @PostMapping("/posts/registration/newUser")
    public String newUser(@ModelAttribute(value = "user")User user, Model model) {

        if(userService.getUserByUserName(user.getUsername()) == null){
            Authority authority = new Authority();
            authority.setUser(user);
            authority.setAuthority("ROLE_USER");
            user.setAuthority(authority);
            user.setRealName(user.getUsername());
            user.setPosts(new ArrayList<Post>());
            userService.encode(user);
            userService.saveUser(user);
            return "redirect:/posts/login";
        }
        return "redirect:/posts/registration?error";

    }

    @PostMapping("/posts/addOrUpdate/add")
    @Transactional
    public String addPost(@ModelAttribute(value = "post") Post post,
                          @RequestParam("username") String username,
                          @RequestParam("files") MultipartFile[] files) throws IOException {
        post.setViews(0);
        post.setUser(userService.getUserByUserName(username));

        List<Image> images = new ArrayList<>();
        for (MultipartFile file : files) {
            if(!Objects.requireNonNull(file.getOriginalFilename()).isBlank()){
                Image imageObj = new Image();
                imageObj.setFileName(file.getOriginalFilename());
                imageObj.setData(file.getBytes());
                imageObj.setPost(post);
                imageObj.setContentType("type");
                images.add(imageObj);
            }

        }
        if(!images.isEmpty()){
            post.setImages(images);
        }

        postService.add(post);

        return "redirect:/posts";
    }
    @GetMapping("/posts/addOrUpdate/add")
    @Transactional
    public String getAddPost(Principal principal, Model model) {
        if (principal != null) {
            model.addAttribute("post", new Post());
        }
        return "addOrUpdate";
    }


    @GetMapping("/posts/addOrUpdate/edit/{id}")
    public String editPostGet(Model model, @PathVariable(value = "id") Integer id) {
        Post post = postService.getById(id);
        model.addAttribute("post", post);
        return "addOrUpdate";
    }

    @PostMapping("/posts/addOrUpdate/edit/update")
    public String editPost(@ModelAttribute(value = "post") Post updated) {
        Post post = postService.getById(updated.getPostId());
        postService.update(post.getPostId(), updated);
        return "redirect:/posts";
    }

    @GetMapping("/posts/addOrUpdate/edit/{id}/del_img/{img_id}")
    public String deleteImage(@PathVariable(value = "id") Long id,
                              @PathVariable(value = "img_id") Long img_id) {
        Post post = postService.getById(id);
        List<Image> images = post.getImages();
        Image image = imageService.getImageById(img_id);
        images.remove(image);
        postService.update(post.getPostId(), post);
        imageService.deleteImage(image);
        return "redirect:/posts";
    }

//    @GetMapping("/posts/comment/add/{id}")
//    public String addComment(Model model, @PathVariable(value = "id") Integer id){
//        Post post = postService.getById(id);
//        model.addAttribute("post", post);
//        model.addAttribute("comment", new Comment());
//        return "/posts";
//    }
//    @PostMapping("/posts/comment/add")
//    public String addComment(@ModelAttribute(value = "comment") Comment comment,
//                             @ModelAttribute(value = "post") Post post,
//                             @RequestParam("username") String username){
//        comment.setPost(post);
//        comment.setUser(userService.getUserByUserName(username));
//        commentService.saveComment(comment);
//
//        return "redirect:/posts";
//    }
    @GetMapping("/posts/show/{id}")
    public String showOnePost(Model model, @PathVariable(value = "id") Integer id) {
        Post post = postService.getById(id);
        postService.incrementViews(post);
        model.addAttribute("post", post);
        return "post-info";

    }

    @PostMapping("/authenticateTheUser")
    @Transactional
    public String authenticateUser(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails != null) {
            String storedPassword = userDetails.getPassword();
            if (password.equals(storedPassword)) {
                model.addAttribute("username", username);
                return "redirect:/posts";
            }
        }
        return "/login";
    }

    @GetMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable(value = "id") Integer id) {
        Post post = postService.getById(id);
        postService.delete(post);
        return "redirect:/posts";
    }
//    @GetMapping("/posts/deleteComment/{id}")
//    public String deleteComment(@PathVariable(value = "id") Long id) {
//        Comment comment = commentService.getCommentById(id);
//        commentService.delete(comment);
//        return "redirect:/posts";
//    }

}