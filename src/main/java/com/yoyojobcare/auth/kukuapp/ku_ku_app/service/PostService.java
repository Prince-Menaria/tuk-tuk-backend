package com.yoyojobcare.auth.kukuapp.ku_ku_app.service;

import org.springframework.data.domain.Page;

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

public interface PostService {

    PostResponseDto createPost(CreatePostRequestDto request);

    Page<PostResponseDto> getUserPosts(ViewUserAllPostsServiceRequestDto serviceRequestDto);

    Page<PostResponseDto> getFeed(ViewUserAllPostsFeedServiceRequestDto serviceRequestDto);

    LikeResponseDto toggleLike(LikeRequestDto request);

    CommentResponseDto addComment(CommentRequestDto request);

    Page<CommentResponseDto> getComments(ViewAllUserCommentsServiceRequestDto serviceRequestDto);

    void deletePost(DeletePostServiceRequestDto serviceRequestDto);

}
