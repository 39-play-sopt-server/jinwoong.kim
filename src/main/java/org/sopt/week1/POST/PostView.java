package org.sopt.week1.POST;

// 작성 시각의 출력 형식에 사용합니다.
import java.time.format.DateTimeFormatter;

import java.util.Scanner;

// 심화: 서버와 클라이언트 분리
import java.util.List;

public class PostView {
    // 콘솔 입력을 읽는 Scanner
    private final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // METHOD: 메뉴 출력
    public void showMenu(){
        System.out.println("\n=== 게시판 ===");
        System.out.println("1. 게시글 작성");
        System.out.println("2. 게시글 목록 조회");
        System.out.println("3. 게시글 단건 조회");
        System.out.println("4. 게시글 수정");
        System.out.println("5. 게시글 삭제");
        System.out.println("6. 종료");
    }

    // METHOD: 메뉴 번호 입력
    public int readCommand() {
        System.out.print("선택: ");
        return Integer.parseInt(scanner.nextLine());
    }

    // METHOD: 제목 입력
    public String readTitle() {
        System.out.print("제목: ");
        return scanner.nextLine();
    }

    // METHOD: 본문 입력
    public String readContent() {
        System.out.print("내용: ");
        return scanner.nextLine();
    }

    // 심화 METHOD: 고유 ID 범위에 맞춰 long 타입으로 입력
    public long readPostNumber(String message) {
        System.out.print(message);
        return Long.parseLong(scanner.nextLine());
    }

    // METHOD: 카테고리 선택
    public Category readCategory() {
        System.out.println("카테고리: 1. 일반 / 2. 질문 / 3. 정보");
        System.out.print("선택: ");

        int number = Integer.parseInt(scanner.nextLine());

        return switch (number) {
            case 1 -> Category.GENERAL;
            case 2 -> Category.QUESTION;
            case 3 -> Category.INFORMATION;
            default -> throw new IllegalArgumentException(
                    "카테고리는 1~3 중 선택해주세요."
            );
        };
    }

    // 심화 METHOD: 서버에서 받은 게시글 목록 출력
    public void printPostList(List<Post> posts) {
        printMessage("\n=== 게시글 목록 ===");

        if (posts.isEmpty()) {
            printMessage("게시글이 없습니다.");
            return;
        }

        for (Post post : posts) {
            printMessage(post.getId() + ". " + post.getTitle());
        }
    }

    // METHOD: 게시글 상세 출력
    public void printPost(Post post) {
        System.out.println("\n=== 게시글 ===");
        System.out.println("제목: " + post.getTitle());
        System.out.println("내용: " + post.getContent());
        System.out.println("카테고리: " + post.getCategory());
        System.out.println("작성 시각: " + post.getCreatedAt().format(DATE_TIME_FORMATTER));
    }

    // METHOD: 안내 및 오류 메시지 출력
    public void printMessage(String message) {
        System.out.println(message);
    }
}
