import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class RSA {
	/**
	 * Ключ, содержащий два целых числа
	 */
	public static class Key {
		private int product;
		private int exp;
		public Key(int product, int exp) {
			this.product = product;
			this.exp = exp;
		}
		public int product() { return product; }
		public int exp() { return exp; }
	}
	
	private Key openKey;	// Открытый ключ
	private Key closeKey;	// Закрытыый ключ
	
	/**
	 * Конструктор производит все необходимые вычисления и генерирует два ключа.
	 * @param base	Исходная база - ширина кодирования.
	 */
	public RSA(int base) {
		List<Integer> primes = generatePrimes(base);
		Random random = new Random(System.currentTimeMillis());
		int primesCount = primes.size();
		int upper = primesCount >> 2;
		int lower = primesCount >> 3;
		int index1 = random.nextInt(upper - lower + 1) + lower;
		int p = primes.get(index1);
		int q = primes.get(2);
		while (p == q || p*q < base) q = primes.get(++lower);
		int m = (p-1) * (q-1);
		int ndx = random.nextInt(7) + 2;
		int d = primes.get(ndx);
		while (m % d == 0) d = primes.get(++ndx);
		int e = (int)inverse(d, m);
		openKey = new Key(p*q, d);
		closeKey = new Key(p*q, e);
	}
	
	/**
	 * Открытый ключ
	 * @return
	 */
	public Key open() { return openKey; }
	
	/**
	 * Закрытый ключ
	 * @return
	 */
	public Key close() { return closeKey; }
	
	/**
	 * Функция кодирования (декодирования) сообщения
	 * @param message
	 * @param key
	 * @return
	 */
	public static int[] code(int[] message, Key key) {
		int[] codedMessage = new int[message.length];
		int i = 0;
		for (int letter : message) {
			codedMessage[i++] = (int)power(letter, key.exp(), key.product());
		}
		return codedMessage;
	}
	
	/**
	 * Кодирование открытым ключом
	 * @param message
	 * @return
	 */
	public int[] encode(int[] message) {
		return code(message, openKey);
	}
	
	/**
	 * Декодирование закрытым ключом
	 * @param message
	 * @return
	 */
	public int[] decode(int[] message) {
		return code(message, closeKey);
	}
	
	/**
	 * Генерирует список простых чисел заданного размера.
	 * Перебирает нечетные числа, начиная с последнего сгенерированного числа,
	 * и проверяет их на простоту. Первое встретившееся простое число добавляется
	 * в список простых чисел.
	 */
	private static List<Integer> generatePrimes(int last) {
		LinkedList<Integer> primes = new LinkedList<>();
		primes.add(2);
		primes.add(3);
		int next;
		do {
			next = primes.getLast() + 2;
			while (!isPrime(next, primes)) next += 2;
			primes.add(next);
		} while (next < last);
		return primes;
	}
	
	/**
	 * Проверка числа на простоту.
	 * Используется уже сгенерированный список простых чисел.
	 * @param n	Проверяемое число.
	 * @return
	 */
	private static boolean isPrime(int n, List<Integer> primes) {
		for (Integer p : primes) {
			if (p * p > n) return true;
			if (n % p == 0) return false;
		}
		return true;
	}
	
	/**
	 * Быстрое возведение в степень с операциями в модульной
	 * арифметике с модулем mod
	 * @param base	Основание.
	 * @param exp	Показатель степени
	 * @param mod	Модуль в модулярной арифметике
	 * @return		Степень
	 */
	public static long power(long base, int exp, long mod) {
		long res = 1;
		while (exp != 0) {
			if ((exp & 1) == 0) {
				base = (base * base) % mod;
				exp >>= 1;
			} else {
				res = (res * base) % mod;
				exp--;
			}
		}
		return res;
	}
	
	/**
	 * Нахождение обратного элемента в кольце по заданному модулю.
	 * Предполагается, что обратная величина заведомо существует.
	 * @param a		Исходное число
	 * @param mod	Модуль, по которому ведутся вычисления.
	 * @return		Обратная к "a" величина.
	 */
	public static int inverse(int a, int mod) {
		int q;
		int rPred = mod, rNext = a;
		int tPred = 0, tNext = 1;
		while (rNext != 0) {
			q = rPred / rNext;
			int r = rPred - q * rNext; rPred = rNext; rNext = r;
			int t = tPred - q * tNext; tPred = tNext; tNext = t;
		}
		if (tPred < 0) {
			tPred += mod;
		}
		return tPred;
	}


	public static void main(String[] args) {
		RSA coder = new RSA(1 << 16);
		String message = "Lucy in the sky with diamonds";
		int size = message.length();
		int[] intMessage = new int[size];
		for (int i = 0; i < size; i++) intMessage[i] = message.charAt(i);
		int[] codedMessage = coder.encode(intMessage);
		int[] decodedMessage = coder.decode(codedMessage);
		char[] decodedCharMessage = new char[size];
		for (int i = 0; i < size; i++) decodedCharMessage[i] = (char)decodedMessage[i];
		System.out.println(new String(decodedCharMessage));
	}
}
