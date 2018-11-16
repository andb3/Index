package com.andb.apps.todo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TagLinks {
    @SerializedName("tagPosition")
    private int tagPosition;

    @SerializedName("links")
    private ArrayList<Integer> links;

    public TagLinks (int parent, ArrayList<Integer> links){
        this.tagPosition = parent;
        this.links = links;
    }

    public int tagParent(){
        return tagPosition;
    }

    public void addLink(int pos){
        links.add(pos);
    }

    public int getTagLink (int linkPos){
        return links.get(linkPos);
    }

    public void removeTagLink(int pos) {
        links.remove((Integer) pos);
    }

    public ArrayList<Integer> getAllTagLinks(){
        return links;
    }

    public boolean isTagLinked(){
        if(links.isEmpty()){
            return false;
        }else {
            return true;
        }
    }

    public int getLinkPosition(int pos){
        return links.indexOf(pos);
    }

    public boolean contains(int pos){
        if(links.contains(pos)){
            return true;
        }else {
            return false;
        }
    }
}
