package com.angrycow1111.springboot.chatroom;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link EndPointServer}
 *
 * @Author yanghui
 * @Description // 聊天室服务端类
 * @Date 09:57 2019/1/2 0002
 * @Classname EndPointServer
 **/

@ServerEndpoint( "/chatroom/{username}" )
public class EndPointServer {
    private static Map<String, Session> livingSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void openSession(@PathParam( "username" ) String username, Session session, @PathParam( "message" ) String message) {

        if (!Objects.isNull(session)) {
            String sessionId = session.getId();

            livingSessions.put(sessionId, session);
            message = "欢迎[" + username + "]进入房间！";
            sendMessageToAll(message);
        }

    }

    @OnMessage
    public void processMessage(String message) {
        sendMessageToAll(message);

    }

    @OnClose
    public void close(Session session) {

        if (!Objects.isNull(session)) {
            livingSessions.remove(session.getId());
            String message = "[" + session.getUserPrincipal().getName() + "]离开了房间！";
            if (livingSessions != null && livingSessions.isEmpty()) {
                sendMessageToAll(message);
            }
        }
    }

    private void sendMessageToAll(String message) {

        livingSessions.forEach((sessionId, session) -> {
            sendMessage(session, message);
        });

    }

    private void sendMessage(Session session, String message) {
        final RemoteEndpoint.Basic remote = session.getBasicRemote();

        try {
            remote.sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
