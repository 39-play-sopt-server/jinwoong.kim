package org.sopt.week1.POST;

public class Main {
    public static void main(String[] args) {
        PostRepository repo = new PostRepository();

        // 심화: ID 생성기를 한 번 생성해 Service에서 공유합니다.
        PostIdGenerator idGenerator = new PostIdGenerator();
        PostService service = new PostService(repo, idGenerator);

        PostView view = new PostView();

        // 심화: 서버와 클라이언트 분리
        PostController controller = new PostController(service);

        // 심화: 클라이언트 역할의 Main에서 메뉴 실행을 담당
        while (true) {
            try {
                view.showMenu();
                int command = view.readCommand();

                switch (command) {
                    case 1 -> {
                        String title = view.readTitle();
                        String content = view.readContent();
                        Category category = view.readCategory();

                        Response<Void> response = controller.createPost(title, content, category);
                        view.printMessage(response.getMessage());
                    }
                    case 2 -> {
                        Response<java.util.List<Post>> response = controller.readPostList();

                        if (response.isSuccess()) {
                            view.printPostList(response.getData());
                        } else {
                            view.printMessage(response.getMessage());
                        }
                    } // 어려웠음
                    case 3 -> {
                        long id = view.readPostNumber("조회할 게시글 번호: ");
                        Response<Post> response = controller.readPost(id);

                        if (response.isSuccess()) {
                            view.printPost(response.getData());
                        } else {
                            view.printMessage(response.getMessage());
                        }
                    }
                    case 4 -> {
                        long id = view.readPostNumber("수정할 게시글 번호: ");
                        Response<Post> readResponse = controller.readPost(id);

                        if(!readResponse.isSuccess()) {
                            view.printMessage(readResponse.getMessage());
                        } else {
                            Post post = readResponse.getData();
                            view.printMessage("변경하지 않을 항목은 Enter를 누르세요.");

                            String title = view.readTitle();
                            String content = view.readContent();

                            if (title.isEmpty() && content.isEmpty()) {
                                view.printMessage("수정할 내용이 없습니다.");
                            } else {
                                if (title.isEmpty()) {
                                    title = post.getTitle();
                                }

                                if (content.isEmpty()) {
                                    content = post.getContent();
                                }

                                Response<Void> response = controller.updatePost(id, title, content);

                                view.printMessage(response.getMessage());
                            }

                        }


                    }
                    case 5 -> {
                        long id = view.readPostNumber("삭제할 게시글 번호: ");
                        Response<Void> response = controller.deletePost(id);

                        view.printMessage(response.getMessage());
                    }
                    case 6 -> {
                        view.printMessage("프로그램 종료합니다.");
                        return;
                    }
                    default -> view.printMessage("잘못된 메뉴 번호입니다.");
                }
            } catch (NumberFormatException exc) {
                view.printMessage("번호는 숫자로 입력해주세요.");
            } catch (IllegalArgumentException exc) {
                view.printMessage(exc.getMessage());
            }
        }
    }
}
