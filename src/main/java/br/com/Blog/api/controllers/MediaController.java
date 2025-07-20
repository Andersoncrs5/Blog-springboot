package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.*;
import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.Media;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.enums.ActionSumOrReduceComment;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/media")
public class MediaController {

    private final UnitOfWork unit;

    @PostMapping(value = "/save-media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> putObject(@RequestBody MediaDTO mediaDTO, @ModelAttribute UploadFileDTO dto , HttpServletRequest request) {
        this.unit.s3Service.bucketExists(dto.getBucketName());

        Long userId = this.unit.jwtService.extractId(request);
        User user = this.unit.userService.getV2(userId);
        Post post = this.unit.postService.Get(dto.getPostId());

        this.unit.s3Service.putObject(dto.getBucketName(),dto.getKey(), dto.getFile(), user, post);

        Media media = this.unit.mediaService.create(unit.mediaMapper.toMedia(mediaDTO), post);

        this.unit.postMetricsService.sumOrReduceMedia(this.unit.postMetricsService.get(post), ActionSumOrReduceComment.SUM);

        return new ResponseEntity<>(this.unit.responseDefault.response(
                "File uploaded and midia created!",
                200,
                request.getRequestURL().toString(),
                media,
                true
        ), HttpStatus.OK);
    }

    @DeleteMapping("/delete-object")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteObject(@RequestBody ObjectDTO dto, HttpServletRequest request) {
        this.unit.s3Service.bucketExists(dto.bucketName());
        this.unit.s3Service.deleteObject(dto.bucketName(), dto.key());

        Media media = this.unit.mediaService.get(dto.mediaId());
        this.unit.mediaService.delete(media);

        Post post = this.unit.postService.Get(dto.postId());
        this.unit.postMetricsService.sumOrReduceMedia(this.unit.postMetricsService.get(post), ActionSumOrReduceComment.REDUCE);

        return new ResponseEntity<>(this.unit.responseDefault.response(
                "Object deleted",
                200,
                request.getRequestURL().toString(),
                "",
                true
        ), HttpStatus.OK);
    }

    @PostMapping("/generate-link")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> generateLinkDownload(@RequestBody DownloadFileDTO dto, HttpServletRequest request) {
        URL url = this.unit.s3Service.generateLinkToDownloadFile(dto.bucketName(), dto.key(), dto.expirationDays());

        return new ResponseEntity<>(this.unit.responseDefault.response(
                "Url to download generated!",
                200,
                request.getRequestURL().toString(),
                url,
                true
        ), HttpStatus.OK);
    }

    @GetMapping("/{mediaId}")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> get(@PathVariable Long mediaId, HttpServletRequest request) {
        Media media = this.unit.mediaService.get(mediaId);

        return new ResponseEntity<>(this.unit.responseDefault.response(
                "Media founded!",
                200,
                request.getRequestURL().toString(),
                media,
                true
        ), HttpStatus.OK);
    }

    @GetMapping("/get-all-midias-by-post/{postId}")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getAllMidiasByPost(@PathVariable Long postId, HttpServletRequest request) {
        Post post = this.unit.postService.Get(postId);
        List<Media> medias = this.unit.mediaService.getAllMidiasByPost(post);

        return new ResponseEntity<>(this.unit.responseDefault.response(
                "Media founded!",
                200,
                request.getRequestURL().toString(),
                medias,
                true
        ), HttpStatus.OK);
    }

    @PutMapping("{mediaId}")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> update(@PathVariable Long mediaId, @RequestBody UpdateMediaDTO dto, HttpServletRequest request) {
        Media media = this.unit.mediaService.get(mediaId);
        Media update = this.unit.mediaService.update(media, dto);

        return new ResponseEntity<>(this.unit.responseDefault.response(
                "Media updated!",
                200,
                request.getRequestURL().toString(),
                update,
                true
        ), HttpStatus.OK);
    }

}