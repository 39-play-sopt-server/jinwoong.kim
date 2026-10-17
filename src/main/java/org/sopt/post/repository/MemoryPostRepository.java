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

/**
 * CLASS: TreeMap으로 게시글을 메모리에 저장하며, 목록은 ID 오름차순으로 반환합니다.
 * 서버를 재시작하면 저장된 데이터는 사라집니다.
 */
@Repository
public class MemoryPostRepository implements PostRepository {

    private final Map<Long, Post> posts = new TreeMap<>(); // 예: 키 1L → 해당 ID의 Post 객체

    private final PostIdGenerator idGenerator; // 예: 생성자로 주입받은 PostIdGenerator 객체

    /**
     * CONSTRUCTOR: 게시글 생성 시 사용할 ID 생성기를 주입받습니다.
     */
    public MemoryPostRepository(PostIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /*
     전체 다 synchronized 추가: 같은 저장소 객체에서 이 네 메서드 중 하나에 한번에 하나의 스레드만 들어갈 수 있습니다.
     스레드 A: create()로 저장 중
     스레드 B: findAll() 호출 → 대기
     스레드 A: 저장 완료, 메서드 종료
     스레드 B: 목록 조회 시작
     */
    /**
     * METHOD: 게시글을 조회하며, 존재 여부에 대한 예외 판단은 Service에 맡깁니다.
     */
    @Override
    public synchronized Optional<Post> findById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    /**
     * METHOD: 게시글을 삭제하고, 삭제된 객체가 있었는지 반환합니다.
     */
    @Override
    public synchronized boolean deleteById(long id) {
        return posts.remove(id) != null;
    }

    /**
     * METHOD: TreeMap의 ID 순서대로 별도 목록을 만듭니다. 목록 안의 Post 객체는 저장소와 공유됩니다.
     */
    @Override
    public synchronized List<Post> findAll() {
        return new ArrayList<>(posts.values());
    }

    /**
     * METHOD: ID를 발급하고 Post 생성자의 검증을 통과한 게시글을 저장합니다.
     */
    @Override
    public synchronized void create(String title, String content, Category category) {
        long id = idGenerator.generate();
        Post post = new Post(title, content, category, id);
        posts.put(id, post);
    }
}
