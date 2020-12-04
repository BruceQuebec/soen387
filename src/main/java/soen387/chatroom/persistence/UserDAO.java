package soen387.chatroom.persistence;

import soen387.chatroom.model.User;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends DAO{
    protected String  db_user_table;

    public String db_user_table() {
        return db_user_table;
    }

    public UserDAO(String db_url, String db_driver, String db_root, String db_password, String db_user_table){
        super(db_url, db_driver, db_root, db_password);
        this.db_user_table = db_user_table;
    }

    public List<User> getUserByName(String username) throws SQLException, ClassNotFoundException, NoSuchAlgorithmException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM " + db_user_table + " WHERE username LIKE ?";
        PreparedStatement ps = super.db_connect().prepareStatement(sql);
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            users.add(new User(rs.getInt("uid"), rs.getString("username"), rs.getString("email"), rs.getString("password"), rs.getString("membership"),false));
        }
        rs.close();
        return users;
    }

    public List<User> getUserById(int uid) throws SQLException, ClassNotFoundException, NoSuchAlgorithmException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM " + db_user_table + " WHERE uid=" + uid + "";
        PreparedStatement ps = super.db_connect().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            users.add(new User(rs.getInt("uid"), rs.getString("username"), rs.getString("email"), rs.getString("password"), rs.getString("membership"), false));
        }
        rs.close();
        return users;
    }
}
