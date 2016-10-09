package com.stronans.motozero.motors;

import com.pi4j.io.gpio.*;
import org.apache.log4j.Logger;

/**
 * Main controller class for controlling the Pi MotoZero controller card.
 * <p>
 * Created by S.King on 01/10/2016.
 */
public final class Motors {
    /**
     * The <code>Logger</code> to be used.
     */
    private static final Logger logger = Logger.getLogger(Motors.class);

    private enum USAGE {
        Enable,
        Positive,
        Negative;

        static int size() {
            return 3;
        }
    }

    // Map the Motozero GPIO BCM pins to the Pi4J pin outs.
    private final Pin config[][] = {
            {RaspiPin.GPIO_21, RaspiPin.GPIO_05, RaspiPin.GPIO_02},
            {RaspiPin.GPIO_00, RaspiPin.GPIO_22, RaspiPin.GPIO_03},
            {RaspiPin.GPIO_26, RaspiPin.GPIO_04, RaspiPin.GPIO_27},
            {RaspiPin.GPIO_06, RaspiPin.GPIO_24, RaspiPin.GPIO_01}
    };

    private final GpioPinDigitalOutput pins[][] = new GpioPinDigitalOutput[MotorId.size()][USAGE.size()];


    // create gpio controller
    private final GpioController gpio = GpioFactory.getInstance();
    ;

    // Reserve a current status of a motor so that we can change the speed and state.
    private State motorState[] = new State[MotorId.size()];

    public Motors() {
        for (int i = 0; i < MotorId.size(); i++) {
            motorState[i] = new State();
        }

        for (int i = 0; i < MotorId.size(); i++) {
            for (int j = 0; j < USAGE.size(); j++) {
                GpioPinDigitalOutput pin;
                // provision gpio pin #01 as an output pin and turn off
                pin = gpio.provisionDigitalOutputPin(config[i][j], PinState.LOW);

                // set shutdown state for this pin
                pin.setShutdownOptions(true, PinState.LOW);

                pin.low();
                pins[i][j] = pin;
            }
        }
    }

    public void forward(MotorId id, int speed) {
        int motor = id.ordinal();
        if (!motorState[motor].running) {
            motorState[motor].running = true;
            if (speed > -1) {
                motorState[motor].speed = speed;
                speed(id, speed);
            } else {
                speed(id, motorState[motor].speed);
            }

            pins[motor][USAGE.Positive.ordinal()].high();    // Positive is high
            pins[motor][USAGE.Negative.ordinal()].low();     // Negative is low (ground)

            pins[motor][USAGE.Enable.ordinal()].high();      // Enable it
        }
    }

    public void forward(MotorId id) {
        forward(id, motorState[id.ordinal()].speed);
    }

    public void reverse(MotorId id, int speed) {
        int motor = id.ordinal();
        if (motorState[motor].running) {
            motorState[motor].running = false;
            motorState[motor].speed = speed;

            pins[motor][USAGE.Positive.ordinal()].low();     // Positive is low
            pins[motor][USAGE.Negative.ordinal()].high();    // Negative is high (ground)

            pins[motor][USAGE.Enable.ordinal()].high();      // Enable it
        }
    }

    public void reverse(MotorId id) {
        reverse(id, motorState[id.ordinal()].speed);
    }

    public void stop(MotorId id) {
        int motor = id.ordinal();
        if (motorState[motor].running) {
            pins[motor][USAGE.Enable.ordinal()].low();      // disable it
        }
    }

    public void speed(MotorId id, int speed) {
        int motor = id.ordinal();
        if (motorState[motor].running) {
            motorState[motor].speed = speed;

            pins[motor][USAGE.Enable.ordinal()].pulse(speed, true);      // Pulse it
        }
    }

    public void shutdown() {
        gpio.shutdown();
    }

    private class State {
        int speed;
        boolean running;

        State() {
            speed = 0;
            running = false;
        }
    }
}
