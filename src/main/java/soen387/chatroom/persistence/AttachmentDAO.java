package soen387.chatroom.persistence;

import soen387.chatroom.model.Attachment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttachmentDAO extends DAO{
    protected String  db_attachment_table;

    public String getDb_attachment_table() {
        return db_attachment_table;
    }

    public AttachmentDAO(String db_url, String db_driver, String db_root, String db_password, String db_attachment_table){
        super(db_url, db_driver, db_root, db_password);
        this.db_attachment_table = db_attachment_table;
    }

    public int addNewAttachment(int pid, String filetype, String filename, double filesize, Blob filecontent) throws SQLException, ClassNotFoundException {
        String addNewAttSql = "INSERT INTO " + this.db_attachment_table + " (aid,pid,filetype,filename,filesize,filecontent,uptime,mtime) "
                + " values (?,?,?,?,?,?,NOW(),NOW())";
        Connection db_connection = super.db_connect();
        PreparedStatement ps = db_connection.prepareStatement(addNewAttSql);
        int aid = super.getMaxTableId(db_connection, this.db_attachment_table, "aid") + 1;
        ps.setInt(1, aid);
        ps.setInt(2, pid);
        ps.setString(3,filetype);
        ps.setString(4,filename);
        ps.setDouble(5,filesize);
        ps.setBlob(6, filecontent);
        ps.executeUpdate();
        super.db_closeConnection(db_connection);
        return aid;
    }

    public void deleteAttachment(int pid) throws SQLException, ClassNotFoundException {
        String deleteAttSql = "DELETE FROM " + this.db_attachment_table + " WHERE pid=" + pid +"";
        Connection db_connection = super.db_connect();
        PreparedStatement ps = db_connection.prepareStatement(deleteAttSql);
        ps.executeUpdate(deleteAttSql);
        ps.close();
        super.db_closeConnection(db_connection);
    }

    public void deleteAttachmentByAid(int aid) throws SQLException, ClassNotFoundException {
        String deleteAttSql = "DELETE FROM " + this.db_attachment_table + " WHERE aid=" + aid +"";
        Connection db_connection = super.db_connect();
        PreparedStatement ps = db_connection.prepareStatement(deleteAttSql);
        ps.executeUpdate(deleteAttSql);
        ps.close();
        super.db_closeConnection(db_connection);
    }

    public int modifyAttachment(int aid, String filetype, String filename, double filesize, Blob filecontent) throws SQLException, ClassNotFoundException {
        String modifyAttSql = "UPDATE " + this.db_attachment_table + " SET filetype=?,filename=?,filesize=?,filecontent=?,mtime=NOW() WHERE aid=?)";
        Connection db_connection = super.db_connect();
        PreparedStatement ps = db_connection.prepareStatement(modifyAttSql);
        ps.setString(1, filetype);
        ps.setString(2, filename);
        ps.setDouble(3, filesize);
        ps.setBlob(4, filecontent);
        ps.setInt(5, aid);
        ps.executeUpdate();
        super.db_closeConnection(db_connection);
        return aid;
    }

    public List<Attachment> getAttachment(int aid) throws SQLException, ClassNotFoundException {
        List<Attachment> attachments = new ArrayList<>();
        String queryAttachmentSql = "SELECT pid, filetype, filename, filecontent FROM " + db_attachment_table + " WHERE aid=?";
        Connection db_connection = super.db_connect();
        PreparedStatement ps = db_connection.prepareStatement(queryAttachmentSql);
        ps.setInt(1, aid);
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            int pid = rs.getInt("pid");
            String filetype = rs.getString("filetype");
            String filename = rs.getString("filename");
            Blob fileDataAsBlob = rs.getBlob("filecontent");
            int blobLength = (int) fileDataAsBlob.length();
            byte[] fileData = fileDataAsBlob.getBytes(1, blobLength);
            double filesize = fileDataAsBlob.length();
            Attachment attachment = new Attachment(filetype, filename, filesize, fileData);
            attachment.setPid(pid);
            attachments.add(attachment);
            fileDataAsBlob.free();
        }
        return attachments;
    }
}
