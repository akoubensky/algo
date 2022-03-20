package ru.unecon.trie;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {

    @Test
    void get() {
        Trie<Integer> trie = new Trie<>();
        assertNull(trie.put("bit", 4));
        assertNull(trie.put("bite", 5));
        assertEquals(4, trie.get("bit"));
        assertEquals(5, trie.get("bite"));
    }

    @Test
    void put() {
        Trie<Integer> trie = new Trie<>();
        assertNull(trie.put("be", 1));
        assertNull(trie.put("add", 2));
        assertNull(trie.put("abba", 3));
        assertNull(trie.put("bit", 4));
        assertNull(trie.put("bite", 8));
        assertEquals(8, trie.put("bite", 5));
    }

    @Test
    void remove() {
        Trie<Integer> trie = new Trie<>();
        assertNull(trie.put("be", 1));
        assertNull(trie.put("add", 2));
        assertNull(trie.put("abba", 3));
        assertNull(trie.put("bit", 4));
        assertNull(trie.put("bite", 5));
        assertEquals(4, trie.remove("bit"));
        assertNull(trie.remove("bit"));
        assertEquals(5, trie.remove("bite"));
        assertEquals(1, trie.remove("be"));
        assertEquals(2, trie.remove("add"));
        assertEquals(3, trie.remove("abba"));
    }
}