package com.yoyojobcare.auth.kukuapp.ku_ku_app.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.PostService;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.post.CommentRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.post.CreatePostRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.post.DeletePostServiceRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.post.LikeRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.post.ViewAllUserCommentsServiceRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.post.ViewUserAllPostsFeedServiceRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceRequestDto.post.ViewUserAllPostsServiceRequestDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.post.CommentResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.post.LikeResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.dto.serviceResponseDto.post.PostResponseDto;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.utility.MobileResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    // ✅ Post create
    @PostMapping("/create-post")
    public ResponseEntity<MobileResponse<PostResponseDto>> createPost(
            @RequestBody CreatePostRequestDto request) {
        try {
            log.info("Request create new post ", request);
            PostResponseDto response = postService.createPost(request);
            return ResponseEntity.ok(MobileResponse.<PostResponseDto>builder()
                    .status(true).message("Post created successful ..").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<PostResponseDto>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    // ✅ User ke posts
    @GetMapping("/view-user-posts/{userId}")
    public ResponseEntity<MobileResponse<Page<PostResponseDto>>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam Long currentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            ViewUserAllPostsServiceRequestDto serviceRequestDto = new ViewUserAllPostsServiceRequestDto();
            serviceRequestDto.setCurrentUserId(currentUserId);
            serviceRequestDto.setPage(page);
            serviceRequestDto.setSize(size);
            serviceRequestDto.setUserId(userId);
            Page<PostResponseDto> posts = postService.getUserPosts(serviceRequestDto);
            return ResponseEntity.ok(MobileResponse.<Page<PostResponseDto>>builder()
                    .status(true).message("Posts fetched").data(posts).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<Page<PostResponseDto>>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    // ✅ Feed — sab posts
    @GetMapping("/view-feed")
    public ResponseEntity<MobileResponse<Page<PostResponseDto>>> getFeed(
            @RequestParam Long currentUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            ViewUserAllPostsFeedServiceRequestDto serviceRequestDto = new ViewUserAllPostsFeedServiceRequestDto();
            serviceRequestDto.setCurrentUserId(currentUserId);
            serviceRequestDto.setPage(page);
            serviceRequestDto.setSize(size);
            Page<PostResponseDto> feed = postService.getFeed(serviceRequestDto);
            return ResponseEntity.ok(MobileResponse.<Page<PostResponseDto>>builder()
                    .status(true).message("Feed fetched").data(feed).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<Page<PostResponseDto>>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    // ✅ Like toggle
    @PostMapping("/add-like")
    public ResponseEntity<MobileResponse<LikeResponseDto>> toggleLike(
            @RequestBody LikeRequestDto request) {
        try {
            LikeResponseDto response = postService.toggleLike(request);
            return ResponseEntity.ok(MobileResponse.<LikeResponseDto>builder()
                    .status(true).message(response.getMessage()).data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<LikeResponseDto>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    // ✅ Comment add
    @PostMapping("/add-comment")
    public ResponseEntity<MobileResponse<CommentResponseDto>> addComment(
            @RequestBody CommentRequestDto request) {
        try {
            CommentResponseDto response = postService.addComment(request);
            return ResponseEntity.ok(MobileResponse.<CommentResponseDto>builder()
                    .status(true).message("Comment added").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<CommentResponseDto>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    // ✅ Comments fetch
    @GetMapping("/view-comments")
    public ResponseEntity<MobileResponse<Page<CommentResponseDto>>> getComments(
            @RequestParam Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            ViewAllUserCommentsServiceRequestDto serviceRequestDto = new ViewAllUserCommentsServiceRequestDto();
            Page<CommentResponseDto> comments = postService.getComments(serviceRequestDto);
            return ResponseEntity.ok(MobileResponse.<Page<CommentResponseDto>>builder()
                    .status(true).message("Comments fetched").data(comments).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<Page<CommentResponseDto>>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }

    // ✅ Post delete
    @DeleteMapping("/delete-post")
    public ResponseEntity<MobileResponse<String>> deletePost(
            @RequestParam Long postId,
            @RequestParam Long userId) {
        try {
            DeletePostServiceRequestDto serviceRequestDto = new DeletePostServiceRequestDto();
            serviceRequestDto.setPostId(postId);
            serviceRequestDto.setUserId(userId);
            postService.deletePost(serviceRequestDto);
            return ResponseEntity.ok(MobileResponse.<String>builder()
                    .status(true).message("Post deleted").data("success").build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MobileResponse.<String>builder()
                    .status(false).message(e.getMessage()).build());
        }
    }


}
