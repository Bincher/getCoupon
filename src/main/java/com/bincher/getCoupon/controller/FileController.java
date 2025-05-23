package com.bincher.getCoupon.controller;

import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bincher.getCoupon.service.FileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {
    
    private final FileService fileService;

    @PostMapping("/upload")
    public String upload(
        @RequestParam("file") MultipartFile file
    ){
        String url = fileService.upload(file);
        return url;
    }

    @GetMapping(value="{fileName}", produces={org.springframework.http.MediaType.IMAGE_JPEG_VALUE, org.springframework.http.MediaType.IMAGE_PNG_VALUE})
    public Resource getImage(
        @PathVariable("fileName") String fileName
    ){
        Resource resource = fileService.getImage(fileName);
        return resource;
    }
    
}
