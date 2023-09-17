package fr.blackwell.plugins.permission;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.utils.BWJSONUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class BWPlayerProfileManagement {

    public static HashMap<String, BWPlayer> PLAYER_MAP = new HashMap<>();

    public static void createPlayersJSON(File file) {

        if (!file.exists()) {
            BWJSONUtils.createNewJson(file);
            JsonArray root = new JsonArray();
            JsonObject rootObject = new JsonObject();
            rootObject.add("index", new JsonObject());
            root.add(rootObject);


            try (FileWriter fileWriter = new FileWriter(file)) {
                //We can write any JSONArray or JSONObject instance to the file
                fileWriter.write(root.toString());
                fileWriter.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getDefaultPermGroup() {

        JsonArray permFileArray = BWJSONUtils.getJsonArrayFromFile(BlackwellPlugins.PERM_FILE);
        String defaultGroup = "Error";
        for (int i = 0; i < permFileArray.size(); i++) {
            JsonObject group = permFileArray.get(i).getAsJsonObject();
            if (group.has("default") && group.get("default").getAsBoolean())
                defaultGroup = group.get("name").getAsString();
        }

        return defaultGroup;
    }

    public static void writePlayerBWProfile(JsonObject profile, String username) {

        JsonArray permArray = new JsonArray();

        //Détermine le group par défaut
        JsonArray permFileArray = BWJSONUtils.getJsonArrayFromFile(BlackwellPlugins.PERM_FILE);
        String defaultGroup = "";
        for (int i = 0; i < permFileArray.size(); i++) {
            JsonObject group = permFileArray.get(i).getAsJsonObject();
            if (group.has("default") && group.get("default").getAsBoolean())
                defaultGroup = group.get("name").getAsString();
        }

        permArray.add(defaultGroup);
        profile.add("group", permArray);

        JsonObject index = BWJSONUtils.getJsonRootObject(BlackwellPlugins.PLAYERS_FILE);
        index.add(username, profile);

        try (FileWriter fileWriter = new FileWriter(BlackwellPlugins.PLAYERS_FILE)) {
            //We can write any JSONArray or JSONObject instance to the file
            fileWriter.write(index.toString());
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JsonObject getPlayerProfileFromFile(String username) {

        JsonObject index = BWJSONUtils.getJsonRootObject(BlackwellPlugins.PLAYERS_FILE);

        if (index.has(username)) {
            JsonObject profile = index.get(username).getAsJsonObject();

            if (profile.has("group"))
                return profile;
            else {
                JsonArray permArray = new JsonArray();
                permArray.add(getDefaultPermGroup());
                profile.add("group", permArray);
                return profile;
            }
        } else {
            BlackwellPlugins.logger.warn("Profil du joueur " + username + "introuvable");
            JsonObject profile = BWJSONUtils.getPlayerBlackWellDataFromWebsite(username);
            BWPlayerProfileManagement.writePlayerBWProfile(profile, username);
            return profile;
        }
    }

    public static void loadPlayerData(String username) {

        PLAYER_MAP.put(username, new BWPlayer(username, getPlayerProfileFromFile(username)));
    }

    public static void savePlayerData(String username) {
        JsonObject profile = PLAYER_MAP.get(username).getProfile();
        PLAYER_MAP.remove(username);

        JsonObject index = BWJSONUtils.getJsonRootObject(BlackwellPlugins.PLAYERS_FILE);
        index.add(username, profile);

        try (FileWriter fileWriter = new FileWriter(BlackwellPlugins.PLAYERS_FILE)) {
            //We can write any JSONArray or JSONObject instance to the file
            fileWriter.write(index.toString());
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addGroupToPlayer(String username, String groupToAdd) {

        JsonObject index = BWJSONUtils.getJsonRootObject(BlackwellPlugins.PLAYERS_FILE);
        if (index.has(username)) {
            JsonObject profile = index.get(username).getAsJsonObject();
            JsonArray group = profile.get("group").getAsJsonArray();

            boolean alreadyAssigned = false;
            for (int i = 0; i < group.size(); i++)
                if (groupToAdd.equals(group.get(i).getAsString())) alreadyAssigned = true;

            if (!alreadyAssigned) {
                group.add(groupToAdd);
                profile.add("group", group);
                index.add(username, profile);

                try (FileWriter fileWriter = new FileWriter(BlackwellPlugins.PLAYERS_FILE)) {
                    //We can write any JSONArray or JSONObject instance to the file
                    fileWriter.write(index.toString());
                    fileWriter.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (PLAYER_MAP.containsKey(username)) {
                    loadPlayerData(username);
                }
            }
        }
    }

    public static void removeGroupToPlayer(String username, String groupToRemove) {

        JsonObject index = BWJSONUtils.getJsonRootObject(BlackwellPlugins.PLAYERS_FILE);
        if (index.has(username)) {
            JsonObject profile = index.get(username).getAsJsonObject();
            JsonArray group = profile.get("group").getAsJsonArray();

            for (int i = 0; i < group.size(); i++)
                if (group.get(i).getAsString().equals(groupToRemove)) group.remove(i);

            profile.add("group", group);
            index.add(username, profile);

            try (FileWriter fileWriter = new FileWriter(BlackwellPlugins.PLAYERS_FILE)) {
                //We can write any JSONArray or JSONObject instance to the file
                fileWriter.write(index.toString());
                fileWriter.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (PLAYER_MAP.containsKey(username)) {
                loadPlayerData(username);
            }
        }
    }

}
