package tones;

import javax.sound.sampled.SourceDataLine;
import javax.swing.JOptionPane;

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
			line_.start();
			for  (Tone n : toneList_) {
				 n.play(line_);
			 }
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(null, "Something went wrong!");
		}
	}
}