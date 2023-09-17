package fr.blackwell.plugins.permission;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.blackwell.plugins.BlackwellPlugins;

import java.util.ArrayList;
import java.util.List;

public class Group {

    private final JsonObject group;

    private List<String> permListOriginal;
    private List<String> permChildList;
    private final List<String> childList;
    private final String name;

    private List<String> globalPermList;

    private boolean isDefault = false;
    private boolean isStaff = false;

    public Group(JsonObject group, JsonArray rootArray) {

        this.group = group;
        this.name = group.get("name").getAsString();
        this.childList = new ArrayList<>();
        JsonArray childArray = group.get("child").getAsJsonArray();

        for (int i = 0; i < childArray.size(); i++)
            childList.add(childArray.get(i).getAsString());

        if (group.has("default"))
            this.isDefault = group.get("default").getAsBoolean();

        if(group.has("staff"))
            this.isStaff = group.get("staff").getAsBoolean();

        this.initializePermList();
        this.initializeChildPermList(rootArray);

        globalPermList = new ArrayList<String>();
        globalPermList.addAll(this.permListOriginal);
        globalPermList.addAll(this.permChildList);
    }

    public String getName() {

        return this.name;
    }

    public boolean isDefault() {

        return this.isDefault;
    }

    public boolean isStaff() {

        return this.isStaff;
    }

    public List<String> getGlobalPermList(){

        return this.globalPermList;
    }
    public String[] getPermArray(){
        //String[] y = x.toArray(new String[0]);
        return this.globalPermList.toArray(new String[0]);
    }


    //Charge les permissions associées au groupe
    public void initializePermList() {

        permListOriginal = new ArrayList<>();

        JsonArray extractedPerm = this.group.get("permission").getAsJsonArray();

        String permNode;

        for (int i = 0; i < extractedPerm.size(); i++) {
            permNode = extractedPerm.get(i).getAsString();
            if (!permListOriginal.contains(permNode)) permListOriginal.add(permNode);
        }
    }

    //Charge les permissions associées aux groupes enfant du groupe actuel
    public void initializeChildPermList(JsonArray rootArray) {

        permChildList = new ArrayList<String>();

        for (int i = 0; i < rootArray.size(); i++) {
            if (this.childList.contains(rootArray.get(i).getAsJsonObject().get("name").getAsString())) {

                JsonArray extractedPerm = rootArray.get(i).getAsJsonObject().get("permission").getAsJsonArray();

                String permNode;

                for (int j = 0; j < extractedPerm.size(); j++) {
                    permNode = extractedPerm.get(j).getAsString();
                    if (!permChildList.contains(permNode)) {
                        permChildList.add(permNode);
                        BlackwellPlugins.logger.info("Le groupe " + this.name + " a chargé la permission du groupe " + rootArray.get(i).getAsJsonObject().get("name") + ":" + permNode);
                    }
                }
            }
        }
    }

    public List<String> getPermListOriginal() {
        return permListOriginal;
    }

    public List<String> getChildList(){
        return this.childList;
    }
}
