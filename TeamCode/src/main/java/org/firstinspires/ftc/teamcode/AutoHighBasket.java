package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Disabled
@Autonomous(name="Auto High Basket", group="Auto")
public class AutoHighBasket extends LinearOpMode {
    org.firstinspires.ftc.teamcode.RobotHardware robot = new RobotHardware(this);

    @Override
    public void runOpMode(){

        robot.init();
        waitForStart();

        if (opModeIsActive()){
            //Auto Code goes here
        }
    }
}
