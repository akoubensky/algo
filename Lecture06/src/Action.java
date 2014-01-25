
/**
 * Абстрактное действие
 * @param <T> Тип элемента, над которым производится действие.
 */
public interface Action<T> {
	public void action(T param);
}
