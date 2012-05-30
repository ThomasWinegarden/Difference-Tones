package tones;

import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Tone {
    public static final int SAMPLE_RATE = 16 * 1024; // ~16KHz
    public static final int SECONDS = 2;
    public static final double BASE_FREQ = 440d;
    
	private double frequency_;
	private int duration_;
	private SourceDataLine sdl_;
	private byte[] sine_window_;
	
	public Tone(SourceDataLine line,double freq, int duration) throws LineUnavailableException{
		sdl_  = line;
		frequency_ = freq;
		duration_ = duration;
		sine_window_ =  new byte[SECONDS * SAMPLE_RATE];
        for (int i = 0; i < sine_window_.length; i++) {
            double period = (double)SAMPLE_RATE / frequency_;
            double angle = 2.0 * Math.PI * i / period;
            sine_window_[i] = (byte)(Math.sin(angle) * 127f);
        }
	}
	
	public Tone(SourceDataLine line,Note n, int duration) throws LineUnavailableException{
        this(line,BASE_FREQ * Math.pow(2d, ((double) n.ordinal()) / 12d),duration);
	}
	
	public void play(){
        double ms = Math.min(duration_, SECONDS * 1000);
        int length = ((int)(SAMPLE_RATE * ms / 1000));
        int count = sdl_.write(sine_window_, 0, length);
	}
	public static Tone[] generateChromaticScale(SourceDataLine line, int duration)
			throws  LineUnavailableException {
		Tone[]  tones = new Tone[Note.values().length];
		for (int i = 0; i<tones.length; i++){
			tones[i] = new Tone(line,Note.values()[i],duration);
		}
		return tones;
	}
}

enum Note {
    REST, A4, A4$, B4, C4, C4$, D4, D4$, E4, F4, F4$, G4, G4$, A5;
}

	
	