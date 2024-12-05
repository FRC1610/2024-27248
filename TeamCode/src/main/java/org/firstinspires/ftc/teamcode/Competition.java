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
        double oldTime = 0;

        //Mecanum Drive
        double x = 0;
        double y = 0;
        double rotation = 0;

        //Elevator
        boolean manualControl = false; // Default to position-based control
        boolean backButtonPreviouslyPressed = false; // To track toggle state
        double elevatorPower = 0;
        double intakeSlidePower = 0;

        //Intake
        boolean IntakeClosed = true;
        boolean IntakeButtonWasPressed = false;
        boolean RightBumperPressed = false;
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

            ///MANUAL CONTROL

            if (gamepad1.back && !backButtonPreviouslyPressed) {
                manualControl = !manualControl; // Toggle control mode
            }
            backButtonPreviouslyPressed = gamepad1.back; // Update previous state

            if (manualControl) {
                /// Manual elevator control using dpad
                if (gamepad1.dpad_up) {
                    elevatorPower = Constants.elevatorPowerUp;
                } else if (gamepad1.dpad_down) {
                    elevatorPower = Constants.elevatorPowerDown;
                } else {
                    elevatorPower = 0;
                }

                // Run the elevator with manual power
                robot.runElevator(elevatorPower);

                ///Manual INTAKE SLIDE Control
                if (gamepad1.dpad_right){
                    intakeSlidePower = Constants.intakeSlidePowerOut;
                } else if (gamepad1.dpad_left) {
                    intakeSlidePower = Constants.intakeSlidePowerIn;
                }
                else
                    intakeSlidePower = 0;

                robot.runIntakeSlide(intakeSlidePower);

            } else {
                // Position-based control
                /*
                if (gamepad1.y) {
                    // Move to high chamber position
                    robot.setElevator(Constants.elevatorHighChamber);
                } else if (gamepad1.b) {
                    // Move to mid-level position (example)
                    robot.setElevator(Constants.elevatorIntake);
                } else if (gamepad1.a) {
                    // Move to low-level position (example)
                    robot.setElevator(Constants.elevatorHome);
                }

                 */

                if (gamepad1.dpad_right){
                    robot.IntakeRotate(-0.005);
                } else if (gamepad1.dpad_left) {
                    robot.IntakeRotate(.005);
                } else robot.IntakeRotate(0);

                if (gamepad1.dpad_up){
                    intakeSlidePower = Constants.intakeSlidePowerOut;
                } else if (gamepad1.dpad_down) {
                    intakeSlidePower = Constants.intakeSlidePowerIn;
                } else intakeSlidePower = 0;

                robot.runIntakeSlide(intakeSlidePower);
            }

            /*
            if (gamepad1.dpad_up){
                elevatorPower = 0.75;
            } else if (gamepad1.dpad_down) {
                elevatorPower = -0.50;
            }
            else
                elevatorPower = 0;

            robot.runElevator(elevatorPower);

            //Elevator Position
            if (gamepad1.y){
                robot.setElevator(Constants.elevatorHighChamber);
            }

             */

            /*
            ///TEST ELEVATOR PIVOT
            if (gamepad1.dpad_right){
                robot.elevatorPivot(0.01);
            } else if (gamepad1.dpad_left) {
                robot.elevatorPivot(-.01);
            } else robot.elevatorPivot(0);
            telemetry.addData("Elev Pivot Pos", robot.elevatorPivot.getPosition());

             */

            ///RESET ENCODERS
            if (gamepad1.start){
                robot.resetSlideEncoders();
            }

            ///INTAKE

            //Intake Pincher
            boolean IntakeButtonPressed = gamepad1.left_bumper; //Check if button pressed

            if (IntakeButtonPressed && !IntakeButtonWasPressed){ //Button pressed in this loop
                IntakeClosed = !IntakeClosed; //Toggle position state
                if (IntakeClosed){
                    robot.CloseIntakePincher(); //Move to position 1
                } else {
                    robot.OpenIntakePincher(); //Move to position 2
                }
            }

            IntakeButtonWasPressed = IntakeButtonPressed; //Update previous button state


            if (gamepad2.a){
                robot.ElevatorPincherRotate(0.05);
            } else if (gamepad2.b) {
                robot.ElevatorPincherRotate(-0.05);
            } else {
                robot.ElevatorPincherRotate(0);
            }
            telemetry.addData("Elev Pinch Rotate", robot.elevatorPincherRotate.getPosition());

            if (gamepad2.y){
                robot.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
            } else if (gamepad2.x) {
                robot.elevatorPincher.setPosition(Constants.elevatorPincherClosed);
            }
            telemetry.addData("Elev Pinch Pos", robot.elevatorPincher.getPosition());

            ///STATE CHANGE BUTTON SETUP
            if (gamepad1.x) {
                StateMachine.setState(State.HOME);
            } else if (gamepad1.a) {
                StateMachine.setState(State.PICKUP);
            } else if (gamepad1.b) {
                StateMachine.setState(State.WALLPICKUP);
            } else if (gamepad1.y) {
                StateMachine.setState(State.HIGHCHAMBER);
            } else if (gamepad1.right_bumper && StateMachine.getState() == State.PICKUP && !RightBumperPressed){
                StateMachine.setState(State.PICKUP_TO_HANDOFF);
                RightBumperPressed = true;
            } else if (gamepad1.right_bumper && StateMachine.getState() == State.WALLPICKUP && !RightBumperPressed) {
                StateMachine.setState(State.WALLTOCHAMBER);
                RightBumperPressed = true;
            }

            if (!gamepad1.right_bumper) {
                RightBumperPressed = false;
            }

            if (gamepad1.right_trigger > 0.1){
                robot.IntakePincherRotate(-0.01);
            } else if (gamepad1.left_trigger > 0.1) {
                robot.IntakePincherRotate(0.01);
            } else {
                robot.IntakePincherRotate(0);
            }

            StateMachine.update();
            telemetry.addData("State", StateMachine.getState());

            //telemetry.addData("Intake Button Pressed", IntakeButtonPressed);

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