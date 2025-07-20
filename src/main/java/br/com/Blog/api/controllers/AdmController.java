package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.*;
import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.pagebleDtos.PageableDTO;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.utils.Specifications.UserSpecification;
import br.com.Blog.api.utils.filtersDtos.UserFilterDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/adm")
public class AdmController {

    @Autowired
    private UnitOfWork unit;

    @PutMapping("enable-versioning-bucket")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> enableBucketVersioning(@RequestBody BucketDTO dto, HttpServletRequest request) {
        this.unit.s3Service.createBucket(dto.bucket());
        this.unit.s3Service.enableBucketVersioning(dto.bucket());

        Map<String, Object> response = this.unit.responseDefault.response(
                "Bucket enabled versioning!",
                200,
                request.getRequestURL().toString(),
                "",
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("suspended-versioning-bucket")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> suspendedBucketVersioning(@RequestBody BucketDTO dto, HttpServletRequest request) {
        this.unit.s3Service.createBucket(dto.bucket());
        this.unit.s3Service.suspendedBucketVersioning(dto.bucket());

        Map<String, Object> response = this.unit.responseDefault.response(
                "Bucket suspended versioning!",
                200,
                request.getRequestURL().toString(),
                "",
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/create-bucket")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> createBucket(@RequestBody BucketDTO dto, HttpServletRequest request) {
        this.unit.s3Service.createBucket(dto.bucket());

        Map<String, Object> response = this.unit.responseDefault.response(
                "Bucket created",
                200,
                request.getRequestURL().toString(),
                "",
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete-bucket")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteBucket(@RequestBody BucketDTO dto, HttpServletRequest request) {
        this.unit.s3Service.bucketExists(dto.bucket());
        this.unit.s3Service.deleteBucket(dto.bucket());

        Map<String, Object> response = this.unit.responseDefault.response(
                "Bucket deleted",
                200,
                request.getRequestURL().toString(),
                "",
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get-all-bucket")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 8)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getAllBuckets(HttpServletRequest request) {
        List<String> buckets = this.unit.s3Service.listAllBuckets();

        return ResponseEntity.ok(buckets);
    }

    @DeleteMapping("/delete-new-object")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteObject(@RequestBody ObjectDTO dto, HttpServletRequest request) {
        this.unit.s3Service.bucketExists(dto.bucketName());
        this.unit.s3Service.deleteObject(dto.bucketName(), dto.key());

        Map<String, Object> response = this.unit.responseDefault.response(
                "Object deleted",
                200,
                request.getRequestURL().toString(),
                "",
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/apply-life-cycle-policy-abort-incomplete-multipart-uploads")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> applyLifeCyclePolicyAbortIncompleteMultipartUploads(@RequestBody AbortIncompleteMultipartDTO dto, HttpServletRequest request) {
        this.unit.s3Service.createBucket(dto.getBucketName());
        this.unit.s3Service.applyLifeCyclePolicyAbortIncompleteMultipartUploads(dto.getBucketName(), dto.getPrefix(), dto.getDays());

        Map<String, Object> response = this.unit.responseDefault.response(
                "Abort Incomplete Multipart Uploads Policy applied",
                200,
                request.getRequestURL().toString(),
                "",
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/apply-life-cycle-policy-expire-noncurrent-versions")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> applyLifeCyclePolicyExpireNonCurrentVersions(@RequestBody ExpireNonCurrentVersionsDTO dto, HttpServletRequest request) {
        this.unit.s3Service.createBucket(dto.getBucketName());
        this.unit.s3Service.applyLifeCyclePolicyExpireNonCurrentVersions(dto.getBucketName(), dto.getPrefix(), dto.getNoncurrentDays());

        Map<String, Object> response = this.unit.responseDefault.response(
                "Expire NonCurrent Versions Policy applied to bucket " + dto.getBucketName(),
                200,
                request.getRequestURL().toString(),
                "",
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/apply-life-cycle-policy-move-to-standardIA-and-expire-current-versions")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> applyLifeCyclePolicyMoveToStandardIAAndExpireCurrentVersions(@RequestBody MoveToStandardIAAndExpireCurrentVersionsDTO dto, HttpServletRequest request) {
        this.unit.s3Service.createBucket(dto.getBucketName());
        this.unit.s3Service.applyLifeCyclePolicyMoveToStandardIAAndExpireCurrentVersions(dto.getBucketName(), dto.getPrefix(), dto.getDays(), dto.getExpDays());

        Map<String, Object> response = this.unit.responseDefault.response(
                "Move To StandardIA and ExpireCurrentVersions Policy applied to bucket " + dto.getBucketName(),
                200,
                request.getRequestURL().toString(),
                "",
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get-all-user")
    @RateLimit(capacity = 10, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAllUser(@RequestParam PageableDTO dto, @RequestParam UserFilterDTO filterDTO) {
        Specification<User> spec = UserSpecification.filterBy(filterDTO);

        Page<User> allUser = this.unit.admService.getAllUser(dto.getPageble(), spec);

        return ResponseEntity.ok(allUser);
    }

    @GetMapping("get-all-adms")
    @RateLimit(capacity = 10, refillTokens = 5, refillSeconds = 10)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAllAdm(@RequestParam PageableDTO dto, HttpServletRequest request) {
        Page<User> users = this.unit.admService.listAllAdm(dto.getPageble());

        return ResponseEntity.ok(users);
    }

    @GetMapping("add-role-adm-to-user/{userId}")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 4)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> AddRoleAdmToUser(@PathVariable Long userId, HttpServletRequest request) {
        User user = this.unit.userService.get(userId);
        this.unit.admService.AddRoleAdmToUser(user);
        // send email to user inforing about he is new adm

        Map<String, Object> response = this.unit.responseDefault.response(
                "Role adm added the user: "+ user.getEmail(),
                200,
                request.getRequestURL().toString(),
                null,
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("remove-role-adm-to-user/{userId}")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 4)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> removeRoleAdmToUser(@PathVariable Long userId, HttpServletRequest request) {
        User user = this.unit.userService.get(userId);
        this.unit.admService.RemoveRoleAdmToUser(user);
        // send email to user inforing about he is not adm

        Map<String, Object> response = this.unit.responseDefault.response(
                "Role adm removed the user: "+ user.getEmail(),
                200,
                request.getRequestURL().toString(),
                null,
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("block-user/{userId}")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 4)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> blockUser(@PathVariable Long userId, @RequestBody BlockTimeDTO dto, HttpServletRequest request) {
        User user = this.unit.userService.get(userId);
        this.unit.admService.blockUser(user, dto);
        // send email to user inforing about block of user

        Map<String, Object> response = this.unit.responseDefault.response(
                "User: "+ user.getEmail() + " blocked!",
                200,
                request.getRequestURL().toString(),
                null,
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("unblock-user/{userId}")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 4)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> unBlockUser(@PathVariable Long userId, HttpServletRequest request) {
        User user = this.unit.userService.get(userId);
        this.unit.admService.unBlockUser(user);
        // send email to user inforing about unblock of user

        Map<String, Object> response = this.unit.responseDefault.response(
                "User: "+ user.getEmail() + " blocked!",
                200,
                request.getRequestURL().toString(),
                null,
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("remove-post/{postId}")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 4)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> removePost(@PathVariable Long postId, HttpServletRequest request) {
        Post post = this.unit.postService.Get(postId);
        this.unit.admService.removePost(post);

        // to info user about removetion of post

        Map<String, Object> response = this.unit.responseDefault.response(
                "Post deleted",
                200,
                request.getRequestURL().toString(),
                null,
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("remove-comment/{id}")
    @RateLimit(capacity = 15, refillTokens = 5, refillSeconds = 4)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> removeComment(@PathVariable Long id, HttpServletRequest request) {
        Comment comment = this.unit.commentService.Get(id);
        this.unit.admService.removeComment(comment);

        // to info user about removetion of comment

        Map<String, Object> response = this.unit.responseDefault.response(
                "Comment deleted",
                200,
                request.getRequestURL().toString(),
                null,
                true
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}