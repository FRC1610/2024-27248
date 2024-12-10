package org.firstinspires.ftc.teamcode;

public class Constants {
    ///ELEVATOR SETPOINTS
    public static final int elevatorHome = 0;
    public static final int elevatorMax = 3500;
    //public static final int elevatorIntake = 250;
    //public static final int elevatorWallPickup = 500;
    public static final int elevatorHighChamber = 450;
    public static final int elevatorHighChamberScore = 400;
    public static final int elevatorLowBasket = 1750; //TODO Measure This
    public static final int elevatorHighBasket = 2450;

    ///ELEVATOR POWER
    public static final double elevatorPowerUp = 0.85;
    public static final double elevatorPowerDown = -0.50;
    public static final double elevatorPowerHome = -0.33;

    ///INTAKE SLIDE SETPOINTS
    public static final int intakeSlideHome = 0;
    public static final int intakeSlideIntake = 800;
    public static final int intakeSlideMax = 550;

    ///ELEVATOR SERVO POSITIONS
    //Elevator Pivot
    public static final double elevatorPivotHome = 1.0;
    public static final double elevatorPivotHandoff = 0.14; //.125
    public static final double elevatorPivotWait = 0.30;
    public static final double elevatorPivotWallPickup = 0.91;
    public static final double elevatorPivotVertical = 0.50;
    public static final double elevatorPivotBasket = 0.75;
    public static final double elevatorPivotChamber = 0.3;
    //Elevator Pincher
    public static final double elevatorPincherHome = 0.40;
    public static final double elevatorPincherOpen = 1.0;
    public static final double elevatorPincherClosed = 0.40;

    //Elevator Pincher Rotate
    public static final double elevatorPincherRotateHome = 0.1;
    public static final double elevatorPincherRotateHandoff = 0.8;

    ///INTAKE SLIDE POWER
    public static final double intakeSlidePowerOut = 0.85;
    public static final double intakeSlidePowerIn = -0.85;
    public static final double intakeSlidePowerHome = -0.25;

    ///INTAKE SERVO POSITIONS
    //Intake Pincher
    public static final double intakePincherClosed = 0;
    public static final double intakePincherOpen = 0.60;
    //Intake Lift
    public static final double intakeLiftFullBack = 0.12;
    public static final double intakeLiftHandoff = 0.25;
    public static final double intakeLiftHome = 0.30;
    public static final double intakeLiftSearchPosition = 0.60;
    public static final double intakeLiftIntakePosition = 0.64;
    public static final double intakeLiftDropoffPosition = 0.45;
    //Intake Rotate
    public static final double intakeRotateHome = 0.40;
    public static final double intakeRotateIntakePosition = 0.80;
    public static final double intakeRotateHandoffPosition = 0.80;
    //Intake Pincher Rotate
    public static final double intakePincherRotateIntake = 0.085;
    public static final double intakePincherRotateHandoff = 0.085;
    public static final double intakePincherRotateHome = 0.085;
}