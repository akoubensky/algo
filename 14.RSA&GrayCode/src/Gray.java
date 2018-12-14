import java.util.Arrays;

/**
 * Преобразование двоичных чисел в коды Грея.
 */
public class Gray {
	/**
	 * Получение таблицы кодов Грея заданной длины рекурсивным методом.
	 * @param n	Длина кодов Грея
	 * @return	Таблица кодов Грея
	 */
	public static int[] grayTableRec(int n) {
		// База рекурсии: коды нулевой длины.
		if (n == 0) {
			return new int[] { 0 };
		}
		
		// Рекурсивное построение кодов меньшей длины
		int[] gray = grayTableRec(n-1);
		int length = gray.length;
		
		// Обращаем таблицу кодов
		int[] grayReversed = new int[length];
		int mask = 1 << (n - 1);
		for (int i = 0; i < length; ++i) {
			grayReversed[i] = mask | gray[length - 1 - i];
		}
		
		// Формируем окончательный результат путем склеивания.
		int[] result = new int[2 * length];
		System.arraycopy(gray, 0, result, 0, length);
		System.arraycopy(grayReversed, 0, result, length, length);
		
		return result;
	}	// grayTableRec
	
	/**
	 * Нерекурсивная версия функции построения таблицы кодов Грея.
	 * @param n	Длина кодов Грея
	 * @return	Таблица кодов Грея
	 */
	public static int[] grayTable(int n) {
		int[] table = new int[1 << n];
		
		// Внешний цикл по номеру бита
		for (int k = 0; k < n; ++k) {
			int length = 1 << k;
			// Внутренний цикл по заполняемому сегменту таблицы
			for (int i = length; i < 2*length; ++i) {
				table[i] = table[2*length - i - 1] | length;
			}
		}
		
		return table;
	}	// grayTable
	
	/**
	 * Получение индивидуального кода Грея по заданному значению числа.
	 * @param code	Исходное число
	 * @return		Код Грея этого числа
	 */
	public static int gray(int code) {
		return code ^ (code >>> 1);
	}	// gray

	/**
	 * Рекурсивная функция получения индивидуального кода Грея,
	 * подтверждающая описанный выше алгоритм.
	 * @param a
	 * @param n
	 * @return
	 */
	public static int gray(int a, int n) {
		if (n == 0) return 0;
		int mask = 1 << (n-1);
		if (a < mask) {
			return gray(a, n-1); 
		} else {
			return mask | gray(~(a^mask) & (mask-1), n-1);
		}
	}	// gray
	
	/**
	 * Функция получения порядкового номера заданного кода Грея.
	 * @param code	Код Грея
	 * @return		Порядковый номер кода в таблице
	 */
	public static int fromGray(int code) {
		int n = 0;
		for (; code != 0; code >>>= 1) {
			n ^= code;
		}
		return n;
	}

	/**
	 * Вспомогательная функция дополнения строки до заданной длины нулями слева.
	 * @param s			Исходная строка
	 * @param length	Нужная длина
	 * @return			Строка, дополненная слева нулями
	 */
	private static String toLength(String s, int length) {
		char[] prefix = new char[length - s.length()];
		Arrays.fill(prefix, '0');
		return new String(prefix) + s;
	}	// toLength
	
	/**
	 * Тестовая функция
	 * @param args
	 */
	public static void main(String[] args) {
		int[] table = grayTableRec(4);
		for (int code : table) {
			System.out.println(toLength(Integer.toBinaryString(code), 4));
		}
		System.out.println();
		
		table = grayTable(4);
		for (int code : table) {
			System.out.println(toLength(Integer.toBinaryString(code), 4));
		}
		System.out.println();
		
		for (int i = 0; i < 16; ++i) {
			System.out.println(toLength(Integer.toBinaryString(gray(i)), 4));
		}
		System.out.println();
		
		for (int i = 0; i < 16; ++i) {
			System.out.println(toLength(Integer.toBinaryString(gray(i, 4)), 4));
		}
		System.out.println();
		
		for (int code : table) {
			System.out.println(fromGray(code));
		}
	}	// main
}
