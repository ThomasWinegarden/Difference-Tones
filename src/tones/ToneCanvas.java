package tones;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JPanel;

public class ToneCanvas extends JPanel{
	public static final int SINGLE_NOTE_MODE = 0;
	public static final int DIFFERENCE_TONE_MODE = 1;
	public static final int NOTE_WIDTH = 30;  
	public static final int NOTE_HEIGHT = 5;
	private DifferenceTonesGUI parent_;
	public HashMap<Integer,Rectangle> notes_;
	protected Pair<Integer,Integer> temp_note_;
	protected int temp_width_;
	private Color color_;
	
	public ToneCanvas(DifferenceTonesGUI par){
		parent_ = par;
		color_ = new Color((int)(Math.random()*245),(int)(Math.random()*245),(int)(Math.random()*245));
        notes_ = new HashMap<Integer,Rectangle>();
        temp_note_ = new Pair<Integer,Integer>(-1,-1);
        temp_width_ = -1;
	}
	
	public Rectangle sanitizeNote(int x, int y, int num_spaces, boolean replaceNote){
		if (x % NOTE_WIDTH != 0){
			x -= x % NOTE_WIDTH;
		}
		//limit the notes locations
		if (x<0)
			x = 0;
		else if (x > getWidth()) 
			x = getWidth() -( getWidth() % NOTE_WIDTH);  
		if (y + NOTE_HEIGHT > getHeight() - NOTE_HEIGHT)
			y = getHeight() - NOTE_HEIGHT;
		else if (y< 0)
			y = 0;
		Rectangle note = null;
		if (!replaceNote || (note=notes_.get(x)) == null)
			note = new Rectangle();
		note.setBounds(new Rectangle(x,y,num_spaces*NOTE_WIDTH,NOTE_HEIGHT));
		return note;
	}

	public void addNote(int x, int y, int num_spaces){
		Rectangle note = sanitizeNote(x, y, num_spaces, true);
		if (num_spaces == 0){
			return;
		}
		//check on both side of the new note and kill any notes it collides with
		removeCollidingLines(note);
		notes_.put(note.x, note);
    	parent_.getButtonPanel().setCurrentNote("");
	}
	
	public void clearNotes(){
		notes_ = new HashMap<Integer,Rectangle>();
	}
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.WHITE);
        //paint the bottom rest bar
        g.setColor(new Color(200,250,225));// i have no idea what color this is
        g.fillRect(0, getHeight() - NOTE_HEIGHT, getWidth(), NOTE_HEIGHT);
        //draw the temporary note
        if(temp_width_ > 0 ){
        	Rectangle r = sanitizeNote(temp_note_.obj_1_,temp_note_.obj_2_,temp_width_,false);
        	g.setColor(new Color(230,250,200));
	        g.fillRect(r.x,0,r.width,getHeight());
        	g.setColor(color_);
        	g.fillRect(r.x, r.y, r.width, r.height);
        	//it hz = getHzValueOfPixel(r.y);
        	int hz = getHzValueOfPixel(r.y);
        	if(hz == 0)
        		parent_.getButtonPanel().setCurrentNote("Rest");
        	else
        		parent_.getButtonPanel().setCurrentNote("" + getHzValueOfPixel(r.y));
        }
        //paint the notes
        g.setColor(color_);
        for(Integer i: notes_.keySet()){
        	g.fillRect(notes_.get(i).x,notes_.get(i).y, notes_.get(i).width, notes_.get(i).height);
        }
        //paint the separators
        g.setColor(new Color(200,250,225));// i have no idea what color this is
        for(int i = 0; i < getWidth(); i+= NOTE_WIDTH){
        	g.setColor(new Color(150,200,175));
        	g.drawLine(i, 0, i, getHeight());
        }
        //paint the reference lines
        g.setColor(new Color(255,80,80));
        for(Note x: Note.values()){
        	if(x.equals(Note.REST))
        		continue;
        	double exp = ((double) x.ordinal() - 1) / 12d;
            int freq = (int) (Tone.BASE_FREQ * Math.pow(2d, exp));	
        	int yloc = getPxValueOfHz(freq);
        	g.drawLine(0, yloc, getWidth(),yloc);
        	g.drawString(x.name(), 10 + 10*x.ordinal(), yloc);
        }
        
    }
	public void removeCollidingLines(Rectangle note){
		Iterator<Integer> iter = notes_.keySet().iterator();
		while(iter.hasNext()){
			int key =  iter.next();
			Rectangle r  = notes_.get(key);
			if(linesCollide(r.x,r.x+r.width,note.x,note.x + note.width))
				iter.remove();
		}
	}
	
	public Tone[] synthesizeTones(){
		if(notes_.keySet().size() == 0)
			return new Tone[0];
		int duration = parent_.getButtonPanel().getDuration();
		ArrayList<Tone> tones = new ArrayList<Tone>();
		Integer[] keys =  notes_.keySet().toArray(new Integer[0]);
		Arrays.sort(keys);
		int lastIndex = notes_.get(keys[keys.length-1]).x;
		System.out.println("Last: " + lastIndex);
		for(int i = 0 ; i<=lastIndex;){
			System.out.println(i);
			 Rectangle r = notes_.get(i);
			 if(r == null){//add a rest
				 tones.add(new Tone(0,duration));
				 i+=NOTE_WIDTH;
			 }
			 else {
				 int note_dur =  (int)(r.width/NOTE_WIDTH);
				 tones.add(new Tone(getHzValueOfPixel(r.y),note_dur*duration));
				 i+=note_dur*NOTE_WIDTH;
			 }
		}
		for(Tone t: tones)
			System.out.println(t);
		System.out.println("------------");
		return tones.toArray(new Tone[1]);
	}
	//returns what a one pixel difference corresponds to in frequency
	public double getScale(){
		return (parent_.getButtonPanel().getTopHz() - parent_.getButtonPanel().getBottomHz())/(getHeight() - 2*NOTE_HEIGHT); 
	}
	
	public int getHzValueOfPixel(int pix){
		if (pix >= getHeight() - NOTE_HEIGHT)
			return 0; //a rest
		return (int)(pix*getScale()+parent_.getButtonPanel().getBottomHz());
	}
	
	public int getPxValueOfHz(int hz){
		if (hz <= 0)
			return getHeight() - NOTE_HEIGHT;
		return (int)((hz-parent_.getButtonPanel().getBottomHz())/getScale());
	}
	public static boolean linesCollide(int a1,int a2,int b1,int b2){
		return lineContainsPoint(a1, b1, b2)
				|| lineContainsPoint(a2, b1, b2)
				|| lineContainsPoint(b1, a1, a2)
				|| lineContainsPoint(b2, a1, a2);
	}
	
	public static boolean lineContainsPoint(int p, int a1, int a2){
		return ((p<a1 && p>a2 ) || (p>a1 && p<a2));
	}
}

