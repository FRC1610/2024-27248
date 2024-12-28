package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.teamcode.drivers.GoBildaPinpointDriver;
import org.firstinspires.ftc.teamcode.drivers.rgbIndicator;
import org.firstinspires.ftc.teamcode.drivers.rgbIndicator.LEDColors;
//import com.qualcomm.robotcore.hardware.LED;

public class RobotHardware {

    /* Declare OpMode members. */
    private LinearOpMode myOpMode = null;   // gain access to methods in the calling OpMode.

    // Define Motor and Servo objects  (Make them private so they can't be accessed externally)
    public DcMotor leftFront = null;
    public DcMotor rightFront = null;
    public DcMotor leftBack = null;
    public DcMotor rightBack = null;
    public DcMotorEx elevatorLift = null;
    public DcMotorEx intakeSlide = null;
    public Servo intakePincher = null;
    public Servo intakeRotate = null;
    public Servo intakeLift = null;
    public Servo intakePincherRotate = null;
    public Servo elevatorPivot = null;
    public Servo elevatorPincher = null;
    public Servo elevatorPincherRotate = null;
    public Servo intakeLeft = null;
    public Servo intakeRight = null;
    public DigitalChannel intakeTouch = null;
    public ColorSensor intakeColor = null;
    private int intakeSlideLastPosition = 0;
    private boolean holdIntakeEnabled = true; // Controls whether to hold position
    Limelight3A limelight = null;
    GoBildaPinpointDriver odo = null; // Declare OpMode member for the Odometry Computer
    org.firstinspires.ftc.teamcode.drivers.rgbIndicator rgbIndicator = null;
    //private RevBlinkinLedDriver blinkinLedDriver = null;
    //private RevBlinkinLedDriver.BlinkinPattern pattern = null;
    private DigitalChannel allianceButton = null;
    //private LED LED_red = null;
    //private LED LED_green = null;

    // Define a constructor that allows the OpMode to pass a reference to itself.
    public RobotHardware(LinearOpMode opmode) {
        myOpMode = opmode;
    }

    /**
     * Initialize all the robot's hardware.
     * This method must be called ONCE when the OpMode is initialized.
     * <p>
     * All of the hardware devices are accessed via the hardware map, and initialized.
     */
    public void init()    {

        rgbIndicator = new rgbIndicator(myOpMode.hardwareMap, "rgbLight");
        rgbIndicator.setColor(LEDColors.YELLOW);

        ///GoBilda Odometry Pod Setup
        //Deploy to Control Hub to make Odometry Pod show in hardware selection list
        odo = myOpMode.hardwareMap.get(GoBildaPinpointDriver.class, "odo");
        odo.setOffsets(-100.0, -65.0);
        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.REVERSED);
        odo.resetPosAndIMU();

        ///REV LED Setup
        //LED_green = myOpMode.hardwareMap.get(LED.class, "LED_green");
        //LED_red = myOpMode.hardwareMap.get(LED.class, "LED_red");

        /*
        ///Blinkin Setup
        blinkinLedDriver = myOpMode.hardwareMap.get(RevBlinkinLedDriver.class, "blinkin");
        allianceButton = myOpMode.hardwareMap.get(DigitalChannel.class,"allianceButton");
        if (allianceButton.getState()) {
            pattern = RevBlinkinLedDriver.BlinkinPattern.HEARTBEAT_RED;
            LED_red.on();
            LED_green.off();
        } else {
            pattern = RevBlinkinLedDriver.BlinkinPattern.HEARTBEAT_BLUE;
            LED_red.off();
            LED_green.on();
        }
        blinkinLedDriver.setPattern(pattern);

         */

        ///Drive Motor Setup
        // Define and Initialize Drive Motors
        leftFront  = myOpMode.hardwareMap.get(DcMotorEx.class, "leftFront");
        rightFront = myOpMode.hardwareMap.get(DcMotorEx.class, "rightFront");
        leftBack = myOpMode.hardwareMap.get(DcMotorEx.class, "leftBack");
        rightBack = myOpMode.hardwareMap.get(DcMotorEx.class, "rightBack");

        // Drive motor brake mode
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Run Using Encoder
        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Set Directions
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        ///Linear Slide Motor Setup
        //ELEVATOR
        elevatorLift = myOpMode.hardwareMap.get(DcMotorEx.class, "elevatorLift");
        elevatorLift.setDirection(DcMotor.Direction.REVERSE);
        elevatorLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        elevatorLift.setTargetPositionTolerance(5);
        elevatorLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        elevatorLift.setTargetPosition(Constants.elevatorHome);
        elevatorLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //INTAKE
        intakeSlide = myOpMode.hardwareMap.get(DcMotorEx.class,"intakeSlide");
        intakeSlide.setDirection(DcMotor.Direction.REVERSE);
        intakeSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeSlide.setTargetPositionTolerance(5);
        intakeSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeSlide.setTargetPosition(Constants.intakeSlideHome);
        intakeSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Define and initialize ALL installed servos.
        ///Intake Servos
        intakePincher = myOpMode.hardwareMap.get(Servo.class, "intakePincher");
        intakePincher.setPosition(Constants.intakePincherClosed);

        intakeRotate = myOpMode.hardwareMap.get(Servo.class, "intakeRotate");
        intakeRotate.setPosition(Constants.intakeRotateHome);

        intakeLift = myOpMode.hardwareMap.get(Servo.class, "intakeLift");
        intakeLift.setPosition(Constants.intakeLiftHome);

        intakePincherRotate = myOpMode.hardwareMap.get(Servo.class, "intakePincherRotate");
        intakePincherRotate.setPosition(Constants.intakePincherRotateHome);

        //Active Intake Servos
        intakeLeft = myOpMode.hardwareMap.get(Servo.class, "intakeLeft");
        intakeRight = myOpMode.hardwareMap.get(Servo.class, "intakeRight");

        //Intake Sensors
        intakeTouch = myOpMode.hardwareMap.get(DigitalChannel.class,"intakeTouch");
        intakeTouch.setMode(DigitalChannel.Mode.INPUT);
        intakeColor = myOpMode.hardwareMap.get(RevColorSensorV3.class, "intakeColor");

        ///Elevator Servos
        elevatorPivot = myOpMode.hardwareMap.get(Servo.class, "elevatorPivot");
        elevatorPivot.setPosition(Constants.elevatorPivotHome);

        elevatorPincher = myOpMode.hardwareMap.get(Servo.class, "elevatorPincher");
        elevatorPincher.setPosition(Constants.elevatorPincherHome);

        elevatorPincherRotate = myOpMode.hardwareMap.get(Servo.class, "elevatorPincherRotate");
        elevatorPincherRotate.setPosition(Constants.elevatorPincherRotateHome);

        //Limelight Setup
        limelight = myOpMode.hardwareMap.get(Limelight3A.class, "limelight");
        //odo.setMsTransmissionInterval(11);
        limelight.pipelineSwitch(0);
        limelight.start();

        //Telemetry Data
        myOpMode.telemetry.addData("Status", "Initialized");
        myOpMode.telemetry.addData("X offset", odo.getXOffset());
        myOpMode.telemetry.addData("Y offset", odo.getYOffset());
        myOpMode.telemetry.addData("Device Version Number:", odo.getDeviceVersion());
        myOpMode.telemetry.addData("Device Scalar", odo.getYawScalar());
        myOpMode.telemetry.update();
    }

    /**
     * Calculates the left/right motor powers required to achieve the requested
     * robot motions: Drive (Axial motion) and Turn (Yaw motion).
     * Then sends these power levels to the motors.
     *
     * @param x     x-axis power
     * @param y     y-axis power
     * @param rotation Rotation
     */
    public void mecanumDrive(double x, double y, double rotation) {
        // Combine drive and turn for blended motion.
        double leftFrontPower = y + x + rotation;
        double rightFrontPower = y - x - rotation;
        double leftBackPower = y - x + rotation;
        double rightBackPower = y + x - rotation;

        // Normalize power values to keep them between -1 and 1
        double maxPower = Math.max(1.0, Math.abs(leftFrontPower));
        maxPower = Math.max(maxPower, Math.abs(rightFrontPower));
        maxPower = Math.max(maxPower, Math.abs(leftBackPower));
        maxPower = Math.max(maxPower, Math.abs(rightBackPower));

        leftFrontPower /= maxPower;
        rightFrontPower /= maxPower;
        leftBackPower /= maxPower;
        rightBackPower /= maxPower;

        // Use existing function to drive both wheels.
        setDrivePower(leftFrontPower, rightFrontPower, leftBackPower, rightBackPower);
    }

    /**
     * Pass the requested wheel motor powers to the appropriate hardware drive motors.
     *
     * @param leftFrontPower Left Front Power
     * @param rightFrontPower Right Front Power
     * @param leftBackPower Left Back Power
     * @param rightBackPower Right Back Power
     */
    public void setDrivePower(double leftFrontPower, double rightFrontPower, double leftBackPower, double rightBackPower) {
        // Output the values to the motor drives.
        leftFront.setPower(leftFrontPower);
        rightFront.setPower(rightFrontPower);
        leftBack.setPower(leftBackPower);
        rightBack.setPower(rightBackPower);

        //myOpMode.telemetry.addData("Front Left Power", leftFrontPower);
        //myOpMode.telemetry.addData("Front Right Power", rightFrontPower);
        //myOpMode.telemetry.addData("Rear Left Power", leftBackPower);
        //myOpMode.telemetry.addData("Rear Right Power", rightBackPower);
        //myOpMode.telemetry.update();
    }

    /**
     * Pass desired power to elevator
     *
     * @param elevatorPower Elevator Power
     */
    public void runElevator (double elevatorPower){
        elevatorLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        elevatorLift.setPower(elevatorPower);
    }

    /**
     * Pass desired position to elevator
     *
     * @param elevatorTargetPosition  Elevator position
     */
    public void setElevator (int elevatorTargetPosition){
        double elevatorPower = 0;
        int ElevatorCurrentPosition = elevatorLift.getCurrentPosition();
        if (elevatorTargetPosition < 0 ){
            elevatorPower = 0;
        } else if (elevatorTargetPosition > ElevatorCurrentPosition){
            elevatorPower = Constants.elevatorPowerUp;
        } else
            elevatorPower = Constants.elevatorPowerDown;

        elevatorLift.setTargetPosition(elevatorTargetPosition);
        elevatorLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        elevatorLift.setPower(elevatorPower);
    }

    /**
     * Pass desired position to intake slide
     *
     * @param intakeSlideTargetPosition  Intake Slide position
     */
    public void setIntakeSlide (int intakeSlideTargetPosition){
        double intakeSlidePower = 0;
        intakeSlideLastPosition = intakeSlideTargetPosition;
        int intakeSlideCurrentPosition = intakeSlide.getCurrentPosition();
        if (intakeSlideTargetPosition <0 ){
            intakeSlidePower = 0;
        } else if (intakeSlideTargetPosition > intakeSlideCurrentPosition){
            intakeSlidePower = Constants.intakeSlidePowerOut;
        } else
            intakeSlidePower = Constants.intakeSlidePowerIn;

        intakeSlide.setTargetPosition(intakeSlideTargetPosition);
        intakeSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        intakeSlide.setPower(intakeSlidePower);
    }

    public void holdIntakeSlidePosition (){
        if (holdIntakeEnabled) {
            intakeSlide.setTargetPosition(intakeSlideLastPosition);
            intakeSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            intakeSlide.setPower(0.1); // Minimal power to hold position
        }
    }

    public void disableHoldIntake() {
        holdIntakeEnabled = false;
    }

    public void enableHoldIntake() {
        holdIntakeEnabled = true;
    }

    /**
     * Pass desired power to elevator
     *
     * @param intakeSlidePower Elevator Power
     */
    public void runIntakeSlide (double intakeSlidePower){

        intakeSlide.setPower(intakeSlidePower);
    }

    public void resetSlideEncoders(){
        elevatorLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        elevatorLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        intakeSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void CloseIntakePincher (){
        intakePincher.setPosition(Constants.intakePincherClosed);
    }
    public void OpenIntakePincher (){
        intakePincher.setPosition(Constants.intakePincherOpen);
    }

    public void IntakePosition (double PosChange){
        double CurrentPosition = elevatorPincher.getPosition();
        double NewPosition = CurrentPosition + PosChange;
        elevatorPincher.setPosition(NewPosition);
    }

    public void IntakeRotate(double PosChange){
        double CurrentPosition = intakeRotate.getPosition();
        double NewPosition = CurrentPosition + PosChange;
        intakeRotate.setPosition(NewPosition);
    }

    public void IntakePincherRotate(double PosChange){
        double CurrentPosition = intakePincherRotate.getPosition();
        double NewPosition = CurrentPosition + PosChange;
        intakePincherRotate.setPosition(NewPosition);
    }

    public void ElevatorPincherRotate(double PosChange){
        double CurrentPosition = elevatorPincherRotate.getPosition();
        double NewPosition = CurrentPosition + PosChange;
        elevatorPincherRotate.setPosition(NewPosition);
    }

    public void IntakeLift(double PosChange){
        double CurrentPosition = intakeLift.getPosition();
        double NewPosition = CurrentPosition + PosChange;
        intakeLift.setPosition(NewPosition);
    }

    public void elevatorPivot(double PosChange){
        double CurrentPosition = elevatorPivot.getPosition();
        double NewPosition = CurrentPosition + PosChange;
        elevatorPivot.setPosition(NewPosition);
    }

    public void setElevatorPincher(double PosChange){
        double CurrentPosition = elevatorPincher.getPosition();
        double NewPosition = CurrentPosition + PosChange;
        elevatorPincher.setPosition(NewPosition);
    }

    public void runIntake(String Direction) {
        if ("OUT".equalsIgnoreCase(Direction)) {
            intakeLeft.setPosition(1.0);  // Full speed in
            intakeRight.setPosition(0.0); // Full speed in (opposite)
        } else if ("IN".equalsIgnoreCase(Direction) && intakeTouch.getState()) {
            intakeLeft.setPosition(0.0);  // Full speed out
            intakeRight.setPosition(1.0); // Full speed out (opposite)
        } else {
            intakeLeft.setPosition(0.5);  // Stop
            intakeRight.setPosition(0.5); // Stop
        }
    }
}
