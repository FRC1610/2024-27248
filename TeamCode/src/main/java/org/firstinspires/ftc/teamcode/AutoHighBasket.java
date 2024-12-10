package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.StateMachine.State;

//@Disabled
@Autonomous(name="Auto High Basket", group="Auto")
public class AutoHighBasket extends LinearOpMode {
    org.firstinspires.ftc.teamcode.RobotHardware hardware = new RobotHardware(this);
    private SimplifiedOdometryRobot robot = new SimplifiedOdometryRobot(this, hardware);
    StateMachine StateMachine;
    private ElapsedTime AutoTimer = new ElapsedTime();

    @Override
    public void runOpMode(){
        StateMachine = new StateMachine(hardware);

        hardware.init();
        waitForStart();

        if (opModeIsActive()){
            StateMachine.update();
            AutoTimer.reset();
            StateMachine.setState(State.HANDOFF_WAIT);
            robot.drive(12,0.6,.25);
            robot.turnTo(90,.60,.25);
            StateMachine.setState(State.HIGH_BASKET);
            robot.drive(12,0.6,.25); //This is where it breaks
            StateMachine.setState(State.HIGH_BASKET_SCORE);
            robot.drive(-12,0.6,.25);
            sleep(500);
        }
    }
}
