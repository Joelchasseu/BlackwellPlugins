package fr.blackwell.plugins.utils;

public class BWUtils {

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
           Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String formatTime(int input) {
        // Transform ticks in input to hours and minutes
        int minutes = (Math.abs((input + 6000) % 24000) % 1000) * 6 / 100;
        int hours =   (int) ((float)Math.abs((input+ 6000) % 24000) / 1000F);

        String hourString = String.valueOf(hours);
        if (hours < 10){
            hourString = "0"+hourString;
        }

        String minutesString = String.valueOf(minutes);
        if (minutes < 10){
            minutesString = "0"+minutesString;
        }

        return hourString + ":" + minutesString;
    }
}
