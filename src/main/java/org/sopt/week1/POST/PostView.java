package org.sopt;

public class PostView {
    // CLASS Scanner to read input data
    private final Scanner scanner = new Scanner(System.in);

    // METHOD: show menu:
    public int showMenu(){
        System.out.println("\n=== 게시판 ===");
        System.out.println("1. 게시글 작성");
        System.out.println("2. 게시글 목록 조회");
        System.out.println("3. 게시글 단건 조회");
        System.out.println("4. 게시글 수정");
        System.out.println("5. 게시글 삭제");
        System.out.println("6. 종료");
        System.out.print("선택: ");

        return Integer.parseInt(scanner.nextLine());
    }
    // METHOD: read command:
    public String read(String label) { System.out.print(label + ": "); return scanner.nextLine(); }
    public int readNumber(String label) { return Integer.parseInt(read(label)) - 1; }

    // METHOD: input post:
    public void

    // METHOD: show Post list:
    public void showPostList(List<Post> posts){
        // 게시글 목록 조회
        System.out.println("\n=== 게시글 목록 ===");

        if (posts.isEmpty()) {
            System.out.println("게시글이 없습니다.");
            break;
        }

        for (int i = 0; i < posts.size(); i++) {
            Post currentPost = posts.get(i);

            System.out.println(
                    (i + 1) + ". " + currentPost.title
            );
        }
    }

    // METHOD: show Post
    public void showPost(Post post){
        if (posts.isEmpty()) {
            System.out.println("게시글이 없습니다.");
            break;
        }

        System.out.print("조회할 게시글 번호: ");
        int readIndex = Integer.parseInt(scanner.nextLine()) - 1;

        if (readIndex < 0 || readIndex >= posts.size()) {
            System.out.println("존재하지 않는 게시글입니다.");
            break;
        }

        Post readPost = posts.get(readIndex);

        System.out.println("\n=== 게시글 ===");
        System.out.println("제목: " + readPost.title);
        System.out.println("내용: " + readPost.content);
    }

    // METHOD: show message:
    public void printMessage(String message) { System.out.println(message); }

}