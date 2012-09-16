package test;

import processing.core.PApplet;
import pstructuresynth.PStructureSynth;


public class Export3 extends PApplet {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	PStructureSynth ss;
	int d = 20;
	float step = 0.5f;
	
	public void setup() {
		  ss = new PStructureSynth(this, "output2.es");
		  for(float x=-d;x<d;x+= step) {
			  float y = sin(x) * 5;
			  ss.repeat(20).z(1).endLine();
			  ss.repeat(1).s(step,step,1).x(x).y(y).box();
		  }
		  ss.export();
		  ss.open();
		  exit();
	}

	  
	  public static void main(String args[]) {
		    PApplet.main(new String[] { "test.Test" });
		  }
	}
