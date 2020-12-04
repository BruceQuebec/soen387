package soen387.chatroom.persistence;

import javafx.util.Pair;
import soen387.chatroom.model.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO extends DAO {
    protected String db_post_table;
    protected String db_ptr_table;

    public String getDb_post_table() {
        return db_post_table;
    }
    public String getDb_ptr_table() {
        return db_ptr_table;
    }

    public PostDAO(String db_url, String db_driver, String db_root, String db_password, String db_post_table, String db_ptr_table){
        super(db_url, db_driver, db_root, db_password);
        this.db_post_table = db_post_table;
        this.db_ptr_table = db_ptr_table;
    }

    public int addNewPost(int uid, String username, String title, String content, String groupToSee) throws SQLException, ClassNotFoundException {
        String addNewPostSql = "INSERT INTO " + db_post_table + " (pid,title,content,uid,username,groupToSee,ctime,mtime)"
                + " values (?,?,?,?,?,?,NOW(),NOW())";
        Connection db_connection = super.db_connect();
        PreparedStatement ps = db_connection.prepareStatement(addNewPostSql);
        int pid = super.getMaxTableId(db_connection, this.db_post_table, "pid") + 1;
        ps.setInt(1, pid);
        ps.setString(2, title);
        ps.setString(3,content);
        ps.setInt(4,uid);
        ps.setString(5,username);
        ps.setString(6, groupToSee);
        ps.executeUpdate();
        super.db_closeConnection(db_connection);
        return pid;
    }

    public int addPostTagRelation(int pid, int tid) throws SQLException, ClassNotFoundException {
        Connection db_connection = super.db_connect();
        String queryExistingPtrSql = "SELECT COUNT(*) AS rowcount FROM " + this.db_ptr_table + " WHERE pid=" + pid + " AND tid=" + tid + "";
        PreparedStatement ps = db_connection.prepareStatement(queryExistingPtrSql);
        ResultSet rs = ps.executeQuery(queryExistingPtrSql);
        rs.next();
        int count = rs.getInt("rowcount");
        int ptrid = 0;
        if(count==0){
            String addNewPtrSql = "INSERT INTO " + this.db_ptr_table + " (ptrid, pid, tid) VALUES (?,?,?)";
            ptrid = super.getMaxTableId(db_connection, this.db_ptr_table, "ptrid") + 1;
            ps = db_connection.prepareStatement(addNewPtrSql);
            ps.setInt(1, ptrid);
            ps.setInt(2,pid);
            ps.setInt(3,tid);
            ps.executeUpdate();
        }
        rs.close();
        super.db_closeConnection(db_connection);
        return ptrid;
    }

    public List<Pair<Post, List<Pair<Integer, String>>>> findPosts(int offset, int limit) throws SQLException, ClassNotFoundException {
        String listPostsSql = "SELECT * FROM " + db_post_table + " ORDER BY ctime DESC LIMIT " + limit + " OFFSET " + offset + "";
        List<Pair<Post, List<Pair<Integer, String>>>> posts = queryPost(listPostsSql);
        return posts;
    }

    public void deletePost(int pid) throws SQLException, ClassNotFoundException {
        deletePTROfPost(pid);
        String deletePostsSql = "DELETE FROM " + db_post_table + " WHERE pid=" + pid + "";
        Connection db_connection = super.db_connect();
        PreparedStatement ps = db_connection.prepareStatement(deletePostsSql);
        ps.executeUpdate(deletePostsSql);
        ps.close();
        super.db_closeConnection(db_connection);
    }

    public void deletePTROfPost(int pid) throws SQLException, ClassNotFoundException {
        String queryExistingTagSql = "DELETE FROM " + this.db_ptr_table + " WHERE pid=" + pid +"";
        Connection db_connection = super.db_connect();
        PreparedStatement ps = db_connection.prepareStatement(queryExistingTagSql);
        ps.executeUpdate(queryExistingTagSql);
        ps.close();
        super.db_closeConnection(db_connection);
    }

    public int modifyPost(int pid, String title, String content, String groupToSee) throws SQLException, ClassNotFoundException {
        String modifyPostSql = "UPDATE " + db_post_table + " SET title=?,content=?,groupToSee=?,mtime=NOW() WHERE pid=" + pid + "";
        Connection db_connection = super.db_connect();
        PreparedStatement ps = db_connection.prepareStatement(modifyPostSql);
        ps.setString(1, title);
        ps.setString(2, content);
        ps.setString(3, groupToSee);
//        ps.setInt(3,pid);
        //System.out.println("modifyPostSql: " + modifyPostSql);
        ps.executeUpdate();

        super.db_closeConnection(db_connection);
        return pid;
    }

    public int findTotalNumPost() throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) AS total FROM " + db_post_table + "";
        return getPostNum(sql, "total");
    }

    public List<Pair<Post, List<Pair<Integer, String>>>> findByPid(int pid) throws SQLException, ClassNotFoundException {
        String queryPostUidSql = "SELECT * FROM " + db_post_table + " WHERE pid=" + pid + "";
        List<Pair<Post, List<Pair<Integer, String>>>> posts = queryPost(queryPostUidSql);
        return posts;
    }

    public int findTotalNumByUid(int uid) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) AS total FROM " + db_post_table + " WHERE uid=" + uid + "";
        return getPostNum(sql, "total");
    }

    public List<Pair<Post, List<Pair<Integer, String>>>> findByUid(int uid, int offset, int limit) throws SQLException, ClassNotFoundException {
        String queryPostUidSql = "SELECT * FROM " + db_post_table + " WHERE uid=" + uid + " ORDER BY ctime DESC LIMIT " + limit + " OFFSET " + offset + "";
        List<Pair<Post, List<Pair<Integer, String>>>> posts = queryPost(queryPostUidSql);
        return posts;
    }

    public int findTotalNumByTime(Time startTime, Time endTime) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) AS total FROM " + db_post_table + " WHERE ctime>=" + startTime + " AND ctime<= " + endTime + "";
        return getPostNum(sql, "total");
    }

    public List<Pair<Post, List<Pair<Integer, String>>>> findByTime(Time startTime, Time endTime, int offset, int limit) throws SQLException, ClassNotFoundException {
        String queryPostTimeSql = "SELECT * FROM " + db_post_table + " WHERE ctime>=" + startTime + " AND ctime<=" + endTime + " ORDER BY ctime DESC LIMIT " + limit + " OFFSET " + offset + "";
        List<Pair<Post, List<Pair<Integer, String>>>> posts = queryPost(queryPostTimeSql);
        return posts;
    }

    public int findTotalNumByHashtag(int tid) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) AS total FROM " + db_post_table + " WHERE pid IN (SELECT pid FROM " + db_ptr_table + " WHERE tid = " + tid + ")";
        return getPostNum(sql, "total");
    }

    public List<Pair<Post, List<Pair<Integer, String>>>> findByHashtag(int tid, int offset, int limit) throws SQLException, ClassNotFoundException {
        String queryPostTagSql = "SELECT * FROM " + db_post_table + " WHERE pid IN (SELECT pid FROM " + db_ptr_table + " WHERE tid = " + tid + ") ORDER BY ctime DESC LIMIT " + limit + " OFFSET " + offset + "";
        List<Pair<Post, List<Pair<Integer, String>>>> posts = queryPost(queryPostTagSql);
        return posts;
    }

    public int getPostNum(String sql, String columnlabel) throws SQLException, ClassNotFoundException {
        Connection db_connection = super.db_connect();
        PreparedStatement ps = db_connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery(sql);
        int postNum = 0;
        if(rs.next()){
            postNum = rs.getInt(columnlabel);
        }
        ps.close();
        super.db_closeConnection(db_connection);
        return postNum;
    }

    private List<Pair<Post, List<Pair<Integer, String>>>> queryPost(String querySel) throws SQLException, ClassNotFoundException {
        List<Pair<Post, List<Pair<Integer, String>>>> posts = new ArrayList<>();
        Connection db_connection = super.db_connect();
        PreparedStatement ps = db_connection.prepareStatement(querySel);
        ResultSet rs = ps.executeQuery(querySel);
        while(rs.next()){
            int pid = rs.getInt(1);
            String title = rs.getString(2);
            String content = rs.getString(3);
            int uid = rs.getInt(4);
            String username = rs.getString(5);
            String groupToSee = rs.getString(6);
            Time ctime = rs.getTime(7);
            Time mtime = rs.getTime(8);


            List<Pair<Integer, String>> attachmentIdList = new ArrayList<>();
            String listAttCheckSql = "SELECT aid, filename FROM attachment WHERE pid=" + pid + "";
            PreparedStatement psAtt = db_connection.prepareStatement(listAttCheckSql);
            ResultSet rsAtt = psAtt.executeQuery(listAttCheckSql);
            while(rsAtt.next()){ attachmentIdList.add(new Pair<Integer, String>(rsAtt.getInt("aid"), rsAtt.getString("filename"))); }
            List<String> hashTags = new ArrayList<>();
            String listTagCheckSql = "SELECT tname FROM tag WHERE tid IN (SELECT tid FROM " + db_ptr_table + " WHERE pid=" + pid + ")";
            PreparedStatement psTag = db_connection.prepareStatement(listAttCheckSql);
            ResultSet rsTag = psTag.executeQuery(listTagCheckSql);
            while(rsTag.next()){ hashTags.add(rsTag.getString("tname")); }
            Post post = new Post(title, content, uid, username, groupToSee);

            post.setPid(pid);
            post.setCreatedTime(ctime);
            post.setLastModifiedTime(mtime);
            post.setHashTags(hashTags);
            posts.add(new Pair<>(post, attachmentIdList));
        }
        rs.close();
        db_connection.close();
        return posts;
    }
}
