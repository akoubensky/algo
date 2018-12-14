
/**
 * Прерывание, которое возникает, если структура графа неверна или
 * не соответствует ожиданиям применяемого алгоритма.
 */
@SuppressWarnings("serial")
public class InvalidGraphException extends RuntimeException {
	public InvalidGraphException() {
		super("The graph is not a DAG");
	}
}
