package fr.grousset.fastsnail

/**
 * Android utilities class.
 * @author gilles.grousset@backelite.com
 */
public class AndroidUtils {

    /**
     * Converts a member property name to a classical name.
     * Example : mMyText, becomes myText
     * @param memberName Name to convert
     * @return Classical vairable name
     */
    public static String extractNameFromMember(String name) {

        String result =  name

        if (name.length() > 1 && name.startsWith('m') && Character.isUpperCase(name.charAt(1))) {
            String noPrefix = name - 'm'
            result = noPrefix[0].toLowerCase() + noPrefix.substring(1)
        }

        return result
    }
}