package com.example.demo.chat;

import com.example.demo.chat.MessageRepository;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j // Using Lombok to simplify logging
@Controller // Marks this class as a component detectable by Spring's component scanning
@ServerEndpoint(value = "/chat/{username}") // Declares this class as a WebSocket endpoint
public class ChatSocket {

	// Static field to hold a reference to the message repository, to be shared among instances
	private static MessageRepository msgRepo;
	// Maps for efficient lookup of sessions and usernames, supporting concurrent access
	private final ConcurrentHashMap<Session, String> sessionUsernameMap = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Session> usernameSessionMap = new ConcurrentHashMap<>();

	// Method to set the message repository, enabling Spring's dependency injection mechanism
	@Autowired
	public void setMessageRepository(MessageRepository repo) {
		ChatSocket.msgRepo = repo;
	}

	// Handles the opening of a new WebSocket session
	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) {
		log.info("Session opened, username: {}", username);
		// Checks if the username is already in use to prevent duplicates
		if (usernameSessionMap.containsKey(username)) {
			// Closes the session with a reason if the username is already taken
			closeSessionWithReason(session, "This username is already in use. Please choose a different one.",
					CloseReason.CloseCodes.VIOLATED_POLICY, "Username is already in use");
			return;
		}

		// Updates mappings with the new session and username
		sessionUsernameMap.put(session, username);
		usernameSessionMap.put(username, session);
		// Sends existing chat history to the new user
		sendMessageToParticularUser(username, getChatHistory());
		// Broadcasts a message to all users announcing the new user's arrival
		broadcast("User:" + username + " has Joined the Chat");
	}

	// Handles incoming messages from clients
	@OnMessage
	public void onMessage(Session session, String message) {
		String username = sessionUsernameMap.get(session);
		// Direct messages are prefixed with '@' and handled separately
		if (message.startsWith("@")) {
			handleDirectMessage(username, message);
		} else {
			// Broadcasts the message to all connected users
			broadcast(username + ": " + message);
		}
		// Persists the received message
		msgRepo.save(new Message(username, message));
	}

	// Handles the closure of a WebSocket session
	@OnClose
	public void onClose(Session session) {
		String username = sessionUsernameMap.remove(session);
		usernameSessionMap.remove(username);
		// Broadcasts a user's departure message to all connected users
		broadcast(username + " disconnected");
	}

	// Logs errors that occur within the WebSocket session
	@OnError
	public void onError(Session session, Throwable throwable) {
		log.error("WebSocket error for session " + session.getId(), throwable);
	}

	// Handles direct messages between users
	private void handleDirectMessage(String senderUsername, String message) {
		String destUsername = message.split(" ")[0].substring(1);
		String formattedMessage = "[DM] " + senderUsername + ": " + message;
		// Sends the message privately to the intended recipient and also echoes it back to the sender
		sendMessageToParticularUser(destUsername, formattedMessage);
		sendMessageToParticularUser(senderUsername, formattedMessage);
	}

	// Sends a message to a specific user identified by username
	private void sendMessageToParticularUser(String username, String message) {
		Session session = usernameSessionMap.get(username);
		if (session != null) {
			try {
				session.getBasicRemote().sendText(message);
			} catch (IOException e) {
				log.error("Failed to send message to {}: {}", username, e.getMessage());
			}
		}
	}

	// Broadcasts a message to all connected users
	private void broadcast(String message) {
		sessionUsernameMap.keySet().forEach(session -> {
			try {
				session.getBasicRemote().sendText(message);
			} catch (IOException e) {
				log.error("Failed to broadcast message", e);
			}
		});
	}

	// Closes a WebSocket session with a specified reason, primarily used for username conflicts
	private void closeSessionWithReason(Session session, String message, CloseReason.CloseCodes code, String reason) {
		try {
			session.getBasicRemote().sendText(message);
			session.close(new CloseReason(code, reason));
		} catch (IOException e) {
			log.error("Error closing session: {}", e.getMessage());
		}
	}

	// Retrieves and formats the chat history for a newly connected user
	private String getChatHistory() {
		List<Message> messages = msgRepo.findAll();
		StringBuilder sb = new StringBuilder();
		messages.forEach(message -> sb.append(message.getUserName()).append(": ").append(message.getContent()).append("\n"));
		return sb.toString();
	}
}
