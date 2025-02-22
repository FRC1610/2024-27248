/* Created by Phil Malone. 2023.
    This class illustrates my simplified Odometry Strategy.
    It implements basic straight line motions but with heading and drift controls to limit drift.
    See the readme for a link to a video tutorial explaining the operation and limitations of the code.
 */

package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.SimplifiedOdometryRobot;

/*
 * This OpMode illustrates an autonomous opmode using simple Odometry
 * All robot functions are performed by an external "Robot" class that manages all hardware interactions.
 * Pure Drive or Strafe motions are maintained using two Odometry Wheels.
 * The IMU gyro is used to stabilize the heading during all motions
 */

@Disabled
@Autonomous(name="Sample Autonomous", group = "Mr. Phil")
public class SampleAutonomous extends LinearOpMode
{
    // get an instance of the "Robot" class.
    org.firstinspires.ftc.teamcode.RobotHardware hardware = new RobotHardware(this);
    private SimplifiedOdometryRobot robot = new SimplifiedOdometryRobot(this, hardware);

    @Override public void runOpMode()
    {
        // Initialize the robot hardware & Turn on telemetry
        hardware.init();
        System.out.println("Auto: Hardware Initialized");

        // Wait for driver to press start
        telemetry.addData(">", "Touch Play to run Auto");
        telemetry.update();

        waitForStart();
        System.out.println("Auto: Call resetHeading next");
        robot.resetHeading();  // Reset heading to set a baseline for Auto

        // Run Auto if stop was not pressed.
        if (opModeIsActive())
        {
            // Note, this example takes more than 30 seconds to execute, so turn OFF the auto timer.
            System.out.println("Auto: Begin Left Turn to 90");
            robot.turnTo(90,0.25,.25);
            System.out.println("Auto: Turn complete.");
            System.out.println("Auto: Beginning drive forward.");
            robot.drive(6,0.25,.15);
            System.out.println("Auto: Drive forward complete.");

            /*
            robot.drive(  3, 0.20, 0.25);
            System.out.println("Drive 1 finished. Beginning next.");
            robot.turnTo(90, 0.25, 0.5);
            System.out.println("Turn 1 finished. Beginning next.");
            robot.drive(  3, 0.20, 0.25);
            System.out.println("Drive 2 finished. Beginning next.");
            robot.turnTo(180, 0.25, 0.5);
            System.out.println("Turn 2 finished. Beginning next.");
             */

            // Drive the path again without turning.
            //robot.drive(  12, 0.60, 0.15);
            //robot.strafe( 12, 0.60, 0.15);
            //robot.drive( -12, 0.60, 0.15);
            //robot.strafe(-12, 0.60, 0.15);

            sleep(500);
            /*
            // Drive a large rectangle, turning at each corner
            robot.drive(  12, 0.60, 0.25);
            robot.turnTo(90, 0.45, 0.5);
            robot.drive(  12, 0.60, 0.25);
            robot.turnTo(180, 0.45, 0.5);
            robot.drive(  12, 0.60, 0.25);
            robot.turnTo(270, 0.45, 0.5);
            robot.drive(  12, 0.60, 0.25);
            robot.turnTo(0, 0.45, 0.5);
             */
        }
    }
}
