package org.firstinspires.ftc.teamcode;

public class Constants {
    ///ELEVATOR SETPOINTS
    public static final int elevatorHome = 0;
    public static final int elevatorMax = 3500;
    public static final int elevatorIntake = 250;
    public static final int  elevatorWallPickup = 500;
    public static final int  elevatorHighChamber = 1000;
    public static final int  elevatorLowBasket = 2000;
    public static final int  elevatorHighBasket = 3000;

    ///ELEVATOR POWER
    public static final double elevatorPowerUp = 0.85;
    public static final double elevatorPowerDown = -0.50;

    ///INTAKE SLIDE SETPOINTS
    public static final int intakeSlideHome = 0;
    public static final int intakeSlideIntake = 1000;
    public static final int intakeSlideMax = 2100;

    ///INTAKE SLIDE POWER
    public static final double intakeSlidePowerOut = 0.85;
    public static final double intakeSlidePowerIn = -0.75;

    ///INTAKE SERVO POSITIONS
    //Intake Pincher
    public static final double intakePincherClosed = 0;
    public static final double intakePincherOpen = 0.60;
    //Intake Lift
    public static final double intakeLiftFullBack = 0.12;
    public static final double intakeLiftHome = 0.30;
    public static final double intakeLiftIntakePosition = 0.62;
    //Intake Rotate
    public static final double intakeRotateHome = 0.40;
    public static final double intakeRotateIntakePosition = 0.80;
    public static final double intakeRotateHandoffPosition = 0.80;
    //Intake Pincher Rotate
    public static final double intakePincherRotateIntake = 0.80;
    public static final double intakePincherRotateHandoff = 0.80;
    public static final double intakePincherRotateHome = 1;
}