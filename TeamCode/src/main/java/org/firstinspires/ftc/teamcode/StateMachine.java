package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;


/* STATE FLOW

HOME > PICKUP > SEARCH_WAIT > PICKUP_RETRACT > INTAKE_WAIT

INTAKE_WAIT > HANDOFF //TODO
INTAKE_WAIT > DROPOFF //TODO

HOME > WALL_PICKUP > WALL_TO_CHAMBER > SCORE_CHAMBER

HANDOFF > HIGH_BASKET > HIGH_BASKET_WAIT > HIGH_BASKET_SCORE > HOME

 */

public class StateMachine {
    public enum State {
        HOME,  //All positions home
        PICKUP, //Search Position
        WALL_PICKUP, //Wall Pickup Position
        HANDOFF, //Execute Handoff Sequence
        PICKUP_RETRACT, //Pickup and retract intake
        HIGH_CHAMBER, //High chamber scoring position
        HIGH_BASKET, //High basket scoring position
        WALL_TO_CHAMBER, //Pickup from wall and flip to Chamber position
        INTAKE_SEARCH, //Intake is in search position
        MINI_INTAKE, //Lower and pickup but don't retract
        INTAKE_WAIT, //Wait after retracting intake
        DROPOFF, //Rotate and drop onto side shield
        SEARCH_WAIT, //Hold in search position
        PICKUP_WAIT, //Hold in pickup position after mini intake
        HANDOFF_WAIT, //Wait after handoff
        SCORE_CHAMBER, //Score chamber sequence
        CHAMBER_WAIT,
        HIGH_BASKET_SCORE, //Score high basket sequence
        HIGH_BASKET_WAIT
    }

    private ElapsedTime intakeTimer1 = new ElapsedTime();
    private ElapsedTime handoffTimer = new ElapsedTime();
    private ElapsedTime pickupTimer = new ElapsedTime();
    private ElapsedTime wallPickupTimer = new ElapsedTime();
    private ElapsedTime scoreChamberTimer = new ElapsedTime();
    private ElapsedTime scoreHighBasketTimer = new ElapsedTime();
    private ElapsedTime dropoffTimer = new ElapsedTime();
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

        //Set all substep trackers to 0
        this.currentPickupSequenceSubstep = 0;
        this.currentHandoffSubStep = 0;
        this.currentSearchSubstep = 0;
        this.currentMiniIntakeSubstep = 0;
        this.currentScoreChamberSubstep = 0;
        this.currentWallToChamberSubstep = 0;
        this.currentHighBasketScoreSubstep = 0;
        this.SearchSubstep = 0;
        this.DropoffSubstep = 0;

        //Reset all timers
        intakeTimer1.reset();
        handoffTimer.reset();
        pickupTimer.reset();
        wallPickupTimer.reset();
        scoreChamberTimer.reset();
        scoreHighBasketTimer.reset();
        dropoffTimer.reset();

        updatePositions();
    }

    public void update(){ //This is called from the OpMode to make sure long sequences are able to complete
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
            case PICKUP_RETRACT:
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
                dropoffSequence();
                break;
            case SEARCH_WAIT:
                //System.out.println("Currently in SEARCH_WAIT update");
                //setState(State.SEARCH_WAIT);
                break;
            case PICKUP_WAIT:
                //setState(State.PICKUP_WAIT);
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
            case INTAKE_WAIT:
                break;
        }
    }

    public State getState() { //Return the current state
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

            case DROPOFF:
                dropoffSequence();
                break;

            case PICKUP_RETRACT: //Results in a set of cases being called
                //System.out.println("Currently in PICKUP_RETRACT positions");
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

            case HIGH_BASKET:
                robot.setElevator(Constants.elevatorHighBasket);
                robot.elevatorPivot.setPosition(Constants.elevatorPivotBasket);
                break;

            case WALL_TO_CHAMBER:
                wallToChamber();
                break;

            case SCORE_CHAMBER:
                scoreChamber();
                break;

            case HIGH_BASKET_SCORE:
                highBasketScore();
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
                currentHighBasketScoreSubstep++;  //Move to next step
                break;
            case 1:
                if (scoreHighBasketTimer.seconds() > 0.25){
                    robot.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
                    currentHighBasketScoreSubstep++;  //Move to next step
                    break;
                }
                break;
            case 2:
                if (scoreHighBasketTimer.seconds() > 0.75){
                    robot.setElevator(Constants.elevatorHome);
                    currentHighBasketScoreSubstep++;  //Move to next step
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
                currentWallToChamberSubstep++;  //Move to next step
                break;
            case 1:
                if (wallPickupTimer.seconds() > 1.5){
                    robot.elevatorPincher.setPosition(Constants.elevatorPincherClosed);
                    robot.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
                    robot.setElevator(Constants.elevatorHighChamber);
                    robot.elevatorPincherRotate.setPosition(Constants.elevatorPincherRotateHandoff);
                    currentWallToChamberSubstep++;  //Move to next step
                    break;
                }
                break;
            case 2:
                currentWallToChamberSubstep = 0;
                break;
        }
    }

    private void scoreChamber(){
        //System.out.println("Score Chamber Timer " + scoreChamberTimer.seconds());
        //System.out.println("Score Chamber Substep " + currentScoreChamberSubstep);
        //System.out.println("Elevator Pos: " + robot.elevatorLift.getCurrentPosition());
        switch (currentScoreChamberSubstep){
            case 0:
                scoreChamberTimer.reset();
                robot.elevatorPivot.setPosition(Constants.elevatorPivotChamber);
                currentScoreChamberSubstep++;  //Move to next step
                break;
            case 1:
                if (scoreChamberTimer.seconds() > 0.5){
                    robot.setElevator(Constants.elevatorHighChamberScore);
                    currentScoreChamberSubstep++;  //Move to next step
                    break;
                }
                break;
            case 2:
                if (scoreChamberTimer.seconds() > 2.5) {
                    robot.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
                    currentScoreChamberSubstep++;  //Move to next step
                    break;
                }
                break;
            case 3:
                robot.setElevator(Constants.elevatorHome);
                currentScoreChamberSubstep++;  //Move to next step
                break;
            case 4:
                currentScoreChamberSubstep = 0;
                setState(State.CHAMBER_WAIT);
                break;
        }
    }
    private void dropoffSequence(){
        System.out.println("Dropoff Substep " + DropoffSubstep);
        switch (DropoffSubstep){
            case 0:
                intakeAllHome(); //Make sure the intake is retracted
                if (robot.intakeSlide.getCurrentPosition() < 15){
                    DropoffSubstep++;  //Move to next step
                    break;
                }
                break;
            case 1:
                //robot.intakeLift.setPosition(Constants.intakeLiftDropoffPosition);
                robot.intakeRotate.setPosition(Constants.intakeRotateHome); //This should already be in home from intakeAllHome
                dropoffTimer.reset();
                DropoffSubstep++;  //Move to next step
                break;
            case 2:
                robot.intakePincher.setPosition(Constants.intakePincherOpen);
                if (dropoffTimer.seconds() > 0.25){ //Give the pincher time to open
                    DropoffSubstep++;  //Move to next step
                    break;
                }
                break;
            case 3:
                DropoffSubstep = 0;
                setState(State.WALL_PICKUP); //Giant assumption that you only want to dropoff if you're going to wall pickup
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
                    SearchSubstep++;  //Move to next step
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
                currentSearchSubstep++;  //Move to next step
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
                currentMiniIntakeSubstep++;  //Move to next step
                break;
            case 1:
                if (pickupTimer.seconds() > 0.10){
                    robot.intakePincher.setPosition(Constants.intakePincherClosed);
                    currentMiniIntakeSubstep++;  //Move to next step
                    break;
                }
                break;
            case 2:
                setState(State.PICKUP_WAIT);
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
                currentPickupSequenceSubstep++;  //Move to next step
                break;
            case 1:
                if (intakeTimer1.seconds() > 0.15) {
                    robot.intakePincher.setPosition(Constants.intakePincherClosed);  //Close servo
                    currentPickupSequenceSubstep++;  //Move to next step
                    break;
                }
            break;
            case 2:
                if (intakeTimer1.seconds() > 0.35){ //Give intake servo time to close
                    //Intake
                    robot.setIntakeSlide(Constants.intakeSlideHome);
                    robot.intakeRotate.setPosition(Constants.intakeRotateHandoffPosition);
                    robot.intakePincherRotate.setPosition(Constants.intakePincherRotateHandoff);
                    robot.intakeLift.setPosition(Constants.intakeLiftHandoff);
                    //Elevator
                    robot.setElevator(Constants.elevatorHome);
                    robot.elevatorPivot.setPosition(Constants.elevatorPivotWait);
                    robot.elevatorPincherRotate.setPosition(Constants.elevatorPincherRotateHandoff);
                    robot.elevatorPincher.setPosition(Constants.elevatorPincherOpen);
                    if (robot.intakeSlide.getCurrentPosition() < 15){
                        //handoffTimer.reset();
                        currentPickupSequenceSubstep++;  //Move to next step
                        break;
                    }
                }
                break;
            case 3:
                setState(State.INTAKE_WAIT);
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
                    currentHandoffSubStep++;  //Move to next step
                    break;
                }
                break;
            case 1:
                //System.out.println("Timer2: " + timer2.seconds());
                //if (handoffTimer.seconds() > 0.15){

                    currentHandoffSubStep++;  //Move to next step
                //}
                break;
            case 2:
                if (handoffTimer.seconds() > 0.4){
                    robot.elevatorPincher.setPosition(Constants.elevatorPincherClosed);
                    currentHandoffSubStep++;  //Move to next step
                    break;
                }
                break;
            case 3:
                if (handoffTimer.seconds() > 1.5){
                    robot.intakePincher.setPosition(Constants.intakePincherOpen);
                    currentHandoffSubStep++;  //Move to next step
                    break;
                }
                break;
            case 4:
                if (handoffTimer.seconds() > 2.0){
                    robot.elevatorPivot.setPosition(Constants.elevatorPivotVertical);
                    currentHandoffSubStep++;  //Move to next step
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
