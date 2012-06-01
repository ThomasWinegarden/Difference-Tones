package tones;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class ButtonPanel extends JPanel {

	public static final int WIDTH = 100;
    public static final int MAX_HZ = 20000;
    public static final int MIN_HZ= 20;
    public static final int MIN_DUR = 50;
    public static final int MAX_DUR = 5000;
    
	private DifferenceTonesGUI parent_;
	protected Button playButton_;
	private JComboBox<Beat> beat_;
    private JSpinner topHz_;
    private JSpinner bottomHz_;
    private JSpinner duration_;
    private JLabel currentNote_;
	public ButtonPanel(DifferenceTonesGUI gui){
		super();
		parent_ = gui;
		setPreferredSize(new Dimension(WIDTH,parent_.getHeight()));
		setBackground(new Color(200,200,200));
		setLayout(new FlowLayout());
        playButton_ = new Button("Play");
        playButton_.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		parent_.play();
        	}
        });
        playButton_.setPreferredSize(new Dimension(WIDTH,100));
        add(playButton_);
        beat_= new JComboBox<Beat>(Beat.values());
        beat_.setSelectedIndex(1);
        add(new JLabel("Select a beat:"));
        add(beat_);
        //create the spinners
        topHz_ = new JSpinner(new SpinnerNumberModel(10000,MIN_HZ,MAX_HZ,1));
        bottomHz_ = new JSpinner(new SpinnerNumberModel(100,MIN_HZ,MAX_HZ,1));
        add(new JLabel("Max Freq (Hz)"));
        add(topHz_);
        add(new JLabel("Min Freq (Hz)"));
        add(bottomHz_);
        add(new JLabel("Eighth note (ms)"));
        duration_ = new JSpinner(new SpinnerNumberModel(250,MIN_DUR,MAX_DUR,1));
        add(duration_);
        //show the current note's hz value
        add(new JLabel("Current Hertz"));
        currentNote_ = new JLabel("");
        add(currentNote_);
	}
	public Beat getNoteLength(){
		return (Beat) beat_.getSelectedItem();
	}
	public int getTopHz(){
		return ((Integer) topHz_.getModel().getValue()).intValue();
	}
	public int getBottomHz(){
		return ((Integer) bottomHz_.getModel().getValue()).intValue();
	}
	public int getDuration(){
		return ((Integer) duration_.getModel().getValue()).intValue();
	}
	public void setCurrentNote(String hz){
		currentNote_.setText(hz);
	}
}
