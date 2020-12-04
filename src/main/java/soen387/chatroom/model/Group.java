package soen387.chatroom.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Group {
    String name;
    String type;
    String parent;
    List<String> subGroups;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public List<String> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(List<String> subGroups) {
        this.subGroups = subGroups;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public Group(String name, String type, String parent){
        this.name = name;
        this.type = type;
        this.parent = parent;
        this.subGroups = new ArrayList<>();
    }

    public Group(String name, String type){
        this.name = name;
        this.type = type;
        this.parent = "";
        this.subGroups = new ArrayList<>();
    }

    public void addSubGroup(String subGroupName){
        for(int i=0; i<this.subGroups.size(); i++){
            if(this.subGroups.get(i).equals(subGroupName)) return;
        }
        this.subGroups.add(subGroupName);
    }

    public static void serializationToJson(List<Group> groups, String path) throws IOException {
        System.out.println("path is " + path);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String groupInJson = gson.toJson(groups);
        File file = new File(path);
        if(!file.exists()){
            file.createNewFile();
        }
        FileWriter fw= new FileWriter(file.getAbsoluteFile(),true);
        //BufferedWriter writer give better performance
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(groupInJson);
        //Closing BufferedWriter Stream
        bw.close();
    }

    public static List<Group> deserializationFromJson(String path) throws FileNotFoundException {
        final Type GROUP_TYPE = new TypeToken<List<Group>>() {}.getType();
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(path));
        List<Group> data = gson.fromJson(reader, GROUP_TYPE);
        return data;
    }
}
