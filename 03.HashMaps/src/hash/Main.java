package hash;

import java.util.Iterator;

/**
 * Created by Home on 21.03.2016.
 */
public class Main {
    public static void main(String[] args) {
        Dictionary<Integer> dictionary = new Dictionary<>();
        for (int i = 1; i <= 50; i++) dictionary.put("str" + i, i);
        dictionary.put("str12", 112);
        dictionary.remove("strTwo");
        dictionary.remove("str13");
        for (Iterator<String> i = dictionary.keys(); i.hasNext(); ) {
            String key = i.next();
            System.out.print(key + ", ");
        }
        System.out.println();
        System.out.println(dictionary.get("str38"));
        System.out.println(dictionary.get("str178"));
        for (Integer i : dictionary) {
            System.out.print(i + ", ");
        }
        System.out.println();
    }
}
