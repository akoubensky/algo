import java.util.Objects;


/**
 * Реализация основных операции красно-черного дерева -
 * операций вставки новой ассоциативной пары &lt;ключ, значение&gt; и удаление узла по ключу.
 * <br><br>
 * В данных алгоритмах красный узел всегда располагается слева от черного, что позволяет
 * считать данное красно-черное дерево частным случаем 2-3-дерева.
 * <br><br>
 * Данная реализация является слегка адаптированной версией алгоритмов, приведенных в
 * http://algs4.cs.princeton.edu/33balanced/RedBlackBST.java.html
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class RedBlackTree<K extends Comparable<K>, V> extends BSTree<K, V> {
	/**
	 * Цвет узлов дерева
	 */
	private enum Color {
		RED, BLACK
	}

	/**
	 * Класс представляет узел дерева. Этот класс предназначен только
	 * для внутренних целей, поэтому он private, и доступ к полям объектов
	 * этого класса осуществляется непосредственно.
	 */
	protected class Node extends BSNode {
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
			super(key, value, left, right);
			this.color = color;
		}

		/**
		 * Конструктор листа.
		 * @param key ключ
		 * @param value значение
		 */
		Node(K key, V value) { this(key, value, Color.RED, null, null); }
		
		@Override
		public String toString() {
			return color.toString() + " " + super.toString();
		}
	}
	
	/**
	 * Добавление в дерево новой ассоциативной пары.
	 * @param key	Ключ.
	 * @param value Значение.
	 * @return		Значение, которое было ассоциировано раньше с этим ключом
	 *         (если такое значение было).
	 */
	@SuppressWarnings("unchecked")
	public V put(K key, V value) {
		// Заготовим объект, в который можно записать старое значение
		BSNode found = new BSNode(null, null);
		// Выполняем вставку с запоминанием старого значения
		root = put(Objects.requireNonNull(key, "null key"), value, (Node)root, found);
		// После вставки корень дерева может оказаться красным - перекрасим его.
		((Node)root).color = Color.BLACK;
		// Возвращаем старое значение
		return found.value;
	}
	
	/**
	 * Удаление ассоциативной пары из дерева по заданному ключу.
	 * @param key	Ключ поиска (не равен null)
	 * @return		Удаленное значение (или null, если такого ключа не было в дереве)
	 */
	@SuppressWarnings("unchecked")
	public V remove(K key) {
		// Проверка: ключ поиска не должен быть пустым.
		if (key == null) throw new NullPointerException("null key");

		// Пустое дерево - специальный случай.
		if (root == null) return null;
		
		// Заготовим объект, в который можно записать старое значение
		BSNode found = new BSNode(null, null);
		
		// Если корень дерева и оба его непосредственных потомка - черные, то временно
		// сделаем корень красным
		if (isBlack((Node)root.left) && isBlack((Node)root.right)) {
			((Node)root).color = Color.RED;
		}
		// Выполняем удаление с запоминанием старого значения
		root = remove(Objects.requireNonNull(key, "null key"), (Node)root, found);
		// После удаления корень дерева может оказаться красным - перекрасим его.
		if (isRed((Node)root)) ((Node)root).color = Color.BLACK;
		// Возвращаем старое значение
		return found.value;
	}
	
	//----------------------------------------------------------------------------------
	// Вспомогательные рекурсивные функции
	//----------------------------------------------------------------------------------
	
	/**
	 * Вставка в дерево новой ассоциативной пары.
	 * @param key   Ключ
	 * @param value Значение
	 * @param node  Корень поддерева, в которое вставляется пара.
	 * @return      Корень модифицированного поддерева
	 */
	@SuppressWarnings("unchecked")
	private Node put(K key, V value, Node node, BSNode found) {
		if (node == null) {
			// Новый вставляемый узел всегда красный - это не нарушает баланс черных и красных узлов
			return new Node(key, value);
		}
		int cmp = key.compareTo(node.key);
		if (cmp < 0) {
			node.left = put(key, value, (Node)node.left, found);
		} else if (cmp > 0) {
			node.right = put(key, value, (Node)node.right, found);
		} else {
			found.value = node.value;
			node.value = value;
		}
		
		// После вставки может оказаться нарушенной балансировка дерева - 
		// у красного узла может образоваться красный потомок.
		
		// 1. Если красный узел справа, то перевешиваем его так, чтобы красный узел был слева.
        if (isRed((Node)node.right) && isBlack((Node)node.left)) {
        	node = pivotLeft(node);
        }
        
        // 2. Если есть два рядом расположенных красных узла, то делаем обратный поворот,
        //    чтобы оба красных узла были потомками узла node.
        if (isRed((Node)node.left)  && isRed((Node)node.left.left)) {
        	node = pivotRight(node);
        }
        
        // 3. Если теперь оба потомка красные, то продвигаем красноту наверх, перекрашивая узлы.
        if (isRed((Node)node.left)  && isRed((Node)node.right)) {
        	flipColors(node);
        }
        
        return node;
	}
	
	/**
	 * Удаляет узел с заданным ключом из заданного поддерева в предположении,
	 * что либо корень поддерева, либо его левый потомок - красные.
	 * @param key	Заданный ключ
	 * @param node	Корень заданного поддерева
     * @param found Узел для сохранения удаленного значения
	 * @return		Модифицированное поддерево
	 */
	@SuppressWarnings("unchecked")
	private Node remove(K key, Node node, BSNode found) {
        if (key.compareTo(node.key) < 0)  {
        	// Удаляем из левого поддерева
            if (isBlack((Node)node.left) && isBlack((Node)node.left.left)) {
            	// Узел красный, продвигаем красный цвет к левому потомку
            	node = moveRedLeft(node);
            }
            node.left = remove(key, (Node)node.left, found);
        } else {
        	// Удаляем из правого поддерева или корня
            if (isRed((Node)node.left)) {
            	// Делаем левый потомок черным
            	node = pivotRight(node);
            }
            if (key.compareTo(node.key) == 0 && node.right == null) {
            	// Удаляется корень поддерева, который заведомо является листом.
            	found.value = node.value;
                return null;
            }
            if (isBlack((Node)node.right) && isBlack((Node)node.right.left)) {
            	// Продвигаем красный цвет к правому потомку
                node = moveRedRight(node);
            }
            if (key.compareTo(node.key) == 0) {
            	found.value = node.value;
            	// Надо удалить корень. Вместо этого удаляем минимальный узел из правого поддерева
                BSNode min = new BSNode(null, null);
                node.right = deleteMin((Node)node.right, min);
                node.key = min.key;
                node.value = min.value;
            } else {
            	node.right = remove(key, (Node)node.right, found);
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
		return node != null && node.color == Color.RED;
	}
	
	/**
	 * Проверяет, действительно ли заданный узел - черный
	 * @param node	Проверяемый узел
	 * @return		true, если узел черный, false - если красный
	 */
	private boolean isBlack(Node node) {
		return node == null || node.color == Color.BLACK;
	}
	
	/**
	 * Меняет цвет заданного узла на противоположный
	 * @param node	Заданный узел
	 */
	private void flipColor(Node node) {
		assert node != null;
		
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
    @SuppressWarnings("unchecked")
	private Node deleteMin(Node node, BSNode min) { 
        if (node.left == null) {
        	min.key = node.key;
        	min.value = node.value;
            return null;
        }

        if (isBlack((Node)node.left) && isBlack((Node)node.left.left)) {
            node = moveRedLeft(node);
        }

        node.left = deleteMin((Node)node.left, min);
        return balance(node);
    }

    /**
     * Восстанавливает красно-черный баланс узла.
     * @param node	Узел
     * @return		Поддерево после балансировки
     */
    @SuppressWarnings("unchecked")
	private Node balance(Node node) {
    	assert node != null;

        if (isRed((Node)node.right)) {
        	node = pivotLeft(node);
        }
        if (isRed((Node)node.left) && isRed((Node)node.left.left)) {
        	node = pivotRight(node);
        }
        if (isRed((Node)node.left) && isRed((Node)node.right)) {
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
	@SuppressWarnings("unchecked")
	private Node pivotLeft(Node node) {
		assert node != null && isRed((Node)node.right);
		
		// Перевешиваем ссылки
		Node child = (Node)node.right;
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
	@SuppressWarnings("unchecked")
	private Node pivotRight(Node node) {
		assert node != null && isRed((Node)node.left);
		
		// Перевешиваем ссылки
		Node child = (Node)node.left;
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
    @SuppressWarnings("unchecked")
	private void flipColors(Node node) {
        assert node != null && node.left != null && node.right != null;
        assert ((Node)node.left).color == ((Node)node.right).color &&
        		((Node)node.left).color != node.color;

        flipColor(node);
        flipColor((Node)node.left);
        flipColor((Node)node.right);
    }

    /**
     * Смещает красный цвет от заданного узла к левому потомку в предположении,
     * что этот левый потомок и его левый потомок - оба черные.
     * @param node	Красный узел
     * @return		Корень поддерева после перекрашивания
     */
    @SuppressWarnings("unchecked")
	private Node moveRedLeft(Node node) {
        assert node != null;
        assert isRed(node) && isBlack((Node)node.left) && isBlack((Node)node.left.left);

        flipColors(node);
        if (isRed((Node)node.right.left)) { 
            node.right = pivotRight((Node)node.right);
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
    @SuppressWarnings("unchecked")
	private Node moveRedRight(Node node) {
        assert node != null;
        assert isRed(node) && isBlack((Node)node.right) && isBlack((Node)node.right.left);

        flipColors(node);
        if (isRed((Node)node.left.left)) { 
            node = pivotRight(node);
        }
        return node;
    }

	/**
	 * Тестирующая функция создает АВЛ-дерево последовательной вставкой элементов.
	 * После этого все элементы заменяются на новые, а потом последовательно
	 * удаляются из дерева.
	 * 
	 * @param args не используется.
	 */
	public static void main(String[] args) {
		RedBlackTree<Integer, Integer> tree = new RedBlackTree<>();
		int[] keys = { 10, 6, 14, 2, 8, 12, 17, 15, 19 };
		
		// Вставляем элементы в дерево
		for (int key : keys) {
			System.out.println("Added: <" + key + ", " + 2*key + ">");
			tree.put(key, 2*key);
			tree.print();
			System.out.println("----------------------------");
		}
		System.out.format("Key: %d, value: %d", 7, tree.get(7));
		
		// Заменяем все элементы на новые
		for (int key : keys) {
			System.out.format("Replaced %d to %d%n", tree.put(key, key), key);
		}

		// Удаляем все элементы
		for (int key : keys) {
			System.out.println("Removed: " + tree.remove(key));
			tree.print();
			System.out.println("----------------------------");
		}
	}
}
