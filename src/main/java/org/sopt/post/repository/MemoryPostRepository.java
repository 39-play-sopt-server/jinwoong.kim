package org.sopt.post.repository;

import org.sopt.post.domain.Post;
import org.sopt.post.exception.PostNotFoundException;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;
import java.util.Optional;

import org.sopt.post.domain.Category;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Repository
public class MemoryPostRepository implements PostRepository {
    // 심화: ID를 key로 사용하는 HashMap 저장소ㄴ
    private final Map<Long, Post> posts = new TreeMap<>();

    private final PostIdGenerator idGenerator;

    // CONSTRUCTOR
    public MemoryPostRepository(PostIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    // 심화 METHOD: ID로 조회하고 없는 게시글은 예외 처리
    @Override
    public Optional<Post> findById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    // 심화 METHOD: ID로 삭제하고 없는 게시글은 예외 처리
    @Override
    public boolean deleteById(long id) {
        return posts.remove(id) != null;
    }

    // 심화 METHOD: TreeMap의 값을 ID 순으로 정렬해 목록 반환
    // 별도 List를 반환해 목록 변경이 저장소 구조에 영향을 주지 않게 합니다.
    // List의 Post 객체는 저장소의 객체와 공유합니다.
    @Override
    public List<Post> findAll() {
        return new ArrayList<>(posts.values());
    }

    // METHOD: create
    @Override
    public void create(String title, String content, Category category) {
        long id = idGenerator.generate();
        Post post = new Post(title, content, category, id);
        posts.put(id, post);
    }
}
