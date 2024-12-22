package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.SimplifiedOdometryRobot;
import org.firstinspires.ftc.teamcode.StateMachine;
import org.firstinspires.ftc.teamcode.StateMachine.State;

@Disabled
@Autonomous(name="Auto High Basket x1 + Touch Low Bar", group="Auto")
public class AutoHighBasketTouchLow extends LinearOpMode {
    org.firstinspires.ftc.teamcode.RobotHardware hardware = new RobotHardware(this);
    private SimplifiedOdometryRobot robot = new SimplifiedOdometryRobot(this, hardware);
    org.firstinspires.ftc.teamcode.StateMachine StateMachine;
    private ElapsedTime AutoTimer = new ElapsedTime();

    @Override
    public void runOpMode(){
        StateMachine = new StateMachine(hardware);

        hardware.init();
        waitForStart();

        if (opModeIsActive()){
            AutoTimer.reset();
            StateMachine.setState(State.HIGH_BASKET);
            sleep(2000);
            //hardware.elevatorPincherRotate.setPosition(Constants.elevatorPincherRotateAutoBasket);
            robot.drive(-14.5,0.6,.25);
            StateMachine.setState(State.HIGH_BASKET_SCORE);
            sleep(1500);
            StateMachine.setState(State.HOME);
            sleep(500);
            robot.strafe(16,0.6,.25);
            robot.drive(14.5, 0.6, .25);
            robot.strafe(36, 0.6, .25);
            robot.drive(13,0.6,.25);
            StateMachine.setState(State.AUTO_TOUCH_LOW_BAR);
            sleep(15000);
        }
    }

    /**
     * Executes a given state in the StateMachine and waits for its completion.
     *
     * @param targetState The state to execute.
     */
    private void executeState(StateMachine.State targetState) {
        StateMachine.setState(targetState);

        // Loop until the state is complete or the OpMode is stopped
        while (opModeIsActive() && StateMachine.getState() == targetState) {
            StateMachine.update(); // Allow the state machine to process substeps
            telemetry.addData("State", targetState);
            telemetry.update();
        }

        // Optional delay to avoid immediate transitions
        sleep(100);
    }
}
