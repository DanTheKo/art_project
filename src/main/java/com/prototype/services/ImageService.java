package com.prototype.services;

import com.prototype.entities.Image;
import com.prototype.entities.Post;
import com.prototype.entities.User;
import com.prototype.repositories.ImageRepository;
import com.prototype.repositories.PostRepository;
import com.prototype.specifications.PostSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public Image saveImage(Image image) {
        return imageRepository.save(image);
    }

    public Image getImageById(Long id) {
        return imageRepository.findById(id).orElse(null);
    }

    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    @Transactional
    public void deleteImageById(Long id) {
        Image image = imageRepository.getImageById(id);
        image.getData();
        imageRepository.delete(image);
    }

    @Transactional
    public Image saveImage(MultipartFile imageFile) {
        Image image = new Image();
        image.setFileName(imageFile.getOriginalFilename());
        try {
            image.setData(imageFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return imageRepository.save(image);
    }
    public void deleteImage(Image image) {
        if (image != null) {
            imageRepository.delete(image);
        }
    }

}