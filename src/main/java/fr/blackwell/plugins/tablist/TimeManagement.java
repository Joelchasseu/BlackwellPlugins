package fr.blackwell.plugins.tablist;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.blackwell.plugins.BlackwellPlugins;
import fr.blackwell.plugins.utils.BWJSONUtils;
import scala.tools.nsc.settings.Final;

public class TimeManagement {

    public static JsonObject timeData;

    public static final String[] moisArray = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};

    public static void InitiateTime() {
        JsonObject index;
        if (!BlackwellPlugins.TIME_FILE.exists()) {

            index = new JsonObject();
            int moisNum = 1;
            String mois = moisArray[moisNum - 1];
            index.addProperty("mois", mois);
            index.addProperty("annee", "2021");
            index.addProperty("saison", "Hiver");

            timeData = index;

            BWJSONUtils.writeJsonRootObject(BlackwellPlugins.TIME_FILE, index);
        } else {

            index = BWJSONUtils.getJsonRootObject(BlackwellPlugins.TIME_FILE);

            JsonElement moisElement = index.get("mois");
            String mois;
            if (moisElement.getAsString().matches("[0-9]") || moisElement.getAsString().matches("[0-1][0-2]"))
                mois = moisArray[moisElement.getAsInt() - 1];
            else
                mois = moisElement.getAsString();

            index.addProperty("mois", mois);

            String saison = "";
            if (mois.equals("Janvier") || mois.equals("Février") || mois.equals("Mars"))
                saison = "Hiver";
            else if (mois.equals("Avril") || mois.equals("Mai") || mois.equals("Juin"))
                saison = "Printemps";
            else if (mois.equals("Juillet") || mois.equals("Août") || mois.equals("Septembre"))
                saison = "Eté";
            else if (mois.equals("Octobre") || mois.equals("Novembre") || mois.equals("Décembre"))
                saison = "Automne";

            index.addProperty("saison", saison);

            timeData = index;

            BWJSONUtils.writeJsonRootObject(BlackwellPlugins.TIME_FILE, index);
        }
    }
}
