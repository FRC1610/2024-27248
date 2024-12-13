package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

//@Disabled
@Autonomous(name="Auto High Basket x4", group="Auto")
public class AutoHighBasketx4 extends LinearOpMode {
    RobotHardware hardware = new RobotHardware(this);
    private SimplifiedOdometryRobot robot = new SimplifiedOdometryRobot(this, hardware);
    StateMachine StateMachine;
    private ElapsedTime AutoTimer = new ElapsedTime();
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode(){
        StateMachine = new StateMachine(hardware);

        hardware.init();
        telemetry.addData(">>","Setup Preload!");
        telemetry.addData(">", "Touch Play to run Auto");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()){
            AutoTimer.reset();
            hardware.elevatorPincher.setPosition(Constants.elevatorPincherClosed);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
            robot.drive(8,0.6,.25);
            hardware.setElevator(Constants.elevatorHighBasket);
            robot.strafe(24,0.75,0.15);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotBasket);
            sleep(700);
            hardware.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
            sleep(400);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
            hardware.setElevator(Constants.elevatorHome);

            ///Left Pickup
            hardware.setIntakeSlide(Constants.autoIntakeSlideBasketLeft);
            runtime.reset();
            while (opModeIsActive() &&
                    //runtime.seconds() < 2.5 &&
                    hardware.intakeSlide.isBusy()
            ) {
                hardware.intakeLift.setPosition(Constants.autoIntakeLift);
                hardware.intakePincherRotate.setPosition(Constants.autoIntakePincherRotateBasketLeft);
                hardware.intakeRotate.setPosition(Constants.autoIntakeRotateBasketLeft);
                hardware.intakePincher.setPosition(Constants.intakePincherOpen);
            }

            runtime.reset();
            while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                hardware.intakeLift.setPosition(Constants.intakeLiftIntakePosition);
            }

            runtime.reset();
            while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                hardware.intakePincher.setPosition(Constants.intakePincherClosed);
            }

            ///Intake Retract to Handoff Position
            hardware.setIntakeSlide(Constants.intakeSlideHome);
            while (opModeIsActive() &&
                    //runtime.seconds() < 2.5 &&
                    hardware.intakeSlide.isBusy()
            ) {
                hardware.intakeRotate.setPosition(Constants.intakeRotateHandoffPosition);
                hardware.intakePincherRotate.setPosition(Constants.intakePincherRotateHandoff);
                hardware.intakeLift.setPosition(Constants.intakeLiftHandoff);
                hardware.elevatorPivot.setPosition(Constants.elevatorPivotHandoff);
                hardware.elevatorPincherRotate.setPosition(Constants.elevatorPincherRotateHandoff);
                hardware.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
            }

            hardware.elevatorPincher.setPosition(Constants.elevatorPincherClosed);
            sleep(400);
            hardware.intakePincher.setPosition(Constants.intakePincherOpen);
            sleep(400);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
            sleep(700);

            hardware.setElevator(Constants.elevatorHighBasket);
            //runtime.reset();
            while (opModeIsActive() &&
                    //runtime.seconds() < 2.5 &&
                    hardware.elevatorLift.isBusy()
            ) {
                hardware.elevatorPincher.setPosition(Constants.elevatorPincherClosed);
            }
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotBasket);
            sleep(700);
            hardware.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
            sleep(400);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
            hardware.setElevator(Constants.elevatorHome);

            ///Center Pickup
            hardware.setIntakeSlide(Constants.autoIntakeSlideBasketCenter);
            runtime.reset();
            while (opModeIsActive() &&
                    //runtime.seconds() < 3.0 &&
                    hardware.intakeSlide.isBusy()
            ) {
                hardware.intakeLift.setPosition(Constants.autoIntakeLift);
                hardware.intakePincherRotate.setPosition(Constants.autoIntakePincherRotateBasketCenter);
                hardware.intakeRotate.setPosition(Constants.autoIntakeRotateBasketCenter);
                hardware.intakePincher.setPosition(Constants.intakePincherOpen);
            }

            runtime.reset();
            while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                hardware.intakeLift.setPosition(Constants.intakeLiftIntakePosition);
            }

            runtime.reset();
            while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                hardware.intakePincher.setPosition(Constants.intakePincherClosed);
            }

            ///Intake Retract to Handoff Position
            hardware.setIntakeSlide(Constants.intakeSlideHome);
            while (opModeIsActive() &&
                    //runtime.seconds() < 2.5 &&
                    hardware.intakeSlide.isBusy()
            ) {
                hardware.intakeRotate.setPosition(Constants.intakeRotateHandoffPosition);
                hardware.intakePincherRotate.setPosition(Constants.intakePincherRotateHandoff);
                hardware.intakeLift.setPosition(Constants.intakeLiftHandoff);
                hardware.elevatorPivot.setPosition(Constants.elevatorPivotHandoff);
                hardware.elevatorPincherRotate.setPosition(Constants.elevatorPincherRotateHandoff);
                hardware.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
            }

            hardware.elevatorPincher.setPosition(Constants.elevatorPincherClosed);
            sleep(400);
            hardware.intakePincher.setPosition(Constants.intakePincherOpen);
            sleep(400);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
            sleep(700);

            hardware.setElevator(Constants.elevatorHighBasket);
            //runtime.reset();
            while (opModeIsActive() &&
                    //runtime.seconds() < 2.5 &&
                    hardware.elevatorLift.isBusy()
            ) {
                hardware.elevatorPincher.setPosition(Constants.elevatorPincherClosed);
            }
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotBasket);
            sleep(700);
            hardware.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
            sleep(400);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
            hardware.setElevator(Constants.elevatorHome);

            ///Slide over to get right specimen
            robot.strafe(-10.25,0.6,0.20);

            ///Right (basically center again) Pickup
            hardware.setIntakeSlide(Constants.autoIntakeSlideBasketCenter);
            runtime.reset();
            while (opModeIsActive() &&
                    //runtime.seconds() < 3.0 &&
                    hardware.intakeSlide.isBusy()
            ) {
                hardware.intakeLift.setPosition(Constants.autoIntakeLift);
                hardware.intakePincherRotate.setPosition(Constants.autoIntakePincherRotateBasketCenter);
                hardware.intakeRotate.setPosition(Constants.autoIntakeRotateBasketCenter);
                hardware.intakePincher.setPosition(Constants.intakePincherOpen);
            }

            runtime.reset();
            while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                hardware.intakeLift.setPosition(Constants.intakeLiftIntakePosition);
            }

            runtime.reset();
            while (opModeIsActive() && (runtime.seconds() < 0.5)) {
                hardware.intakePincher.setPosition(Constants.intakePincherClosed);
            }

            ///Intake Retract to Handoff Position
            hardware.setIntakeSlide(Constants.intakeSlideHome);
            while (opModeIsActive() &&
                    //runtime.seconds() < 2.5 &&
                    hardware.intakeSlide.isBusy()
            ) {
                hardware.intakeRotate.setPosition(Constants.intakeRotateHandoffPosition);
                hardware.intakePincherRotate.setPosition(Constants.intakePincherRotateHandoff);
                hardware.intakeLift.setPosition(Constants.intakeLiftHandoff);
                hardware.elevatorPivot.setPosition(Constants.elevatorPivotHandoff);
                hardware.elevatorPincherRotate.setPosition(Constants.elevatorPincherRotateHandoff);
                hardware.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
            }

            hardware.elevatorPincher.setPosition(Constants.elevatorPincherClosed);
            sleep(400);
            hardware.intakePincher.setPosition(Constants.intakePincherOpen);
            sleep(400);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
            sleep(700);

            //Strafe back to wall
            robot.strafe(10.25,0.6,0);

            hardware.setElevator(Constants.elevatorHighBasket);
            //runtime.reset();
            while (opModeIsActive() &&
                    //runtime.seconds() < 2.5 &&
                    hardware.elevatorLift.isBusy()
            ) {
                hardware.elevatorPincher.setPosition(Constants.elevatorPincherClosed);
            }
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotBasket);
            sleep(700);
            hardware.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
            sleep(250);
            //robot.drive(6,0.6,0.25);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
            hardware.setElevator(Constants.elevatorHome);
            sleep(2500);

        }
    }
}
