package extensions;


import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener to Junit that logs beginning and end of tests.
 * @author sela.zvika
 * @since 09.05.2021
 */
public class JunitLoggerExtension implements BeforeAllCallback,
        BeforeEachCallback,  AfterEachCallback, AfterAllCallback,AfterTestExecutionCallback
     {
    private final Logger logger= LoggerFactory.getLogger(JunitLoggerExtension.class);
    //will be invoked before -> before class
    @Override
    public void beforeAll(ExtensionContext context) {
        logger.info("Starting to run all tests");
    }


    //will be invoked before -> after class
    @Override
    public void afterAll(ExtensionContext context)  {
        logger.info("Finished running all tests");
    }

    //will be invoked before -> before test
    @Override
    public void beforeEach(ExtensionContext context)  {
        logger.info("Starting New Test " + context.getTestMethod().get().getName());
    }

    //will be invoked just before -> after test
    @Override
    public void afterEach(ExtensionContext context)  {
        logger.info("Finished Test " + context.getTestMethod().get().getName());
    }


     /**
      * Method logs Exceptions and Errors that were thrown by test
      * @param context the context of the test (inner state).
      * @author lotem.ofek
      * @since 26-NOV-2023
      */
     @Override
     public void afterTestExecution(ExtensionContext context) {
         context.getExecutionException().ifPresent(ex -> {
             String errorMessage;

             // Check for cause and message, and provide a fallback message if both are null
             if (ex.getCause() != null && ex.getCause().getMessage() != null) {
                 errorMessage = ex.getCause().getMessage();
             } else if (ex.getMessage() != null) {
                 errorMessage = ex.getMessage();
             } else {
                 errorMessage = "Test failed with an unknown error.";
             }

             logger.error("Test {} failed with error: {}", context.getTestMethod().get().getName(), errorMessage);
         });
     }
}
