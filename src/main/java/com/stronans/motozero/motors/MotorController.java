package com.stronans.motozero.motors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stronans.messagebus.MessageBus;
import com.stronans.messagebus.MessageListener;
import com.stronans.motozero.messages.MessageProcessor;
import com.stronans.motozero.messages.MotorMessage;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Processes message requests from the Brain and implements them.
 * <p>
 * Created by S.King on 08/10/2016.
 */
public class MotorController implements Runnable, MessageListener {
    /**
     * The <code>Logger</code> to be used.
     */
    private static Logger log = Logger.getLogger(MotorController.class);

    /**
     * Number of queue on messagebus that this class is reading from/writing to.
     */
    private static final int DRIVER = 1;

    private boolean testing = false;
    private MessageProcessor handler;
    private MessageBus messageBus = MessageBus.getInstance();
    /*
     * Used to deserialise JSON messages from the messagebus.
     */
    private ObjectMapper mapper = new ObjectMapper();

    public MotorController() {
    }

    public MotorController(boolean testing) {
        this.testing = testing;
    }

    @Override
    public void run() {
        log.info("Starting Motor Driver");

        try {
            handler = new MessageProcessor(testing);
        }
        catch(Exception e)
        {
            log.error("  ==> FAILURE SETING UP MESSAGE HANDLER: " + e.getMessage(), e);
        }

        log.info("Motor Driver now setup and running");
        messageBus.addConsumer(DRIVER, this);
    }

    /**
     * Fired when a message for this Queue arries from the Message bus.
     *
     * @param queueSize Number of messages still on the messagebus for this queue.
     */
    @Override
    public void msgArrived(int queueSize) {
        String rawMessage = messageBus.getMessage(DRIVER);

        log.info("incomming msg : [" + rawMessage + "]");
        log.info("queue size : " + queueSize);

        try {
            MotorMessage message = mapper.readValue(rawMessage, MotorMessage.class);

            handler.processMessage(message);
        } catch (IOException ioe) {
            log.error(" ==>> FAILED TO DESERIALISE JSON MESSAGE: " + ioe.getMessage());
        }
    }
}
