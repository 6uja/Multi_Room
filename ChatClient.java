import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private static final String SERVER_IP = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader serverIn = null;
        PrintWriter serverOut = null;
        BufferedReader keyboard = null;

        try {
            socket = new Socket(SERVER_IP, PORT);

            serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverOut = new PrintWriter(socket.getOutputStream(), true);
            keyboard = new BufferedReader(new InputStreamReader(System.in));

            BufferedReader finalServerIn = serverIn;

            Thread receiveThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = finalServerIn.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("서버 연결 종료");
                }
            });

            receiveThread.start();

            String input;

            while ((input = keyboard.readLine()) != null) {
                serverOut.println(input);

                if (input.equalsIgnoreCase("/quit")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("클라이언트 오류: " + e.getMessage());
        } finally {
            try {
                if (serverIn != null) {
                    serverIn.close();
                }

                if (serverOut != null) {
                    serverOut.close();
                }

                if (keyboard != null) {
                    keyboard.close();
                }

                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }

            } catch (IOException e) {
                System.out.println("자원 해제 오류: " + e.getMessage());
            }
        }
    }
}