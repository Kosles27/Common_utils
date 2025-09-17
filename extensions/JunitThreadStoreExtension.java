package extensions;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Clean the local thread store after each test.
 * TODO: this class should be deleted after safely removing it from all test projects!.
 * @author Tzvika Sela
 * @since 06.14.2022
 */
public class JunitThreadStoreExtension implements AfterEachCallback {

    /**
     * empty the store
     *
     * @author - Tzvika Sela
     * @since - 06.14.2022
     */
    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {

    }
}
