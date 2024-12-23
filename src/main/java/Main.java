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

        int lastno = 1;

        while(true){
            System.out.print("명령) ");
            String command = sc.nextLine();

            if(command.equals("종료")){
                System.out.println(("명언 앱을 종료합니다"));
                break;
            }

            else if(command.equals("등록")){
                System.out.print("명언 : ");
                sc.nextLine();

                System.out.print("작가 : ");
                sc.nextLine();

                System.out.println(lastno + "번 명언이 등록되었습니다.");
                lastno++;
            }

        }
    }
}
