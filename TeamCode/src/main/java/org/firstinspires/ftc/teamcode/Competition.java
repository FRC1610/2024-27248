package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import org.firstinspires.ftc.teamcode.StateMachine.State;

import com.qualcomm.hardware.limelightvision.LLResult;
//import com.qualcomm.hardware.limelightvision.Limelight3A;
import java.util.Locale;

@TeleOp(name = "Competition Main", group = "TeleOp")
public class Competition extends LinearOpMode {

    GoBildaPinpointDriver odo; // Declare OpMode member for the Odometry Computer

    org.firstinspires.ftc.teamcode.RobotHardware robot = new RobotHardware(this);

    StateMachine StateMachine;

    @Override
    public void runOpMode() {

        StateMachine = new StateMachine(robot);

        ///Variable Setup
        //Odometry
        double oldTime;

        //Mecanum Drive
        double x;
        double y;
        double rotation;

        //Elevator
        boolean manualControl = false; // Default to position-based control
        boolean backButtonPreviouslyPressed = false; // To track toggle state
        double elevatorPower;

        //Intake
        //boolean IntakeClosed = true;
        //boolean IntakeButtonWasPressed = false;
        boolean RightBumperPressed = false;
        boolean aPressed = false;
        boolean leftBumperPressed = false;
        //double PosChange = 0.0;

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
            if (gamepad1.right_stick_button) {
                rotation = gamepad1.right_stick_x * 0.5; //Slow rotation mode when button pressed in
            } else {
                rotation = gamepad1.right_stick_x; // Rotation
            }

            robot.mecanumDrive(x, y, rotation);

            ///MANUAL CONTROL

            if (gamepad2.back && !backButtonPreviouslyPressed) {  //This requires gamepad2 which is normally not in use
                manualControl = !manualControl; // Toggle control mode
            }
            backButtonPreviouslyPressed = gamepad2.back; // Update previous state

            double intakeSlidePower;
            if (manualControl) {
                /// Manual elevator control using dpad
                if (gamepad2.dpad_up) {
                    elevatorPower = Constants.elevatorPowerUp;
                } else if (gamepad2.dpad_down) {
                    elevatorPower = Constants.elevatorPowerDown;
                } else {
                    elevatorPower = 0;
                }

                // Run the elevator with manual power
                robot.runElevator(elevatorPower);

                ///Manual INTAKE SLIDE Control
                if (gamepad2.dpad_right){
                    intakeSlidePower = Constants.intakeSlidePowerOut;
                } else if (gamepad2.dpad_left) {
                    intakeSlidePower = Constants.intakeSlidePowerIn;
                }
                else
                    intakeSlidePower = 0;

                robot.runIntakeSlide(intakeSlidePower);

            } else {
                // Position-based control

                if (gamepad1.dpad_right){
                    robot.IntakeRotate(-0.005);
                } else if (gamepad1.dpad_left) {
                    robot.IntakeRotate(.005);
                } else robot.IntakeRotate(0);

                if (gamepad1.dpad_up && StateMachine.getState() == State.PICKUP){
                    if (robot.intakeSlide.getCurrentPosition() < Constants.intakeSlideMax) {
                        intakeSlidePower = Constants.intakeSlidePowerOut;
                    }
                } else if (gamepad1.dpad_down && StateMachine.getState() == State.PICKUP) {
                    intakeSlidePower = Constants.intakeSlidePowerIn;
                } else intakeSlidePower = 0;

                if (StateMachine.getState() != State.PICKUP ||
                        StateMachine.getState() != State.PICKUP_RETRACT){
                robot.runIntakeSlide(intakeSlidePower);
                }
            }

            ///RESET ENCODERS
            if (gamepad1.back){
                robot.resetSlideEncoders();
                StateMachine.setState(State.HOME);  //Reset State when encoders are reset
            }

            ///INTAKE

            //Intake Pincher
            boolean IntakeButtonPressed = gamepad1.left_bumper; //Check if button pressed

            /// Elevator Pincher Rotation Test
            //TODO Remove this once State Machine handles this
            if (gamepad2.a){
                robot.elevatorPivot(0.01);
            } else if (gamepad2.b) {
                robot.elevatorPivot(-0.01);
            } else {
                robot.elevatorPivot(0);
            }

            //TODO Remove this once State Machine handles this
            if (gamepad2.x){
                robot.setElevatorPincher(0.05);
            } else if (gamepad2.y) {
                robot.setElevatorPincher(-0.05);
            } else {
                robot.setElevatorPincher(0);
            }

            telemetry.addData("Elev Pivot", robot.elevatorPivot.getPosition());
            telemetry.addData("Elev Pinch Rotate", robot.elevatorPincherRotate.getPosition());
            telemetry.addData("Elev Pinch Pos", robot.elevatorPincher.getPosition());

            ///STATE CHANGE BUTTON SETUP
            //TODO Maybe rearrange this
            /// START
            if (gamepad1.start) {
                StateMachine.setState(State.HOME);  //START = HOME Position
            /// X
            } else if (gamepad1.x){
                StateMachine.setState(State.WALL_PICKUP);
            /// A
            } else if (gamepad1.a && !aPressed) {
                StateMachine.setState(State.PICKUP); //A = PICKUP Position
                aPressed = true;
            /// B
            } else if (gamepad1.b && StateMachine.getState() == State.INTAKE_WAIT) {
                StateMachine.setState(State.DROPOFF);
            } else if (gamepad1.b &&
                    StateMachine.getState() != State.WALL_PICKUP &&
                    StateMachine.getState() != State.DROPOFF) { //B = WALL PICKUP Position
                StateMachine.setState(State.WALL_PICKUP);
            /// Y
            } else if (gamepad1.y && StateMachine.getState() == State.WALL_PICKUP) {  //Y + WALL_PICKUP = HIGH_CHAMBER
                StateMachine.setState(State.HIGH_CHAMBER);
            } else if (gamepad1.y && StateMachine.getState() == State.INTAKE_WAIT) { //Y + INTAKE_WAIT = HANDOFF
                StateMachine.setState(State.HANDOFF);
            } else if (gamepad1.y && StateMachine.getState() == State.HANDOFF_WAIT) {
                StateMachine.setState(State.HIGH_BASKET);
            /// RB
            } else if (gamepad1.right_bumper &&
                    StateMachine.getState() == State.WALL_PICKUP
                    && !RightBumperPressed) {
                StateMachine.setState(State.WALL_TO_CHAMBER);
                RightBumperPressed = true;
            } else if (gamepad1.right_bumper &&
                    StateMachine.getState() != State.HANDOFF_WAIT &&
                    !RightBumperPressed) {
                StateMachine.setState(State.PICKUP_RETRACT);
                RightBumperPressed = true;
            /// LB
            } else if (gamepad1.left_bumper && !leftBumperPressed) {
                if (StateMachine.getState() == State.SEARCH_WAIT) {
                    StateMachine.setState(State.MINI_INTAKE);
                } else if (StateMachine.getState() == State.PICKUP_WAIT) {
                    StateMachine.setState(State.INTAKE_SEARCH);
                } else if (StateMachine.getState() == State.WALL_TO_CHAMBER) {
                    StateMachine.setState(State.SCORE_CHAMBER);
                } else if (StateMachine.getState() == State.HIGH_BASKET){
                    StateMachine.setState(State.HIGH_BASKET_SCORE);
                }
                leftBumperPressed = true;
            } else if (!gamepad1.left_bumper) {
                leftBumperPressed = false;
            }

            if (!gamepad1.right_bumper) {
                RightBumperPressed = false;
            }

            if (!gamepad1.a){
                aPressed = false;
            }

            if (gamepad1.right_trigger > 0.1){
                robot.IntakePincherRotate(-0.01);
            } else if (gamepad1.left_trigger > 0.1) {
                robot.IntakePincherRotate(0.01);
            } else {
                robot.IntakePincherRotate(0);
            }

            StateMachine.update(); //Update state machine in case of long running tasks
            telemetry.addData("State", StateMachine.getState());


            telemetry.addData("Elevator Pos", robot.elevatorLift.getCurrentPosition());
            telemetry.addData("Intake Slide Pos", robot.intakeSlide.getCurrentPosition());
            telemetry.addData("Intake Pincher Pos",robot.intakePincher.getPosition());
            telemetry.addData("Intake Rotate Pos",robot.intakeRotate.getPosition());
            telemetry.addData("Intake Pincher Pos",robot.intakeLift.getPosition());
            telemetry.addData("Intake Pincher Pos",robot.intakePincherRotate.getPosition());
            telemetry.update();
        }
    }
}