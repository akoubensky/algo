package ru.unecon.trie;

import java.util.HashMap;
import java.util.Map;

class Trie<V> {
    private class Node {
        V value;
        Map<Character, Node> children = new HashMap<>();
    }

    private class RetPair {
        V retValue;
        Node retNode;

        public RetPair(V retValue, Node retNode) {
            this.retValue = retValue;
            this.retNode = retNode;
        }
    }

    // Корень дерева
    private Node root = new Node();

    /**
     * Поиск по дереву.
     * @param key Ключ
     * @return Ассоциированное с ключом значение или null,
     *         если такого значения нет.
     */
    public V get(String key) {
        Node current = root;
        for (char c : key.toCharArray()) {
            if (current == null) return null;
            current = current.children.get(c);
        }
        return current == null ? null : current.value;
    }

    /**
     * Добавление нового узла в дерево.
     * @param key Ключ
     * @param value Значение
     * @return "Старое" значение узла с заданным ключом,
     *         если оно было в дереве, null, если не было.
     */
    public V put(String key, V value) {
        RetPair ret = put(root, key, value);
        root = ret.retNode;
        return ret.retValue;
    }

    private RetPair put(Node current, String key, V value) {
        if (current == null) {
            Node newNode = new Node();
            return put(newNode, key, value);
        } else if (key.isEmpty()) {
            RetPair ret = new RetPair(current.value, current);
            current.value = value;
            return ret;
        } else {
            char firstChar = key.charAt(0);
            RetPair ret = put(current.children.get(firstChar), key.substring(1), value);
            current.children.put(firstChar, ret.retNode);
            return new RetPair(ret.retValue, current);
        }
    }

    /**
     * Удаление из дерева значения с заданным ключом.
     * Если в результате удаления значения образуется узел
     * с пустым списком потомков, то такой узел тоже
     * должен быть удален.
     * @param key Ключ
     * @return "Старое" значение узла с заданным ключом,
     *         если оно было в дереве, null, если не было.
     */
    public V remove(String key) {
        RetPair ret = remove(root, key);
        root = ret.retNode;
        return ret.retValue;
    }

    private RetPair remove(Node current, String key) {
        if (current == null) {
            return new RetPair(null, null);
        } else if (key.isEmpty()) {
            V retValue = current.value;
            current.value = null;
            return new RetPair(retValue, current.children.isEmpty() ? null : current);
        } else {
            char firstChar = key.charAt(0);
            RetPair retPair = remove(current.children.get(firstChar), key.substring(1));
            if (retPair.retNode == null) {
                current.children.remove(firstChar);
            } else {
                current.children.put(firstChar, retPair.retNode);
            }
            return new RetPair(retPair.retValue,
                    current.children.isEmpty() && current.value == null ? null : current);
        }
    }
}

