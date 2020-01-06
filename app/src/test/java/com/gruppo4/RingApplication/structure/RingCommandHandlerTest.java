package com.gruppo4.RingApplication.structure;


import com.eis.smslibrary.SMSMessage;
import com.eis.smslibrary.SMSPeer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit testing of the class RingCommandHandler
 *
 * @author Alberto Ursino
 */
public class RingCommandHandlerTest {

    private static final String SIGNATURE = "ringUp password: ";
    private static final String VALID_NUMBER = "+393443444546";
    private static final String VALID_PASSWORD = "pass";
    private static final String INVALID_SIGNATURE = "ciao" + SIGNATURE;
    private static final String VALID_CONTENT = SIGNATURE + VALID_PASSWORD;
    private static final String WRONG_CONTENT = VALID_PASSWORD;
    private static final SMSPeer SMS_PEER = new SMSPeer(VALID_NUMBER);
    private RingCommandHandler ringCommandHandler = null;
    private SMSMessage smsMessage = new SMSMessage(new SMSPeer(VALID_NUMBER), VALID_CONTENT);

    @Before
    public void init() {
        ringCommandHandler = RingCommandHandler.getInstance();
    }

    @Test
    public void parseContent_content_isValid() {
        Assert.assertNotEquals(null, ringCommandHandler.parseMessage(smsMessage));
    }

    @Test
    public void parseContent_content_is_isTooShort() {
        Assert.assertEquals(null, ringCommandHandler.parseMessage(new SMSMessage(new SMSPeer(VALID_NUMBER), WRONG_CONTENT)));
    }

    @Test
    public void parseContent_content_hasInvalidSignature() {
        Assert.assertNotEquals(null, ringCommandHandler.parseMessage(new SMSMessage(new SMSPeer(VALID_NUMBER), INVALID_SIGNATURE)));
    }

    @Test
    public void parseContent_ringCommandPasswords_areEquals() {
        Assert.assertEquals(new RingCommand(SMS_PEER, VALID_PASSWORD).getPassword(), ringCommandHandler.parseMessage(smsMessage).getPassword());
    }

    @Test
    public void parseContent_ringCommandPeers_areEquals() {
        Assert.assertEquals(new RingCommand(SMS_PEER, VALID_CONTENT).getPeer(), ringCommandHandler.parseMessage(smsMessage).getPeer());
    }

}