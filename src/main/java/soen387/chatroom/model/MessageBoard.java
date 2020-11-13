package soen387.chatroom.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Collection;
import java.util.List;

public interface MessageBoard {
    public Collection<User> userAuthenticate(String username, String password) throws FileNotFoundException, NoSuchAlgorithmException;
    public void userSignup() throws NoSuchAlgorithmException, IOException;
    public List<? extends Object> listPosts(int startIdx, int postsPerPage) throws SQLException, ClassNotFoundException;
    public int createPost(Post post) throws SQLException, ClassNotFoundException;
    public void deletePost(int pid) throws SQLException, ClassNotFoundException;
    public int editPost(int pid, Post post) throws SQLException, ClassNotFoundException;
    public List<? extends Object> searchPostByPid(int pid) throws SQLException, ClassNotFoundException;
    public List<? extends Object> searchPostByUser(int userId, int offset, int limit) throws SQLException, ClassNotFoundException;
    public List<? extends Object> searchPostBytime(Time startTime, Time endTime, int offset, int limit) throws SQLException, ClassNotFoundException;
    public List<? extends Object> searchPostByTag(int tagId, int offset, int limit) throws SQLException, ClassNotFoundException;
    public List<? extends Object> downloadAttachement(int aid) throws SQLException, ClassNotFoundException;
}
