package br.com.Blog.api.controllers;

import br.com.Blog.api.DTOs.CommentDTO;
import br.com.Blog.api.config.JwtService;
import br.com.Blog.api.config.annotation.RateLimit;
import br.com.Blog.api.controllers.setUnitOfWork.UnitOfWork;
import br.com.Blog.api.entities.Comment;
import br.com.Blog.api.entities.CommentMetrics;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/comment")
@RequiredArgsConstructor
public class CommentController {

    private final UnitOfWork uow;
    private final ResponseDefault responseDefault;

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 10)
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Comment get(@PathVariable Long id){
        Comment comment = this.uow.commentService.Get(id);
        this.uow.commentMetricsService.sumView(comment);

        return comment;
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 15)
    @DeleteMapping("{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Comment comment = this.uow.commentService.Delete(id);
        this.uow.userMetricsService.sumOrRedCommentsCount(comment.getUser(), SumOrReduce.REDUCE);
        this.uow.postMetricsService.sumOrReduceComments(comment.getPost(), ActionSumOrReduceComment.REDUCE);

        return new ResponseEntity<>("",HttpStatus.OK);
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

        return new ResponseEntity<>(comment,HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @RateLimit(capacity = 10, refillTokens = 2, refillSeconds = 15)
    public ResponseEntity<?> Update(@RequestBody @Valid CommentDTO dto, @PathVariable Long id){

        Comment comment = this.uow.commentService.Update(id, dto.MappearToComment());
        this.uow.commentMetricsService.sumEdited(comment);

        return new ResponseEntity<>(comment,HttpStatus.OK);
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 10)
    @GetMapping("/GetAllCommentsOfPost/{id}")
    public ResponseEntity<?> GetAllCommentsOfPost(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return this.uow.commentService.GetAllCommentsOfPost(id, pageable);
    }

    @RateLimit(capacity = 20, refillTokens = 5, refillSeconds = 10)
    @GetMapping("/getMetric/{id}")
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