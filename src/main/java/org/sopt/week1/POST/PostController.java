package org.sopt.week1.POST;

import java.util.List;

public class PostController {

    // 심화 view 없애기
    // 콘솔 입출력을 담당하는 View
    // private final PostView view;
    // 게시글 처리를 담당하는 Service
    private final PostService service;

    // CONSTRUCTOR: 입출력을 담당하는 View와 게시글 처리를 담당하는 Service를 전달받습니다.
    public PostController(PostService service)
    {
        this.service = service;
    }

   /* 심화: Main - view나눔으로써 사용하지 않음
    public void run() {
        while (true) {
            try {
                view.showMenu();
                int command = view.readCommand();

                switch (command) {
                    case 1 -> createPost();
                    case 2 -> readPostList();
                    case 3 -> readPost();
                    case 4 -> updatePost();
                    case 5 -> deletePost();
                    case 6 -> {
                        view.printMessage("프로그램을 종료합니다.");
                        return;
                    }
                    default -> view.printMessage("잘못된 입력입니다.");
                }
            } catch (NumberFormatException exc) {
                view.printMessage("번호는 숫자로 입력해주세요.");
            } catch (IllegalArgumentException exc) {
                view.printMessage(exc.getMessage());
            }
        }
    } */

    // METHODS -----------------
    // METHOD: 게시글 작성
    public Response<Void> createPost(
            String title,
            String content,
            Category category
    ) {
        try {
            service.createPost(title, content, category);
            return Response.success("게시글이 작성되었습니다.", null);
        } catch (IllegalArgumentException exc) {
            return Response.failure(exc.getMessage());
        }
    }

    // 심화 view 없애기
    // METHOD: 게시글 목록 조회
    public Response<List<Post>> readPostList() {
        return Response.success(
                "게시글 목록을 조회했습니다.",
                service.readPostList()
        );
    }

    // 심화 view 없애기
    // METHOD: ID로 게시글 단건 조회
    public Response<Post> readPost(long id){
        try {
            return Response.success(
                    "게시글을 조회했습니다.",
                    service.readPost(id)
            );
        } catch (IllegalArgumentException exc) {
            return Response.failure(exc.getMessage());
        }
    }

    // 심화 view 없애기
    // METHOD: 게시글 수정
    // 입력하지 않은 항목은 기존 값을 유지합니다.
    public Response<Void> updatePost(
            long id,
            String title,
            String content
    ) {
        try {
           service.updatePost(id, title, content);
           return Response.success("ID:"+ id + "게시글이 수정되었습니다.", null);
        } catch (IllegalArgumentException exc) {
            return Response.failure(exc.getMessage());
        }
    }

    // METHOD: ID로 게시글 삭제
    public Response<Void> deletePost(long id) {
        try {
            service.deletePost(id);
            return Response.success("ID:"+ id + "게시글이 삭제되었습니다.", null);
        } catch (IllegalArgumentException exc) {
            return Response.failure(exc.getMessage());
        }
    }
}
