package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.StateMachine.State;

@TeleOp (name="Slide Testing", group = "TeleOp")
public class SlideTesting extends LinearOpMode {

    org.firstinspires.ftc.teamcode.RobotHardware robot = new RobotHardware(this);

    StateMachine StateMachine;

    @Override
    public void runOpMode(){
        StateMachine = new StateMachine(robot);

        //Mecanum Drive
        double x = 0;
        double y = 0;
        double rotation = 0;

        int ElevatorCurrentPosition = 0;
        int IntakeSlideCurrentPosition = 0;
        int ElevatorTargetPosition = 0;
        int IntakeTargetPosition = 0;

        boolean dPadUp = false;
        boolean dPadDown = false;
        boolean dPadRight = false;
        boolean dPadLeft = false;

        robot.init();
        robot.intakeRotate.setPosition(Constants.intakeRotateIntakePosition);
        waitForStart();

        while (opModeIsActive()) {

            ///MECANUM DRIVE

            // Get joystick inputs
            y = -gamepad1.left_stick_y * 0.75; // Forward/backward - multiply by 0.75 to scale speed down
            x = gamepad1.left_stick_x * 0.75;  // Strafe - multiply by 0.75 to scale speed down
            if (gamepad1.right_stick_button) {
                rotation = gamepad1.right_stick_x * 0.45; //Slow rotation mode when button pressed in
            } else {
                rotation = gamepad1.right_stick_x * 0.75; // Rotation - multiply by 0.75 to scale speed down
            }

            robot.mecanumDrive(x, y, rotation);

            ElevatorCurrentPosition = robot.elevatorLift.getCurrentPosition();
            IntakeSlideCurrentPosition = robot.intakeSlide.getCurrentPosition();

            if (gamepad1.dpad_up && !dPadUp) {
                dPadUp = true;
                ElevatorTargetPosition = ElevatorCurrentPosition + 50;
            } else if (gamepad1.dpad_down && !dPadDown) {
                dPadDown = true;
                ElevatorTargetPosition = ElevatorCurrentPosition - 50;
            }

            if (gamepad1.dpad_right && !dPadRight){
                dPadRight = true;
                IntakeTargetPosition = IntakeSlideCurrentPosition + 50;
            } else if (gamepad1.dpad_left && !dPadLeft){
                dPadLeft = true;
                IntakeTargetPosition = IntakeSlideCurrentPosition - 50;
            }

            if (ElevatorTargetPosition <0 ){
                ElevatorTargetPosition = 0;
            }

            if (IntakeTargetPosition <0) {
                IntakeTargetPosition = 0;
            }

            robot.setElevator(ElevatorTargetPosition);
            robot.setIntakeSlide(IntakeTargetPosition);

            //Active Intake Testing
            if (gamepad1.right_bumper && robot.intakeTouch.getState() ){
                robot.runIntake(RobotHardware.ActiveIntake.IN);
            } else if (gamepad1.left_bumper) {
                robot.runIntake(RobotHardware.ActiveIntake.OUT);
            } else if (!robot.intakeTouch.getState() && StateMachine.getState() != State.INTAKE_COLOR_CHECK){
                StateMachine.setState(State.INTAKE_COLOR_CHECK);
            } else if (StateMachine.getState() != State.INTAKE_COLOR_CHECK){
                robot.runIntake(RobotHardware.ActiveIntake.STOP);
            }

            if (gamepad1.a){
                robot.IntakeLift(0.01);
            } else if (gamepad1.b) {
                robot.IntakeLift(- 0.01);
            }

            if (gamepad1.x){
                robot.ElevatorPivot(0.01 );
            } else if (gamepad1.y){
                robot.ElevatorPivot(-0.01);
            }

            //Reset button trackers
            if (!gamepad1.dpad_up){
                dPadUp = false;
            }

            if (!gamepad1.dpad_down){
                dPadDown = false;
            }

            if (!gamepad1.dpad_right){
                dPadRight = false;
            }

            if (!gamepad1.dpad_left){
                dPadLeft = false;
            }

            telemetry.addData("Elevator Pos: ", robot.elevatorLift.getCurrentPosition());
            telemetry.addData("Intake Pos: ", robot.intakeSlide.getCurrentPosition());
            telemetry.addData("Intake Touch: ", robot.intakeTouch.getState());
            telemetry.addData("Intake Color ARGB: ", robot.intakeColor.argb());
            telemetry.addData("Intake Color R: ", robot.intakeColor.red());
            telemetry.addData("Intake Color G: ", robot.intakeColor.green());
            telemetry.addData("Intake Color B: ", robot.intakeColor.blue());
            telemetry.addData("Detected Color: ", StateMachine.detectedColor);
            telemetry.addData("State: ", StateMachine.getState());
            telemetry.addData("Elev Pivot: ", robot.elevatorPivot.getPosition());
            telemetry.addData("Intake Lift: ", robot.intakeLift.getPosition());
            telemetry.update();

            StateMachine.update();

        }

    }
}
