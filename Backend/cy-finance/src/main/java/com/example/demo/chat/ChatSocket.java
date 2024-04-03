package com.example.demo.chat;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j // Lombok annotation for simplifying logging; replaces manual logger instantiation
@Controller
@ServerEndpoint(value = "/chat/{username}") // Defines the WebSocket server endpoint URL
public class ChatSocket {

	private static MessageRepository msgRepo;
	private static Map<Session, String> sessionUsernameMap = new ConcurrentHashMap<>(); // Updated for better concurrency handling
	private static Map<String, Session> usernameSessionMap = new ConcurrentHashMap<>();

	@Autowired
	public void setMessageRepository(MessageRepository repo) {
		ChatSocket.msgRepo = repo;
	}

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) throws IOException {
		log.info("Entered into Open");

		// Check if username is already in use
		if (usernameSessionMap.containsKey(username)) {
			// Username is already in use, so close the new session and send a message to the client
			session.getBasicRemote().sendText("This username is already in use. Please choose a different one.");
			session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Username is already in use"));
			return; // Stop further processing
		}

		sessionUsernameMap.put(session, username);
		usernameSessionMap.put(username, session);

		sendMessageToParticularUser(username, getChatHistory());

		String message = "User:" + username + " has Joined the Chat";
		broadcast(message);
	}


	@OnMessage
	public void onMessage(Session session, String message) throws IOException {
		log.info("Entered into Message: Got Message:" + message);
		String username = sessionUsernameMap.get(session);

		if (message.startsWith("@")) {
			String destUsername = message.split(" ")[0].substring(1);

			sendMessageToParticularUser(destUsername, "[DM] " + username + ": " + message);
			sendMessageToParticularUser(username, "[DM] " + username + ": " + message);
		} else {
			broadcast(username + ": " + message);
		}

		msgRepo.save(new Message(username, message));
	}

	@OnClose
	public void onClose(Session session) throws IOException {
		log.info("Entered into Close");

		String username = sessionUsernameMap.get(session);
		sessionUsernameMap.remove(session);
		usernameSessionMap.remove(username);

		String message = username + " disconnected";
		broadcast(message);
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		log.error("Entered into Error", throwable); // Enhanced error logging for better visibility into issues
	}

	private void sendMessageToParticularUser(String username, String message) {
		try {
			usernameSessionMap.get(username).getBasicRemote().sendText(message);
		} catch (IOException e) {
			log.error("Error sending message to user {}: {}", username, e.getMessage()); // Improved error logging
		}
	}

	private void broadcast(String message) {
		sessionUsernameMap.forEach((session, username) -> {
			try {
				session.getBasicRemote().sendText(message);
			} catch (IOException e) {
				log.error("Exception in broadcasting message: {}", e.getMessage()); // Changed from 'info' to 'error'
			}
		});
	}

	private String getChatHistory() {
		List<Message> messages = msgRepo.findAll();

		StringBuilder sb = new StringBuilder();
		if(messages.size() != 0) {
			for (Message message : messages) {
				sb.append(message.getUserName()).append(": ").append(message.getContent()).append("\n");
			}
		}
		return sb.toString();
	}
}
