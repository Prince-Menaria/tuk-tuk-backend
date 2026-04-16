package com.yoyojobcare.auth.kukuapp.ku_ku_app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.yoyojobcare.auth.kukuapp.ku_ku_app.entity.PostComment;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    // ✅ Post ke top-level comments
    @EntityGraph(attributePaths = { "user" })
    Page<PostComment> findByPostPostIdAndParentCommentIsNullAndIsActiveTrueOrderByCommentedAtDesc(
            Long postId,
            Pageable pageable);

    // ✅ Comment replies
    @EntityGraph(attributePaths = { "user" })
    List<PostComment> findByParentCommentCommentIdInAndIsActiveTrueOrderByCommentedAtAsc(
            List<Long> topCommentIds);
}
