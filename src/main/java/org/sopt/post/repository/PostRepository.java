package org.sopt.post.repository;

import org.sopt.post.domain.Post;

import java.util.List;

import java.util.Optional;

import org.sopt.post.domain.Category;

/**
 * INTERFACE: 저장 방식과 관계없이 게시글 저장소가 제공해야 하는 기능을 정의합니다.
 */
public interface PostRepository {
    /**
     * METHOD: ID로 조회한 게시글을 반환하며, 없으면 빈 Optional을 반환합니다.
     */
    Optional<Post> findById(long id);

    /**
     * METHOD: ID에 해당하는 게시글을 삭제하고, 실제 삭제 여부를 반환합니다.
     */
    boolean deleteById(long id);

    /**
     * METHOD: 전체 게시글을 ID 오름차순 목록으로 반환합니다.
     */
    List<Post> findAll();

    /**
     * METHOD: 저장소에서 ID를 발급하고 게시글을 생성해 저장합니다.
     */
    void create(String title, String content, Category category);
}
