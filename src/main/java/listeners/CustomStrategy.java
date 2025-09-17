package listeners;

import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfiguration;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfigurationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;


/**
 * Class purpose is to allow junit to run with a fixed strategy (by using a custom strategy)
 * due to issue: https://github.com/junit-team/junit5/issues/2273
 */
public class CustomStrategy implements ParallelExecutionConfiguration, ParallelExecutionConfigurationStrategy {
    private static final Logger logger = LoggerFactory.getLogger(CustomStrategy.class);
    public static int numOfThreads = getStaticProp();

    /**
     * get number of Threads from external param (1st priority) or  junit-platform.properties file (2nd priority)
     * if none is configured we set it to 1 thread
     * @since 03.05.2023
     * @author sela.zvika
     * @return num of threads to run the test plan
     */
    private static int getStaticProp() {
        try {
            String externalNumOfThreads = System.getProperty("junit.jupiter.execution.parallel.config.fixed.parallelism");
            if (externalNumOfThreads!=null){
                return Integer.valueOf(externalNumOfThreads);
            }

            InputStream junitProps = CustomStrategy.class.getClassLoader().getResourceAsStream("junit-platform.properties");
            Properties prop = new Properties();
            prop.load(new InputStreamReader(junitProps, Charset.forName("UTF-8")));
            return Integer.valueOf(prop.getProperty("junit.jupiter.execution.parallel.config.fixed.parallelism"));
        }
        catch (Exception e){
            return 1;
        }

    }

    /**
     * Get the parallelism to be used.
     * @since 03.05.2023
     * @author sela.zvika
     * @return num of threads to be used
     */
    @Override
    public int getParallelism() {
        logger.info("num of Threads " + numOfThreads);
        return  numOfThreads;
    }

    /**
     * Get the minimum number of runnable threads to be used.
     * @return min num of threads
     */
    @Override
    public int getMinimumRunnable() {
        return 0;
    }

    /**
     * Get the maximum number of runnable threads to be used.
     * @since 03.05.2023
     * @author sela.zvika
     * @return max num of threads
     */
    @Override
    public int getMaxPoolSize() {
        return numOfThreads;
    }

    /**
     * Get the core thread pool size to be used.
     * @since 03.05.2023
     * @author sela.zvika
     * @return thread pool size
     */

    @Override
    public int getCorePoolSize() {

        return numOfThreads;
    }

    /**
     * Get the number of seconds for which inactive threads
     * should be kept alive before terminating them and shrinking the thread pool.
     *
     * @since 03.05.2023
     * @author sela.zvika
     * @return num of seconds
     */
    @Override
    public int getKeepAliveSeconds() {
        return 120;
    }

    /**
     * A strategy to use for configuring parallel test execution.
     * @since 03.05.2023
     * @author sela.zvika
     * @param configurationParameters - configuration parameters that TestEngines may use to influence test discovery and execution.
     * @return the custom strategy
     */
    @Override
    public ParallelExecutionConfiguration createConfiguration(final ConfigurationParameters configurationParameters) {
        return this;
    }
}