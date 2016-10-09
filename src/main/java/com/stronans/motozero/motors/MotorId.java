package com.stronans.motozero.motors;

/**
 * enum to define each of the motor controllers on the MotoZero board.
 *
 * Created by S.King on 01/10/2016.
 */
public enum MotorId {
    ONE,
    TWO,
    THREE,
    FOUR;

    public static int size()
    {
        return 4;
    }
}
