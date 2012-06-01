package tones;

import javax.sound.sampled.SourceDataLine;

public class PlayThread extends Thread{
	Tone[] toneList_;
	SourceDataLine line_;
	public PlayThread(Tone[] notes, SourceDataLine line){
		line_ = line;
		toneList_ = notes;			
	}

	@Override
	public void run(){
		try{
			 for  (Tone n : toneList_) {
				System.out.println("Playing Note: " + n.frequency_);
				 n.play(line_);
			 }
		}
		catch(Exception e){
			System.out.println("Thread: " + this.getId() + " could not play tone");
		}
	}
}