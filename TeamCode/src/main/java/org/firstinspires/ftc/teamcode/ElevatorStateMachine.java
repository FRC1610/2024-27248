package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;
public class ElevatorStateMachine {
    public enum ElevatorState {
        HOME,
        WALLPICKUP,
        HANDOFF,
        HIGHCHAMBER,
        HIGHBASKET;
    }

    private ElevatorState currentElevatorState;
    private final RobotHardware robot;

    public ElevatorStateMachine (RobotHardware hardware){
        this.robot = hardware;
        this.currentElevatorState = ElevatorState.HOME;
    }

    public void setElevatorState(ElevatorState state){
        this.currentElevatorState = state;
        updateElevator();
    }

    public void update() {
        switch (currentElevatorState) {
            case HOME:
                break;
            case WALLPICKUP:
                break;
            case HANDOFF:
                break;
            case HIGHCHAMBER:
                break;
            case HIGHBASKET:
                break;
        }
    }

    public ElevatorState getElevatorState(){
        return currentElevatorState;
    }

    private void updateElevator(){
        switch (currentElevatorState){
            case HOME:
                break;
            case WALLPICKUP:
                break;
            case HANDOFF:
                break;
            case HIGHCHAMBER:
                break;
            case HIGHBASKET:
                break;
        }
    }

}
