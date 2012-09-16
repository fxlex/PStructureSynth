package test;

import processing.core.PApplet;
import processing.core.PImage;
import pstructuresynth.PStructureSynth;


public class Export5 extends PApplet {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	PStructureSynth ss;

	String fileName = "img.jpg";
	PImage img;
	int averageNum = 20;
	int averageAll = (int)pow(averageNum,2);
	
	public void setup() {
		  img = loadImage(fileName);
		  
		  ss = new PStructureSynth(this, "output.es");
		  ss.colorPool("list:white,orange");
		  ss.repeat(10).x(1).endLine();
		  ss.repeat(10).y(1).endLine();
		  ss.repeat(10).z(1).rule("RBox");
		  ss.new Rule("RBox") {
			public void rule() {
				ss.repeat(1).colorRandom().box();
			} 
		  };
		  ss.new Rule("RBox") {
			public void rule() {
			} 
		  };
		  ss.open();
		  exit();
		}
	
	  public static void main(String args[]) {
		    PApplet.main(new String[] { "test.Test" });
		  }
	}
