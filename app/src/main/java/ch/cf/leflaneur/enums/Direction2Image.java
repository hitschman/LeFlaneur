package ch.cf.leflaneur.enums;

import ch.cf.leflaneur.R;
import ch.cf.leflaneur.enums.Direction;

/**
 * Created by christianfallegger on 23.02.17.
 */

public class Direction2Image {
    static public int getImageName(Direction direction){

        int image;

        switch (direction){
            case START:
            case CONTINUE:
            case FORWARDS:
                image =  R.drawable.leflaneurstraight;
                break;
            case LEFT:
                image = R.drawable.leflaneurleft;
                break;
            case RIGHT:
                image = R.drawable.leflaneurright;
                break;
            case BACKWARDS:
            case STOP:
            case INFORMATION:
                image = R.drawable.leflaneurinfo;
                break;
            default:
                image = R.drawable.bug;
        }
        return image;
    }
}
