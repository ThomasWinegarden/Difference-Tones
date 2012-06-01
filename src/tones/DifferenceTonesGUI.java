package tones;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

 
/* ScrollDemo2.java requires no other files. */
public class DifferenceTonesGUI extends JPanel
                         implements MouseListener,MouseMotionListener{
	public final int RESIZE_AMOUNT = 100; 
	
	private Dimension area_; //indicates area taken up by graphics
    private ToneCanvas drawingPane_;
    private ButtonPanel buttons_;
    private AudioFormat af_;
    public DifferenceTonesGUI() {
        super(new BorderLayout());
        area_ = new Dimension(0,0);
        af_  = new AudioFormat(Tone.SAMPLE_RATE, 8, 1, true, true); 
        //Set up the drawing area.
        drawingPane_ = new ToneCanvas(this);
        drawingPane_.setBackground(Color.white);
        drawingPane_.addMouseListener(this);
        drawingPane_.addMouseMotionListener(this);
        //Put the drawing area in a scroll pane.
        JScrollPane scroller = new JScrollPane(drawingPane_);
        scroller.setPreferredSize(new Dimension(200,200));
        add(scroller, BorderLayout.CENTER);
        buttons_ = new ButtonPanel(this);
        add(buttons_,BorderLayout.WEST);
    }
 
    public void play(){
    	try {
	    	SourceDataLine line = AudioSystem.getSourceDataLine(af_);
			line.open(af_, Tone.SAMPLE_RATE);
			line.flush();
//	    	PlayThread thread1 = new PlayThread(drawingPane_.synthesizeTones(),line);
			PlayThread thread1 = new PlayThread(Tone.generateChromaticScale(500),line);
	    	buttons_.playButton_.setEnabled(false);
	    	thread1.start();
	    	thread1.join();
	    	buttons_.playButton_.setEnabled(true);
	    	line.close();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    //Handle mouse events.
    public void mouseReleased(MouseEvent e) {
        boolean changed = false;
        if (SwingUtilities.isRightMouseButton(e)) {
            //This will clear the graphic objects.
            area_.width=0;
            area_.height=0;
            changed = true;
            drawingPane_.clearNotes();
        } else {
            int x = e.getX() - RESIZE_AMOUNT/2;
            int y = e.getY() - RESIZE_AMOUNT/2;
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            Rectangle rect = new Rectangle(x, y, RESIZE_AMOUNT, RESIZE_AMOUNT);
            drawingPane_.scrollRectToVisible(rect);
            drawingPane_.addNote(e.getX(),e.getY(),buttons_.getNoteLength().ordinal());
            int this_width = (x + RESIZE_AMOUNT*2 + 2);
            if (this_width > area_.width) {
                area_.width = this_width; changed=true;
            }
        }
        if (changed) {
            drawingPane_.setPreferredSize(area_);
            drawingPane_.revalidate();
        }
        drawingPane_.repaint();
        drawingPane_.temp_width_ = -1;
    }
    public void mouseClicked(MouseEvent e){
        drawingPane_.temp_width_ = -1;
		drawingPane_.temp_note_.obj_1_ = e.getX();
		drawingPane_.temp_note_.obj_2_ = e.getY();
    }
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mousePressed(MouseEvent e){} 
	public void mouseDragged(MouseEvent e) {
		drawingPane_.temp_note_.obj_1_ = e.getX();
		drawingPane_.temp_note_.obj_2_ = e.getY();
		drawingPane_.temp_width_ = buttons_.getNoteLength().ordinal();
		drawingPane_.revalidate();
		drawingPane_.repaint();
	}

	public void mouseMoved(MouseEvent e) {
	}
	public ButtonPanel getButtonPanel(){
		return buttons_;
	}

	private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Difference Tones");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800,600));
        frame.setResizable(false);
        //Create and set up the content pane.
        JComponent newContentPane = new DifferenceTonesGUI();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
	}
 
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}