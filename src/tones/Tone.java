package tones;
import java.util.Iterator;
import java.util.LinkedList;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Tone {
    public static final int SAMPLE_RATE = 16 * 1024; // ~16KHz
    public static final int SECONDS = 2;
    public static final double BASE_FREQ = 440d;
    public static final double LOWEST_AUIDIBLE_FREQ  = 100;
    public static final double HIGHEST_AUDIBLE_FREQ = 2000;
    public static final double AUDIBLE_FREQ_RANGE = HIGHEST_AUDIBLE_FREQ -  LOWEST_AUIDIBLE_FREQ;
    protected double frequency_;
	protected int duration_;
	private byte[] sine_window_;
	
	public Tone(double freq, int duration) {
		frequency_ = freq;
		duration_ = duration;
		sine_window_ =  new byte[SECONDS * SAMPLE_RATE];
        for (int i = 0; i < sine_window_.length; i++) {
            double period = (double)SAMPLE_RATE / frequency_;
            double angle = 2.0 * Math.PI * i / period;
            sine_window_[i] = (byte)(Math.sin(angle) * 127f);
        }
	}
	
	public Tone(Note n, int duration) {
        this((n==Note.REST)? 0 : BASE_FREQ * Math.pow(2d, ((double) n.ordinal()) / 12d),duration);
	}
	
	public Pair<Tone,Tone> generateDifferenceTones() {
		if (frequency_ == 0)// a rest, return 2 zero frequency tones
			return new Pair<Tone,Tone>(new Tone(0,duration_),new Tone(0,duration_));
		
		double deviation = AUDIBLE_FREQ_RANGE - frequency_;
		long base_freq = (int)Math.floor(LOWEST_AUIDIBLE_FREQ + Math.random()*deviation);
		System.out.println("BaseFreq: " + base_freq);
		return new Pair<Tone,Tone>(
				new Tone(base_freq,duration_),
				new Tone(base_freq + frequency_,duration_));
	}
	
	public void play(SourceDataLine sdl){
			double ms = Math.min(duration_, SECONDS * 1000);
	        int length = ((int)(SAMPLE_RATE * ms / 1000));
	        int count = sdl.write(sine_window_, 0, length);
    }
	
	public static Tone[] generateChromaticScale(int duration){
		Tone[]  tones = new Tone[Note.values().length];
		for (int i = 0; i<tones.length; i++){
			tones[i] = new Tone(Note.values()[i],duration);
		}
		return tones;
	}
	
	public static Pair<Tone[],Tone[]> generateDifferenceMelody(Tone[] song){
		Pair<Tone[],Tone[]> toReturn = new Pair<Tone[],Tone[]>(new Tone[song.length], new Tone[song.length]);
		for (int i = 0; i< song.length; i++){
			Pair<Tone,Tone> tones =  song[i].generateDifferenceTones();
			toReturn.obj_1_[i] = tones.obj_1_;
			toReturn.obj_2_[i] = tones.obj_2_;
		}
		return toReturn;
	}
	
	public static Tone[] generateMelody(LinkedList<Pair<Note,Integer> > notes){
		Tone[] tones = new Tone[notes.size()];
		Iterator<Pair<Note,Integer>> iter = notes.iterator();
		int i = 0;
		while(iter.hasNext()){
			Pair<Note,Integer> combo  = iter.next();
			tones[i] = new Tone(combo.obj_1_,combo.obj_2_); 
			i++;
		}
		return tones;
	}
	public Tone[] song(){
		LinkedList<Pair<Note,Integer>> notes = new LinkedList<Pair<Note,Integer> >();
		notes.add(new Pair<Note,Integer>(Note.C4,1000));
		notes.add(new Pair<Note,Integer>(Note.B4,1000));
		notes.add(new Pair<Note,Integer>(Note.A4,1000));
		notes.add(new Pair<Note,Integer>(Note.REST,1000));
		notes.add(new Pair<Note,Integer>(Note.C4,500));
		notes.add(new Pair<Note,Integer>(Note.B4,500));
		notes.add(new Pair<Note,Integer>(Note.A4,500));
		notes.add(new Pair<Note,Integer>(Note.A4,300));
		notes.add(new Pair<Note,Integer>(Note.B4,300));
		notes.add(new Pair<Note,Integer>(Note.C4,300));
		notes.add(new Pair<Note,Integer>(Note.D4,300));
		notes.add(new Pair<Note,Integer>(Note.C4,300));
		notes.add(new Pair<Note,Integer>(Note.B4,300));
		notes.add(new Pair<Note,Integer>(Note.A4,300));
		notes.add(new Pair<Note,Integer>(Note.A4,1000));
		return Tone.generateMelody(notes);
	}
}
class Pair<E,F>{
	public E obj_1_;
	public F obj_2_;
	public Pair(E obj1, F obj2){
		obj_1_ = obj1;
		obj_2_ = obj2;
	}
	
}

enum Note {
    REST, A4, A4$, B4, C4, C4$, D4, D4$, E4, F4, F4$, G4, G4$, A5;
}

enum Beat{
	 Select,Eighth,Quarter,Half,Whole
}


	
	