package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class RgbIndicator {
    private Servo rgbLight;

    // Predefined color values based on PWM positions
    public static final double OFF = 0.0;
    public static final double RED = 0.1;
    public static final double GREEN = 0.2;
    public static final double BLUE = 0.3;
    public static final double YELLOW = 0.4;
    public static final double CYAN = 0.5;
    public static final double MAGENTA = 0.6;
    public static final double WHITE = 0.7;

    // Constructor

    public RgbIndicator(HardwareMap hardwareMap, String deviceName) {
        // Initialize the servo from the hardware map
        rgbLight = hardwareMap.get(Servo.class, deviceName);
    }

    // Set the light color by PWM value
    public void setColor(double pwmValue) {
        if (rgbLight != null) {
            rgbLight.setPosition(pwmValue);
        }
    }

    // Turn off the light
    public void turnOff() {
        setColor(OFF);
    }
}
