import java.util.Arrays;


/**
 * Реализация основной операции красно-черного дерева -
 * операции вставки новой ассоциативной пары <ключ, значение>.
 * В данных алгоритмах красный узел всегда располагается слева от черного, что позволяет
 * считать данное красно-черное дерево частным случаем 2-3-дерева.
 * 
 * (c) http://algs4.cs.princeton.edu/33balanced/RedBlackBST.java.html
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class RedBlackTree<K extends Comparable<K>, V> {
	/**
	 * Цвет узлов дерева
	 */
	private static enum Color {
		RED, BLACK;
	}

	/**
	 * Класс представляет узел дерева. Этот класс предназначен только
	 * для внутренних целей, поэтому он private, и доступ к полям объектов
	 * этого класса осуществляется непосредственно.
	 */
	private class Node {
		// Ссылки на левое и правое поддеревья:
		Node left, right;
		// Ключ:
		K key;
		// Значение:
		V value;
		// Цвет узла:
		Color color;

		/**
		 * Конструктор произвольного узла.
		 * @param key ключ
		 * @param value значение
		 * @param color цвет узла
		 * @param left левое поддерево
		 * @param right правое поддерево
		 */
		Node(K key, V value, Color color, Node left, Node right) {
			this.key = key; this.value = value; this.color = color;
			this.left = left; this.right = right;
		}

		/**
		 * Конструктор листа.
		 * @param key ключ
		 * @param value значение
		 */
		Node(K key, V value) { this(key, value, Color.RED, SENTINEL, SENTINEL); }
	}

	// Корень дерева.
	final Node SENTINEL = new Node(null, null, Color.BLACK, null, null);
	Node root = SENTINEL;

	/**
	 * Поиск в дереве по ключу.
	 * @param key ключ поиска.
	 * @return найденное значение или null, если такого ключа нет в дереве.
	 */
	public V get(K key) {
		// Проверка: ключ поиска не должен быть пустым.
		if (key == null) throw new NullPointerException("null key");

		return get(key, root);
	}
	
	/**
	 * Добавление в дерево новой ассоциативной пары.
	 * @param key	Ключ.
	 * @param value Значение.
	 * @return		Значение, которое было ассоциировано раньше с этим ключом
	 *         (если такое значение было).
	 */
	public V put(K key, V value) {
		// Проверка: ключ поиска не должен быть пустым.
		if (key == null) throw new NullPointerException("null key");

		// Сначала ищем старое значение
		V oldValue = get(key);
		root = put(key, value, root);
		// После вставки корень дерева может оказаться красным - перекрасим его.
		root.color = Color.BLACK;
		return oldValue;
	}
	
	/**
	 * Удаление ассоциативной пары из дерева по заданному ключу.
	 * @param key	Ключ поиска (не равен null)
	 * @return		Удаленное значение (или null, если такого ключа не было в дереве)
	 */
	public V remove(K key) {
		// Проверка: ключ поиска не должен быть пустым.
		if (key == null) throw new NullPointerException("null key");

		// Пустое дерево - специальный случай.
		if (root == SENTINEL) return null;
		
		// Сначала ищем старое значение
		V oldValue = get(key);
		
		// Если корень дерева и оба его непосредственных потомка - черные, то временно
		// сделаем корень красным
		if (isBlack(root) && isBlack(root.left) && isBlack(root.right)) {
			root.color = Color.RED;
		}
		root = remove(key, root);
		// После удаления корень дерева может оказаться красным - перекрасим его.
		root.color = Color.BLACK;
		return oldValue;
	}
	
	//----------------------------------------------------------------------------------
	// Вспомогательные рекурсивные функции
	//----------------------------------------------------------------------------------
	
	/**
	 * Стандартный двоичный поиск в дереве по ключу
	 * @param key	Ключ поиска
	 * @param node	Начальный корень
	 * @return
	 */
	private V get(K key, Node node) {
		while (node != SENTINEL) {
			int cmp = key.compareTo(node.key);
			if (cmp < 0) node = node.left; else
			if (cmp > 0) node = node.right; else
			return node.value;
		}
		// Ключ не найден
		return null;
	}
	
	/**
	 * Вставка в дерево новой ассоциативной пары.
	 * @param key
	 * @param value
	 * @param node
	 * @return
	 */
	private Node put(K key, V value, Node node) {
		if (node == SENTINEL) {
			// Новый вставляемый узел всегда красный - это не нарушает баланс черных и красных узлов
			return new Node(key, value);
		}
		int cmp = key.compareTo(node.key);
		if (cmp < 0) {
			node.left = put(key, value, node.left);
		} else if (cmp > 0) {
			node.right = put(key, value, node.right);
		} else {
			node.value = value;
		}
		
		// После вставки может оказаться нарушенной балансировка дерева - 
		// у красного узла может образоваться красный потомок.
		
		// 1. Если красный узел справа, то перевешиваем его так, чтобы красный узел был слева;
		//    корень становится черным.
        if (isRed(node.right) && isBlack(node.left)) {
        	node = pivotLeft(node);
        }
        
        // 2. Если есть два рядом расположенных красных узла, то делаем обратный поворот,
        //    чтобы оба красных узла были потомками узла node.
        if (isRed(node.left)  && isRed(node.left.left)) {
        	node = pivotRight(node);
        }
        
        // 3. Если теперь оба потомка красные, то продвигаем красноту наверх, перекрашивая узлы.
        if (isRed(node.left)  && isRed(node.right)) {
        	flipColors(node);
        }
        
        return node;
	}
	
	/**
	 * Удаляет узел с заданным ключом из заданного поддерева в предположении, что либо корень поддерева,
	 * либо его левый потомок - красные.
	 * @param key	Заданный ключ
	 * @param node	Корень заданного поддерева
	 * @return		Модифицированное поддерево
	 */
	private Node remove(K key, Node node) {
        if (key.compareTo(node.key) < 0)  {
        	// Удаляем из левого поддерева
            if (isBlack(node.left) && isBlack(node.left.left)) {
            	// Узел красный, продвигаем красный цвет к левому потомку
            	node = moveRedLeft(node);
            }
            node.left = remove(key, node.left);
        } else {
        	// Удаляем из правого поддерева
            if (isRed(node.left)) {
            	// Корень был черным, делаем его красным
            	node = pivotRight(node);
            }
            if (key.compareTo(node.key) == 0 && (node.right == SENTINEL)) {
            	// Удаляется сам корень поддерева
                return SENTINEL;
            }
            if (isBlack(node.right) && isBlack(node.right.left)) {
            	// Продвигаем красный цвет к правому потомку
                node = moveRedRight(node);
            }
            if (key.compareTo(node.key) == 0) {
            	// Надо удалить корень. Вместо этого удаляем минимальный узел из правого поддерева
            	Node subst = node.right;
            	while (subst.left != SENTINEL) {
            		subst = subst.left;
            	}
                node.key = subst.key;
                node.value = subst.value;
                node.right = deleteMin(node.right);
            } else {
            	node.right = remove(key, node.right);
            }
        }
        // После удаления у красного узла может образоваться красный потомок. Исправляем.
        return balance(node);
	}
	
	/**
	 * Проверяет, действительно ли заданный узел - красный
	 * @param node	Проверяемый узел
	 * @return		true, если узел красный, false - если черный
	 */
	private boolean isRed(Node node) {
		return node.color == Color.RED;
	}
	
	/**
	 * Проверяет, действительно ли заданный узел - черный
	 * @param node	Проверяемый узел
	 * @return		true, если узел черный, false - если красный
	 */
	private boolean isBlack(Node node) {
		return node.color == Color.BLACK;
	}
	
	/**
	 * Меняет цвет заданного узла на противоположный
	 * @param node	Заданный узел
	 */
	private void flipColor(Node node) {
		switch (node.color) {
		case RED: node.color = Color.BLACK; break;
		case BLACK: node.color = Color.RED; break;
		}
	}
	
    /**
     * Удаляет узел с минимальным ключом из поддерева с заданным корнем.
     * @param node	Корень поддерева
     * @return		Поддерево после удаления узла
     */
    private Node deleteMin(Node node) { 
        if (node.left == SENTINEL) {
            return SENTINEL;
        }

        if (isBlack(node.left) && isBlack(node.left.left)) {
            node = moveRedLeft(node);
        }

        node.left = deleteMin(node.left);
        return balance(node);
    }

    /**
     * Восстанавливает красно-черный баланс узла.
     * @param node	Узел
     * @return		Поддерево после балансировки
     */
    private Node balance(Node node) {
    	assert node != SENTINEL;

        if (isRed(node.right)) {
        	node = pivotLeft(node);
        }
        if (isRed(node.left) && isRed(node.left.left)) {
        	node = pivotRight(node);
        }
        if (isRed(node.left) && isRed(node.right)) {
        	flipColors(node);
        }

        return node;
    }

	/**
	 * Вытаскивает наверх правое поддерево узла при условии, что корень
	 * этого правого поддерева - красный.
	 * 
	 * @param node	Узел - точка поворота
	 * @return		Корень поддерева после поворота
	 */
	private Node pivotLeft(Node node) {
		assert node != SENTINEL && isRed(node.right);
		
		// Перевешиваем ссылки
		Node child = node.right;
		node.right = child.left;
		child.left = node;
		
		// Перекрашиваем узлы
		child.color = node.color;
		node.color = Color.RED;
		
		return child;
	}
	
	/**
	 * Вытаскивает наверх левое поддерево узла при условии, что корень
	 * этого левого поддерева - красный.
	 * 
	 * @param node	Узел - точка поворота
	 * @return		Корень поддерева после поворота
	 */
	private Node pivotRight(Node node) {
		assert node != SENTINEL && isRed(node.left);
		
		// Перевешиваем ссылки
		Node child = node.left;
		node.left = child.right;
		child.right = node;
		
		// Перекрашиваем узлы
		child.color = node.color;
		node.color = Color.RED;
		
		return child;
	}
	
    /**
     * Перекрашивает в противоположные цвета заданный узел и двоих его потомков.
     * Предполагается, что эти потомки имели одинаковый цвет и этот цвет не тот,
     * что у заданного узла. 
     * @param node	Узел для перекрашивания
     */
    private void flipColors(Node node) {
        assert node != SENTINEL && node.left != SENTINEL && node.right != SENTINEL;
        assert node.left.color == node.right.color && node.left.color != node.color;

        flipColor(node);
        flipColor(node.left);
        flipColor(node.right);
    }

    /**
     * Смещает красный цвет от заданного узла к левому потомку в предположении,
     * что этот левый потомок и его левый потомок - оба черные.
     * @param node	Красный узел
     * @return		Корень поддерева после перекрашивания
     */
    private Node moveRedLeft(Node node) {
        assert node != SENTINEL;
        assert isRed(node) && isBlack(node.left) && isBlack(node.left.left);

        flipColors(node);
        if (isRed(node.right.left)) { 
            node.right = pivotRight(node.right);
            node = pivotLeft(node);
        }
        return node;
    }

    /**
     * Смещает красный цвет от заданного узла к правому потомку в предположении,
     * что этот правый потомок и его левый потомок - оба черные.
     * @param node	Красный узел
     * @return		Корень поддерева после перекрашивания
     */
    private Node moveRedRight(Node node) {
        assert node != SENTINEL;
        assert isRed(node) && isBlack(node.right) && isBlack(node.right.left);

        flipColors(node);
        if (isRed(node.left.left)) { 
            node = pivotRight(node);
        }
        return node;
    }

	/**
	 * "Красивая" печать дерева.
	 */
	public void print() {
		print(root, 0);
		System.out.println("--------------------");
	}

	/**
	 * Вспомогательная функция для "красивой" печати дерева.
	 * @param node корневой узел.
	 * @param indent начальный отступ при печати.
	 */
	private void print(Node node, int indent) {
		if (node != SENTINEL) {
			// Формируем строку из indent пробелов.
			char[] spaces = new char[indent];
			Arrays.fill(spaces, ' ');
			System.out.print(String.valueOf(spaces));

			// Печать узла и его поддеревьев.
			System.out.println(node.color + " <" + node.key + ", " + node.value + ">");
			print(node.left, indent + 2);
			print(node.right, indent + 2);
		}
	}

	/**
	 * Тестирующая функция создает АВЛ-дерево последовательной вставкой элементов.
	 * @param args не используется.
	 */
	public static void main(String[] args) {
		RedBlackTree<Integer, Integer> tree = new RedBlackTree<Integer, Integer>();
		int[] keys = { 5, 7, 9, 1, 11, 8, 15, 13, 3, 10 };
		for (int key : keys) {
			System.out.println("Added: <" + key + ", " + 2*key + ">");
			tree.put(key, 2*key);
			tree.print();
			System.out.println("----------------------------");
		}

		for (int key : keys) {
			System.out.println("Removed: " + tree.remove(key));
			tree.print();
			System.out.println("----------------------------");
		}
	}
}
