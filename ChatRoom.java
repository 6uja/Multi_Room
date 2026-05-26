import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoom {
    private String roomName;
    private Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    public ChatRoom(String roomName) {
        this.roomName = roomName;
    }

    public void addClient(ClientHandler client) {
        clients.add(client);
        broadcast("[알림] " + client.getNickname() + "님이 입장했습니다.");
        ChatServer.log(client.getNickname() + "님이 [" + roomName + "] 방에 입장");
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        broadcast("[알림] " + client.getNickname() + "님이 퇴장했습니다.");
        ChatServer.log(client.getNickname() + "님이 [" + roomName + "] 방에서 퇴장");
    }

    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
        ChatServer.log("[" + roomName + "] " + message);
    }

    public String getUserList() {
        if (clients.isEmpty()) {
            return "현재 방에 사용자가 없습니다.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("현재 방 접속자 목록:\n");

        for (ClientHandler client : clients) {
            sb.append("- ").append(client.getNickname()).append("\n");
        }

        return sb.toString();
    }

    public ClientHandler findUser(String nickname) {
        for (ClientHandler client : clients) {
            if (client.getNickname().equals(nickname)) {
                return client;
            }
        }
        return null;
    }

    public String getRoomName() {
        return roomName;
    }
}