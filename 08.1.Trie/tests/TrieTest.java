import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {
    Trie<String> t;

    @BeforeEach
    void setUp() {
        t = new Trie<>();
        t.put("bad", "редиска");
        t.put("bat", "мышь летучая");
        t.put("rat", "крыса");
        t.put("red", "красный");
        t.put("bag", "мешок");
        t.put("battle", "битва");
        t.put("bad", "плохой");
    }

    @Test
    void get() {
        assertEquals(Optional.of("мешок"), t.get("bag"));
        assertEquals(Optional.of("плохой"), t.get("bad"));
        assertEquals(Optional.empty(), t.get("ban"));
    }

    @Test
    void put() {
        assertEquals(Optional.of("мешок"), t.put("bag", "рюкзак"));
        assertEquals(Optional.empty(), t.put("ban", "запрет"));
    }

    @Test
    void remove() {
        assertEquals(Optional.of("мышь летучая"), t.remove("bat"));
        assertEquals(Optional.of("битва"), t.remove("battle"));
        assertEquals(Optional.empty(), t.remove("bat"));
        assertEquals(Optional.empty(), t.remove("bit"));
    }

    @Test
    void entryList() {
        List<Map.Entry<String, String>> entries = t.entryList();
        List<Map.Entry<String, String>> expected = Arrays.asList(
                new AbstractMap.SimpleEntry<String, String>("bad", "плохой"),
                new AbstractMap.SimpleEntry<String, String>("bag", "мешок"),
                new AbstractMap.SimpleEntry<String, String>("bat", "мышь летучая"),
                new AbstractMap.SimpleEntry<String, String>("battle", "битва"),
                new AbstractMap.SimpleEntry<String, String>("rat", "крыса"),
                new AbstractMap.SimpleEntry<String, String>("red", "красный")
        );
        assertEquals(6, entries.size());
        for (int i = 0; i < entries.size(); i++)
        assertEquals(entries.get(i), expected.get(i));
    }
}