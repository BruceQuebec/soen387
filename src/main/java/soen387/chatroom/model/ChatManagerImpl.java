package soen387.chatroom.model;

import javafx.util.Pair;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ChatManagerImpl implements ChatManager {
    private Map<Integer, Pair<String, String>> messages;
    int count;

    private ChatManagerImpl(){ this.messages = new TreeMap<>(); this.count = 0;}

    private static class ChatManagerInstanceGen{
        private static final ChatManagerImpl INSTANCE = new ChatManagerImpl();
    }
    public static ChatManagerImpl getInstance(){
        return ChatManagerInstanceGen.INSTANCE;
    }

    @Override
    public void postMessage(String user, String message) { this.messages.put(this.count++, new Pair<String, String>(user, message)); }

    @Override
    public Map<Integer, Pair<String, String>> listMessages(int start, int end) {
        Map<Integer, Pair<String, String>> result = this.messages.
                entrySet().
                stream().
                filter(entry->entry.getKey().compareTo(start)>=0 && entry.getKey().compareTo(end)<=0)
                        .collect(Collectors.toMap(entry->entry.getKey(), entry->entry.getValue()));
        return result;
    }

    public Map<Integer, Pair<String, String>> listMessages(){
        return this.messages;
    }

    @Override
    public void clearChat(int start, int end) {
        this.messages.entrySet().removeIf(entry -> entry.getKey().compareTo(start)>=0 && entry.getKey().compareTo(end)<=0);
        this.count = 0;
        Map<Integer, Pair<String, String>> clearedMessageMap = new HashMap<>();
        this.messages.forEach((k,v)->{ clearedMessageMap.put(this.count++, v); });
        this.messages = clearedMessageMap;
    }

    @Override
    public void clearChat(){
        this.messages.clear();
    }

    public int getCount(){
        return count;
    }
}
