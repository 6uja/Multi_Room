import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private static final int PORT = 5000;

    public static Map<String, ChatRoom> rooms = new ConcurrentHashMap<>();
    public static Map<String, ClientHandler> sessions = new ConcurrentHashMap<>();

    private static final ExecutorService threadPool = Executors.newFixedThreadPool(20);

    public static void main(String[] args) {
        System.out.println("멀티룸 채팅 서버 시작");
        System.out.println("포트 번호: " + PORT);
        log("서버 시작");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log("서버 종료");
            threadPool.shutdown();
        }));

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("클라이언트 접속: " + socket.getInetAddress());
                log("클라이언트 접속: " + socket.getInetAddress());

                ClientHandler handler = new ClientHandler(socket);
                threadPool.execute(handler);
            }
        } catch (IOException e) {
            System.out.println("서버 오류: " + e.getMessage());
            log("서버 오류: " + e.getMessage());
        }
    }

    public static synchronized boolean registerUser(String nickname, ClientHandler handler) {
        if (sessions.containsKey(nickname)) {
            return false;
        }

        sessions.put(nickname, handler);
        log(nickname + " 로그인");
        return true;
    }

    public static void removeUser(String nickname) {
        if (nickname != null) {
            sessions.remove(nickname);
            log(nickname + " 로그아웃");
        }
    }

    public static ChatRoom getOrCreateRoom(String roomName) {
        rooms.putIfAbsent(roomName, new ChatRoom(roomName));
        return rooms.get(roomName);
    }

    public static String getRoomList() {
        if (rooms.isEmpty()) {
            return "현재 생성된 채팅방이 없습니다.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("현재 채팅방 목록:\n");

        for (String room : rooms.keySet()) {
            sb.append("- ").append(room).append("\n");
        }

        return sb.toString();
    }

    public static synchronized void log(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("server_log.txt", true))) {
            writer.println("[" + LocalDateTime.now() + "] " + message);
        } catch (IOException e) {
            System.out.println("로그 저장 오류: " + e.getMessage());
        }
    }
}