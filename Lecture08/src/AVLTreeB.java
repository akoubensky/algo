import java.util.Objects;


/**
 * Реализация основных операций АВЛ-дерева. В узлах дерева хранятся показатели
 * сбалансированности узла - числа, равные 0, +1 или -1 в зависимости от того,
 * равны ли высоты поддеревьев или высота левого поддерева на единицу больше,
 * или высота правого поддерева на единицу больше. Другие значения показателей
 * сбалансированности не допускаются.
 * 
 * Реализованы следующие основные операции:
 * -    V get(K key) - поиск по ключу;
 * -    V put(K key, V value) - добавление или изменение ассоциативной пары;
 * -    V remove(K key) - удаление ассоциативной пары по ключу.
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class AVLTreeB<K extends Comparable<K>, V> extends BSTree<K, V> {
	/**
	 * Класс представляет узел дерева. Этот класс предназначен только
	 * для внутренних целей, поэтому он private, и доступ к полям объектов
	 * этого класса осуществляется непосредственно.
	 */
	private class Node extends BSNode {
		// Показатель сбалансированности узла:
		short balance;

		/**
		 * Конструктор произвольного узла.
		 * @param key ключ
		 * @param value значение
		 * @param balance показатель сбалансированности узла
		 * @param left левое поддерево
		 * @param right правое поддерево
		 */
		Node(K key, V value, short balance, Node left, Node right) {
			super(key, value, left, right);
			this.balance = balance;
		}

		/**
		 * Конструктор листа.
		 * @param key ключ
		 * @param value значение
		 */
		Node(K key, V value) {
			super(key, value);
			this.balance = 0;
		}
		
		@Override
		public String toString() {
			return super.toString() + " (" + balance + ")";
		}
	}

	/**
	 * Объект, возвращаемый рекурсивными функциями put и remove.
	 */
	private class RetValue {
		Node node;			// Корень модифицированного дерева
		boolean hChanged;	// Признак измененной высоты модифицированного дерева
		K oldKey;			// Старое значение ключа в измененном узле
		V oldValue;			// Старое значение в измененном узле (если узел был раньше)
		
		RetValue(Node node, boolean hChanged, K oldKey, V oldValue) {
			this.node = node;
			this.hChanged = hChanged;
			this.oldKey = oldKey;
			this.oldValue = oldValue;
		}
		
		RetValue(Node node, boolean hChanged, V oldValue) {
			this(node, hChanged, null, oldValue);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V put(K key, V value) {
		RetValue retVal = put(Objects.requireNonNull(key, "null key"), value, (Node)root);
		root = retVal.node;
		return retVal.oldValue;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V remove(K key) {
		RetValue retVal = remove(Objects.requireNonNull(key, "null key"), (Node)root);
		root = retVal.node;
		return retVal.oldValue;
	}

	/**
	 * Заносит новое значение в поддерево, заданное корнем.
	 * 
	 * @param key	Ключ поиска
	 * @param value	Заносимое значение
	 * @param node	Корень поддерева
	 * @return		Результат модификации: корень измененного поддерева,
	 *              признак изменившейся высоты, старое значение узла.
	 */
	@SuppressWarnings("unchecked")
	private RetValue put(K key, V value, Node node) {
		if (node == null) {
			// Создаем и возвращаем новый узел
			return new RetValue(new Node(key, value), true, null);
		} else {
			int cmp = key.compareTo(node.key);
			RetValue retVal;
			if (cmp == 0) {
				// Нашли ключ, замещаем значение. Структура дерева не меняется.
				V oldValue = node.value;
				node.value = value;
				retVal = new RetValue(node, false, oldValue);
			} else if (cmp < 0) {
				// Помещаем новое значение в левое поддерево
				retVal = put(key, value, (Node)node.left);
				node.left = retVal.node;
				retVal.node = node;
				// Проверяем, надо ли изменить баланс
				if (retVal.hChanged) {
					if (++node.balance == 2) {
						// Дерево разбалансировано; исправляем
						if (((Node)node.left).balance == -1) {
							node.left = pivotLeft((Node)node.left);
						}
						retVal.node = pivotRight(node);
						// После исправления высота уменьшается.
						retVal.hChanged = false;
					} else {
						retVal.hChanged = node.balance == 1;
					}
				}
			} else {
				// Помещаем новое значение в правое поддерево
				retVal = put(key, value, (Node)node.right);
				node.right = retVal.node;
				retVal.node = node;
				// Проверяем, надо ли изменить баланс
				if (retVal.hChanged) {
					if (--node.balance == -2) {
						// Дерево разбалансировано; исправляем
						if (((Node)node.right).balance == 1) {
							node.right = pivotRight((Node)node.right);
						}
						retVal.node = pivotLeft(node);
						// После исправления высота уменьшается.
						retVal.hChanged = false;
					} else {
						retVal.hChanged = node.balance == -1;
					}
				}
			}
			return retVal;
		}
	}
	
	/**
	 * Удаляет узел с заданным ключом из поддерева, заданного корнем.
	 * 
	 * @param key	Ключ, который нужно удалить
	 * @param node	Корень поддерева
	 * @return		Результат, содержащий корень модифицированного поддерева,
	 * 				старое значение узла и признак того, что дерево изменило высоту.
	 */
	@SuppressWarnings("unchecked")
	private RetValue remove(K key, Node node) {
		RetValue retVal;
		if (node == null) {
			retVal = new RetValue(null, false, null);
		} else {
			int cmp = key.compareTo(node.key);
			if (cmp < 0) {
				// Удаляем узел из левого поддерева
				retVal = remove(key, (Node)node.left);
				node.left = retVal.node;
				retVal.node = node;
				// Проверяем, надо ли изменить баланс
				if (retVal.hChanged) {
					if (--node.balance == -2) {
						// Дерево разбалансировано; исправляем
						if (((Node)node.right).balance == 1) {
							node.right = pivotRight((Node)node.right);
						}
						retVal.node = pivotLeft(node);
						// После исправления высота может уменьшиться.
						retVal.hChanged = retVal.node.balance == 0;
					} else {
						retVal.hChanged = node.balance == 0;
					}
				}
			} else if (cmp > 0) {
				// Удаляем узел из правого поддерева
				retVal = remove(key, (Node)node.right);
				node.right = retVal.node;
				retVal.node = node;
				// Проверяем, надо ли изменить баланс
				if (retVal.hChanged) {
					if (++node.balance == 2) {
						// Дерево разбалансировано; исправляем
						if (((Node)node.left).balance == -1) {
							node.left = pivotLeft((Node)node.left);
						}
						retVal.node = pivotRight(node);
						// После исправления высота может уменьшиться.
						retVal.hChanged = retVal.node.balance == 0;
					} else {
						retVal.hChanged = node.balance == 0;
					}
				}
			} else if (node.left == null) {
				retVal = new RetValue((Node)node.right, true, node.value);
			} else if (node.right == null) {
				retVal = new RetValue((Node)node.left, true, node.value);
			} else {
				retVal = deleteMin((Node)node.right);
				node.right = retVal.node;
				retVal.node = node;
				node.key = retVal.oldKey;
				V oldValue = node.value;
				node.value = retVal.oldValue;
				retVal.oldValue = oldValue;
				// Проверяем, надо ли изменить баланс
				if (retVal.hChanged) {
					if (++node.balance == 2) {
						// Дерево разбалансировано; исправляем
						if (((Node)node.left).balance == -1) {
							node.left = pivotLeft((Node)node.left);
						}
						retVal.node = pivotRight(node);
						// После исправления высота может уменьшиться.
						retVal.hChanged = retVal.node.balance == 0;
					} else {
						retVal.hChanged = node.balance == 0;
					}
				}
			}
		}
		return retVal;
	}
	
	/**
	 * Удаляет из поддерева, заданного корнем, узел с минимальным значением.
	 * 
	 * @param node	Корень поддерева
	 * @return		Результат, содержащий корень модифицированного поддерева,
	 * 				ключ и значение удаленного узла, признак изменившейся высоты.
	 */
	@SuppressWarnings("unchecked")
	private RetValue deleteMin(Node node) {
		if (node.left == null) {
			return new RetValue((Node)node.right, true, node.key, node.value);
		} else {
			RetValue retVal = deleteMin((Node)node.left);
			node.left = retVal.node;
			retVal.node = node;
			if (retVal.hChanged) {
				if (--node.balance == -2) {
					// Дерево разбалансировано; исправляем
					if (((Node)node.right).balance == 1) {
						node.right = pivotRight((Node)node.right);
					}
					retVal.node = pivotLeft(node);
					// После исправления высота может уменьшиться.
					retVal.hChanged = retVal.node.balance == 0;
				} else {
					retVal.hChanged = node.balance == 0;
				}
			}
			return retVal;
		}
	}
	
	/**
	 * Функция левого поворота вокруг заданного узла.
	 * 
	 * @param node	Корневой узел, вокруг которого производится поворот
	 * @return		Корень модифицированного дерева
	 */
	@SuppressWarnings("unchecked")
	private Node pivotLeft(Node node) {
		// Меняем структуру дерева
		Node child = (Node)node.right;
		node.right = child.left;
		child.left = node;
		
		// Меняем показатели баланса
		short b1 = node.balance;
		short b2 = child.balance;
		short cb2 = (short)Math.max(b2, 0);
		
		node.balance = (short)(cb2 + 1 + b1 - b2);
		child.balance = (short)(Math.max(cb2 + 1 + b1, b2) + 1);
		
		return child;
	}
	
	/**
	 * Функция правого поворота вокруг заданного узла.
	 * 
	 * @param node	Корневой узел, вокруг которого производится поворот
	 * @return		Корень модифицированного дерева
	 */
	@SuppressWarnings("unchecked")
	private Node pivotRight(Node node) {
		// Меняем структуру дерева
		Node child = (Node)node.left;
		node.left = child.right;
		child.right = node;
		
		// Меняем показатели баланса
		short b1 = node.balance;
		short b2 = child.balance;
		short cb2 = (short)Math.max(b2, 0);
		
		node.balance = (short)(b1 - 1 - cb2);
		child.balance = (short)(b2 - Math.max(0, cb2 + 1 - b1) - 1);
		
		return child;
	}
	
	/**
	 * Тестирующая функция создает АВЛ-дерево последовательной вставкой элементов.
	 * @param args не используется.
	 */
	public static void main(String[] args) {
		AVLTreeB<Integer, Integer> tree = new AVLTreeB<Integer, Integer>();
		int[] keys = { 5, 7, 9, 1, 11, 8, 15, 13, 3, 10 };
		for (int key : keys) {
			System.out.println("Added: <" + key + ", " + 2*key + ">");
			tree.put(key, 2*key);
			tree.print();
			System.out.println("----------------------------");
		}
		
		for (int key : keys) {
			System.out.println("Changed: <" + key + ", " + tree.put(key, key) + "> to " + key);
		}

		for (int key : keys) {
			System.out.println("Removed: " + tree.remove(key));
			tree.print();
			System.out.println("----------------------------");
		}
	}
}
