package com.stronans.motozero.motors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stronans.messagebus.MessageBus;
import com.stronans.messagebus.MessageListener;
import com.stronans.motozero.Application;
import com.stronans.motozero.messages.MotorMessage;
import com.stronans.motozero.messages.MessageProcessor;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Motor driver. Processes message requests from the Brain and implements them.
 * <p>
 * Created by S.King on 08/10/2016.
 */
public class MotorController implements Runnable, MessageListener {
    /**
     * The <code>Logger</code> to be used.
     */
    private static Logger log = Logger.getLogger(MotorController.class);

    public static final int DRIVER = 1;

    private MessageProcessor handler;
    private MessageBus messageBus = MessageBus.getInstance();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void run() {
        log.info("Starting Motor Driver");

        try {
            handler = new MessageProcessor();
        }
        catch(Exception e)
        {
            log.error("  ==> FAILURE SETING UP MESSAGE HANDLER: " + e.getMessage(), e);
        }

        log.info("Motor Driver now setup and running");
        messageBus.addConsumer(DRIVER, this);
    }


    @Override
    public void msgArrived(int i) {
        String rawMessage = messageBus.getMessage(DRIVER);

        try {
            MotorMessage message = mapper.readValue(rawMessage, MotorMessage.class);

            handler.processMessage(message);
        } catch (IOException ioe) {
            log.error(" ==>> FAILED TO DESERIALISE JSON MESSAGE: " + ioe.getMessage(), ioe);
        }
    }
}
