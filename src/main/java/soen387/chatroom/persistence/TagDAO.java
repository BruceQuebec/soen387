package soen387.chatroom.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TagDAO extends DAO {
    protected String  db_tag_table;

    public String getDb_tag_table() {
        return db_tag_table;
    }

    public TagDAO(String db_url, String db_driver, String db_root, String db_password, String db_tag_table){
        super(db_url, db_driver, db_root, db_password);
        this.db_tag_table = db_tag_table;
    }

    public int addNewTag(String tname) throws SQLException, ClassNotFoundException {
        String queryExistingTagSql = "SELECT tid FROM " + this.db_tag_table + " WHERE tname LIKE '" + tname + "'";
        Connection db_connection = super.db_connect();
        PreparedStatement ps = db_connection.prepareStatement(queryExistingTagSql);
        //ps.setString(1,tname);
        ResultSet rs = ps.executeQuery(queryExistingTagSql);
        int tid = 0;
        if(rs.next()){
            tid = rs.getInt(1);
            return tid;
        }
        String addNewTagSql = "INSERT INTO " + this.db_tag_table + " (tid,tname) values (?,?)";
        tid = super.getMaxTableId(db_connection, this.db_tag_table, "tid") + 1;
        ps = db_connection.prepareStatement(addNewTagSql);
        ps.setInt(1, tid);
        ps.setString(2,tname);
        ps.executeUpdate();
        super.db_closeConnection(db_connection);
        return tid;
    }

    public int findTagIdByName(String tname) throws SQLException, ClassNotFoundException {
        String queryExistingTagSql = "SELECT tid FROM " + this.db_tag_table + " WHERE tname LIKE '" + tname + "' LIMIT 1";

        Connection db_connection = super.db_connect();
        PreparedStatement ps = db_connection.prepareStatement(queryExistingTagSql);
        //ps.setString(1,tname);
        ResultSet rs = ps.executeQuery(queryExistingTagSql);
        int tid = 0;
        if(rs.next()){
            tid = rs.getInt("tid");
        }
        rs.close();
        super.db_closeConnection(db_connection);
        return tid;
    }
}
