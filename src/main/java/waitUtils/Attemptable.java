package waitUtils;

/**
 * interface for Retry mechanism
 * will be used with anonymous class
 * @author rozenfeld.yael
 * @since 28.06.2021
 */
public interface Attemptable {

    void attempt() throws Throwable;
    void onAttemptFail();

}
