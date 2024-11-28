package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import java.util.Locale;

@TeleOp(name = "Competition Main", group = "TeleOp")
public class Competition extends LinearOpMode {


    GoBildaPinpointDriver odo; // Declare OpMode member for the Odometry Computer

    org.firstinspires.ftc.teamcode.RobotHardware robot = new RobotHardware(this);

    @Override
    public void runOpMode() {

        ///Variable Setup
        //Odometry
        double oldTime = 0;

        //Mecanum Drive
        double x = 0;
        double y = 0;
        double rotation = 0;

        //Elevator
        double elevatorPower = 0;

        //Intake
        boolean IntakeClosed = true;
        boolean IntakeButtonWasPressed = false;
        double PosChange = 0.0;

        robot.init();  //Hardware configuration in RobotHardware.java

        waitForStart();
        resetRuntime();

        while (opModeIsActive()) {

            //Limelight Data
            LLResult result = robot.limelight.getLatestResult();
            if (result != null) {
                if (result.isValid()) {
                    Pose3D botpose = result.getBotpose();
                    telemetry.addData("tx", result.getTx());
                    telemetry.addData("ty", result.getTy());
                    telemetry.addData("Botpose", botpose.toString());
                }
            }

            //Odometry
            robot.odo.update(); //Update odometry
            double newTime = getRuntime();
            double loopTime = newTime - oldTime;
            double frequency = 1 / loopTime;
            oldTime = newTime;
            Pose2D pos = robot.odo.getPosition();
            String data = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", pos.getX(DistanceUnit.MM), pos.getY(DistanceUnit.MM), pos.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Position", data);
            Pose2D vel = robot.odo.getVelocity();
            String velocity = String.format(Locale.US, "{XVel: %.3f, YVel: %.3f, HVel: %.3f}", vel.getX(DistanceUnit.MM), vel.getY(DistanceUnit.MM), vel.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Velocity", velocity);
            telemetry.addData("Status", robot.odo.getDeviceStatus());
            telemetry.addData("Pinpoint Frequency", robot.odo.getFrequency()); //prints/gets the current refresh rate of the Pinpoint
            telemetry.addData("REV Hub Frequency: ", frequency); //prints the control system refresh rate

            ///MECANUM DRIVE

            // Get joystick inputs
            y = -gamepad1.left_stick_y; // Forward/backward
            x = gamepad1.left_stick_x;  // Strafe
            rotation = gamepad1.right_stick_x; // Rotation

            robot.mecanumDrive(x, y, rotation);

            ///ELEVATOR
            if (gamepad1.dpad_up){
                elevatorPower = 0.75;
            } else if (gamepad1.dpad_down) {
                elevatorPower = -0.50;
            }
            else
                elevatorPower = 0;

            robot.runElevator(elevatorPower);

            ///INTAKE

            //Intake Pincher
            boolean IntakeButtonPressed = gamepad1.a; //Check if button pressed

            if (IntakeButtonPressed && !IntakeButtonWasPressed){ //Button pressed in this loop
                IntakeClosed = !IntakeClosed; //Toggle position state
                if (IntakeClosed){
                    robot.CloseIntakePincher(); //Move to position 1
                } else {
                    robot.OpenIntakePincher(); //Move to position 2
                }
            }

            IntakeButtonWasPressed = IntakeButtonPressed; //Update previous button state

            /*
            if (gamepad1.right_bumper){
                robot.IntakePosition(0.005);
            } else if (gamepad1.left_bumper) {
                robot.IntakePosition(-0.005);
            } else {
                robot.IntakePosition(0);
            }

            //telemetry.addData("Intake Button Pressed", IntakeButtonPressed);
             */

            telemetry.update();
        }
    }
}