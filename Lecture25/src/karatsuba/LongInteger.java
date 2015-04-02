package karatsuba;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Задание : Целое число неограниченной длины разбивается на блоки по 9 цифр,
 * и каждый блок в виде обычного целого числа записывается в список
 * (блоки с младшими цифрами находятся в начале списка).
 * Описать класс LongInteger для представления таких чисел и определить в этом
 * классе операции сложения, вычитания и умножения по алгоритму Карацубы.
 */
public class LongInteger implements Comparable<LongInteger> {
    // Некоторые полезные константы
    public final static LongInteger ZERO = new LongInteger();
    public final static LongInteger ONE = new LongInteger(1);

    // Основание "системы счисления"
    private final static int BASE = 1000000000;

    // Внутреннее представление - массив "цифр"
    private ArrayList<Integer> number;

    /**
     * Создает нулевое число.
     */
    public LongInteger() {
        number = new ArrayList<>();
    }

    /**
     * Создает число по обычному целому.
     * @param num Исходное число
     */
    public LongInteger(int num) {
        number = new ArrayList<>();
        if (num == Integer.MIN_VALUE || Math.abs(num) >= BASE) {
            // Две цифры
            number.add(num % BASE);
            number.add(num / BASE);
        } else if (num != 0) {
            // Одна цифра
            number.add(num);
        }
    }

    /**
     * Создает копию заданного числа.
     * @param num Исходное длинное число
     */
    public LongInteger(LongInteger num) {
        number = new ArrayList<>(num.number);
    }

    /**
     * Создает число по переданному ему массиву цифр.
     * Осторожно! Массив не копируется! Этот конструктор только для внутреннего использования!
     * @param num Исходный массив.
     */
    private LongInteger(ArrayList<Integer> num) {
        number = num;
    }

    @Override
    public int compareTo(LongInteger other) {
        int len1 = number.size();
        int len2 = other.number.size();
        // Если длины не равны, то старшая цифра более длинного числа определяет результат.
        if (len1 > len2) return number.get(len1-1);
        if (len1 < len2) return other.number.get(len2-1);
        // Если длины равны, то ищем первую несовпадающую цифру.
        for (int i = len1 - 1; i >= 0; i--) {
            if (number.get(i) != other.number.get(i)) {
                return number.get(i) - other.number.get(i);
            }
        }
        // Все цифры совпали - числа равны.
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof LongInteger)) {
            return false;
        }
        return equals((LongInteger)o);
    }

    /**
     * Сравнивает числа на совпадение.
     * @param other
     * @return
     */
    public boolean equals(LongInteger other) {
        return compareTo(other) == 0;
    }

    @Override
    /**
     * Хеш-код вычисляется по составляющим число цифрам.
     * Равные числа будут иметь одинаковые хеш-коды.
     */
    public int hashCode() {
        int result = 0;
        for (Integer digit : number) {
            result ^= digit.hashCode();
        }
        return result;
    }

    @Override
    /**
     * При выводе отделяем разделителем каждые 9 знаков
     */
    public String toString() {
        if (number.isEmpty()) return "0";

        // Запрашиваем системную установку для разделитея групп цифр
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();

        StringBuilder builder = new StringBuilder();
        for (int i = number.size() - 1; i >= 0; i--) {
            builder.append(String.format("%09d", Math.abs(number.get(i))));
            if (i > 0) builder.append(symbols.getGroupingSeparator());
        }
        int first = -1;
        while (builder.charAt(++first) == '0') ;
        builder = builder.replace(0, first, "");
        if (sign() < 0) builder.insert(0, '-');
        return builder.toString();
    }

    /**
     * Сложение чисел. Складываем данное число с аргументом функции.
     * @param other Второе слагаемое.
     * @return      Сумма
     */
    public LongInteger add(LongInteger other) {
        return new LongInteger(add(number, other.number));
    }

    /**
     * Сложение чисел. Складываются два аргумента функции.
     * @param num1  Первое слагаемое.
     * @param num2  Второе слагаемое.
     * @return      Сумма.
     */
    public static LongInteger add(LongInteger num1, LongInteger num2) {
        return num1.add(num2);
    }

    /**
     * Вычитание чисел. Вычитаем аргумент функции из <i>этого</i> числа.
     * @param other Второе слагаемое.
     * @return      Разность
     */
    public LongInteger subtract(LongInteger other) {
        return add(other.inverse());
    }

    /**
     * Вычитание чисел. Из первого аргумента функции вычитается второй.
     * @param num1  Уменьшаемое.
     * @param num2  Вычитаемое.
     * @return      Разность.
     */
    public static LongInteger subtract(LongInteger num1, LongInteger num2) {
        return num1.add(num2.inverse());
    }

    /**
     * Вычисляет число, обратное к <i>этому</i>.
     * @return  Число, равное <i>этому</i> по абсолютной величине, но с противоположным знаком.
     */
    public LongInteger inverse() {
        ArrayList<Integer> result = new ArrayList<>(number);
        return new LongInteger(inverse(result));
    }

    /**
     * Умножение чисел. Умножаем аргумент функции на <i>это</i> число.
     * @param other Второй сомножитель.
     * @return      Произведение
     */
    public LongInteger multiply(LongInteger other) {
        int size1 = number.size();
        int size2 = other.number.size();
        // Если один из сомножителей - ноль, то и результат - ноль.
        if (size1 == 0 || size2 == 0) {
            return ZERO;
        }

        // Если один из сомножителей - единица, то возвращаем второй сомножитель.
        if (equals(ONE)) return other;
        if (other.equals(ONE)) return this;

        // Копируем оба операнда без знаков из-за возможных изменений знака операндов.
        // Уничтожаем знаки операндов. Запоминаем знак будущего результата.
        ArrayList<Integer> number1 = new ArrayList<>(number);
        ArrayList<Integer> number2 = new ArrayList<>(other.number);
        boolean negate = makeUnsigned(number1) != makeUnsigned(number2);

        // Умножаем два беззнаковых числа по методу Карацубы.
        ArrayList<Integer> result = karatsuba(number1, number2);

        LongInteger longResult = new LongInteger(result);
        longResult.makeSigned(negate);
        return longResult;
    }

    /**
     * Умножение чисел. Умножаем один аргумент функции на второй.
     * @param num1  Первый сомножитель.
     * @param num2  Второй сомножитель.
     * @return      Произведение
     */
    public static LongInteger multiply(LongInteger num1, LongInteger num2) {
        return num1.multiply(num2);
    }

    /**
     * Вычисляет число, обратное к аргументы функции.
     * @return  Число, равное аргументу по абсолютной величине, но с противоположным знаком.
     */
    public static LongInteger inverse(LongInteger num) {
        return num.inverse();
    }

    /**
     * Вычисляет знак числа.
     * @return 0, 1 или -1 в зависимости от знака числа.
     */
    public int sign() {
        return sign(number);
    }

    /**
     * Возведение в неотрицательную целую степень с помощью
     * алгоритма "быстрого возведения в степень".
     *
     * @param p Показатель степени
     * @return  this в степени p.
     */
    public LongInteger power(int p) {
        if (p < 0) {
            throw new IllegalArgumentException("Negative power");
        }
        LongInteger result = LongInteger.ONE;
        LongInteger z = new LongInteger(this);
        while (p > 0) {
            if ((p & 1) == 0) {
                z = z.multiply(z);
                p >>= 1;
            } else {
                result = z.multiply(result);
                p--;
            }
        }
        return result;
    }

    //========================================================================
    // Вспомогательные функции (в которых, собственно, и делается вся работа).
    //========================================================================

    /**
     * Умножение двух беззнаковых целых по методу Карацубы.
     * @param a Первый операнд
     * @param b Второй операнд
     * @return  Произведение
     */
    private ArrayList<Integer> karatsuba(ArrayList<Integer> a, ArrayList<Integer> b) {
        int size1 = a.size();
        int size2 = b.size();

        // Если одно из чисел - ноль, то и результат нулевой.
        if (size1 == 0) return a;
        if (size2 == 0) return b;

        int size = Math.max(size1, size2);
        if (size == 1) {
            // Перемножение двух "однозначных" чисел делается с помощью встроенного умножения.
            long p = (long)a.get(0) * b.get(0);

            int low = (int)(p % BASE);
            int high = (int)(p / BASE);
            ArrayList<Integer> res = new ArrayList<>();
            res.add(low);
            if (high != 0) {
                res.add(high);
            }
            return res;
        } else {
            // Многозначные числа перемножаются путем рекурсивного вызова функции
            // и комбинирования результатов с помощью трех умножений и шести сложений.
            int halfSize = (size >> 1) + (size & 1);
            // Положим B = BASE^^halfSize. Тогда
            ArrayList<Integer> a0 = takeLow(a, halfSize);   // a0 = a % B
            ArrayList<Integer> a1 = takeHigh(a, halfSize);  // a1 = a / B
            ArrayList<Integer> b0 = takeLow(b, halfSize);   // b0 = b % B
            ArrayList<Integer> b1 = takeHigh(b, halfSize);  // b1 = b / B
            ArrayList<Integer> z0 = karatsuba(a0, b0);      // z0 = a0 * b0
            ArrayList<Integer> z2 = karatsuba(a1, b1);      // z2 = a1 * b1
            a0 = add(a0, a1);                               // a0 + a1
            b0 = add(b0, b1);                               // b0 + b1
            // z1 = (a0 + a1) * (b0 + b1) - z0 - z1
            ArrayList<Integer> z1 = subtract(subtract(karatsuba(a0, b0), z0), z2);
            // result = b^^2 * z2 + B * z1 + z0
            return add(add(shiftLeft(z2, 2*halfSize), shiftLeft(z1, halfSize)), z0);
        }
    }

    /**
     * Берет младшие цифры числа (вычисление по модулю B).
     * @param num       Исходное число.
     * @param digits    Количество цифр в модуле.
     * @return          num % B
     */
    private ArrayList<Integer> takeLow(ArrayList<Integer> num, int digits) {
        if (digits >= num.size()) return num;
        ArrayList<Integer> res = new ArrayList<>();
        for (Integer e : num) {
            if (digits-- == 0) break;
            res.add(e);
        }
        return strip(res);
    }

    /**
     * Берет старшие цифры числа (целочисленное деление на B).
     * @param num       Исходное число.
     * @param digits    Количество цифр в модуле.
     * @return          num / B
     */
    private ArrayList<Integer> takeHigh(ArrayList<Integer> num, int digits) {
        ArrayList<Integer> res = new ArrayList<>();
        if (digits >= num.size()) return res;
        for (int i = digits; i < num.size(); i++) res.add(num.get(i));
        return strip(res);
    }

    /**
     * Умножение на заданный модуль (сдвиг на заданное число цифр влево).
     * @param num       Исходное число
     * @param digits    Количество цифр сдвига
     * @return          Сдвинутое число
     */
    private ArrayList<Integer> shiftLeft(ArrayList<Integer> num, int digits) {
        ArrayList<Integer> res = new ArrayList<>(Collections.nCopies(digits, 0));
        res.addAll(num);
        return res;
    }

    /**
     * Вычисляет знак числа.
     * @return 0б 1 или -1 в зависимости от знака числа.
     */
    public int sign(ArrayList<Integer> number) {
        int size = number.size();
        if (size == 0) return 0;
        // Знак определяется знаком старшей цифры.
        return Integer.signum(number.get(size - 1));
    }

    /**
     * Складывает два числа, заданные массивами цифр.
     * @param num1  Первое слагаемое.
     * @param num2  Второе слагаемое.
     * @return      Сумма.
     */
    private ArrayList<Integer> add(ArrayList<Integer> num1, ArrayList<Integer> num2) {
        ArrayList<Integer> result = new ArrayList<>();
        Iterator<Integer> i1 = num1.iterator();
        Iterator<Integer> i2 = num2.iterator();
        int transfer = 0;
        while (i1.hasNext() && i2.hasNext()) {
            int e = i1.next() + i2.next() + transfer;
            result.add(e % BASE);
            transfer = e / BASE;
        }
        if (i2.hasNext()) i1 = i2;
        while (i1.hasNext()) {
            int e = i1.next() + transfer;
            result.add(e % BASE);
            transfer = e / BASE;
        }
        return strip(result);
    }

    /**
     * Производит вычитание одного числа из другого, числа заданы массивами цифр.
     * @param num1  Уменьшаемое.
     * @param num2  Вычитаемое.
     * @return      Разность.
     */
    private ArrayList<Integer> subtract(ArrayList<Integer> num1, ArrayList<Integer> num2) {
        ArrayList<Integer> num2Inv = new ArrayList<>(num2);
        return add(num1, inverse(num2Inv));
    }

    /**
     * Удаляет знак числа. Вннимание! Это модифицирующая функция!
     * @return  Знак числа до операции.
     */
    private int makeUnsigned(ArrayList<Integer> number) {
        int sign = sign(number);
        if (sign < 0) {
            for (int i = 0; i < number.size(); i++) {
                number.set(i, - number.get(i));
            }
        }
        return sign;
    }

    /**
     * Приписывает числу заданный знак. Вннимание! Это модифицирующая функция!
     * @param negative  true, если требуемый знак - отрицательный.
     */
    private void makeSigned(boolean negative) {
        int sign = sign();  // Знак исходного числа
        if (negative != (sign < 0)) {
            for (int i = 0; i < number.size(); i++) {
                number.set(i, - number.get(i));
            }
        }
    }

    /**
     * Удаляет нули из старших разрядов числа.
     * @return  Число после удаления незначащих нулей.
     */
    private ArrayList<Integer> strip(ArrayList<Integer> number) {
        int size;
        while ((size = number.size()) > 0 && number.get(size - 1) == 0) {
            number.remove(size - 1);
        }
        return number;
    }

    /**
     * Инвертирование (изменение знака) числа. Вннимание! Это модифицирующая функция!
     * @param number    Исходное число
     * @return          Результат инвертирования
     */
    private ArrayList<Integer> inverse(ArrayList<Integer> number) {
        for (int i = 0; i < number.size(); i++) {
            number.set(i, -number.get(i));
        }
        return number;
    }

    /**
     * Некоторые тесты.
     * @param args
     */
    public static void main(String[] args) {
        // Правильно ли создается в критическом случае?
        System.out.println(Integer.MIN_VALUE + " = " + new LongInteger(Integer.MIN_VALUE));
        // Проверка правильности исполнения основных операций.
        LongInteger longInt1 = new LongInteger(-100000);
        LongInteger longInt2 = new LongInteger(100000);
        LongInteger sum = LongInteger.add(longInt1, longInt2);
        LongInteger mult = LongInteger.multiply(longInt1, longInt2);
        System.out.println("Sum: " + sum + "; product: " + mult);
        // Вычисляем 10**36 по формуле ((-10**9)**2)**2
        LongInteger base = new LongInteger(-1000000000); // -10**9
        LongInteger base2 = LongInteger.ZERO.subtract(base.multiply(base)); // -(-10**9)**2
        LongInteger base4 = base2.multiply(base2); // (-(-10**9)**2)**2
        System.out.println("(-(-10**9)**2)**2 = " + base4);
        // Еще несколько операций.
        LongInteger max = new LongInteger(999999999);
        LongInteger num1 = LongInteger.add(max, LongInteger.multiply(max,  max));
        System.out.println("999999999 000000000 = " + num1);
        LongInteger num2 = LongInteger.add(LongInteger.ONE, LongInteger.add(num1, max));
        System.out.println("1 000000000 000000000 = " + num2);
        LongInteger zero = num1.subtract(num1).multiply(num2);
        System.out.println("zero = " + zero);
        System.out.println(new LongInteger(-2).power(31));
        System.out.println(new LongInteger(-10).power(99));
        System.out.println(new LongInteger(-2).power(999));
    }
}
