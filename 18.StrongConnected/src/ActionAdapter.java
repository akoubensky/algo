/**
 * Адаптер, определяющий пустые действия при обходе.
 * Полезен в том случае, когда не все действия при обходе определяются.
 */
public class ActionAdapter implements Action {
    @Override
    public void startComponent() {}

    @Override
    public void finishComponent() {}

    @Override
    public void passOut(int u) {}
}
