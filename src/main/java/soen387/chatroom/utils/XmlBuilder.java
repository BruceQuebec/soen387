package soen387.chatroom.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class XmlBuilder {
    private List<String> tags;
    private List<XmlBuilder> children;
    private int parentIndentLevel;
    private String text;

    public XmlBuilder(){
        this.tags = new ArrayList<String>();
        this.children = new ArrayList<XmlBuilder>();
        text = "";
        parentIndentLevel = 0;
    }

//    public XmlBuilder(int parentIndentLevel){
//        this.tags = new ArrayList<String>();
//        this.children = new ArrayList<XmlBuilder>();
//        text = "";
//        parentIndentLevel = parentIndentLevel;
//    }

    public XmlBuilder addTag(String tag){
        this.tags.add(tag);
        return this;
    }

    public XmlBuilder addChild(XmlBuilder xmlBuilder){
        //xmlBuilder.setParentIndentLevel(xmlBuilder.getParentIndentLevel()+this.tags.size());
        //System.out.println("the current child parent level is: " + xmlBuilder.getParentIndentLevel());
        this.children.add(xmlBuilder);
        //this.children.get(this.children.size()-1).setParentIndentLevel(this.parentIndentLevel+this.tags.size());

        return this;
    }

    public XmlBuilder setText(String text){
        this.text = text;
        return this;
    }

    public void setParentIndentLevel(int parentIndentLevel) {
        this.parentIndentLevel = parentIndentLevel;
    }

    public int getParentIndentLevel(){
        return this.parentIndentLevel;
    }

    public String build(boolean ifAddHead, int parentLevel){
        String str = "";
        String indentStr = "";
        int parentLevelCount = parentLevel;
        for(int i=0; i<this.tags.size(); i++){
            indentStr = "";
            for(int j=0; j<i+parentLevel; j++) {
                indentStr += "\t";
            }
            str += indentStr + "<" + this.tags.get(i) + ">\n";
            parentLevelCount++;
        }
        if(this.children.size()>0){
            for(int i=0; i< this.children.size(); i++){
                str += this.children.get(i).build(false, parentLevelCount) + "\n";
            }
        }
        else{
            str += indentStr + "\t" + this.text + "\n";
        }
        for(int i=this.tags.size()-1; i>=0; i--){
            indentStr = "";
            for(int j=0; j<i+parentLevel; j++) {
                indentStr += "\t";
            }
            str += indentStr + "</" + this.tags.get(i) + ">";
            if(i!=0) str += "\n";
        }
        if (ifAddHead) str = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" + str;
        return str;
    }
    public List<String> buildToList(){
        return Arrays.stream(this.build(true, 0).split("\n")).collect(Collectors.toList());
    }
}
