package tones;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;


public class DifferenceTonesGUI extends JFrame {
public static final int WIDTH = 500;
public static final int HEIGHT= 300;
public static final String PLAY_BUTTON = "Play";
private Button playButton_;
private AudioFormat af_;
private SourceDataLine line_;
	public DifferenceTonesGUI() throws LineUnavailableException{
		super("DifferenceTones");
		af_  = new AudioFormat(Tone.SAMPLE_RATE, 8, 1, true, true);
		line_ = AudioSystem.getSourceDataLine(af_);
		line_.open(af_, Tone.SAMPLE_RATE);
        line_.start();
		createInterface();
	}
	public void createInterface(){
		setResizable(false);
		addWindowListener(new WindowAdapter() {public void
			windowClosing(WindowEvent e) {closeWindow();}});
		setSize(WIDTH,HEIGHT);
		setLayout(new BorderLayout());
		playButton_ = new Button(PLAY_BUTTON);
		playButton_.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(arg0.getActionCommand() == PLAY_BUTTON){
					line_.drain();
					playButton_.setEnabled(false);
					play();
					playButton_.setEnabled(true);
				}
			}
		});
		add(playButton_,BorderLayout.SOUTH);
	}
	public void play(){
		System.out.println("Playing Melody");
		try {
			PlayThread thread1 = new PlayThread(Tone.generateChromaticScale(line_, 1000));
			PlayThread thread2 = new PlayThread(Tone.generateChromaticScale(line_, 1000));
			thread1.start();
			thread2.start();
			thread1.join();
			thread2.join();
		}catch (Exception e){
			System.out.println("Exception");
		}
	}
	
	public void closeWindow(){
		if(line_ != null)
			line_.close();
		System.exit(0);
	}
	public static void main(String[] args) {
		try{
		DifferenceTonesGUI gui = new DifferenceTonesGUI();
		gui.setVisible(true);
		}catch(Exception e){
			System.out.println("FAAIIILLL!");
		}
	}
	
	private class PlayThread extends Thread{
		Tone[] toneList_;
	//TODO make each thread play on their own line thingy	
		public PlayThread(Tone[] notes){
			toneList_ = notes;			
		}
		
		@Override
		public void run(){
			try{
				 for  (Tone n : toneList_) {
					 n.play();
				 }
			}
			catch(Exception e){
				System.out.println("Thread: " + this.getId() + " could not play tone");
			}
		}
	}
}
