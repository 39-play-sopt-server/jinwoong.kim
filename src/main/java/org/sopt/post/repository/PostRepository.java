package org.sopt.post.repository;

import org.sopt.post.domain.Post;

import java.util.List;

public interface PostRepository {
    void save(Post post);

    Post findById(long id);

    void deleteById(long id);

    List<Post> findAll();
}
