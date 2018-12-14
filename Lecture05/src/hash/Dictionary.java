package hash;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Реализация простого словаря, содержащего словарные статьи заданного типа.
 * Словарь не поддерживает упорядоченность.
 * Реализация сделана на основе хеширования строк.
 * @param <V> тип словарых статей.
 */
public class Dictionary<V> implements Iterable<V> {
    /**
     * Класс определяет элемент списка. Содержит слово и словарную статью.
     * @param <V>
     */
    private static class Node<V> {
        String key; // ключ - слово
        V value;    // значение - словарная статья
        Node<V> next;   // ссылка на следующий элемент списка

        // Конструктор:
        Node(String key, V value, Node<V> next) {
            this.key = key; this.value = value; this.next = next;
        }
    }

    /**
     * Итератор элементов списков, содержащихся в словаре.
     */
    private class DictIterator implements Iterator<Node<V>> {

        int currentIndex = 0;   // Индекс списка
        Node<V> current = hashTable[0]; // Текущий элемент
        // Сначала устанавливаем указатель на первый элемент первого непустого списка.
        { if (current == null) setNextCurrent(); }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Node<V> next() {
            if (current == null) throw new NoSuchElementException();
            Node<V> oldCurrent = current;
            setNextCurrent();
            return oldCurrent;
        }

        /**
         * Ищет очередной непустой элемент в таблице.
         */
        private void setNextCurrent() {
            if (current != null) current = current.next;
            if (current == null) {
                while (currentIndex < hashSize - 1) {
                    if ((current = hashTable[++currentIndex]) != null) break;
                }
            }
        }
    }

    final static double rate = 0.75;    // Коэффициент заполненности таблицы.
    Node<V>[] hashTable = new Node[10]; // Таблица.
    int hashSize = 10;  // Длина таблицы.
    int dictSize = 0;   // Количество элементов в словаре.

    /**
     * Вычисление хеш-функции по заданной строке
     * @param s Исходная строка.
     * @return  Хеш-код.
     */
    private int hash(String s) {
        int hash = 0;
        for (int i = 0; i < s.length(); i++) {
            hash *= 37;
            hash += s.charAt(i);
        }
        return Math.abs(hash % hashSize);
    }

    /**
     * Поиск значения по ключу.
     * @param word  Ключ
     * @return      Значение, связанное с ключом в случае, если ключ имеется.
     *              Выдает null, если такого ключа нет.
     */
    public V get(String word) {
        if (word == null) return null;
        Node<V> current = hashTable[hash(word)];
        while (current != null) {
            if (current.key.equals(word)) return current.value;
            current = current.next;
        }
        return null;
    }

    /**
     * Заносит новую пару из ключа и значения или заменяет имеющееся значение.
     * @param word  Ключ
     * @param value Новое значение
     * @return      Значение, которое было ранее связано с заданным ключом, или null.
     */
    public V put(String word, V value) {
        if (word == null || value == null) {
            throw new IllegalArgumentException("Null key or value");
        }
        int index = hash(word);
        Node<V> current = hashTable[index];
        while (current != null) {
            if (current.key.equals(word)) {
                V oldValue = current.value;
                current.value = value;
                return oldValue;
            }
            current = current.next;
        }
        hashTable[index] = new Node<V>(word, value, hashTable[index]);
        if (++dictSize * rate > hashSize) rehash();
        return null;
    }

    /**
     * Удаляет из таблицы пару с заданным ключом.
     * @param word  Ключ
     * @return      Значение, которое было связано ранее с удаляемым ключом
     *              или null, если ранее в таблице не было этого ключа.
     */
    public V remove(String word) {
        int index = hash(word);
        Node<V> current = hashTable[index];
        Node<V> pred = null;
        while (current != null) {
            if (current.key.equals(word)) {
                V oldValue = current.value;
                if (pred == null) {
                    hashTable[index] = current.next;
                } else {
                    pred.next = current.next;
                }
                dictSize--;
                return oldValue;
            }
            pred = current;
            current = current.next;
        }
        return null;
    }

    /**
     * Выдает количество слов в словаре
     * @return
     */
    public int size() { return dictSize; }

    /**
     * Проверяет, является ли словарь пустым.
     * @return
     */
    public boolean isEmpty() { return dictSize == 0; }

    /**
     * Выдает итератор всех ключейй в словаре.
     * @return
     */
    public Iterator<String> keys() {
        return new Iterator<String>() {
            DictIterator di = new DictIterator();

            public boolean hasNext() {
                return di.hasNext();
            }

            public String next() {
                return di.next().key;
            }
        };
    }

    /**
     * Выдает итератор всех словарных статей в словаре.
     * @return
     */
    @Override
    public Iterator<V> iterator() {
        return new Iterator<V>() {
            DictIterator di = new DictIterator();

            public boolean hasNext() {
                return di.hasNext();
            }

            public V next() {
                return di.next().value;
            }
        };
    }

    /**
     * Функция перехеширования вызывается каждый раз, когда количество слов в словаре
     * становится критически большим в соответствии с заданным критерием.
     * Размер таблицы увеличивается в два раза.
     */
    private void rehash() {
        Node<V>[] oldTable = hashTable;
        hashTable = new Node[hashSize <<= 1];
        for (int i = 0; i < (hashSize >> 1); i++) {
            Node<V> current = oldTable[i];
            while (current != null) {
                int newIndex = hash(current.key);
                hashTable[newIndex] = new Node<V>(current.key, current.value, hashTable[newIndex]);
                current = current.next;
            }
        }
    }
}
