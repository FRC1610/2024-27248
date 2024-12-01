package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

public class IntakeStateMachine {
    public enum IntakeState {
        HOME,
        PICKUP,
        HANDOFF,
        PICKUP_TO_HANDOFF;
    }

    private ElapsedTime timer1 = new ElapsedTime();
    private ElapsedTime timer2 = new ElapsedTime();
    private int currentHandoffSubStep = 0;
    private int currentPickupSequenceSubstep = 0;
    private IntakeState currentIntakeState;
    private final RobotHardware robot;

    public IntakeStateMachine(RobotHardware hardware) {
        this.robot = hardware;
        this.currentIntakeState = IntakeState.HOME;
    }

    public void setIntakeState(IntakeState state) {
        this.currentIntakeState = state;
        updateIntakeServos();
    }

    public IntakeState getIntakeState() {
        return currentIntakeState;
    }

    private void updateIntakeServos() {
        switch (currentIntakeState) {
            case HOME:
                robot.intakePincher.setPosition(Constants.intakePincherClosed);
                robot.intakeRotate.setPosition(Constants.intakeRotateHome);
                robot.intakeLift.setPosition(Constants.intakeLiftHome);
                robot.intakePincherRotate.setPosition(Constants.intakePincherRotateHome);
                robot.setIntakeSlide(Constants.intakeSlideHome);
                break;

            case PICKUP:
                robot.intakePincher.setPosition(Constants.intakePincherOpen);
                robot.intakeRotate.setPosition(Constants.intakeRotateIntakePosition);
                robot.intakeLift.setPosition(Constants.intakeLiftIntakePosition);
                robot.intakePincherRotate.setPosition(Constants.intakePincherRotateIntake);
                robot.setIntakeSlide(Constants.intakeSlideIntake);
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
                robot.intakePincher.setPosition(Constants.intakePincherClosed);
                timer1.reset();
                currentPickupSequenceSubstep++;
                break;
            case 1:
                if (timer1.seconds() > 0.25) {
                    setIntakeState(IntakeState.HANDOFF);
                    //intakeHandoffSequence();
                    currentPickupSequenceSubstep++;
                }
                break;
            case 2:
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
                currentHandoffSubStep++;
                if (robot.intakeSlide.getCurrentPosition() < 50){
                    break;
                }
            case 1:
                if (timer2.seconds() > 0.25){
                    robot.intakeLift.setPosition(Constants.intakeLiftFullBack);
                    robot.setIntakeSlide(Constants.intakeSlideHome);
                    robot.intakePincherRotate.setPosition(Constants.intakePincherRotateHandoff);
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
