package org.sopt.week1.POST;

import java.util.ArrayList;
import java.util.List;

public class PostController {

    // POST ARRAY
    private final List<Post> posts = new ArrayList<>();
    // POST VIEW
    private final PostView view;

    // CONSTRUCTOR
    public PostController(PostView view) {
        this.view = view;
    }

    // METHOD: main run
    public void run() {
        while (true) {
            view.showMenu();
            int command = view.readCommand();

            switch (command) {
                case 1 -> createPost();
                case 2 -> readPostList();
                case 3 -> readPost();
                case 4 -> updatePost();
                case 5 -> deletePost();
                case 6 -> {
                    view.printMessage("프로그램 종료합니다.");
                    return;
                }
                default -> view.printMessage("잘못된 입력입니다.");
            }
        }
    }

    // METHOD: Case1 -> create Post
    private void createPost() {
        String title = view.readTitle();
        String content = view.readContent();

        Post post = new Post(title, content);
        posts.add(post);

        view.printMessage("게시글이 작성되었습니다");
    }

    // METHOD: Case2 -> Read List
    private void readPostList() {
        view.printMessage("\n=== 게시글 목록 ===");

        if (posts.isEmpty()) {
            view.printMessage("게시글이 없습니다.");
            return;
        }

        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            view.printMessage((i + 1) + ". " + post.getTitle());
        }
    }

    // METHOD: Case3: read Post
    private void readPost(){
        if (posts.isEmpty()) {
            view.printMessage("게시글이 아무것도 없습니다");
            return;
        }

        int index = view.readPostNumber("조회할 게시글 번호: ") - 1;

        if (!isValidIndex(index)) {
            view.printMessage("존재하지 않는 개시글입니다.");
            return;
        }

        Post post = posts.get(index);
        view.printPost(post);
    }

    // METHOD: Case4: Update Post
    private void updatePost(){
        if (posts.isEmpty()) {
            view.printMessage("게시글이 없습니다.");
            return;
        }

        int index = view.readPostNumber("수정할 게시글 번호: ") - 1;

        if (!isValidIndex(index)) {
            view.printMessage("존재하지 않는 개시글입니다.");
            return;
        }

        String newTitle = view.readTitle();
        String newContent = view.readContent();

        Post post = posts.get(index);
        post.updateContent(newContent);
        post.updateTitle(newTitle);
    }

    // METHOD: case5 -> delete the post
    private void deletePost() {
        if (posts.isEmpty()) {
            view.printMessage("게시글이 없습니다.");
            return;
        }

        int index = view.readPostNumber("삭제할 게시글 번호: ") - 1;

        if (!isValidIndex(index)) {
            view.printMessage("존재하지 않는 개시글입니다.");
        }

        posts.remove(index);

        view.printMessage("개시글이 삭제되었습니다.");
    }

    // METHOD: For check valid index
    private boolean isValidIndex(int index) {
        return index >= 0 && index < posts.size();
    }
}


