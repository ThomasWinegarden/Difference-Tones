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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ButtonPanel extends JPanel {

	public static final int WIDTH = 150;
    public static final int MAX_HZ = 20000;
    public static final int MIN_HZ= 20;
    public static final int MIN_DUR = 50;
    public static final int MAX_DUR = 5000;
    
	private DifferenceTonesGUI parent_;
	protected Button playButton_;
	protected Button differenceButton_;
	private JComboBox<Beat> beat_;
	private JComboBox<DiffAlgorithm> algo_;
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
        add(new JLabel("Right Click to"));
        add(new JLabel("clear the display")); //wow that was lazy, and it looks terrible
        playButton_ = new Button("Play");
        playButton_.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		parent_.play();
        	}
        });
        playButton_.setPreferredSize(new Dimension(WIDTH,100));
        add(playButton_);
        
        differenceButton_ = new Button("Play Difference");
        differenceButton_.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		parent_.playDifferenceTone();
        	}
        });
        differenceButton_.setPreferredSize(new Dimension(WIDTH,100));
        add(differenceButton_);
        /*
        This would be a nice feature to implement, but I have finals to study for
        algo_= new JComboBox<DiffAlgorithm>(DiffAlgorithm.values());
        algo_.setSelectedIndex(1);
        add(new JLabel("Difference Algorithm:"));
        add(algo_);
        */
        beat_= new JComboBox<Beat>(Beat.values());
        beat_.setSelectedIndex(1);
        add(new JLabel("Select a beat:"));
        add(beat_);
        
        
        //create the spinners
        topHz_ = new JSpinner(new SpinnerNumberModel(1400,MIN_HZ,MAX_HZ,1));
        bottomHz_ = new JSpinner(new SpinnerNumberModel(200,MIN_HZ,MAX_HZ,1));
        add(new JLabel("Max Freq (Hz)"));
        topHz_.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				parent_.canvasChanged();
			}
        });
        add(topHz_);
        add(new JLabel("Min Freq (Hz)"));
        bottomHz_.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				parent_.canvasChanged();
			}
        });
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
