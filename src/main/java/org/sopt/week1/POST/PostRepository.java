package org.sopt.week1.POST;

import java.util.ArrayList;
import java.util.List;

public class PostRepository {
    private final List<Post> posts = new ArrayList<>();

    // METHOD: save the Post
    public void save(Post post) {
        posts.add(post);
    }

    // METHOD: delete the Post
    public void deleteByIndex(int index) {
        posts.remove(index);
    }

    // METHOD: searching by index
    // why funtion type Post? return Post so caller can access in any field
    public Post findByIndex(int index) {
        return posts.get(index);
    }

    // METHOD: print all the list of Posts
    // why return by "new.."? if return posts directly, then caller can use clear function to delete all the posts.
    public List<Post> findAll() {
        return new ArrayList<>(posts);
    }

    // METHOD: check if empty
    public boolean isEmpty() {
        return posts.isEmpty();
    }

    // METHOD: Validate that a post exists at the given index
    public boolean validateIndex(int index) {
        return index >= 0 && index < posts.size();
    }


}


