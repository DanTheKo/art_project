package com.prototype.controllers;

import com.prototype.entities.Authority;
import com.prototype.entities.Image;
import com.prototype.entities.Post;
import com.prototype.entities.User;
import com.prototype.services.ImageService;
import com.prototype.services.PostService;
import com.prototype.services.UserService;
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

@Controller
public class PostController {

    private PostService postService;
    private UserService userService;
    private ImageService imageService;
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

    @GetMapping("/")
    @Transactional(readOnly = true)
    public String showPostsList( Principal principal, Model model, @RequestParam(defaultValue = "0") int page) {
        Page<Post> postPage = postService.getAllPostsUser(PageRequest.of(page, 3).withSort( Sort.by(Sort.Direction.ASC,"postId")));
        List<Post> list = postPage.toList();
        model.addAttribute("posts", list);
//        model.addAttribute("filterUrl", "/");
//        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("page", page);
        return "posts";
    }

//    @GetMapping("posts/loadMore")
//    @Transactional(readOnly = true)
//    public String loadMorePosts(Principal principal, Model model, @RequestParam(defaultValue = "0") int page) {
//        Page<Post> postPage = postService.getAllPostsUser(PageRequest.of(page, 3).withSort(Sort.by(Sort.Direction.ASC, "postId")));
//        List<Post> list = postPage.toList();
//        model.addAttribute("posts", list);
////        model.addAttribute("filterUrl", "/loadMore");
////        model.addAttribute("totalPages", postPage.getTotalPages());
//        model.addAttribute("page", page);
//        return "posts"; // Возвращаем только фрагмент с записями
//    }
//    @GetMapping("/loadMore")
//    public String loadMore( Model model,@RequestParam(defaultValue = "0") int page) {
//        // Получить данные следующей страницы
//        Page<Post> posts = postService.getAllPostsUser(PageRequest.of(page, 3).withSort( Sort.by(Sort.Direction.ASC,"postId")));
//        // Добавить данные к модели и вернуть представление
//        model.addAttribute("posts", posts.toList());
//        return "posts";
//    }
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

        Authority authority = new Authority();
        authority.setUser(user);
        authority.setAuthority("ROLE_USER");
        user.setAuthority(authority);
        user.setRealName(user.getUsername());
        userService.encode(user);
        userService.saveUser(user);
        return "redirect:/posts/login";
    }

    @PostMapping("/posts/addOrUpdate/add")
    @Transactional
    public String addPost(@ModelAttribute(value = "post") Post post,
                          @RequestParam("username") String username,
                          @RequestParam("file") MultipartFile file) throws IOException {
        post.setViews(0);
        post.setUser(userService.getUserByUserName(username));
        Image imageObj = new Image();
        imageObj.setFileName(file.getOriginalFilename());
        imageObj.setData(file.getBytes());
        imageObj.setPost(post);
        imageObj.setContentType("type");
        imageObj.setData(file.getBytes());
        List<Image> images = new ArrayList<>();
        images.add(imageObj);
        post.setImages(images);
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
        return "redirect:/posts/filter?text=";
    }

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
}