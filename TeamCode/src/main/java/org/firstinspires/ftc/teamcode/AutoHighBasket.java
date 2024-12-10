package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.StateMachine.State;

@Disabled
@Autonomous(name="Auto High Basket", group="Auto")
public class AutoHighBasket extends LinearOpMode {
    org.firstinspires.ftc.teamcode.RobotHardware robot = new RobotHardware(this);
    StateMachine StateMachine;
    private ElapsedTime AutoTimer = new ElapsedTime();

    @Override
    public void runOpMode(){
        StateMachine = new StateMachine(robot);

        robot.init();
        waitForStart();

        if (opModeIsActive()){
            StateMachine.update();
            AutoTimer.reset();
            StateMachine.setState(State.HANDOFF_WAIT);
            sleep(1500);
        }
    }
}
