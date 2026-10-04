package org.sopt.week1.POST;


public class PostService {
    private final PostRepository repo;

    // constructor
    // why? make PostRepository class in the service
    public PostService(PostRepository repo) {
        this.repo = repo;
    }
    
}
