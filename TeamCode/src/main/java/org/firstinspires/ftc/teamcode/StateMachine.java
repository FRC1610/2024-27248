package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

public class StateMachine {
    public enum State {
        HOME,
        PICKUP,
        WALLPICKUP,
        HANDOFF,
        PICKUP_TO_HANDOFF,
        HIGHCHAMBER,
        HIGHBASKET;
    }

    private ElapsedTime timer1 = new ElapsedTime();
    private ElapsedTime timer2 = new ElapsedTime();
    private int currentHandoffSubStep = 0;
    private int currentPickupSequenceSubstep = 0;
    private State currentState;
    private final RobotHardware robot;

    public StateMachine(RobotHardware hardware) {
        this.robot = hardware;
        this.currentState = State.HOME;
    }

    public void setState(State state) {
        this.currentState = state;
        this.currentPickupSequenceSubstep = 0;
        this.currentHandoffSubStep = 0;
        timer1.reset();
        timer2.reset();
        updateIntakeServos();
    }

    public void update(){
        switch (currentState){
            case HOME:
                break;
            case PICKUP:
                break;
            case WALLPICKUP:
                break;
            case HANDOFF:
                intakeHandoffSequence();
            case PICKUP_TO_HANDOFF:
                intakePickupSequence();
            case HIGHCHAMBER:
                break;
            case HIGHBASKET:
                break;
        }
    }

    public State getState() {
        return currentState;
    }

    private void updateIntakeServos() {
        switch (currentState) {
            case HOME:
                robot.intakePincher.setPosition(Constants.intakePincherClosed);
                robot.intakeRotate.setPosition(Constants.intakeRotateHome);
                robot.intakeLift.setPosition(Constants.intakeLiftHome);
                robot.intakePincherRotate.setPosition(Constants.intakePincherRotateHome);
                robot.setIntakeSlide(Constants.intakeSlideHome);
                robot.elevatorPivot.setPosition(Constants.elevatorPivotHome);
                robot.setElevator(Constants.elevatorHome);
                break;

            case PICKUP:
                robot.intakePincher.setPosition(Constants.intakePincherOpen);
                robot.intakeRotate.setPosition(Constants.intakeRotateIntakePosition);
                robot.intakeLift.setPosition(Constants.intakeLiftSearchPosition);
                robot.intakePincherRotate.setPosition(Constants.intakePincherRotateIntake);
                robot.setIntakeSlide(Constants.intakeSlideIntake);
                robot.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
                break;

            case HANDOFF:
                intakeHandoffSequence();
                if (currentHandoffSubStep ==0){
                    //TODO
                }
                break;

            case PICKUP_TO_HANDOFF:
                robot.intakePincher.setPosition(Constants.intakePincherClosed);
                intakePickupSequence();
                break;
        }
    }

    private void intakePickupSequence(){
        switch (currentPickupSequenceSubstep){
            case 0:
                timer1.reset();
                robot.intakeLift.setPosition(Constants.intakeLiftIntakePosition);
                currentPickupSequenceSubstep++;
                break;
            case 1:
                if (timer1.seconds() > 0.15) {
                    robot.intakePincher.setPosition(Constants.intakePincherClosed);
                    currentPickupSequenceSubstep++;
            }
                break;
            case 2:
                //System.out.println("Timer1: " + timer1.seconds());
                if (timer1.seconds() > 0.1) {
                    setState(State.HANDOFF);
                    //intakeHandoffSequence();
                    currentPickupSequenceSubstep++;
                }
                break;
            case 3:
                currentPickupSequenceSubstep = 0;
                break;
        }
    }

    private void intakeHandoffSequence(){
        switch (currentHandoffSubStep){
            case 0:
                timer2.reset();
                robot.intakeRotate.setPosition(Constants.intakeRotateHandoffPosition);
                robot.setIntakeSlide(Constants.intakeSlideHome);
                robot.setElevator(Constants.elevatorHome);
                currentHandoffSubStep++;
                if (robot.intakeSlide.getCurrentPosition() < 50){
                    break;
                }
            case 1:
                //System.out.println("Timer2: " + timer2.seconds());
                if (timer2.seconds() > 0.10){
                    robot.intakeLift.setPosition(Constants.intakeLiftHome);
                    robot.setIntakeSlide(Constants.intakeSlideHome);
                    robot.intakePincherRotate.setPosition(Constants.intakePincherRotateHandoff);
                    robot.elevatorPivot.setPosition(Constants.elevatorPivotHandoff);
                    currentHandoffSubStep++;
                    //setIntakeState(IntakeState.HAND_OFF);
                }
                break;
            case 2:
                //Sequence complete
                currentHandoffSubStep = 0;
                break;
        }
    }
}
