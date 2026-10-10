package org.sopt;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostRepository repo;
    // 심화: 게시글 ID 생성기
    private final PostIdGenerator idGenerator;

    // CONSTRUCTOR: 저장소와 ID 생성기를 외부에서 전달받습니다.
    // Service는 전달받은 Repository를 통해 저장소에 접근합니다.
    public PostService(PostRepository repo, PostIdGenerator idGenerator) {

        this.repo = repo;
        this.idGenerator = idGenerator;
    }

    // METHOD: 게시글 작성
    // 심화: ID를 발급하고 검증된 게시글을 저장합니다.
    public void createPost(
            String title,
            String content,
            Category category) {
        long id = idGenerator.generate();
        Post post = new Post(title, content, category, id);

        repo.save(post);
    }

    // 심화 METHOD: 목록 위치 대신 고유 ID로 게시글 조회
    public Post readPost(long id) { return repo.findById(id); }

    // METHOD: 게시글 목록 조회
    public List<Post> readPostList() {
        return repo.findAll();
    }

    // 심화 METHOD: 고유 ID로 게시글 수정
    public void updatePost(
            long id,
            String newTitle,
            String newContent
    ) {
        Post post = repo.findById(id);
        post.update(newTitle, newContent);
    }

    // 심화 METHOD: 고유 ID로 게시글 삭제
    public void deletePost(long id) {

        repo.deleteById(id);
    }
}
