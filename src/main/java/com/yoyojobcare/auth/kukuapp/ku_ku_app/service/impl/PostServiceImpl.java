package com.yoyojobcare.auth.kukuapp.ku_ku_app.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.Post;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.PostComment;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.PostLike;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.User;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.PostCommentRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.PostLikeRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.PostRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.repository.UserRepository;
import com.yoyojobcare.auth.kukuapp.ku_ku_app.service.NotificationService;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository likeRepository;
    private final PostCommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public PostResponseDto createPost(CreatePostRequestDto request) {
        log.info("📝 Creating post for user: {}", request.getUserId());
        try {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Post post = new Post();
            post.setUser(user);
            post.setContent(request.getContent());
            post.setMediaUrl(request.getMediaUrl());
            post.setMediaType(request.getMediaType() != null ? request.getMediaType() : "TEXT");
            post.setVisibility(request.getVisibility() != null ? request.getVisibility() : "PUBLIC");
            post.setLikeCount(0);
            post.setCommentCount(0);
            post.setIsActive(Boolean.TRUE);

            Post saved = postRepository.save(post);
            log.info("✅ Post created: {}", saved.getPostId());
            return toPostDto(saved, request.getUserId());

        } catch (Exception e) {
            log.error("Create post error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getUserPosts(ViewUserAllPostsServiceRequestDto serviceRequestDto) {
        try {
            log.info("Request View all user posts : {} ", serviceRequestDto);
            Pageable pageable = PageRequest.of(serviceRequestDto.getPage(), serviceRequestDto.getSize());
            return postRepository
                    .findByUserUserIdAndIsActiveTrueOrderByCreatedAtDesc(serviceRequestDto.getUserId(), pageable)
                    .map(p -> toPostDto(p, serviceRequestDto.getCurrentUserId()));

        } catch (Exception e) {
            log.error("View all user posts error: {}", e);
            throw e;
        }

    }

    @Override
    public Page<PostResponseDto> getFeed(ViewUserAllPostsFeedServiceRequestDto serviceRequestDto) {
        try {
            log.info("Request View all user feed posts : {} ", serviceRequestDto);
            Pageable pageable = PageRequest.of(serviceRequestDto.getPage(), serviceRequestDto.getSize());
            return postRepository
                    .findByIsActiveTrueOrderByCreatedAtDesc(pageable)
                    .map(p -> toPostDto(p, serviceRequestDto.getCurrentUserId()));

        } catch (Exception e) {
            log.error("View all user feed posts error: {}", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public LikeResponseDto toggleLike(LikeRequestDto request) {
        log.info("❤️ Toggle like: user={}, post={}", request.getUserId(), request.getPostId());
        try {
            Post post = postRepository.findById(request.getPostId())
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<PostLike> existingLike = likeRepository
                    .findByPostPostIdAndUserUserId(request.getPostId(), request.getUserId());

            boolean liked;
            if (existingLike.isPresent()) {
                // ✅ Already liked — unlike karo
                likeRepository.delete(existingLike.get());
                post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                liked = false;
            } else {
                // ✅ Like karo
                PostLike postLike = new PostLike();
                postLike.setPost(post);
                postLike.setUser(user);
                postLike.setLikedAt(LocalDateTime.now());
                this.likeRepository.save(postLike);
                post.setLikeCount(post.getLikeCount() + 1);
                liked = true;
            }

            this.postRepository.save(post);

            // ✅ toggleLike mein — like true hone pe
            if (Boolean.TRUE.equals(liked)) {
                this.notificationService.createNotification(
                        post.getUser(), // receiver — post ka owner
                        user, // sender — jo like kiya
                        "POST_LIKE",
                        user.getFullName() + " liked your post",
                        post.getPostId(),
                        "POST");
            }

            return LikeResponseDto.builder()
                    .postId(request.getPostId())
                    .liked(liked)
                    .totalLikes(post.getLikeCount())
                    .message(liked ? "Post liked!" : "Post unliked!")
                    .build();

        } catch (Exception e) {
            log.error("Toggle like error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(CommentRequestDto request) {
        log.info("💬 Adding comment: user={}, post={}", request.getUserId(), request.getPostId());
        try {
            Post post = this.postRepository.findById(request.getPostId())
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            User user = this.userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            PostComment parent = null;
            if (request.getParentCommentId() != null) {
                parent = this.commentRepository.findById(request.getParentCommentId())
                        .orElseThrow(
                                () -> new RuntimeException("Post Comment not found .." + request.getParentCommentId()));
            }

            PostComment comment = new PostComment();
            comment.setPost(post);
            comment.setUser(user);
            comment.setContent(request.getContent());
            comment.setParentComment(parent);
            comment.setLikeCount(0);
            comment.setIsActive(Boolean.TRUE);
            comment.setCommentedAt(LocalDateTime.now());

            PostComment saved = this.commentRepository.save(comment);

            // ✅ Comment count update
            post.setCommentCount(post.getCommentCount() + 1);
            this.postRepository.save(post);

            // ✅ add Comment notification send
            this.notificationService.createNotification(
                    post.getUser(),
                    user,
                    "POST_COMMENT",
                    user.getFullName() + " commented on your post",
                    post.getPostId(),
                    "POST");

            return this.toCommentDto(saved);

        } catch (Exception e) {
            log.error("Add comment error: {}", e);
            throw e;
        }
    }

    @Override
    public Page<CommentResponseDto> getComments(ViewAllUserCommentsServiceRequestDto serviceRequestDto) {
        log.info("💬 Get comment: {} ", serviceRequestDto);

        try {
            Pageable pageable = PageRequest.of(
                    serviceRequestDto.getPage(),
                    serviceRequestDto.getSize());

            Page<PostComment> topComments = commentRepository
                    .findByPostPostIdAndParentCommentIsNullAndIsActiveTrueOrderByCommentedAtDesc(
                            serviceRequestDto.getPostId(),
                            pageable);

            // Step 1: Top comment IDs collect karo
            List<Long> topCommentIds = topComments.getContent()
                    .stream()
                    .map(PostComment::getCommentId)
                    .collect(Collectors.toList());

            // Step 2: Ek query me sab replies fetch karo
            List<PostComment> allReplies = commentRepository
                    .findByParentCommentCommentIdInAndIsActiveTrueOrderByCommentedAtAsc(topCommentIds);

            // Step 3: Group replies by parent comment ID
            Map<Long, List<CommentResponseDto>> repliesMap = allReplies.stream()
                    .map(this::toCommentDto)
                    .collect(Collectors.groupingBy(
                            r -> r.getParentCommentId()));

            // Step 4: Final DTO build
            return topComments.map(comment -> {
                CommentResponseDto dto = toCommentDto(comment);
                dto.setReplies(
                        repliesMap.getOrDefault(
                                comment.getCommentId(),
                                Collections.emptyList()));
                return dto;
            });

        } catch (Exception e) {
            log.error("get Comments error: {}", e);
            throw e;
        }
    }

    @Override
    public void deletePost(DeletePostServiceRequestDto serviceRequestDto) {
        try {
            Post post = postRepository.findById(serviceRequestDto.getPostId())
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            if (!post.getUser().getUserId().equals(serviceRequestDto.getUserId())) {
                throw new RuntimeException("Not authorized to delete this post userid : "+ serviceRequestDto.getUserId());
            }

            post.setIsActive(false);
            postRepository.save(post);
            log.info("✅ Post {} deleted by user {}", serviceRequestDto.getPostId(), serviceRequestDto.getUserId());

        } catch (Exception e) {
            log.error("Delete Post error: {}", e);
            throw e;
        }

    }

    // ✅ Helper
    private PostResponseDto toPostDto(Post post, Long currentUserId) {
        boolean isLiked = currentUserId != null &&
                likeRepository.existsByPostPostIdAndUserUserId(post.getPostId(), currentUserId);

        return PostResponseDto.builder()
                .postId(post.getPostId())
                .userId(post.getUser().getUserId())
                .userName(post.getUser().getFullName())
                .userImage(post.getUser().getImage())
                .content(post.getContent())
                .mediaUrl(post.getMediaUrl())
                .mediaType(post.getMediaType())
                .visibility(post.getVisibility())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .isLikedByMe(isLiked)
                .createdAt(LocalDateTime.ofInstant(post.getCreatedAt(), null))
                .build();
    }

    private CommentResponseDto toCommentDto(PostComment comment) {
        return CommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .postId(comment.getPost().getPostId())
                .userId(comment.getUser().getUserId())
                .userName(comment.getUser().getFullName())
                .userImage(comment.getUser().getImage())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .parentCommentId(comment.getParentComment() != null
                        ? comment.getParentComment().getCommentId()
                        : null)
                .replies(new ArrayList<>())
                .commentedAt(comment.getCommentedAt())
                .build();
    }

}
