package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

public class StateMachine {
    public enum State {
        HOME,
        PICKUP,
        WALL_PICKUP,
        HANDOFF,
        PICKUP_TO_HANDOFF,
        HIGHCHAMBER,
        HIGHBASKET,
        WALL_TO_CHAMBER,
        INTAKE_SEARCH,
        MINI_INTAKE,
        DROPOFF,
        SEARCH_WAIT
    }

    private ElapsedTime intakeTimer1 = new ElapsedTime();
    private ElapsedTime intakeTimer2 = new ElapsedTime();
    private ElapsedTime pickupTimer = new ElapsedTime();
    private int currentHandoffSubStep = 0;
    private int currentPickupSequenceSubstep = 0;
    private int currentSearchSubstep = 0;
    private int currentMiniIntakeSubstep = 0;
    private int SearchSubstep = 0;
    private int DropoffSubstep = 0;
    private State currentState;
    private final RobotHardware robot;

    public StateMachine(RobotHardware hardware) {
        this.robot = hardware;
        this.currentState = State.HOME;
    }

    public void setState(State state) {
        System.out.println("Transitioning to State: " + state);
        this.currentState = state;
        this.currentPickupSequenceSubstep = 0;
        this.currentHandoffSubStep = 0;
        this.currentSearchSubstep = 0;
        this.currentMiniIntakeSubstep = 0;
        this.SearchSubstep = 0;
        this.DropoffSubstep = 0;
        intakeTimer1.reset();
        intakeTimer2.reset();
        pickupTimer.reset();
        updatePositions();
    }

    public void update(){
        switch (currentState){
            case HOME:
                break;
            case PICKUP:
                searchPosition();
                break;
            case WALL_PICKUP:
                break;
            case HANDOFF:
                intakeHandoffSequence();
                break;
            case PICKUP_TO_HANDOFF:
                intakePickupSequence();
                break;
            case HIGHCHAMBER:
                break;
            case HIGHBASKET:
                break;
            case WALL_TO_CHAMBER:
                break;
            case INTAKE_SEARCH:
                intakeSearch();
                break;
            case MINI_INTAKE:
                intakeMini();
                break;
            case DROPOFF:
                break;
            case SEARCH_WAIT:
                break;
        }
    }

    public State getState() {
        return currentState;
    }

    private void updatePositions() {
        switch (currentState) {
            case HOME: //Set all positions to home
                robot.intakePincher.setPosition(Constants.intakePincherClosed);
                robot.intakeRotate.setPosition(Constants.intakeRotateHome);
                robot.intakeLift.setPosition(Constants.intakeLiftHome);
                robot.intakePincherRotate.setPosition(Constants.intakePincherRotateHome);
                robot.setIntakeSlide(Constants.intakeSlideHome);
                robot.elevatorPivot.setPosition(Constants.elevatorPivotHome);
                robot.setElevator(Constants.elevatorHome);
                break;

            case PICKUP: //Set floor pickup default search position (slide out, intake deployed)
                searchPosition();
                break;

            case INTAKE_SEARCH:
                intakeSearch();
                break;

            case MINI_INTAKE:
                intakeMini();
                break;

            case HANDOFF: //Call the Handoff sequence
                intakeHandoffSequence();
                break;

            case PICKUP_TO_HANDOFF: //Results in a set of cases being called
                robot.intakePincher.setPosition(Constants.intakePincherClosed);
                intakePickupSequence();
                break;

            case WALL_PICKUP: //Set the position to pickup specimen from HP wall, claw open
                intakeAllHome();
                robot.setElevator(Constants.elevatorHome);
                robot.elevatorPivot.setPosition(Constants.elevatorPivotWallPickup);
                robot.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
                break;

            case HIGHCHAMBER:
                intakeAllHome(); //Make sure the intake is inside frame
                robot.setElevator(Constants.elevatorHighChamber);
                robot.elevatorPivot(Constants.elevatorPivotBasket);
                break;

            case DROPOFF:

                break;

            case SEARCH_WAIT:
                break;
        }
    }

    private void intakeAllHome(){
        ///Set all intake positions to home
        robot.intakePincher.setPosition(Constants.intakePincherClosed);
        robot.intakeRotate.setPosition(Constants.intakeRotateHome);
        robot.intakeLift.setPosition(Constants.intakeLiftHome);
        robot.intakePincherRotate.setPosition(Constants.intakePincherRotateHome);
        robot.setIntakeSlide(Constants.intakeSlideHome);
    }

    private void dropoffSequence(){
        switch (DropoffSubstep){
            case 0:
                intakeAllHome();
                DropoffSubstep++;
                break;
            case 1:
                if (robot.intakeSlide.getCurrentPosition() < 50){
                    robot.intakeLift.setPosition(Constants.intakeLiftDropoffPosition);
                    DropoffSubstep++;
                    break;
                }
                break;
            case 2:
                robot.intakePincher.setPosition(Constants.intakePincherOpen);
                DropoffSubstep++;
                break;
            case 3:
                DropoffSubstep = 0;
                break;
        }
    }

    private void searchPosition(){
        switch (SearchSubstep){
            case 0:
                robot.intakePincher.setPosition(Constants.intakePincherOpen);
                robot.intakeRotate.setPosition(Constants.intakeRotateIntakePosition);
                robot.intakeLift.setPosition(Constants.intakeLiftSearchPosition);
                robot.intakePincherRotate.setPosition(Constants.intakePincherRotateIntake);
                robot.setIntakeSlide(Constants.intakeSlideIntake);
                robot.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
                if (Math.abs(robot.intakeSlide.getCurrentPosition() - Constants.intakeSlideIntake) < 50){
                    SearchSubstep++;
                    break;
                }
                break;
            case 1:
                setState(State.SEARCH_WAIT);
                SearchSubstep = 0;
                break;
        }
    }

    private void intakeSearch(){
        switch (currentSearchSubstep){
            case 0:
                //pickupTimer.reset();
                robot.intakeLift.setPosition(Constants.intakeLiftSearchPosition);
                robot.intakePincher.setPosition(Constants.intakePincherOpen);
                currentSearchSubstep++;
                break;
            case 1:
                currentSearchSubstep = 0;
                break;
        }
    }

    private void intakeMini(){
        switch (currentMiniIntakeSubstep){
            case 0:
                pickupTimer.reset();
                robot.intakePincher.setPosition(Constants.intakePincherClosed);
                currentMiniIntakeSubstep++;
                break;
            case 1:
                if (pickupTimer.seconds() > 0.10){
                    robot.intakeLift.setPosition(Constants.intakeLiftSearchPosition);
                    currentMiniIntakeSubstep++;
                    break;
                }
                break;
            case 2:
                currentMiniIntakeSubstep = 0;
                break;
        }
    }

    private void intakePickupSequence(){
        switch (currentPickupSequenceSubstep){
            case 0:
                intakeTimer1.reset();
                robot.intakeLift.setPosition(Constants.intakeLiftIntakePosition);  //Move intake servo down to pickup position
                currentPickupSequenceSubstep++;
                break;
            case 1:
                if (intakeTimer1.seconds() > 0.15) {
                    robot.intakePincher.setPosition(Constants.intakePincherClosed);  //Close servo
                    currentPickupSequenceSubstep++;
            }
                break;
            case 2:
                //System.out.println("Timer1: " + timer1.seconds());
                if (intakeTimer1.seconds() > 0.15) {
                    setState(State.HANDOFF);  //Move to Handoff
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
                intakeTimer2.reset();
                robot.intakeRotate.setPosition(Constants.intakeRotateHandoffPosition);
                robot.setIntakeSlide(Constants.intakeSlideHome);
                robot.setElevator(Constants.elevatorHome);
                currentHandoffSubStep++;
                if (robot.intakeSlide.getCurrentPosition() < 50){
                    break;
                }
                break;
            case 1:
                //System.out.println("Timer2: " + timer2.seconds());
                if (intakeTimer2.seconds() > 0.10){
                    robot.intakeLift.setPosition(Constants.intakeLiftHome);
                    robot.setIntakeSlide(Constants.intakeSlideHome);
                    robot.intakePincherRotate.setPosition(Constants.intakePincherRotateHandoff);
                    //robot.elevatorPivot.setPosition(Constants.elevatorPivotHandoff);
                    currentHandoffSubStep++;
                    //setIntakeState(IntakeState.HAND_OFF);
                }
                break;
            case 2:
                if (intakeTimer2.seconds() > 0.25){
                    robot.elevatorPincher.setPosition(Constants.elevatorPincherClosed);
                    currentHandoffSubStep++;
                    break;
                }
                break;
            case 3:
                if (intakeTimer2.seconds() > 0.5){
                    robot.intakePincher.setPosition((Constants.intakePincherOpen));
                    currentHandoffSubStep++;
                    break;
                }
                break;
            case 4:
                //Sequence complete
                currentHandoffSubStep = 0;
                break;
        }
    }
}
