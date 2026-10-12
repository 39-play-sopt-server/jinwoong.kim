package org.sopt.post.repository;

import org.sopt.post.domain.Post;

import java.util.List;

import java.util.Optional;

import org.sopt.post.domain.Category;

public interface PostRepository {
    Optional<Post> findById(long id);

    boolean deleteById(long id);

    List<Post> findAll();

    void create(String title, String content, Category category);
}
