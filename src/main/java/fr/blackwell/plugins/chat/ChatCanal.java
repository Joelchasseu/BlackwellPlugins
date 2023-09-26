package fr.blackwell.plugins.chat;

import com.google.gson.JsonObject;
import fr.blackwell.plugins.BlackwellPlugins;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public class ChatCanal {

    private final Style style;
    private final String name;
    private final int range;
    private final String prefix;
    private final String structureType;
    private final String chatPrefix;

    public ChatCanal(JsonObject canal) {

        this.name = canal.get("name").getAsString();
        this.range = canal.get("range").getAsInt();
        this.prefix = canal.get("prefix").getAsString();
        this.structureType = canal.get("structure").getAsString();
        this.chatPrefix = canal.get("chatPrefix").getAsString();
        if (canal.get("color").getAsInt() >= 0 && canal.get("color").getAsInt() <= 15)
            this.style = new Style().setColor(TextFormatting.fromColorIndex(canal.get("color").getAsInt())).setBold(canal.get("bold").getAsBoolean()).setItalic(canal.get("italic").getAsBoolean());
        else{
            this.style = new Style().setColor(TextFormatting.WHITE).setBold(canal.get("bold").getAsBoolean()).setItalic(canal.get("italic").getAsBoolean());
            BlackwellPlugins.logger.warn("Le canal " + this.name + "ne peut avoir pour couleur l'index " + TextFormatting.fromColorIndex(canal.get("color").getAsInt()) + ", veuillez sélectionner une valeur entre 0 et 15");
        }
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getName() {
        return this.name;
    }

    public int getRange() {
        return this.range;
    }

    public Style getStyle() {
        return this.style;
    }

    public String getStructureType() {
        return this.structureType;
    }

    public String getChatPrefix() {
        return this.chatPrefix;
    }
}
