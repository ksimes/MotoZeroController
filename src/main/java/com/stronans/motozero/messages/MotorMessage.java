package com.stronans.motozero.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.log4j.Logger;

/**
 * Type of JSON messages sent to the MotorController
 *
 * Created by S.King on 01/10/2016.
 */
public final class MotorMessage {
    /**
     * The <code>Logger</code> to be used.
     */
    private static Logger log = Logger.getLogger(MotorMessage.class);

    private final MotorMessages messageType;
    private final int payload;

    @JsonCreator
    public MotorMessage(@JsonProperty("messageType")String messageType, @JsonProperty("payload")int payload) {
        this.messageType = MotorMessages.valueOf(messageType);
        this.payload = payload;
    }

    @JsonProperty("messageType")
    public MotorMessages getMessageType() {
        return messageType;
    }

    @JsonProperty("payload")
    public int getPayload() {
        return payload;
    }

}
