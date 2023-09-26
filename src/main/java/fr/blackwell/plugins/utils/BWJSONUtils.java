package fr.blackwell.plugins.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.blackwell.plugins.BlackwellPlugins;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.*;
import java.net.URL;

public class BWJSONUtils {

    /**
     * Crée un fichier Json après avoir vérifier si ce dernier existe déjà ou non
     *
     * @param file Objet File
     */
    @SideOnly(Side.SERVER)
    public static void createNewJsonServerSide(File file) {

        file.getParentFile().mkdirs();

        if (!file.exists()) {

            try {
                file.createNewFile();

                BlackwellPlugins.logger.info("File " + file.getName() + " created at the location : " + file.getPath());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void writeJsonRootObject(File file, JsonObject rootJsonObject) {

        try (FileWriter fileWriter = new FileWriter(file)) {
            //We can write any JSONArray or JSONObject instance to the file
            fileWriter.write(rootJsonObject.toString());
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode permettant de récupérer la table à la racine d'un fichier JSON
     *
     * @param name     Nom du fichier sans l'extension
     * @param location Chemin vers le fichier en partant de la racine du projet
     * @return La table à la racine d'un fichier JSON
     */
    public static JsonArray getJsonArrayFromFile(String name, String location) {

        JsonParser parser = new JsonParser();
        File file = new File(location + "/" + name + ".json");

        if (file.exists()) {

            try (FileReader reader = new FileReader(file)) {

                JsonArray jsonArray = (JsonArray) parser.parse(reader);

                return jsonArray;

            } catch (IOException e) {
                BlackwellPlugins.logger.warn("Impossible de lire le fichier : " + file.getName());
                throw new RuntimeException(e);
            }
        } else {
            BlackwellPlugins.logger.warn("Fichier " + file.getName() + " introuvable");
            return null;
        }
    }

    /**
     * Méthode permettant de récupérer la table à la racine d'un fichier JSON
     *
     * @param file fichier Json
     * @return La table à la racine d'un fichier JSON
     */
    public static JsonArray getJsonArrayFromFile(File file) {

        if (file.exists()) {

            JsonParser parser = new JsonParser();

            try (FileReader reader = new FileReader(file)) {

                JsonElement element = parser.parse(reader);

                if (element.isJsonArray())
                    return element.getAsJsonArray();
                else
                    return new JsonArray();

            } catch (IOException e) {
                BlackwellPlugins.logger.warn("Impossible de lire le fichier : " + file.getName() + "\n Vérifiez qu'il s'agit bien d'un fichier JSON");
                throw new RuntimeException(e);
            }
        } else return null;
    }

    public static JsonObject getJsonRootObject(File file) {

        if (file.exists()) {

            JsonParser parser = new JsonParser();

            try (FileReader reader = new FileReader(file)) {

                JsonElement element = parser.parse(reader);

                if (element.isJsonObject()) {

                    return element.getAsJsonObject();
                } else {

                    return new JsonObject();
                }

            } catch (IOException e) {
                BlackwellPlugins.logger.warn("Impossible de lire le fichier : " + file.getName() + "\n Vérifiez qu'il s'agit bien d'un fichier JSON");
                throw new RuntimeException(e);
            }
        } else return null;
    }

    public static JsonObject getPlayerBlackWellDataFromWebsite(String username) {
        String urlContents = "";

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://custom.blackwell-university.fr/skins/" +
                    username + "/details.json").openStream()));
            String line;

            while ((line = in.readLine()) != null) {
                urlContents += line;
            }

            in.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (urlContents.isEmpty()) {
            urlContents = "{}";
        }

        return new JsonParser().parse(urlContents).getAsJsonObject();
    }
}
