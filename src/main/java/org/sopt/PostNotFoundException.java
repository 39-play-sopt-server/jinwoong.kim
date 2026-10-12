package org.sopt;

// extends사용이유: 실행 중 발생하는 예외의 한 종류를 직접 정의.
public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException() {
        super("존재하지 않는 개시글입니다."); //super: 부모 클랫의 생성자에 예외 메세지 전달
    }
}
