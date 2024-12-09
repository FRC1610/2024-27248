/*
Class for the GoBilda LED Indicator
https://www.gobilda.com/rgb-indicator-light-pwm-controlled/
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class rgbIndicator {
    private Servo rgbLight;

    public class LEDColors {
        // Predefined color values based on PWM positions per GoBilda Product Insight #4
        public static final double OFF = 0.0;
        public static final double RED = 0.28; //This doesn't work - need to adjust
        public static final double ORANGE = 0.333;
        public static final double YELLOW = 0.388;
        public static final double SAGE = 0.444;
        public static final double GREEN = 0.500;
        public static final double AZURE = 0.555;
        public static final double BLUE = 0.611;
        public static final double INDIGO = 0.666;
        public static final double VIOLET = 0.722;
        public static final double WHITE = 1.0;
    }

    // Constructor
    public rgbIndicator(HardwareMap hardwareMap, String deviceName) {
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
        setColor(LEDColors.OFF);
    }
}