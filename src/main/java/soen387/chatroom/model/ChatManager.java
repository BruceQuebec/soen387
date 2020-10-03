package soen387.chatroom.model;

import javafx.util.Pair;
import java.util.Map;

public interface ChatManager {
    public void postMessage(String user, String message);
    public Map<Integer, Pair<String, String>> ListMessages(int start, int end);
    public void ClearChat(int start, int end);
}
