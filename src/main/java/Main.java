import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        App app = new App();
        app.run();
    }
}

class App{
    public void run(){
        Scanner sc = new Scanner(System.in);

        System.out.println("==명언 앱==");

        System.out.print("명언 : ");
        sc.nextLine();
        System.out.print("작가 : ");
        sc.nextLine();
        System.out.println("명령) 종료");

        sc.close();
    }
}
