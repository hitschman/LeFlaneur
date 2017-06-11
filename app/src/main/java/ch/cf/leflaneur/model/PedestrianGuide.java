package ch.cf.leflaneur.model;

import android.app.Activity;
import android.content.res.Resources;
import android.speech.tts.TextToSpeech;

import ch.cf.leflaneur.enums.Direction;
import ch.cf.leflaneur.output.Vibration;

public class PedestrianGuide extends AbstractGuide {

    Vibration vibration;

    public PedestrianGuide(Vibration vibration, Activity activity, TextToSpeech tts) {
        super(activity, tts);
        this.vibration = vibration;
    }

    @Override
    protected void ouput(Direction direction) {
        this.vibration.output(direction);
    }
}
