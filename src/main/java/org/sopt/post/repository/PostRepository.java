package org.sopt.post.repository;

import org.sopt.post.domain.Post;

import java.util.List;

import java.util.Optional;

public interface PostRepository {
    void save(Post post);

    Optional<Post> findById(long id);

    boolean deleteById(long id);

    List<Post> findAll();
}
