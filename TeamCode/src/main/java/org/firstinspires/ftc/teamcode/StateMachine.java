package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

public class StateMachine {
    public enum State {
        HOME,
        PICKUP,
        WALL_PICKUP,
        HANDOFF,
        PICKUP_TO_HANDOFF,
        HIGH_CHAMBER,
        HIGH_BASKET,
        WALL_TO_CHAMBER,
        INTAKE_SEARCH,
        MINI_INTAKE,
        DROPOFF,
        SEARCH_WAIT,
        INTAKE_WAIT,
        HANDOFF_WAIT,
        SCORE_CHAMBER,
        CHAMBER_WAIT,
        HIGH_BASKET_SCORE,
        HIGH_BASKET_WAIT
    }

    private ElapsedTime intakeTimer1 = new ElapsedTime();
    private ElapsedTime handoffTimer = new ElapsedTime();
    private ElapsedTime pickupTimer = new ElapsedTime();
    private ElapsedTime wallPickupTimer = new ElapsedTime();
    private ElapsedTime scoreChamberTimer = new ElapsedTime();
    private ElapsedTime scoreHighBasketTimer = new ElapsedTime();
    private int currentHandoffSubStep = 0;
    private int currentPickupSequenceSubstep = 0;
    private int currentSearchSubstep = 0;
    private int currentMiniIntakeSubstep = 0;
    private int currentScoreChamberSubstep = 0;
    private int currentWallToChamberSubstep = 0;
    private int currentHighBasketScoreSubstep = 0;
    private int SearchSubstep = 0;
    private int DropoffSubstep = 0;
    private State currentState;
    private final RobotHardware robot;

    public StateMachine(RobotHardware hardware) {
        this.robot = hardware;
        this.currentState = State.HOME;
    }

    public void setState(State state) {
        System.out.println("Transitioning from " + currentState + " to " + state);
        this.currentState = state;
        this.currentPickupSequenceSubstep = 0;
        this.currentHandoffSubStep = 0;
        this.currentSearchSubstep = 0;
        this.currentMiniIntakeSubstep = 0;
        this.currentScoreChamberSubstep = 0;
        this.currentWallToChamberSubstep = 0;
        this.currentHighBasketScoreSubstep = 0;
        this.SearchSubstep = 0;
        this.DropoffSubstep = 0;
        intakeTimer1.reset();
        handoffTimer.reset();
        pickupTimer.reset();
        wallPickupTimer.reset();
        scoreChamberTimer.reset();
        scoreHighBasketTimer.reset();
        updatePositions();
    }

    public void update(){
        switch (currentState){
            case HOME:
                break;
            case PICKUP:
                //System.out.println("Currently in PICKUP update");
                searchPosition();
                break;
            case WALL_PICKUP:
                break;
            case HANDOFF:
                //System.out.println("Currently in HANDOFF update");
                HandoffSequence();
                break;
            case PICKUP_TO_HANDOFF:
                //System.out.println("Currently in PICKUP_TO_UPDATE update");
                intakePickupSequence();
                break;
            case HIGH_CHAMBER:
                break;
            case HIGH_BASKET:
                robot.setElevator(Constants.elevatorHighBasket);
                robot.elevatorPivot.setPosition(Constants.elevatorPivotBasket);
                break;
            case WALL_TO_CHAMBER:
                wallToChamber();
                break;
            case INTAKE_SEARCH:
                intakeSearch();
                //setState(State.INTAKE_SEARCH);
                break;
            case MINI_INTAKE:
                intakeMini();
                //setState(State.MINI_INTAKE);
                break;
            case DROPOFF:
                break;
            case SEARCH_WAIT:
                //System.out.println("Currently in SEARCH_WAIT update");
                //setState(State.SEARCH_WAIT);
                break;
            case INTAKE_WAIT:
                //setState(State.INTAKE_WAIT);
                break;
            case HANDOFF_WAIT:
                break;
            case SCORE_CHAMBER:
                scoreChamber();
                break;
            case CHAMBER_WAIT:
                break;
            case HIGH_BASKET_SCORE:
                highBasketScore();
                break;
        }
    }

    public State getState() {
        return currentState;
    }

    private void updatePositions() {
        switch (currentState) {
            case HOME: //Set all positions to home
                robot.elevatorPivot.setPosition(Constants.elevatorPivotHome);
                robot.setElevator(Constants.elevatorHome);
                intakeAllHome();
                break;

            case PICKUP: //Set floor pickup default search position (slide out, intake deployed)
                //System.out.println("Currently in PICKUP positions");
                searchPosition();
                break;

            case INTAKE_SEARCH:
                intakeSearch();
                break;

            case MINI_INTAKE:
                intakeMini();
                break;

            case HANDOFF: //Call the Handoff sequence
                //System.out.println("Currently in HANDOFF positions");
                HandoffSequence();
                break;

            case PICKUP_TO_HANDOFF: //Results in a set of cases being called
                //System.out.println("Currently in PICKUP_TO_HANDOFF positions");
                robot.intakePincher.setPosition(Constants.intakePincherClosed);
                intakePickupSequence();
                break;

            case WALL_PICKUP: //Set the position to pickup specimen from HP wall, claw open
                intakeAllHome();
                robot.setElevator(Constants.elevatorHome);
                robot.elevatorPivot.setPosition(Constants.elevatorPivotWallPickup);
                robot.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
                robot.elevatorPincherRotate.setPosition(Constants.elevatorPincherRotateHome);
                break;

            case HIGH_CHAMBER:
                intakeAllHome(); //Make sure the intake is inside frame
                robot.setElevator(Constants.elevatorHighChamber);
                robot.elevatorPivot(Constants.elevatorPivotBasket);
                break;

            case DROPOFF:
                break;

            case SEARCH_WAIT:
                break;

            case INTAKE_WAIT:
                break;

            case WALL_TO_CHAMBER:
                wallToChamber();
                break;

            case SCORE_CHAMBER:
                scoreChamber();
                break;

            case CHAMBER_WAIT:
                break;

            case HIGH_BASKET_SCORE:
                highBasketScore();
                break;

            case HIGH_BASKET_WAIT:
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

    private void highBasketScore(){
        switch (currentHighBasketScoreSubstep){
            case 0:
                scoreHighBasketTimer.reset();
                robot.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
                currentHighBasketScoreSubstep++;
                break;
            case 1:
                if (scoreHighBasketTimer.seconds() > 0.25){
                    robot.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
                    currentHighBasketScoreSubstep++;
                    break;
                }
                break;
            case 2:
                if (scoreHighBasketTimer.seconds() > 0.75){
                    robot.setElevator(Constants.elevatorHome);
                    currentHighBasketScoreSubstep++;
                    break;
                }
                break;
            case 3:
                currentHighBasketScoreSubstep = 0;
                setState(State.HIGH_BASKET_WAIT);
                break;
        }
    }

    private void wallToChamber(){
        System.out.println("Wall Timer " + wallPickupTimer.seconds());
        System.out.println("Wall Substep " + currentWallToChamberSubstep);
        switch (currentWallToChamberSubstep){
            case 0:
                wallPickupTimer.reset();
                robot.elevatorPincher.setPosition(Constants.elevatorPincherClosed);
                currentWallToChamberSubstep++;
                break;
            case 1:
                if (wallPickupTimer.seconds() > 1.5){
                    robot.elevatorPincher.setPosition(Constants.elevatorPincherClosed);
                    robot.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
                    robot.setElevator(Constants.elevatorHighChamber);
                    robot.elevatorPincherRotate.setPosition(Constants.elevatorPincherRotateHandoff);
                    currentWallToChamberSubstep++;
                    break;
                }
                break;
            case 2:
                currentWallToChamberSubstep = 0;
                break;
        }
    }

    private void scoreChamber(){
        System.out.println("Score Chamber Timer " + scoreChamberTimer.seconds());
        System.out.println("Score Chamber Substep " + currentScoreChamberSubstep);
        System.out.println("Elevator Pos: " + robot.elevatorLift.getCurrentPosition());
        switch (currentScoreChamberSubstep){
            case 0:
                scoreChamberTimer.reset();
                robot.elevatorPivot.setPosition(Constants.elevatorPivotChamber);
                currentScoreChamberSubstep++;
                break;
            case 1:
                if (scoreChamberTimer.seconds() > 0.5){
                    robot.setElevator(Constants.elevatorHighChamberScore);
                    currentScoreChamberSubstep++;
                    break;
                }
                break;
            case 2:
                if (scoreChamberTimer.seconds() > 2.5) {
                    robot.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
                    currentScoreChamberSubstep++;
                    break;
                }
                break;
            case 3:
                robot.setElevator(Constants.elevatorHome);
                currentScoreChamberSubstep++;
                break;
            case 4:
                currentScoreChamberSubstep = 0;
                setState(State.CHAMBER_WAIT);
                break;
        }
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
                robot.elevatorPivot.setPosition(Constants.elevatorPivotHandoff);
                robot.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
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
                setState(State.SEARCH_WAIT);
                currentSearchSubstep = 0;
                break;
        }
    }

    private void intakeMini(){
        switch (currentMiniIntakeSubstep){
            case 0:
                pickupTimer.reset();
                robot.intakeLift.setPosition(Constants.intakeLiftIntakePosition);
                currentMiniIntakeSubstep++;
                break;
            case 1:
                if (pickupTimer.seconds() > 0.10){
                    robot.intakePincher.setPosition(Constants.intakePincherClosed);
                    currentMiniIntakeSubstep++;
                    break;
                }
                break;
            case 2:
                setState(State.INTAKE_WAIT);
                currentMiniIntakeSubstep = 0;
                break;
        }
    }

    private void intakePickupSequence(){
        switch (currentPickupSequenceSubstep){
            case 0:
                intakeTimer1.reset();
                robot.intakeLift.setPosition(Constants.intakeLiftIntakePosition);  //Move intake servo down to pickup position
                robot.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
                robot.elevatorPincherRotate.setPosition(Constants.elevatorPincherRotateHandoff);
                currentPickupSequenceSubstep++;
                break;
            case 1:
                if (intakeTimer1.seconds() > 0.15) {
                    robot.intakePincher.setPosition(Constants.intakePincherClosed);  //Close servo
                    currentPickupSequenceSubstep++;
                    break;
                }
            break;
            case 2:
                //System.out.println("Timer1: " + timer1.seconds());
                if (intakeTimer1.seconds() > 0.15) {
                    setState(State.HANDOFF);  //Move to Handoff

                    //HandoffSequence();
                    currentPickupSequenceSubstep++;
                    break;
                }
                break;
            case 3:
                currentPickupSequenceSubstep = 0;
                break;
        }
    }

    private void HandoffSequence(){
        System.out.println("Entering Handoff Sequence");
        System.out.println("Handoff Substep: " + currentHandoffSubStep);
        System.out.println("Handoff Timer: " + handoffTimer.seconds());
        switch (currentHandoffSubStep){
            case 0:
                //handoffTimer.reset();
                robot.setIntakeSlide(Constants.intakeSlideHome);
                robot.setElevator(Constants.elevatorHome);
                robot.intakeRotate.setPosition(Constants.intakeRotateHandoffPosition);
                robot.intakePincherRotate.setPosition(Constants.intakePincherRotateHandoff);
                robot.elevatorPivot.setPosition(Constants.elevatorPivotHandoff);
                robot.elevatorPincherRotate.setPosition(Constants.elevatorPincherRotateHandoff);
                robot.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
                robot.intakeLift.setPosition(Constants.intakeLiftHandoff);
                if (robot.intakeSlide.getCurrentPosition() < 15){
                    handoffTimer.reset();
                    currentHandoffSubStep++;
                    break;
                }
                break;
            case 1:
                //System.out.println("Timer2: " + timer2.seconds());
                //if (handoffTimer.seconds() > 0.15){

                    currentHandoffSubStep++;
                //}
                break;
            case 2:
                if (handoffTimer.seconds() > 0.4){
                    robot.elevatorPincher.setPosition(Constants.elevatorPincherClosed);
                    currentHandoffSubStep++;
                    break;
                }
                break;
            case 3:
                if (handoffTimer.seconds() > 0.75){
                    robot.intakePincher.setPosition(Constants.intakePincherOpen);
                    currentHandoffSubStep++;
                    break;
                }
                break;
            case 4:
                if (handoffTimer.seconds() > 1.0){
                    robot.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
                    currentHandoffSubStep++;
                    break;
                }
                break;
            case 5:
                currentHandoffSubStep = 0;
                setState(State.HANDOFF_WAIT);
                break;
        }
    }
}
