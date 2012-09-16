package test;

import processing.core.PApplet;
import processing.core.PImage;
import pstructuresynth.PStructureSynth;


public class Export4 extends PApplet {

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
		  println("Started.");
		  img = loadImage(fileName);
		  
		  ss = new PStructureSynth(this, "output.es");
		  ss.comment("Camera settings. Place these before first rule call.");
		  ss.camera.translation(0.754915f,2.34604f,-20);
		  ss.camera.rotation(-0.999687f,0.015607f,-0.0195854f,-0.014989f,-0.999396f,-0.0313012f,-0.0200621f,-0.0309986f,0.999317f);
		  ss.camera.pivot(0,0,0);
		  ss.camera.scale(0.25f);
		  ss.define("SMALL",0.5f);
		  
		    for (int x =0;x < img.width; x+=averageNum) {
		    for (int y =0;y < img.height;y+=averageNum) {
		      int[] c = new int[averageAll];
		      float[] grays = new float[averageAll];
		      float[] hues = new float[averageAll];
		      float[] sats = new float[averageAll];
		      float[] brights = new float[averageAll];

		      for (int xi=0;xi<averageNum;xi++) {
		        for (int yi=0;yi<averageNum;yi++) {
		          int index = yi*averageNum+xi;
		          c[index] = img.get(x+xi, y+yi);
		          grays[index] = (blue(c[index])+red(c[index])+green(c[index]))/3f;
		          hues[index] = hue(c[index]);
		          sats[index] = saturation(c[index]);
		          brights[index] = brightness(c[index]);
		        }
		      }

		      float h = map(average(hues),0,255,0,1);
		      float s = map(average(sats),0,255,0,1);
		      float b = map(average(brights),0,255,0,1);
		      float g = average(grays)/5;
		      ss.repeat(1).s(0.5f).hue(h).sat(s).brightness(b).x(x/averageNum).y(y/averageNum).z(g).sphere();
		    }
		  }
		  
		  ss.export();
		  ss.open();
		  println("Finished.");
		  exit();
		}
	
	float average(float...values) {
		  float r=0;
		  for (int i=0;i<values.length;i++)
		    r+=values[i];
		  return r/values.length;
		}


	  
	  public static void main(String args[]) {
		    PApplet.main(new String[] { "test.Test" });
		  }
	}
