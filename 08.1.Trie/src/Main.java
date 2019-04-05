public class Main {
    /**
     * Тестовая функция
     */
    private static void testTrie() {
        Trie<String> t = new Trie<>();
        System.out.println(t.put("bad", "редиска"));
        System.out.println(t.put("bat", "мышь летучая"));
        System.out.println(t.put("rat", "крыса"));
        System.out.println(t.put("red", "красный"));
        System.out.println(t.put("bag", "мешок"));
        System.out.println(t.put("battle", "битва"));
        System.out.println(t.put("bad", "плохой"));
        System.out.println();

        System.out.println(t.get("bad"));
        System.out.println(t.get("doodle"));
        System.out.println();

        System.out.println(t.remove("bat"));
        System.out.println(t.remove("battle"));
        System.out.println(t.remove("bit"));
        System.out.println(t.put("bit", "бит"));
        System.out.println();

        System.out.println(t.entryList());
    }

    public static void main(String[] args) {
        testTrie();
    }
}
