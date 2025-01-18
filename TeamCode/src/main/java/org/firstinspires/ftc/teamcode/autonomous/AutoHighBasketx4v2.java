package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.SimplifiedOdometryRobot;
import org.firstinspires.ftc.teamcode.StateMachine;

//@Disabled
@Autonomous(name="Auto High Basket x4 v2", group="Auto")
public class AutoHighBasketx4v2 extends LinearOpMode {
    RobotHardware hardware = new RobotHardware(this);
    private SimplifiedOdometryRobot robot = new SimplifiedOdometryRobot(this, hardware);
    org.firstinspires.ftc.teamcode.StateMachine StateMachine;
    private ElapsedTime AutoTimer = new ElapsedTime();
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode(){
        StateMachine = new StateMachine(hardware);

        hardware.init();
        telemetry.addData(">>","Setup Preload!");
        telemetry.addData(">>","Check Alliance LED!");
        telemetry.addData(">", "Touch Play to run Auto");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()){
            AutoTimer.reset();

            hardware.elevatorPincher.setPosition(Constants.elevatorPincherClosed);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotVertical);

            ///Drive forward 8 inches then strafe left 24 inches
            robot.drive(8,0.6,.25);
            hardware.setElevator(Constants.elevatorHighBasket);
            robot.strafe(24,0.75,0.15);

            ///Score in High Basket
            hardware.setIntakeSlide(Constants.intakeSlideAutoReady);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotBasket);
            sleep(1000);
            hardware.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
            sleep(400);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotWait);
            hardware.setElevator(Constants.elevatorHome);

            ///Left Pickup
            hardware.intakeLift.setPosition(Constants.intakeLiftIntakePosition);
            sleep(250);
            hardware.intakeRotate.setPosition(Constants.autoIntakeRotateBasketLeft);
            sleep(250);
            hardware.setIntakeSlide(Constants.autoIntakeSlideBasketLeft);
            //runtime.reset();
            while (opModeIsActive() && hardware.intakeSlide.isBusy()) {
                hardware.elevatorPivot.setPosition(Constants.elevatorPivotWait);
                hardware.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
                hardware.intakeLift.setPosition(Constants.intakeLiftIntakePosition);
                hardware.intakeRotate.setPosition(Constants.autoIntakeRotateBasketLeft);
                hardware.runIntake(RobotHardware.ActiveIntake.IN);
            }

            ///INTAKE UNTIL SENSOR - 2.5 SEC TIMEOUT
            runtime.reset();
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotHandoff);
            while (opModeIsActive() && runtime.seconds() < 1.5 && hardware.intakeTouch.getState()){
                hardware.runIntake(RobotHardware.ActiveIntake.IN);
            }
            hardware.runIntake(RobotHardware.ActiveIntake.STOP);
            ///PICKUP
            while (opModeIsActive() && !StateMachine.intakeActivePickupSequence()){
                System.out.println("Auto: Active pickup sequence running.");
            }
            sleep(500);
            ///HANDOFF
            while (opModeIsActive() && !StateMachine.ActiveHandoffSequence()){
                System.out.println("Auto: Active handoff sequence running.");
            }
            sleep(500);
            ///ELEVATOR UP & SCORE
            hardware.setElevator(Constants.elevatorHighBasket);
            while (opModeIsActive() && hardware.elevatorLift.isBusy()){
                System.out.println("Waiting for elevator.");
            }
            hardware.setIntakeSlide(Constants.intakeSlideAutoReady);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotBasket);
            sleep(700);
            hardware.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
            sleep(400);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
            hardware.setElevator(Constants.elevatorHome);

            ///Center Pickup

            hardware.intakeLift.setPosition(Constants.intakeLiftIntakePosition);
            sleep(250);
            hardware.intakeRotate.setPosition(Constants.autoIntakeRotateBasketCenter);
            sleep(250);
            hardware.setIntakeSlide(Constants.autoIntakeSlideBasketCenter);
            //runtime.reset();
            while (opModeIsActive() && hardware.intakeSlide.isBusy()) {
                hardware.elevatorPivot.setPosition(Constants.elevatorPivotWait);
                hardware.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
                hardware.intakeLift.setPosition(Constants.intakeLiftIntakePosition);
                hardware.intakeRotate.setPosition(Constants.autoIntakeRotateBasketCenter);
                hardware.runIntake(RobotHardware.ActiveIntake.IN);
            }

            ///INTAKE UNTIL SENSOR - 2.5 SEC TIMEOUT
            runtime.reset();
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotHandoff);
            while (opModeIsActive() && runtime.seconds() < 1.5 && hardware.intakeTouch.getState()){
                hardware.runIntake(RobotHardware.ActiveIntake.IN);
            }
            hardware.runIntake(RobotHardware.ActiveIntake.STOP);
            ///PICKUP
            while (opModeIsActive() && !StateMachine.intakeActivePickupSequence()){
                System.out.println("Auto: Active pickup sequence running.");
            }
            sleep(500);
            ///HANDOFF
            while (opModeIsActive() && !StateMachine.ActiveHandoffSequence()){
                System.out.println("Auto: Active handoff sequence running.");
            }
            sleep(500);
            ///ELEVATOR UP & SCORE
            hardware.setElevator(Constants.elevatorHighBasket);
            while (opModeIsActive() && hardware.elevatorLift.isBusy()){
                System.out.println("Waiting for elevator.");
            }
            hardware.setIntakeSlide(Constants.intakeSlideAutoReady);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotBasket);
            sleep(700);
            hardware.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
            sleep(400);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
            hardware.setElevator(Constants.elevatorHome);

            ///Slide over to get right specimen
            robot.strafe(-10.25,0.6,0.20);

            ///Right (basically center again) Pickup

            hardware.intakeLift.setPosition(Constants.intakeLiftIntakePosition);
            sleep(250);
            hardware.intakeRotate.setPosition(Constants.autoIntakeRotateBasketCenter);
            sleep(250);
            hardware.setIntakeSlide(Constants.autoIntakeSlideBasketCenter);
            //runtime.reset();
            while (opModeIsActive() && hardware.intakeSlide.isBusy()) {
                hardware.elevatorPivot.setPosition(Constants.elevatorPivotWait);
                hardware.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
                hardware.intakeLift.setPosition(Constants.intakeLiftIntakePosition);
                hardware.intakeRotate.setPosition(Constants.autoIntakeRotateBasketCenter);
                hardware.runIntake(RobotHardware.ActiveIntake.IN);
            }

            ///INTAKE UNTIL SENSOR - 2.5 SEC TIMEOUT
            runtime.reset();
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotHandoff);
            while (opModeIsActive() && runtime.seconds() < 1.5 && hardware.intakeTouch.getState()){
                hardware.runIntake(RobotHardware.ActiveIntake.IN);
            }
            hardware.runIntake(RobotHardware.ActiveIntake.STOP);
            ///PICKUP
            while (opModeIsActive() && !StateMachine.intakeActivePickupSequence()){
                System.out.println("Auto: Active pickup sequence running.");
            }
            sleep(500);
            ///HANDOFF
            while (opModeIsActive() && !StateMachine.ActiveHandoffSequence()){
                System.out.println("Auto: Active handoff sequence running.");
            }
            sleep(500);

            //Strafe back to wall
            robot.strafe(10.25,0.6,0);

            ///ELEVATOR UP & SCORE
            hardware.setElevator(Constants.elevatorHighBasket);
            while (opModeIsActive() && hardware.elevatorLift.isBusy()){
                System.out.println("Waiting for elevator.");
            }
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotBasket);
            sleep(700);
            hardware.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
            sleep(400);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
            hardware.setElevator(Constants.elevatorHome);

            sleep(2500);



        }
    }
}
