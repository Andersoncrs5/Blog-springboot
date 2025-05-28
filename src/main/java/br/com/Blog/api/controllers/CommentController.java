package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.CommentDTO;
import br.com.Blog.api.Specifications.CommentSpecification;
import br.com.Blog.api.Specifications.PostSpecification;
import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.CommentMetrics;
import br.com.Blog.api.entities.Post;
import br.com.Blog.api.entities.User;
import br.com.Blog.api.entities.enums.ActionSumOrReduceComment;
import br.com.Blog.api.entities.enums.SumOrReduce;
import br.com.Blog.api.services.CommentMetricsService;
import br.com.Blog.api.services.CommentService;
import br.com.Blog.api.services.response.ResponseDefault;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/comment")
@RequiredArgsConstructor
public class CommentController {

    private final UnitOfWork uow;
    private final ResponseDefault responseDefault;

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 6)
    @GetMapping("/getAllByUser")
    @SecurityRequirement(name = "bearerAuth")
    public Page<Comment> getAllCommentByUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) LocalDateTime createdAtBefore,
            @RequestParam(required = false) LocalDateTime createdAtAfter,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Long viewsCountBefore,
            @RequestParam(required = false) Long viewsCountAfter,
            @RequestParam(required = false) Long favorites,
            @RequestParam(required = false) Long likesBefore,
            @RequestParam(required = false) Long likesAfter,
            @RequestParam(required = false) Long dislikesBefore,
            @RequestParam(required = false) Long dislikesAfter
            ) {
        List<String> validSortFields = List.of("createdAt", "content", "viewsCount", "likes", "dislikes");
        if (!validSortFields.contains(sortBy)) {
            sortBy = "createdAt";
        }

        Long userId = this.uow.jwtService.extractId(request);
        User user = this.uow.userService.get(userId);
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Specification<Comment> spec = CommentSpecification.filterBy(
                createdAtBefore, createdAtAfter, content,
                viewsCountBefore, viewsCountAfter, favorites,
                likesBefore, likesAfter, dislikesBefore, dislikesAfter
        );

        return this.uow.commentService.getAllCommentOfUser(user, spec, pageable);
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 10)
    @GetMapping("{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> get(@PathVariable Long id, HttpServletRequest request){
        Comment comment = this.uow.commentService.Get(id);
        this.uow.commentMetricsService.sumView(comment);

        var response = this.uow.responseDefault.response("Comment found with successfully",201,request.getRequestURL().toString(), comment, true);

        return new ResponseEntity<>( response, HttpStatus.OK);
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 15)
    @DeleteMapping("{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request){
        Comment comment = this.uow.commentService.Delete(id);
        this.uow.userMetricsService.sumOrRedCommentsCount(comment.getUser(), SumOrReduce.REDUCE);
        this.uow.postMetricsService.sumOrReduceComments(comment.getPost(), ActionSumOrReduceComment.REDUCE);

        var response = this.uow.responseDefault.response("Comment deleted with successfully",200,request.getRequestURL().toString(), null, true);

        return new ResponseEntity<>( response, HttpStatus.OK);
    }

    @PostMapping("/{postId}")
    @SecurityRequirement(name = "bearerAuth")
    @RateLimit(capacity = 20, refillTokens = 2, refillSeconds = 15)
    public ResponseEntity<?> Create(
            @RequestBody @Valid CommentDTO dto,
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        Long id = this.uow.jwtService.extractId(request);

        Comment comment = this.uow.commentService.Create(dto.MappearToComment(), id, postId);
        this.uow.commentMetricsService.create(comment);
        this.uow.userMetricsService.sumOrRedCommentsCount(comment.getUser(), SumOrReduce.SUM);
        this.uow.postMetricsService.sumOrReduceComments(comment.getPost(), ActionSumOrReduceComment.SUM);

        var response = this.uow.responseDefault.response("Comment created with successfully",200,request.getRequestURL().toString(), comment, true);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 15)
    public ResponseEntity<?> Update(@RequestBody @Valid CommentDTO dto, @PathVariable Long id, HttpServletRequest request){

        Comment comment = this.uow.commentService.Update(id, dto.MappearToComment());
        this.uow.commentMetricsService.sumEdited(comment);
        var response = this.uow.responseDefault.response("Comment updated with successfully",200,request.getRequestURL().toString(), comment, true);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 8)
    @GetMapping("/GetAllCommentsOfPost/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public Page<Comment> GetAllCommentsOfPost(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return this.uow.commentService.GetAllCommentsOfPost(id, PageRequest.of(page, size));
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 10)
    @GetMapping("/getMetric/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getMetric(
            @PathVariable Long id,
            HttpServletRequest request
    ){
        var response = responseDefault.response(
                "Metric got with successfully",
                201,
                request.getRequestURL().toString(),
                this.uow.commentService.getMetric(id),
                true
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}