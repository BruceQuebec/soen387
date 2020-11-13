package soen387.chatroom.persistence;

import java.sql.*;

public class DAO {
    protected String db_url;
    protected String db_driver;
    protected String db_root;
    protected String db_password;

    protected String getDb_url() {
        return db_url;
    }

    protected String getDb_driver() {
        return db_driver;
    }

    protected String getDb_root() {
        return db_root;
    }

    protected String getDb_password() {
        return db_password;
    }

    protected DAO(String db_url, String db_driver, String db_root, String db_password){
        this.db_url = db_url;
        this.db_driver = db_driver;
        this.db_root = db_root;
        this.db_password = db_password;
    }

    protected Connection db_connect() throws ClassNotFoundException, SQLException {
        Class.forName(this.db_driver);
        return DriverManager.getConnection(this.db_url,this.db_root,this.db_password);
    }

    protected void db_closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
        }
    }

    protected int getMaxTableId(Connection conn, String table, String pk) throws SQLException {
        String sql = "Select max(t." + pk + ") from " + table + " t";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int max = rs.getInt(1);
            return max;
        }
        rs.close();
        return 0;
    }
}
