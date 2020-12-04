package soen387.chatroom.utils;

import soen387.chatroom.model.Attachment;
import soen387.chatroom.model.Group;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {
    private static final String jsonUsrKey = "db_user_local_path";
    private static final String jsonGroupKey = "db_group_local_path_load";
    private static final String adminGroupNameKey = "admin_group_name";
    private static final String userManagerClassNameKey = "user_manager_class_path";

    public static Properties getPropertiesFromClasspath(String propFileName, HttpServlet instanceOfServlet) throws IOException
    {
        Properties props = new Properties();
        InputStream inputStream = instanceOfServlet.getClass().getClassLoader().getResourceAsStream(propFileName);
        if (inputStream == null) { throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath"); }
        props.load(inputStream);
        return props;
    }

    public static String getUsrLocalJsonPath(String propFileName, HttpServlet instanceOfServlet) throws IOException {
        Properties props = Utils.getPropertiesFromClasspath(propFileName, instanceOfServlet);
        return props.getProperty(jsonUsrKey);
    }

    public static String getGroupLocalJsonPath(String propFileName, HttpServlet instanceOfServlet) throws IOException {
        Properties props = Utils.getPropertiesFromClasspath(propFileName, instanceOfServlet);
        return props.getProperty(jsonGroupKey);
    }

    public static List<Group> getGroups(String propFileName, HttpServlet instanceOfServlet) throws IOException {
        String group_local_file_path = Utils.getGroupLocalJsonPath(propFileName, instanceOfServlet);
        String jsonGroupLocalFileFullPath = instanceOfServlet.getServletContext().getRealPath(group_local_file_path);
        List<Group> groups = Group.deserializationFromJson(jsonGroupLocalFileFullPath);
        return groups;
    }

    public static List<String> getGroupNameList (String propFileName, HttpServlet instanceOfServlet, String curUsrGroupName) throws IOException {
        List<Group> groups = Utils.getGroups(propFileName, instanceOfServlet);
        List<String> curGroupNames = new ArrayList<>();
        if(curUsrGroupName.equals("admin")){
            groups.forEach(group->{curGroupNames.add(group.getName());});
        }
        else {
            List<Group> curGroup = groups.stream().filter(group->group.getName().equals(curUsrGroupName)).collect(Collectors.toList());
            curGroup.stream().forEach(group->{
                curGroupNames.add(group.getName());
                if(group.getSubGroups().size()>0){ group.getSubGroups().stream().forEach(subGroup->{curGroupNames.add(subGroup);});}
            });
        }
        return curGroupNames;
    }

    public static String getAdminGroupNameKey(String propFileName, HttpServlet instanceOfServlet) throws IOException {
        Properties props = Utils.getPropertiesFromClasspath(propFileName, instanceOfServlet);
        return props.getProperty(adminGroupNameKey);
    }

    public static String getUserManagerClassNameKey(String propFileName, HttpServlet instanceOfServlet) throws IOException {
        Properties props = Utils.getPropertiesFromClasspath(propFileName, instanceOfServlet);
        return props.getProperty(userManagerClassNameKey);
    }

    public static List<Attachment> extractAttachment(HttpServletRequest request) throws IOException, ServletException {
        List<Attachment> attachments = new ArrayList<>();
        for (Part part : request.getParts()) {
            Map<String, String> fileInfo = extractFileInfo(part);

            if (fileInfo.get("filename") != null && fileInfo.get("filename").length() > 0 && fileInfo.get("filetype").length()>0) {
                // File data
                InputStream input = part.getInputStream();
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[10240];
                for (int length = 0; (length = input.read(buffer)) > 0;) output.write(buffer, 0, length);
                byte[] filedata = output.toByteArray();
                attachments.add(new Attachment(fileInfo.get("filetype"), fileInfo.get("filename"), part.getSize(),filedata));
            }
        }
        return attachments;
    }

    private static Map<String, String> extractFileInfo(Part part) {
        // form-data; name="file"; filename="C:\file1.zip"
        // form-data; name="file"; filename="C:\Note\file2.zip"
        Map<String, String> fileInfo = new HashMap<>();
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                // C:\file1.zip
                // C:\Note\file2.zip
                String clientFileName = s.substring(s.indexOf("=") + 2, s.length() - 1);
                clientFileName = clientFileName.replace("\\", "/");
                int i = clientFileName.lastIndexOf('/');
                // file1.zip
                // file2.zip
                String filename =  clientFileName.substring(i + 1);
                int j = clientFileName.lastIndexOf('.');
                String filetype =  clientFileName.substring(j + 1);
                fileInfo.put("filename", filename);
                fileInfo.put("filetype", filetype);
            }
        }
        return fileInfo;
    }

    public static List<String> parseForHashTag(String msg) {
        List<String> hashTags = new ArrayList<>();
        String REGEX = "(?:(?<=\\s)|^)#(\\w*[A-Za-z_]+\\w*)";
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(msg);
        while(matcher.find()){
            hashTags.add(matcher.group(1));
        }
        return hashTags;
    }

    public static Map<String, Integer> getListPageInfo(int current_page, int perPageNumber, int totalPostNum){
        Map<String, Integer> postPageInfo = new HashMap<>();
        int iPageLast = 1;
        int iPageNext = 1;
        int iPageCount = 1;
        int currentPage = 1;

        if (totalPostNum!=0) {
            iPageCount = totalPostNum / perPageNumber;
            if ((totalPostNum % perPageNumber)!=0){
                iPageCount++;
            }
        }
        if(current_page==1){
            currentPage = 1;
            iPageNext = 2;
        }
        else if(current_page>=iPageCount){
            currentPage = iPageNext = iPageCount;
            iPageLast = iPageCount - 1;
        }
        else {
            currentPage = current_page;
            iPageNext = currentPage + 1;
            iPageLast = currentPage - 1;
        }
        int offSet = currentPage*perPageNumber - perPageNumber;
        postPageInfo.put("iPageCurrent",currentPage);
        postPageInfo.put("iPageNext",iPageNext);
        postPageInfo.put("iPageLast",iPageLast);
        postPageInfo.put("iPageCount",iPageCount);
        postPageInfo.put("offset",offSet);
        postPageInfo.put("limit",perPageNumber);

        return postPageInfo;
    }
}
