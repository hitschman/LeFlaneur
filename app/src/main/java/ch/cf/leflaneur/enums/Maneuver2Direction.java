package ch.cf.leflaneur.enums;

import ch.cf.leflaneur.enums.Direction;
/**
 * Created by christianfallegger on 01.02.17.
 */

public class Maneuver2Direction {

    static public Direction getDirection(String maneuver)
    {
        maneuver = maneuver.toLowerCase();
        if (maneuver.contains("sharp-right")){
            return Direction.RIGHT;
        }else if (maneuver.contains("slight-right")){
            return Direction.RIGHT;
        }else if (maneuver.contains("right")){
            return Direction.RIGHT;
        }else if (maneuver.contains("sharp-left")){
            return Direction.LEFT;
        }else if (maneuver.contains("slight-left")){
            return Direction.LEFT;
        }else if (maneuver.contains("left")){
            return Direction.LEFT;
        }else if (maneuver.contains("destination")){
            return Direction.INFORMATION;
        }else if (maneuver.contains("proceed")){
            return Direction.CONTINUE;
        }else if (maneuver.contains("continue")){
            return Direction.CONTINUE;
        }else if (maneuver.contains("head")){
            return Direction.CONTINUE;
        }else if (maneuver.contains("start")){
            return Direction.START;
        }else if (maneuver.contains("stop")){
            return Direction.STOP;
        }
        return Direction.FAULT;
    }
}