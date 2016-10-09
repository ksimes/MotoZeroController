package com.stronans.motozero;

import com.stronans.motozero.motors.MotorController;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;

/**
 * Starting point for MotoZero motor controller.
 * <p/>
 * Created by S.King on 01/10/2016.
 */
public class Application {
    /**
     * The <code>Logger</code> to be used.
     */
    private static Logger log = Logger.getLogger(Application.class);
    private static MotorController motorController = new MotorController();

    /**
     * Handles the loading of the log4j configuration. properties file must be
     * on the classpath.
     *
     * @throws RuntimeException
     */
    private static void initLogging() throws RuntimeException {
        try {
            Properties properties = new Properties();
            properties.load(Application.class.getClassLoader().getResourceAsStream("log4j.properties"));
            PropertyConfigurator.configure(properties);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to load logging properties for System");
        }
    }

    public static void main(String args[]) {

        // Setup initial logging (setup rollover and discard)
        try {
            initLogging();
        } catch (RuntimeException ex) {
            System.out.println("Error setting up log4j logging");
            System.out.println("Application will continue but without any logging.");
        }

        Thread motorControl = new Thread(motorController);
        motorControl.start();
    }
}
