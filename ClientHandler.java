import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private String nickname;
    private ChatRoom currentRoom;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public String getNickname() {
        return nickname;
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private void showHelp() {
        sendMessage("사용 가능한 명령어:");
        sendMessage("/rooms : 채팅방 목록 보기");
        sendMessage("/users : 현재 방 접속자 목록 보기");
        sendMessage("/join 방이름 : 다른 채팅방으로 이동");
        sendMessage("/w 닉네임 메시지 : 귓속말 보내기");
        sendMessage("/help : 명령어 안내");
        sendMessage("/quit : 채팅 종료");
    }

    private void moveRoom(String newRoomName) {
        if (currentRoom != null) {
            currentRoom.removeClient(this);
        }

        currentRoom = ChatServer.getOrCreateRoom(newRoomName);
        currentRoom.addClient(this);

        sendMessage("[" + newRoomName + "] 채팅방으로 이동했습니다.");
    }

    private void sendWhisper(String message) {
        String[] parts = message.split(" ", 3);

        if (parts.length < 3) {
            sendMessage("사용법: /w 닉네임 메시지");
            return;
        }

        String targetName = parts[1];
        String whisperMessage = parts[2];

        ClientHandler target = currentRoom.findUser(targetName);

        if (target == null) {
            sendMessage("현재 방에서 해당 사용자를 찾을 수 없습니다: " + targetName);
            return;
        }

        target.sendMessage("[귓속말] " + nickname + " → " + targetName + " : " + whisperMessage);
        sendMessage("[귓속말] 나 → " + targetName + " : " + whisperMessage);

        ChatServer.log("[귓속말] " + nickname + " -> " + targetName + " : " + whisperMessage);
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                out.println("닉네임을 입력하세요:");
                nickname = in.readLine();

                if (nickname == null) {
                    return;
                }

                nickname = nickname.trim();

                if (nickname.isEmpty()) {
                    out.println("닉네임은 비워둘 수 없습니다.");
                    continue;
                }

                if (ChatServer.registerUser(nickname, this)) {
                    break;
                } else {
                    out.println("이미 사용 중인 닉네임입니다. 다른 닉네임을 입력하세요.");
                }
            }

            out.println("입장할 채팅방 이름을 입력하세요:");
            String roomName = in.readLine();

            if (roomName == null || roomName.trim().isEmpty()) {
                roomName = "default";
            }

            moveRoom(roomName.trim());
            showHelp();

            String message;

            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }

                if (message.equalsIgnoreCase("/rooms")) {
                    sendMessage(ChatServer.getRoomList());
                    continue;
                }

                if (message.equalsIgnoreCase("/users")) {
                    sendMessage(currentRoom.getUserList());
                    continue;
                }

                if (message.equalsIgnoreCase("/help")) {
                    showHelp();
                    continue;
                }

                if (message.startsWith("/join ")) {
                    String newRoomName = message.substring(6).trim();

                    if (newRoomName.isEmpty()) {
                        sendMessage("사용법: /join 방이름");
                    } else {
                        moveRoom(newRoomName);
                    }
                    continue;
                }

                if (message.startsWith("/w ")) {
                    sendWhisper(message);
                    continue;
                }

                currentRoom.broadcast("[" + nickname + "] " + message);
            }

        } catch (IOException e) {
            System.out.println("클라이언트 오류: " + e.getMessage());
            ChatServer.log("클라이언트 오류: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            if (currentRoom != null) {
                currentRoom.removeClient(this);
            }

            ChatServer.removeUser(nickname);

            if (in != null) {
                in.close();
            }

            if (out != null) {
                out.close();
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            System.out.println(nickname + " 접속 종료");
            ChatServer.log(nickname + " 접속 종료");

        } catch (IOException e) {
            System.out.println("자원 해제 오류: " + e.getMessage());
            ChatServer.log("자원 해제 오류: " + e.getMessage());
        }
    }
}