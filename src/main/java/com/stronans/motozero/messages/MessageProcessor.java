package com.stronans.motozero.messages;

import com.stronans.motozero.motors.MotorId;
import com.stronans.motozero.motors.Motors;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Created by S.King on 01/10/2016.
 */
public class MessageProcessor {
    /**
     * The <code>Logger</code> to be used.
     */
    private static Logger log = Logger.getLogger(MessageProcessor.class);
    private boolean shutdownReceived = false;
    private Motors motor = new Motors();

    public MessageProcessor()
    {
        Runtime.getRuntime().addShutdownHook(new Thread() {
                                                 @Override
                                                 public void run() {
                                                     motor.shutdown();
                                                     log.info("Motors  shutdown.");
                                                 }
                                             }
        );
    }

    public boolean shutdownReceived()
    {
        return shutdownReceived;
    }

    public void processMessage(MotorMessage message)
    {
        int payload = message.getPayload();
        // Controls the twin tracks controlled by the MotoZero board.
        // Note that you need to drive the twin gearbox.
        switch(message.getMessageType())
        {
            case Forwards:
                motor.forward(MotorId.ONE, payload);
                motor.forward(MotorId.TWO, payload);
                break;

            case Pause:
                try {
                    TimeUnit.SECONDS.sleep(payload);
                } catch (InterruptedException e) {
                    log.warn(payload + " second sleep interrupted: " + e.getMessage(), e);
                }
                break;

            case Stop:
                motor.stop(MotorId.ONE);
                motor.stop(MotorId.TWO);
                break;

            case Backwards:
                motor.reverse(MotorId.ONE, payload);
                motor.reverse(MotorId.TWO, payload);
                break;

            case Left:
                motor.speed(MotorId.ONE, payload);
                motor.forward(MotorId.ONE, -1);
                break;

            case Right:
                motor.speed(MotorId.TWO, payload);
                motor.forward(MotorId.TWO, -1);
                break;

            case HardLeft:
                break;

            case HardRight:
                break;

            case Shutdown:
                motor.stop(MotorId.ONE);
                motor.stop(MotorId.TWO);
                shutdownReceived = true;
                break;
        }
    }
}
