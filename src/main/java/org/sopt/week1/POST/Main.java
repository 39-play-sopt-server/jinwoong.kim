package org.sopt.week1.POST;

public class Main {
    public static void main(String[] args) {
        PostView view = new PostView();
        PostController controller = new PostController(view);

        controller.run();
    }
}