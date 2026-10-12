package org.sopt;

import java.util.List;

public interface PostRepository {
    void save(Post post);

    Post findById(long id);

    void deleteById(long id);

    List<Post> findAll();
}
