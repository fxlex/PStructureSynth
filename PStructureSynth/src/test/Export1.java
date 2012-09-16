package test;

import processing.core.PApplet;
import pstructuresynth.PStructureSynth;


public class Export1 extends PApplet {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	PStructureSynth ss;
    int t = 50;
    int a = 30;
    float s = 0.4f;
	
	public void setup() {
		  ss = new PStructureSynth(this, "output2.es");
		  ss.colorPool("list:orange,yellow,blue");
		  for(float v=0;v<TWO_PI;v+=TWO_PI/t) {
			  for(float u=0;u<TWO_PI;u+=TWO_PI/t) {
			  float x = cos(u) * sin(v) * a;
			  float y = sin(u) * atan2(a,u);
			  float z = cos(v) * a;
			  if(!Float.isNaN(x) && !Float.isNaN(y) && !Float.isNaN(z)) {
				  String c="red";
				  if(x > v)
					  c = "blue";
				  ss.repeat(1).s(0.2f).endLine();
				  ss.repeat(10).color(c).s(s).ry(map(v-u,-2,2,0,360)).rx(map(v*u,-2,2,0,360)).x(x).y(y).z(z).box();
			  }
			  }
		  }
		  ss.export();
		  ss.open();
		  exit();
	}

	  
	  public static void main(String args[]) {
		    PApplet.main(new String[] { "test.Test" });
		  }
	}
