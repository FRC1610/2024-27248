/* Created by Phil Malone. 2023.
    This class illustrates my simplified Odometry Strategy.
    It implements basic straight line motions but with heading and drift controls to limit drift.
    See the readme for a link to a video tutorial explaining the operation and limitations of the code.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.List;
import java.util.Locale;

public class SimplifiedOdometryRobot {
    // Adjust these numbers to suit your robot.
    private final double ODOM_INCHES_PER_COUNT   = 1.0;   //  Set the odometry pod to output in inches so this can be set to 1
    private final boolean INVERT_DRIVE_ODOMETRY  = false;       //  When driving FORWARD, the odometry value MUST increase.  If it does not, flip the value of this constant.
    private final boolean INVERT_STRAFE_ODOMETRY = false;       //  When strafing to the LEFT, the odometry value MUST increase.  If it does not, flip the value of this constant.

    private static final double DRIVE_GAIN          = 0.03;    // Strength of axial position control
    private static final double DRIVE_ACCEL         = 2.0;     // Acceleration limit.  Percent Power change per second.  1.0 = 0-100% power in 1 sec.
    private static final double DRIVE_TOLERANCE     = 0.5;     // Controller is is "inPosition" if position error is < +/- this amount
    private static final double DRIVE_DEADBAND      = 0.2;     // Error less than this causes zero output.  Must be smaller than DRIVE_TOLERANCE
    private static final double DRIVE_MAX_AUTO      = 0.6;     // "default" Maximum Axial power limit during autonomous

    private static final double STRAFE_GAIN         = 0.03;    // Strength of lateral position control
    private static final double STRAFE_ACCEL        = 1.5;     // Acceleration limit.  Percent Power change per second.  1.0 = 0-100% power in 1 sec.
    private static final double STRAFE_TOLERANCE    = 0.5;     // Controller is is "inPosition" if position error is < +/- this amount
    private static final double STRAFE_DEADBAND     = 0.49;     // Error less than this causes zero output.  Must be smaller than DRIVE_TOLERANCE
    private static final double STRAFE_MAX_AUTO     = 0.6;     // "default" Maximum Lateral power limit during autonomous

    private static final double YAW_GAIN            = 0.01;    // Strength of Yaw position control //default 0.18
    private static final double YAW_ACCEL           = 3.0;     // Acceleration limit.  Percent Power change per second.  1.0 = 0-100% power in 1 sec.
    private static final double YAW_TOLERANCE       = 1.0;     // Controller is is "inPosition" if position error is < +/- this amount
    private static final double YAW_DEADBAND        = 0.49;    // Error less than this causes zero output.  Must be smaller than DRIVE_TOLERANCE //default 0.25
    private static final double YAW_MAX_AUTO        = 0.6;     // "default" Maximum Yaw power limit during autonomous

    // Public Members
    public double driveDistance     = 0; // scaled axial distance (+ = forward)
    public double strafeDistance    = 0; // scaled lateral distance (+ = left)
    public double heading           = 0; // Latest Robot heading from IMU

    // Establish a proportional controller for each axis to calculate the required power to achieve a setpoint.
    public ProportionalControl driveController     = new ProportionalControl(DRIVE_GAIN, DRIVE_ACCEL, DRIVE_MAX_AUTO, DRIVE_TOLERANCE, DRIVE_DEADBAND, false);
    public ProportionalControl strafeController    = new ProportionalControl(STRAFE_GAIN, STRAFE_ACCEL, STRAFE_MAX_AUTO, STRAFE_TOLERANCE, STRAFE_DEADBAND, false);
    public ProportionalControl yawController       = new ProportionalControl(YAW_GAIN, YAW_ACCEL, YAW_MAX_AUTO, YAW_TOLERANCE,YAW_DEADBAND, true);

    // ---  Private Members

    // Hardware interface Objects

    private RobotHardware hardware;

    //private DcMotor leftFront;     //  control the left front drive wheel
    //private DcMotor rightFront;    //  control the right front drive wheel
    //private DcMotor leftBack;      //  control the left back drive wheel
    //private DcMotor rightBack;     //  control the right back drive wheel

    //private DcMotor driveEncoder;       //  the Axial (front/back) Odometry Module (may overlap with motor, or may not)
    //private DcMotor strafeEncoder;      //  the Lateral (left/right) Odometry Module (may overlap with motor, or may not)

    private LinearOpMode myOpMode;
    //private IMU imu;
    private ElapsedTime holdTimer = new ElapsedTime();  // User for any motion requiring a hold time or timeout.

    private double rawDriveOdometer    = 0; // Unmodified axial odometer count
    private double driveOdometerOffset = 0; // Used to offset axial odometer
    private double rawStrafeOdometer   = 0; // Unmodified lateral odometer count
    private double strafeOdometerOffset= 0; // Used to offset lateral odometer
    private double rawHeading       = 0; // Unmodified heading (degrees)
    private double headingOffset    = 0; // Used to offset heading

    private double turnRate           = 0; // Latest Robot Turn Rate from IMU
    private boolean showTelemetry     = true;

    // Robot Constructor
    public SimplifiedOdometryRobot(LinearOpMode opmode, RobotHardware hardware) {
        this.myOpMode = opmode;
        this.hardware = hardware;
    }

    /**
     * Robot Initialization:
     *  Use the hardware map to Connect to devices.
     *  Perform any set-up all the hardware devices.
     * @param showTelemetry  Set to true if you want telemetry to be displayed by the robot sensor/drive functions.
     */
    public void initialize(boolean showTelemetry)
    {
        hardware.init();
        // Initialize the hardware variables. Note that the strings used to 'get' each
        // motor/device must match the names assigned during the robot configuration.

        // !!!  Set the drive direction to ensure positive power drives each wheel forward.
        //leftFront  = setupDriveMotor("leftFront", DcMotor.Direction.REVERSE);
        //rightFront = setupDriveMotor("rightFront", DcMotor.Direction.FORWARD);
        //leftBack  = setupDriveMotor( "leftBack", DcMotor.Direction.REVERSE);
        //rightBack = setupDriveMotor( "rightBack",DcMotor.Direction.FORWARD);
        //imu = myOpMode.hardwareMap.get(IMU.class, "imu");

        //  Connect to the encoder channels using the name of that channel.
        //driveEncoder = myOpMode.hardwareMap.get(DcMotor.class, "axial");
        //strafeEncoder = myOpMode.hardwareMap.get(DcMotor.class, "lateral");

        // Set all hubs to use the AUTO Bulk Caching mode for faster encoder reads
        //List<LynxModule> allHubs = myOpMode.hardwareMap.getAll(LynxModule.class);
        //for (LynxModule module : allHubs) {
        //    module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        //}

        // Tell the software how the Control Hub is mounted on the robot to align the IMU XYZ axes correctly
        //RevHubOrientationOnRobot orientationOnRobot =
        //       new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP,
        //                                    RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);
        //imu.initialize(new IMU.Parameters(orientationOnRobot));

        // zero out all the odometry readings.
        resetOdometry();

        // Set the desired telemetry state
        this.showTelemetry = showTelemetry;
    }

    /**
     *   Setup a drive motor with passed parameters.  Ensure encoder is reset.
     * @param deviceName  Text name associated with motor in Robot Configuration
     * @param direction   Desired direction to make the wheel run FORWARD with positive power input
     * @return the DcMotor object
     */
    private DcMotor setupDriveMotor (String deviceName, DcMotor.Direction direction) {
        DcMotor aMotor = myOpMode.hardwareMap.get(DcMotor.class, deviceName);
        aMotor.setDirection(direction);
        aMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);  // Reset Encoders to zero
        aMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        aMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);  // Requires motor encoder cables to be hooked up.
        return aMotor;
    }

    /**
     * Read all input devices to determine the robot's motion
     * always return true so this can be used in "while" loop conditions
     * @return true
     */
    public boolean readSensors() {
        System.out.println("Reading sensors.");
        hardware.odo.update();
        Pose2D pos = hardware.odo.getPosition();
        rawDriveOdometer = pos.getX(DistanceUnit.INCH);
        rawStrafeOdometer = pos.getY(DistanceUnit.INCH);
        driveDistance = (rawDriveOdometer - driveOdometerOffset);
        strafeDistance = (rawStrafeOdometer - strafeOdometerOffset);

        Pose2D vel = hardware.odo.getVelocity();
        //vel.getHeading(AngleUnit.DEGREES);

        //YawPitchRollAngles orientation = hardware.odo.getRobotYawPitchRollAngles();
        //AngularVelocity angularVelocity = hardware.odo.getRobotAngularVelocity(AngleUnit.DEGREES);
        //YawPitchRollAngles orientation =
        //double angularVelocity = hardware.odo.getHeadingVelocity();

        rawHeading  = pos.getHeading(AngleUnit.DEGREES);
        heading     = rawHeading - headingOffset;
        turnRate    = vel.getHeading(AngleUnit.DEGREES);

        if (showTelemetry) {
            String data = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", pos.getX(DistanceUnit.INCH), pos.getY(DistanceUnit.INCH), pos.getHeading(AngleUnit.DEGREES));
            myOpMode.telemetry.addData("Position", data);
            System.out.println("Pinpoint: " + data);
            String velocity = String.format(Locale.US, "{XVel: %.3f, YVel: %.3f, HVel: %.3f}", vel.getX(DistanceUnit.INCH), vel.getY(DistanceUnit.INCH), vel.getHeading(AngleUnit.DEGREES));
            myOpMode.telemetry.addData("Velocity", velocity);
            System.out.println("Pinpoint: " + velocity);
            myOpMode.telemetry.addData("Drive Raw:Offset: ","%.6f %.6f", rawDriveOdometer, driveOdometerOffset);
            System.out.println("Raw Drive Odo: " + rawDriveOdometer + " | Drive Odo Offset: " + driveOdometerOffset);
            myOpMode.telemetry.addData("Strafe Raw:Offset: ","%.6f %.6f", rawStrafeOdometer, strafeOdometerOffset);
            System.out.println("Raw Strafe Odo: " + rawStrafeOdometer + " | Strafe Odo Offset: " + strafeOdometerOffset );
            myOpMode.telemetry.addData("Odom Ax:Lat", "%.6f %.6f", rawDriveOdometer - driveOdometerOffset, rawStrafeOdometer - strafeOdometerOffset);

            myOpMode.telemetry.addData("Dist Ax:Lat", "%5.2f %5.2f", driveDistance, strafeDistance);
            System.out.println("Calc Drive Distance: " + driveDistance + " | Calc Strafe Distance: " + strafeDistance);
            myOpMode.telemetry.addData("Head Deg:Rate", "%5.2f %5.2f", heading, turnRate);
            System.out.println("Calc Heading: " + heading + " | Calc Rate: " + turnRate);
        }
        return true;  // do this so this function can be included in the condition for a while loop to keep values fresh.
    }

    //  ########################  Mid level control functions.  #############################3#

    /**
     * Drive in the axial (forward/reverse) direction, maintain the current heading and don't drift sideways
     * @param distanceInches  Distance to travel.  +ve = forward, -ve = reverse.
     * @param power Maximum power to apply.  This number should always be positive.
     * @param holdTime Minimum time (sec) required to hold the final position.  0 = no hold.
     */
    public void drive(double distanceInches, double power, double holdTime) {
        resetOdometry();
        driveController.reset(distanceInches, power);   // achieve desired drive distance
        strafeController.reset(0);              // Maintain zero strafe drift
        yawController.reset();                          // Maintain last turn heading
        holdTimer.reset();

        while (myOpMode.opModeIsActive() && readSensors()){

            // implement desired axis powers
            moveRobot(driveController.getOutput(driveDistance), strafeController.getOutput(strafeDistance), yawController.getOutput(heading));
            System.out.println("moveRobot Drive: " + driveController.getOutput(driveDistance));
            System.out.println("moveRobot Strafe: " + strafeController.getOutput(strafeDistance));
            System.out.println("moveRobot Heading: " + yawController.getOutput(heading));

            // Time to exit?
            if (driveController.inPosition() && yawController.inPosition()) {
                if (holdTimer.time() > holdTime) {
                    break;   // Exit loop if we are in position, and have been there long enough.
                }
            } else {
                holdTimer.reset();
            }
            myOpMode.sleep(10);
        }
        stopRobot();
    }

    /**
     * Strafe in the lateral (left/right) direction, maintain the current heading and don't drift fwd/bwd
     * @param distanceInches  Distance to travel.  +ve = left, -ve = right.
     * @param power Maximum power to apply.  This number should always be positive.
     * @param holdTime Minimum time (sec) required to hold the final position.  0 = no hold.
     */
    public void strafe(double distanceInches, double power, double holdTime) {
        resetOdometry();

        driveController.reset(0.0);             //  Maintain zero drive drift
        strafeController.reset(distanceInches, power);  // Achieve desired Strafe distance
        yawController.reset();                          // Maintain last turn angle
        holdTimer.reset();

        while (myOpMode.opModeIsActive() && readSensors()){

            // implement desired axis powers
            moveRobot(driveController.getOutput(driveDistance), strafeController.getOutput(strafeDistance), yawController.getOutput(heading));

            // Time to exit?
            if (strafeController.inPosition() && yawController.inPosition()) {
                if (holdTimer.time() > holdTime) {
                    break;   // Exit loop if we are in position, and have been there long enough.
                }
            } else {
                holdTimer.reset();
            }
            myOpMode.sleep(10);
        }
        stopRobot();
    }

    /**
     * Rotate to an absolute heading/direction
     * @param headingDeg  Heading to obtain.  +ve = CCW, -ve = CW.
     * @param power Maximum power to apply.  This number should always be positive.
     * @param holdTime Minimum time (sec) required to hold the final position.  0 = no hold.
     */
    public void turnTo(double headingDeg, double power, double holdTime) {

        yawController.reset(headingDeg, power);
        while (myOpMode.opModeIsActive() && readSensors()) {

            // implement desired axis powers
            moveRobot(0, 0, yawController.getOutput(heading));

            // Time to exit?
            if (yawController.inPosition()) {
                if (holdTimer.time() > holdTime) {
                    break;   // Exit loop if we are in position, and have been there long enough.
                }
            } else {
                holdTimer.reset();
            }
            myOpMode.sleep(10);
        }
        stopRobot();
    }


    //  ########################  Low level control functions.  ###############################

    /**
     * Drive the wheel motors to obtain the requested axes motions
     * @param drive     Fwd/Rev axis power
     * @param strafe    Left/Right axis power
     * @param yaw       Yaw axis power
     */
    public void moveRobot(double drive, double strafe, double yaw){

        double lF = drive - strafe - yaw;
        double rF = drive + strafe + yaw;
        double lB = drive + strafe - yaw;
        double rB = drive - strafe + yaw;

        double max = Math.max(Math.abs(lF), Math.abs(rF));
        max = Math.max(max, Math.abs(lB));
        max = Math.max(max, Math.abs(rB));

        //normalize the motor values
        if (max > 1.0)  {
            lF /= max;
            rF /= max;
            lB /= max;
            rB /= max;
        }

        //send power to the motors
        hardware.leftFront.setPower(lF);
        hardware.rightFront.setPower(rF);
        hardware.leftBack.setPower(lB);
        hardware.rightBack.setPower(rB);

        if (showTelemetry) {
            myOpMode.telemetry.addData("Axes D:S:Y", "%5.2f %5.2f %5.2f", drive, strafe, yaw);
            System.out.println("Drive: " + drive + " | Strafe: " + strafe + " | Yaw: " + yaw);
            myOpMode.telemetry.addData("Wheels lf:rf:lb:rb", "%5.2f %5.2f %5.2f %5.2f", lF, rF, lB, rB);
            System.out.println("Wheels | lF:" + lF + " | rF: " + rF + " | lB: " + lB + " | rB: " + rB);
            myOpMode.telemetry.update(); //  Assume this is the last thing done in the loop.
        }
    }

    /**
     * Stop all motors.
     */
    public void stopRobot() {
        moveRobot(0,0,0);
    }

    /**
     * Set odometry counts and distances to zero.
     */
    public void resetOdometry() {
        System.out.println("Resetting Odometry.");
        readSensors();
        driveOdometerOffset = rawDriveOdometer;
        System.out.println("Reset odo: New driveOdometerOffset: " + driveOdometerOffset);
        driveDistance = 0.0;
        driveController.reset(0);

        strafeOdometerOffset = rawStrafeOdometer;
        System.out.println("Reset odo: New strafeOdometerOffset: " + strafeOdometerOffset);
        strafeDistance = 0.0;
        strafeController.reset(0);
    }

    /**
     * Reset the robot heading to zero degrees, and also lock that heading into heading controller.
     */
    public void resetHeading() {
        System.out.println("Resetting Heading.");
        readSensors();
        headingOffset = rawHeading;
        System.out.println("Reset Heading: New headingOffset: " + headingOffset);
        yawController.reset(0);
        heading = 0;
    }

    public double getHeading() {return heading;}
    public double getTurnRate() {return turnRate;}

    /**
     * Set the drive telemetry on or off
     */
    public void showTelemetry(boolean show){
        showTelemetry = show;
    }
}

//****************************************************************************************************
//****************************************************************************************************

/***
 * This class is used to implement a proportional controller which can calculate the desired output power
 * to get an axis to the desired setpoint value.
 * It also implements an acceleration limit, and a max power output.
 */
class ProportionalControl {
    double  lastOutput;
    double  gain;
    double  accelLimit;
    double  defaultOutputLimit;
    double  liveOutputLimit;
    double  setPoint;
    double  tolerance;
    double deadband;
    boolean circular;
    boolean inPosition;
    ElapsedTime cycleTime = new ElapsedTime();

    public ProportionalControl(double gain, double accelLimit, double outputLimit, double tolerance, double deadband, boolean circular) {
        this.gain = gain;
        this.accelLimit = accelLimit;
        this.defaultOutputLimit = outputLimit;
        this.liveOutputLimit = outputLimit;
        this.tolerance = tolerance;
        this.deadband = deadband;
        this.circular = circular;
        reset(0.0);
    }

    /**
     * Determines power required to obtain the desired setpoint value based on new input value.
     * Uses proportional gain, and limits rate of change of output, as well as max output.
     * @param input  Current live control input value (from sensors)
     * @return desired output power.
     */
    public double getOutput(double input) {
        double error = setPoint - input;
        double dV = cycleTime.seconds() * accelLimit;
        double output;

        // normalize to +/- 180 if we are controlling heading
        if (circular) {
            while (error > 180)  error -= 360;
            while (error <= -180) error += 360;
        }

        inPosition = (Math.abs(error) < tolerance);

        // Prevent any very slow motor output accumulation
        if (Math.abs(error) <= deadband) {
            output = 0;
        } else {
            // calculate output power using gain and clip it to the limits
            output = (error * gain);
            output = Range.clip(output, -liveOutputLimit, liveOutputLimit);

            // Now limit rate of change of output (acceleration)
            if ((output - lastOutput) > dV) {
                output = lastOutput + dV;
            } else if ((output - lastOutput) < -dV) {
                output = lastOutput - dV;
            }
        }

        lastOutput = output;
        cycleTime.reset();
        return output;
    }

    public boolean inPosition(){
        return inPosition;
    }
    public double getSetpoint() {return setPoint;}

    /**
     * Saves a new setpoint and resets the output power history.
     * This call allows a temporary power limit to be set to override the default.
     * @param setPoint
     * @param powerLimit
     */
    public void reset(double setPoint, double powerLimit) {
        liveOutputLimit = Math.abs(powerLimit);
        this.setPoint = setPoint;
        reset();
    }

    /**
     * Saves a new setpoint and resets the output power history.
     * @param setPoint
     */
    public void reset(double setPoint) {
        liveOutputLimit = defaultOutputLimit;
        this.setPoint = setPoint;
        reset();
    }

    /**
     * Leave everything else the same, Just restart the acceleration timer and set output to 0
     */
    public void reset() {
        cycleTime.reset();
        inPosition = false;
        lastOutput = 0.0;
    }
}
