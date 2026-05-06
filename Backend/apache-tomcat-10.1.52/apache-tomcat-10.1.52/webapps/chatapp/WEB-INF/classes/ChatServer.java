import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.PathParam;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@ServerEndpoint("/chat/{username}")
public class ChatServer {

    // store all connected users
    private static Map<String, Session> users = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {

        users.put(username, session);

        System.out.println(username + " connected");
    }

    @OnMessage
    public void onMessage(String message, Session session) {

        try {

            // message format
            // sender|receiver|message
            String[] parts = message.split("\\|", 3);

            // DEBUG: check if server receives message
            System.out.println("Message received: " + message);

            String sender = parts[0];
            String receiver = parts[1];
            String msg = parts[2];

            Session receiverSession = users.get(receiver);
            Session senderSession = users.get(sender);

            // send message to receiver
            if (receiverSession != null && receiverSession.isOpen()) {
                receiverSession.getBasicRemote().sendText(sender + ":" + msg);
            }

            // send message back to sender (to show on sender screen)
            if (senderSession != null && senderSession.isOpen()) {
                senderSession.getBasicRemote().sendText(sender + ":" + msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {

        users.values().remove(session);

        System.out.println("User disconnected");
    }
}
