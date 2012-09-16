package test;

import processing.core.PApplet;
import pstructuresynth.PStructureSynth;


public class Export2 extends PApplet {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	PStructureSynth ss;
	int d = 10;
	
	public void setup() {
		  ss = new PStructureSynth(this, "output2.es");
		  for(int i=0;i<1000;i++) {
			  float x = random(-d,d);
			  float y = random(-d,d);
			  float z = random(-d,d);
			  float s = random(0.01f,0.3f);
			  float rx = map(sin(i/100f),-1,1,0,360);
			  float ry = map(cos(i/100f),-1,1,0,360);
			  String color = random(1) > 0.5 ? "red":"blue";
		  ss.repeat(1).rx(rx).ry(ry).x(x).y(y).z(z).s(s).color(color).box();
		  }
		  ss.export();
		  ss.open();
		  exit();
	}

	  
	  public static void main(String args[]) {
		    PApplet.main(new String[] { "test.Test" });
		  }
	}
