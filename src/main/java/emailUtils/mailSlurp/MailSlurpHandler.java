package emailUtils.mailSlurp;

import com.mailslurp.apis.EmailControllerApi;
import com.mailslurp.apis.InboxControllerApi;
import com.mailslurp.apis.WaitForControllerApi;
import com.mailslurp.clients.ApiClient;
import com.mailslurp.clients.ApiException;
import com.mailslurp.clients.Configuration;
import com.mailslurp.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class to handle Emails of MailSlurp
 * @author lotem.ofek
 * @since 07-JAN-2024
 */
@SuppressWarnings("unused")
public class MailSlurpHandler {

    private final String API_KEY = System.getenv("Mail_Slurp_API_Key");
    private final int SLURP_TIMEOUT_MILLIS = 60000;
    private ApiClient defaultClient;
    private InboxControllerApi inboxControllerApi;
    private EmailControllerApi emailControllerApi;
    private List<InboxDto> inboxes;
    private WaitForControllerApi waitForControllerApi;

    /**
     * Constructor
     * @author lotem.ofek
     * @since 07-JAN-2024
     */
    public MailSlurpHandler() {
        defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setApiKey(API_KEY);
        defaultClient.setConnectTimeout(SLURP_TIMEOUT_MILLIS);
        defaultClient.setWriteTimeout(SLURP_TIMEOUT_MILLIS);
        defaultClient.setReadTimeout(SLURP_TIMEOUT_MILLIS);
        inboxes = new ArrayList<>();
        if (inboxControllerApi==null)
            inboxControllerApi = new InboxControllerApi(defaultClient);
        if (emailControllerApi == null)
            emailControllerApi = new EmailControllerApi(defaultClient);

        waitForControllerApi = new WaitForControllerApi(defaultClient);
    }

    /**
     * Create and return a default inbox.
     * @return the created inbox
     * @throws ApiException ApiException
     * @author lotem.ofek
     * @since 07-JAN-2024
     */
    public InboxDto createDefaultInbox() throws ApiException {
        inboxes.add(inboxControllerApi.createInboxWithDefaults());
        return inboxes.get(inboxes.size()-1);
    }

    /**
     * Get inbox with a specific email address
     * @param emailAddress Email address of inbox to get
     * @return the inbox
     * @author lotem.ofek
     * @since 07-JAN-2024
     */
    public InboxDto getInbox(String emailAddress)
    {
        for (InboxDto inboxDto : inboxes)
        {
            if (inboxDto.getEmailAddress().equalsIgnoreCase(emailAddress))
                return inboxDto;
        }
        return null;
    }

    /**
     * Find a message
     * @param inbox inbox
     * @param isUnreadOnly true or false
     * @param countTypeEnum EXACTLY or ATLEAST
     * @param count How many messages
     * @param matchOptions match options
     * @return Email message
     * @throws ApiException ApiException
     * @author lotem.ofek
     * @since 07-JAN-2024
     */
    public Email findMail(InboxDto inbox, boolean isUnreadOnly, WaitForConditions.CountTypeEnum countTypeEnum, int count, List<MatchOption> matchOptions) throws ApiException {

        WaitForConditions waitForConditions = new WaitForConditions()
                .inboxId(inbox.getId())
                .unreadOnly(isUnreadOnly)
                .countType(countTypeEnum)
                .count(count);
        for (MatchOption matchOption : matchOptions) {
            waitForConditions.addMatchesItem(new MatchOption()
                    .field(matchOption.getField())
                    .should(matchOption.getShould())
                    .value(matchOption.getValue()));
        }
        List<EmailPreview> emailPreviews = waitForControllerApi.waitFor(waitForConditions);

        EmailPreview emailPreview = emailPreviews.get(0);
        UUID id = emailPreview.getId();
        Email emailMessage = emailControllerApi.getEmail(id, null);
        return emailMessage;
    }



}
