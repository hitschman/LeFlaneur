package ch.cf.leflaneur.output;

import android.os.Vibrator;
import android.widget.Toast;

import ch.cf.leflaneur.MapsActivity;
import ch.cf.leflaneur.enums.Direction;

/**
 * Created by christianfallegger on 06.04.17.
 */

public class Vibration
{
    Vibrator v;


    public Vibration(Vibrator v)
    {
        this.v = v; //Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void output(Direction direction)
    {
        switch (direction)
        {
            case LEFT:      this.left();
                break;
            case RIGHT:     this.right();
                break;
            case FORWARDS:
            case CONTINUE:  this.start();
                break;
            case INFORMATION:
            case STOP:
            case BACKWARDS: this.stop();
                break;
            case START:
            default:        this.fault();
        }
    }

    private void left()
    {
        this.action(new long[] {0, 100, 100, 100, 100, 100, 400, 400, 400, 400});
    }

    private void right()
    {
        this.action(new long[] {0, 400, 400, 400, 400, 100, 100, 100, 100, 100});
    }

    private void start(){
        this.action(new long[] {0, 400, 400, 400, 400, 400, 400});
    }

    private void stop(){
        this.action(new long[] {0, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100});
    }

    private void fault(){
        this.action(new long[] {0, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50 });
    }

    private void action(long[] pattern){
        this.v.vibrate(pattern, -1);
    }

}
