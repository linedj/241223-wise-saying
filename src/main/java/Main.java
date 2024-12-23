import java.io.*;
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

        while (true) {
            System.out.print("명령) ");
            String command = sc.nextLine(); // 명령어

            // 명언 등록
            if (command.equals("등록")) {
                System.out.print("명언 : ");
                String quote = sc.nextLine(); // 명언 입력

                System.out.print("작가 : ");
                String author = sc.nextLine(); // 작가 입력

                int idCounter = lastId(); // 마지막 사용된 ID 가져오기
                Quote Quote = new Quote(idCounter, quote, author); // 새 명언 객체 생성

                saveQuote(Quote); // 명언을 파일에 저장
                saveLastId(idCounter + 1); // ID 증가 후 저장

                System.out.println(idCounter + "번 명언이 등록되었습니다.");

                // 명언 목록 출력
            } else if (command.equals("목록")) {
                System.out.println("번호 / 작가 / 명언");
                System.out.println("----------------------");

                File folder = new File("db/wiseSaying");
                if (folder.exists()) { // 폴더가 존재하고 디렉터리인지 확인

                    /*FilenameFilter대신 람다식으로 .json확장자를 가지는 파일만 가져옴, 메모*/
                    File[] files = folder.listFiles((dir, name) -> {
                        if (name.endsWith(".json")) { // 확장자 확인, 파일의 이름이 숫자인 경우만
                            String checknum = name.substring(0, name.lastIndexOf('.')); // 확장자 제거
                            for (char ch : checknum.toCharArray()) {
                                if (!Character.isDigit(ch)) { // 문자가 숫자가 아니면 제외
                                    return false;
                                }
                            }
                            return true; // 모든 문자가 숫자라면 true 반환
                        }
                        return false;
                    });

                    if (files != null) {
                        for (int i = files.length - 1; i >= 0; i--) { // 최신 순으로 출력
                            Quote quote = loadQuote(files[i]); // 파일에서 명언 로드
                            if (quote != null) {
                                System.out.println(quote.id + " / " + quote.author + " / " + quote.quote);
                            }
                        }
                    }
                } else {
                    System.out.println("등록된 명언이 없습니다.");
                }

                // 명언 삭제
            } else if (command.startsWith("삭제?id=")) {
                try {
                    int idToDelete = Integer.parseInt(command.split("=")[1]); // 삭제할 ID 추출
                    if (deleteQuote(idToDelete)) { // 파일 삭제
                        System.out.println(idToDelete + "번 명언이 삭제되었습니다.");
                    } else {
                        System.out.println(idToDelete + "번 명언은 존재하지 않습니다.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("삭제 오류");
                }

                // 명언 수정
            } else if (command.startsWith("수정?id=")) {
                try {
                    int idToFix = Integer.parseInt(command.split("=")[1]); // 수정할 명언 ID 추출
                    Quote Quote = loadQuote(new File("db/wiseSaying/" + idToFix + ".json")); // 파일에서 명언 로드

                    if (Quote != null) {
                        System.out.println("명언(기존) : " + Quote.quote);
                        System.out.print("명언 : ");
                        String quote = sc.nextLine(); // 새로운 명언 입력

                        System.out.println("작가(기존) : " + Quote.author);
                        System.out.print("작가 : ");
                        String author = sc.nextLine(); // 새로운 작가 입력

                        Quote.quote = quote; // 명언 업데이트
                        Quote.author = author;

                        saveQuote(Quote); // 수정된 명언을 파일에 저장
                    } else {
                        System.out.println(idToFix + "번 명언은 존재하지 않습니다.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("수정 오류");
                }

                // 빌드 명령
            } else if (command.equals("빌드")) {
                buildData();
                System.out.println("data.json 파일의 내용이 갱신되었습니다.");

                // 프로그램 종료
            } else if (command.equals("종료")) {
                break;
            } else {
                System.out.println("알 수 없는 명령");
            }
        }
        sc.close(); // 리소스 해제
    }

    // 명언 데이터를 파일로 저장
    private static void saveQuote(Quote quote) {
        try {
            File folder = new File("db/wiseSaying");
            if (!folder.exists()) { // 디렉터리가 없으면 생성
                folder.mkdirs();
            }

            File file = new File("db/wiseSaying/" + quote.id + ".json");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("{\n");
                writer.write("\"id\": " + quote.id + ",\n");
                writer.write("\"content\": \"" + quote.quote + "\",\n");
                writer.write("\"author\": \"" + quote.author + "\"\n");
                writer.write("}");
            }
        } catch (IOException e) {
            System.out.println("파일 저장 오류");
        }
    }

    // 파일에서 명언을 읽어와 Quote 객체로 반환
    private static Quote loadQuote(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder json = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) { // JSON 데이터 줄읽기
                json.append(line); // StringBuilder로 전체 문자열 결합
            }

            return parseJson(json.toString()); // JSON 데이터를 Quote 객체로 변환
        } catch (IOException e) {
            return null; // 실패시 null 반환
        }
    }

    // 특정 ID의 명언 파일 삭제
    private static boolean deleteQuote(int id) {
        File file = new File("db/wiseSaying/" + id + ".json");
        if(file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    // 마지막 ID 값을 파일에서 읽어옴
    private static int lastId() {
        File file = new File("db/wiseSaying/lastId.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                return Integer.parseInt(reader.readLine().trim());
            } catch (IOException | NumberFormatException e) {
                System.out.println("lastId.txt 파일을 읽는 중 오류가 발생했습니다.");
            }
        }
        return 1; // 파일이 없으면 초기값으로 1 반환
    }

    // 마지막 ID 값을 파일에 저장
    private static void saveLastId(int lastId) {
        try {
            File folder = new File("db/wiseSaying");
            if (!folder.exists()) { // 디렉터리가 없으면 생성
                folder.mkdirs();
            }

            File file = new File("db/wiseSaying/lastId.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(String.valueOf(lastId));
            }
        } catch (IOException e) {
            System.out.println("lastId.txt 파일 저장 중 오류가 발생했습니다.");
        }
    }

    // JSON 데이터를 Quote 객체로 변환
    private static Quote parseJson(String json) {
        try {
            // JSON 데이터에서 중괄호, 공백 제거
            json = json.trim().replace("{", "").replace("}", "").replace("\n", "").replace("\r", "");

            // JSON 데이터의 필드별 분리
            String[] lines = json.split(",");

            // 필드 값 추출
            int id = Integer.parseInt(lines[0].split(":")[1].trim());
            String quote = lines[1].split(":")[1].trim().replace("\"", "");
            String author = lines[2].split(":")[1].trim().replace("\"", "");
            // Quote 객체 생성
            return new Quote(id, quote, author);
        } catch (Exception e) {
            System.out.println("JSON 파싱 오류");
            return null;
        }
    }
    //기존의 .json 명언들 합침
    private static void buildData() {
        File folder = new File("db/wiseSaying");
        File[] files = folder.listFiles((dir, name) -> {
            if (name.endsWith(".json")) { // 확장자 확인, 파일의 이름이 숫자인 경우만
                String checknum = name.substring(0, name.lastIndexOf('.')); // 확장자 제거
                for (char ch : checknum.toCharArray()) {
                    if (!Character.isDigit(ch)) { // 문자가 숫자가 아니면 제외
                        return false;
                    }
                }
                return true; // 모든 문자가 숫자라면 true 반환
            }
            return false;
        });
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("db/wiseSaying/data.json"))) {
            writer.write("[\n"); // JSON 배열 시작
            for (int i = 0; i < files.length; i++) {
                try (BufferedReader reader = new BufferedReader(new FileReader(files[i]))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write("  " + line); // 들여쓰기 추가
                    }
                }
                if (i < files.length - 1) writer.write(",\n"); // , 추가
            }
            writer.write("\n]\n"); // JSON 배열 종료
        } catch (IOException e) {
            System.out.println("빌드 오류" + e.getMessage());
        }
    }
}
class Quote {
    int id;         // 명언 ID
    String quote;   // 명언 내용
    String author;  // 명언 작가

    // 생성자
    Quote(int id, String quote, String author) {
        this.id = id;
        this.quote = quote;
        this.author = author;
    }
}
