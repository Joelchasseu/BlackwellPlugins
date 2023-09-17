package fr.blackwell.plugins.permission;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BWPlayer {


    private JsonArray group;
    private JsonObject profile;
    private List<String> permList;
    private String firstName;
    private String lastName;
    private String username;
    private boolean isStaff = false;
    private boolean isVanished = false;

    public BWPlayer(String username, JsonObject profile) {

        this.group = profile.get("group").getAsJsonArray();
        this.profile = profile;
        this.initializePermList();
        this.username = username;
        this.firstName = profile.get("firstname").getAsString();
        this.lastName = profile.get("lastname").getAsString();
    }

    public JsonArray getGroup() {
        return this.group;
    }

    public JsonObject getProfile() {
        this.profile.add("group", group);
        return this.profile;
    }

    public void initializePermList() {

        String groupName;
        HashMap<String, Group> groupMap = BWPermissionManagement.getGroupMap();
        String[] groupPermList;
        this.permList = new ArrayList<String>();

        for (int i = 0; i < this.group.size(); i++) {

            groupName = this.group.get(i).getAsString();

            if (groupMap.containsKey(groupName)) {
                groupPermList = groupMap.get(groupName).getGlobalPermList().toArray(new String[0]);

                if (groupMap.get(groupName).isStaff()) {
                    this.isStaff = true;
                }


                for (int j = 0; j < groupPermList.length; j++) {
                    if (!this.permList.contains(groupPermList[j])) this.permList.add(groupPermList[j]);
                }
            }
        }
    }

    public List<String> getPermList() {
        return this.permList;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getRoleplayName(){ return this.firstName + " " + this.lastName;}

    public boolean isStaff() {
        return this.isStaff;
    }
    public boolean isVanished() {
        return this.isVanished;
    }
    public void setVanished(boolean bool){ this.isVanished = bool;}
}
