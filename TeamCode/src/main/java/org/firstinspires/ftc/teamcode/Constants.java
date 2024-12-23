package org.firstinspires.ftc.teamcode;

public class Constants {
    ///ELEVATOR SETPOINTS
    public static final int elevatorHome = 0;
    //public static final int elevatorMax = 3500;
    ///1150RPM SETTINGS
    public static final int elevatorHighChamber = 450; //TODO Measure This
    public static final int elevatorLowBar = 500;   //TODO Measure This
    public static final int elevatorHighChamberScore = 100; //TODO Measure This
    public static final int elevatorLowBasket = 1750; //TODO Measure This
    public static final int elevatorHighBasket = 975;

    /* 435RPM OLD SETTINGS
    public static final int elevatorHighChamber = 450;
    public static final int elevatorLowBar = 500;
    public static final int elevatorHighChamberScore = 100; //400
    public static final int elevatorLowBasket = 1750;
    public static final int elevatorHighBasket = 2375; //2400 original
    */

    ///ELEVATOR POWER
    public static final double elevatorPowerUp = 1.0;
    public static final double elevatorPowerDown = -0.75;
    public static final double elevatorPowerHome = -0.35;

    ///INTAKE SLIDE SETPOINTS
    ///
    ///312 RPM SETTINGS
    /*
    public static final int intakeSlideHome = 0;
    public static final int intakeSlideHalf = 900;
    public static final int intakeSlideFull = 1750;
    public static final int intakeSlideMax = 2000;

     */

    ///1150 RPM SETTINGS
    public static final int intakeSlideHome = 0;
    public static final int intakeSlideHalf = 250;
    public static final int intakeSlideFull = 500;
    public static final int intakeSlideMax = 550;

    ///ELEVATOR SERVO POSITIONS
    //Elevator Pivot
    public static final double elevatorPivotHome = 1.0;
    public static final double elevatorPivotHandoff = 0.14; //.125
    public static final double elevatorPivotWait = 0.30;
    public static final double elevatorPivotWallPickup = 0.91;
    public static final double elevatorPivotVertical = 0.50;
    public static final double elevatorPivotBasket = 0.75;
    public static final double elevatorPivotChamber = 0.25; //0.3
    //Elevator Pincher
    public static final double elevatorPincherHome = 0.40;
    public static final double elevatorPincherOpen = 1.0;
    public static final double elevatorPincherClosed = 0.40;

    //Elevator Pincher Rotate
    public static final double elevatorPincherRotateHome = 0.1;
    public static final double elevatorPincherRotateAutoBasket = 0.5;
    public static final double elevatorPincherRotateHandoff = 0.8;

    ///INTAKE SLIDE POWER
    public static final double intakeSlidePowerOut = 0.95;
    public static final double intakeSlidePowerIn = -0.95;
    public static final double intakeSlidePowerHome = -0.35;

    ///INTAKE SERVO POSITIONS
    //Intake Pincher
    public static final double intakePincherClosed = 0;
    public static final double intakePincherOpen = 0.75; //Original 0.60
    public static final double intakePincherOpenAuto = 0.80;
    //Intake Lift
    public static final double intakeLiftFullBack = 0.12;
    public static final double intakeLiftHandoff = 0.25;
    public static final double intakeLiftHome = 0.30;
    public static final double intakeLiftSearchPosition = 0.55; //Original 0.60
    public static final double intakeLiftIntakePosition = 0.61; //Original 0.64
    public static final double intakeLiftDropoffPosition = 0.45;
    //Intake Rotate
    public static final double intakeRotateHome = 0.45; //Previously .40
    public static final double intakeRotateDropoff = 0.40;
    public static final double intakeRotateIntakePosition = 0.80;
    public static final double intakeRotateHandoffPosition = 0.80;
    //Intake Pincher Rotate
    public static final double intakePincherRotateIntake = 0.085;
    public static final double intakePincherRotateHandoff = 0.085;
    public static final double intakePincherRotateHome = 0.085;

    ///AUTONOMOUS SETPOINTS
    public static final double autoIntakeLift = 0.55;
    //AUTO INTAKE SLIDE
    public static final int autoIntakeSlideBasketLeft = 1775;
    public static final int autoIntakeSlideBasketCenter = 2000;
    //public static final int autoIntakeSlideBasketRight = 1600;
    //AUTO INTAKE ROTATE
    public static final double autoIntakeRotateBasketLeft = 0.935;
    public static final double autoIntakeRotateBasketCenter = 0.6;
    //public static final double autoIntakeRotateBasketRight = 0.75;
    //AUTO PINCHER ROTATE
    public static final double autoIntakePincherRotateBasketLeft = 0.64;
    public static final double autoIntakePincherRotateBasketCenter = 0.26;

}