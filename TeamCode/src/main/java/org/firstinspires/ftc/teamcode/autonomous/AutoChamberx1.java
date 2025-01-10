package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.SimplifiedOdometryRobot;
import org.firstinspires.ftc.teamcode.StateMachine;

@Disabled
@Autonomous(name="Auto Chamber x1", group="Auto")
public class AutoChamberx1 extends LinearOpMode {
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
        telemetry.addData(">", "Touch Play to run Auto");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()){
            AutoTimer.reset();
            robot.drive(6,0.6,0.25);
            hardware.intakePincher.setPosition(Constants.intakePincherClosed);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotHandoff);
            hardware.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
            hardware.elevatorPincherRotate.setPosition(Constants.elevatorPincherRotateHandoff);
            hardware.intakePincherRotate.setPosition(Constants.intakePincherRotateHandoff);
            hardware.intakeRotate.setPosition(Constants.intakeRotateHandoffPosition);
            hardware.intakeLift.setPosition(Constants.intakeLiftHandoff);
            sleep(1000);

            //Handoff Begin
            hardware.elevatorPincher.setPosition(Constants.elevatorPincherClosed);
            sleep(400);
            hardware.intakePincher.setPosition(Constants.intakePincherOpen);
            sleep(400);
            hardware.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
            sleep(700);
            robot.drive(25, 0.45, 0);

            hardware.setElevator(Constants.elevatorHighChamberScore);
            while (opModeIsActive() &&
                    hardware.elevatorLift.isBusy()
            ) {
                hardware.elevatorPincher.setPosition(Constants.elevatorPincherClosed);
            }

            runtime.reset();
            while (opModeIsActive() &&
                runtime.seconds() < 0.75){
                    // added to make it easier to tweak wheel power
                    double chamberWheelPower = 0.1;
                    hardware.leftFront.setPower(chamberWheelPower);
                    hardware.leftBack.setPower(chamberWheelPower);
                    hardware.rightFront.setPower(chamberWheelPower);
                    hardware.rightBack.setPower(chamberWheelPower);
                    if (runtime.seconds() > 0.5){
                        hardware.elevatorPivot.setPosition(Constants.elevatorPivotChamber);
                    }
            }

            hardware.setElevator(Constants.elevatorHome);
            while (opModeIsActive() &&
                    hardware.elevatorLift.isBusy()
            ) {
                hardware.elevatorPincher.setPosition(Constants.elevatorPincherClosed);
            }
            hardware.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
            sleep(400);
            robot.drive(-7, 0.75, 0.25);
            // working to here just doing this to test my skill
            int autoSleepTime = 300;
            int autoSleepToGiveHumanPlayerTimeToReactOrElseTheyMightGetHurt = 700;
            double autoPowerToPushToHumanPlayer = 0.7;
            double autoPowerToPullFromHumanPlayer = 0.75;
            double autoHoldTime = 0;

            sleep(autoSleepTime);
            robot.strafe(-34, autoPowerToPushToHumanPlayer, autoHoldTime);
            sleep(autoSleepTime);
            robot.drive(30, autoPowerToPushToHumanPlayer, autoHoldTime);
            sleep(autoSleepTime);
            robot.strafe(-6, autoPowerToPushToHumanPlayer, autoHoldTime);
            sleep(autoSleepTime);
            robot.drive(-40, autoPowerToPushToHumanPlayer, autoHoldTime);
            sleep(autoSleepTime);
            robot.drive(40, autoPowerToPullFromHumanPlayer, autoHoldTime);
            sleep(autoSleepToGiveHumanPlayerTimeToReactOrElseTheyMightGetHurt);
            robot.strafe(-12, autoPowerToPushToHumanPlayer, autoHoldTime);
            sleep(autoSleepTime);
            robot.drive(-40, autoPowerToPushToHumanPlayer, autoHoldTime);
            sleep(autoSleepTime);
            robot.drive(40, autoPowerToPullFromHumanPlayer, autoHoldTime);
            sleep(autoSleepToGiveHumanPlayerTimeToReactOrElseTheyMightGetHurt);
            robot.strafe(-6, autoPowerToPushToHumanPlayer, autoHoldTime);
            sleep(autoSleepTime);
            robot.drive(-40, autoPowerToPushToHumanPlayer, autoHoldTime);
            sleep(autoSleepTime);
            robot.drive(40, autoPowerToPullFromHumanPlayer, autoHoldTime);
            sleep(autoSleepToGiveHumanPlayerTimeToReactOrElseTheyMightGetHurt);
        }
    }
}
