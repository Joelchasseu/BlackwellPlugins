package fr.blackwell.plugins.permission;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.utils.BWJSONUtils;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BWPermissionManagement {

    private static HashMap<String, Group> GROUP_MAP = new HashMap<>();
    private static Logger logger = BlackwellPlugins.logger;


    public static void loadPerm() {


        JsonArray rootArray = BWJSONUtils.getJsonArrayFromFile(BlackwellPlugins.PERM_FILE);


        if (rootArray.size() != 0) {
            for (int i = 0; i < rootArray.size(); i++) {

                if (rootArray.get(i).isJsonObject()) {
                    JsonObject roleObject = rootArray.get(i).getAsJsonObject();

                    //Vérifie si l'objet JSONObject contenu dans le fichier de permission a bien un champ name, permission et child
                    if (roleObject.has("name") && roleObject.has("permission") && roleObject.has("child"))
                        registerGroup(new Group(roleObject, rootArray));

                    else
                        logger.warn("Echec du chargement d'un rôle, structure invalide, vérifier que les rôles ont bien un champ \"name\", \"permission\" et \"child\"");

                } else logger.warn("Echec du chargement d'un rôle");

            }

        } else logger.warn("Fichier de permission vide, veuillez vérifier ce dernier");
    }

    public static void savePerm() {

        JsonArray rootArray = new JsonArray();

        Map<String, Group> groupMap = BWPermissionManagement.getGroupMap();
        String[] keyArray = groupMap.keySet().toArray(new String[0]);

        for (int i = 0; i < keyArray.length; i++) {

            Group group = groupMap.get(keyArray[i]);
            JsonObject groupObject = new JsonObject();
            groupObject.addProperty("name", group.getName());
            JsonArray permListFinal = new JsonArray();
            String[] permList = group.getPermListOriginal().toArray(new String[0]);

            for (String s : permList) permListFinal.add(s);
            groupObject.add("permission", permListFinal);

            JsonArray childListFinal = new JsonArray();
            String[] childList = group.getChildList().toArray(new String[0]);

            for (String s : childList) childListFinal.add(s);
            groupObject.add("child", childListFinal);

            groupObject.addProperty("default", group.isDefault());
            groupObject.addProperty("staff", group.isStaff());

            rootArray.add(groupObject);
        }
        try (FileWriter fileWriter = new FileWriter(BlackwellPlugins.PERM_FILE)) {
            //We can write any JSONArray or JSONObject instance to the file
            fileWriter.write(rootArray.toString());
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void registerGroup(Group group) {

        GROUP_MAP.put(group.getName(), group);
        logger.info("Le rôle " + group.getName() + " a bien été chargé");
    }

    public static Group getGroup(String name) {

        if (GROUP_MAP.containsKey(name)) return GROUP_MAP.get(name);
        else {
            logger.warn("Groupe " + name + " introuvable, veuillez vérifié le fichier players.json");
            return null;
        }
    }

    public static Group getGroup(String name, String username) {

        if (GROUP_MAP.containsKey(name)) return GROUP_MAP.get(name);
        else {
            logger.warn("Groupe " + name + "associé au joueur " + username + " introuvable, veuillez vérifié le fichier players.json");
            return null;
        }
    }

    public static int getGroupMapSize() {
        return GROUP_MAP.size();
    }

    public static boolean isGroupName(String name) {
        if (GROUP_MAP.containsKey(name)) return true;
        else return false;
    }

    public static HashMap<String, Group> getGroupMap() {
        return GROUP_MAP;
    }
}
