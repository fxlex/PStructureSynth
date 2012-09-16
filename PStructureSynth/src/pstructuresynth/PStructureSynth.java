package pstructuresynth;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import processing.core.PApplet;


public class PStructureSynth {
	PrintWriter file;
	String fileName;
	PApplet app;
	public Raytracer raytracer;
	public Camera camera;
	public JavaScript javascript;
	
	private boolean error = false;
	private boolean exported = false;
	private HashMap<String,Rule> rules = new HashMap<String,Rule>();
	private ArrayList<String> reqRules = new ArrayList<String>();
	private TreeMap<String,Object> variables = new TreeMap<String,Object>();

	final String COLOR_PATTERN = "(#[0-9a-fA-F]{6})|(#[0-9a-fA-F]{3})|([a-zA-Z]+)";

	enum AUTOCALC {
		WIDTH,HEIGHT
	}

	public PStructureSynth(PApplet app,String fileName) {
		this.app = app;
		this.fileName = fileName;
		if(!fileName.endsWith(".es"))
			fileName += ".es";
		file = app.createWriter(fileName); 
		raytracer = new Raytracer();
		camera = new Camera();
		javascript = new JavaScript();
	}
	
	protected interface RuleImpl {
		abstract public void rule();
	}
	public abstract class Rule implements RuleImpl {
		private String name;
		
		public Rule(String name) {
			this.name = name;
			rules.put(name, this);
			action();
		}
		
		public void action() {
			file.write(String.format("rule %s {\n",name));	
			rule();
			file.write(String.format("}\n"));
		}
	}

	public class Raytracer {

		private Raytracer() {}

		public Raytracer ambientOcclusionSamples(int samples) {
			file.write(String.format("set raytracer::ambient-occlusion-samples %d\n",samples));		
			return this;
		}

		public Raytracer samples(int samples) {
			file.write(String.format("set raytracer::samples %d\n",samples));		
			return this;
		}

		public Raytracer dof(float distance,float strength) {
			file.write(String.format("set raytracer::dof [%f,%f]\n",distance,strength));
			return this;
		}

		public Raytracer shadows(boolean bool) {
			file.write(String.format("set raytracer::shadows %b\n",bool));	
			return this;
		}

		public Raytracer reflection(float value) {
			file.write(String.format("set raytracer::reflection %f\n",value));	
			return this;
		}

		public Raytracer phong(float x,float y,float z) {
			file.write(String.format("set raytracer::phong [%f,%f,%f]\n",x,y,z));
			return this;
		}

		public Raytracer size(int width,int height) {
			file.write(String.format("set raytracer::size [%dx%d]\n",width,height));
			return this;
		}

		public Raytracer size(int s,AUTOCALC autoCalc) {
			if(autoCalc == AUTOCALC.WIDTH)
				file.write(String.format("set raytracer::size [0x%d]\n",s));	
			else if(autoCalc == AUTOCALC.HEIGHT)
				file.write(String.format("set raytracer::size [%dx0]\n",s));	
			return this;
		}

		public Raytracer maxThreads(int threads) {
			file.write(String.format("set raytracer::max-threads %d\n",threads));	
			return this;
		}

		public Raytracer light(float x,float y,float z) {
			file.write(String.format("set raytracer::light [%f,%f,%f]\n",x,y,z));		
			return this;
		}

		public Raytracer voxelSteps(int depth) {
			file.write(String.format("set raytracer::voxel-steps %d\n",depth));	
			return this;
		}

		public Raytracer maxDepth(int depth) {
			file.write(String.format("set raytracer::max-depth %d\n",depth));
			return this;
		}
	}

	public class Camera {

		private Camera() { }

		public Camera scale(float value) {
			file.write(String.format("set scale %f\n",value));
			return this;
		}

		public Camera pivot(float x,float y,float z) {
			file.write(String.format("set pivot [%f %f %f]\n",x,y,z));
			return this;
		}

		public Camera translation(float x,float y,float z) {
			file.write(String.format("set translation [%f %f %f]\n",x,y,z));
			return this;
		}

		public Camera rotation(float...rotation) {
			if(rotation.length != 9) {
				error("%s required %d parameters, got %d.\n",getMethodName(),rotation.length,9);
				return this;
			}
			String rot = makeParam(rotation);
			file.write(String.format("set rotation %s\n",rot));
			return this;
		}

		public Camera perspectiveAngle(float angle) {
			file.write(String.format("set perspective-angle %f\n",angle));
			return this;
		}
	}

	public class JavaScript {

		private JavaScript() { }

		public JavaScript load(String fileName) {
			file.write(String.format("Builder.load(\"%s\");\n",fileName));
			return this;
		}

		public JavaScript define(String txt,Object value) {
			file.write(String.format("Builder.define(\"%s\",\"%s\");\n",txt,value));
			return this;
		}

		public JavaScript prepend(String txt) {
			file.write(String.format("Builder.prepend(%s);\n",txt));
			return this;
		}

		public JavaScript append(String txt) {
			file.write(String.format("Builder.apppend(%s);\n",txt));
			return this;
		}

		public JavaScript build() {
			file.write(String.format("Builder.build();\n"));
			return this;
		}

		public JavaScript renderToFile(String fileName) {
			file.write(String.format("Builder.renderToFile(%s,overwrite);\n",fileName));
			return this;
		}

		public JavaScript raytraceToFile(String fileName) {
			file.write(String.format("Builder.raytraceToFile(%s,overwrite);\n",fileName));
			return this;
		}

		public JavaScript RenderToFile(String template,String file) {
			PStructureSynth.this.file.write(String.format("Builder.templateRenderToFile(%s,%s,overwrite);\n",template,file));
			return this;
		}

		public JavaScript execute(String path,String fileName,boolean bool) {
			file.write(String.format("Builder.execute(%s,%s,%s);\n",path,fileName,bool));
			return this;
		}

		public JavaScript reset() {
			file.write(String.format("Builder.reset();\n"));
			return this;
		}

		public JavaScript setSize(float x,float y) {
			file.write(String.format("Builder.setSize(%f,%f);\n",x,y));
			return this;
		}
	}

	private void error(String message,Object...fields) {
		System.err.println(String.format(message, fields));
		error = true;
	}

	private String getMethodName() {
		return Thread.currentThread().getStackTrace()[2].getMethodName()+"()";
	}

	
	public PStructureSynth export() {
		file.flush();
		file.close();
        
		for(int i=0;i<reqRules.size();i++)
			if(rules.get(reqRules.get(i)) == null)
					error("The rule %s doesn't exist.",reqRules.get(i));
		if(error)
			new File(fileName).delete();
		return this;
	}

	public void open() {
		if(!exported)
			export();
		exported = false;
		if(error)
			return;
		if(Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().open(new File(app.sketchPath+System.getProperty("file.separator")+fileName));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} else {
			error("%s not supported.",getMethodName());
		}
	}
	
	

	private String makeParam(float...params) {
		StringBuilder param = new StringBuilder("[");
		for(int i=0;i<params.length;i++) {
			param.append(String.valueOf(params[i])+(i<params.length-1?" ":""));
		}
		param.append("]");
		return param.toString();
	}
	
	public void load(String txt) {
		file.write("txt");
	}
		
	public PStructureSynth repeat(int num) {
		file.write(String.format("%d * { ",num));
		return this;
	}
	
	public PStructureSynth endLine() {
		file.write("} \n");

		return this;
	}
	
	public void rule(String name) {
		  file.write(String.format("} %s\n",name));
		  reqRules.add(name);
	}

	public PStructureSynth maxdepth(int depth) {
		file.write(String.format("set maxdepth %d\n",depth));
		return this;
	}

	public PStructureSynth maxObjects(int depth) {
		file.write(String.format("set maxobjects %d\n",depth));	
		return this;
	}

	public PStructureSynth minSize(float depth) {
		file.write(String.format("set minsize  %f\n",depth));	
		return this;
	}

	public PStructureSynth maxSize(float depth) {
		file.write(String.format("set maxsize %f\n",depth));
		return this;
	}

	public PStructureSynth seed(int depth) {
		file.write(String.format("set seed %d\n",depth));	
		return this;
	}

	public PStructureSynth seedInitial(int depth) {
		file.write(String.format("set seed initial %d\n",depth));	
		return this;
	}

	public PStructureSynth seedRecursionDepth(int depth) {
		file.write(String.format("set recursion depth %d\n",depth));	
		return this;
	}

	public PStructureSynth seedRngOld() {
		file.write(String.format("set rng old\n"));		
		return this;
	}

	public PStructureSynth seedSyncRandom(boolean bool) {
		file.write(String.format("set sync random %b\n",bool));		
		return this;
	}

	public PStructureSynth background(String depth) {
		file.write(String.format("set background %s\n",depth));		
		return this;
	}

	public PStructureSynth maxDepth(int depth) {
		file.write(String.format("md %d\n",depth));	
		return this;
	}

	public PStructureSynth maxDepth(int depth,String substitution) {
		file.write(String.format("md %d > %s\n",depth,substitution));	
		return this;
	}

	public PStructureSynth md(int depth) {
		maxDepth(depth);	
		return this;
	}

	public PStructureSynth md(int depth,String substitution) {
		maxDepth(depth,substitution);		
		return this;
	}

	public PStructureSynth weight(float weight) {
		file.write(String.format("w %f ",weight));		
		return this;
	}

	public PStructureSynth x(float x) {
		file.write(String.format("x %f ",x));
		return this;
	}

	public PStructureSynth y(float y) {
		file.write(String.format("y %f ",y));
		return this;
	}

	public PStructureSynth z(float z) {
		file.write(String.format("z %f ",z));
		return this;
	}

	public PStructureSynth rx(float angle) {
		file.write(String.format("rx %f ",angle));
		return this;
	}

	public PStructureSynth ry(float angle) {
		file.write(String.format("ry %f ",angle));
		return this;
	}

	public PStructureSynth rz(float angle) {
		file.write(String.format("rz %f ",angle));
		return this;
	}

	public PStructureSynth size(float size) {
		file.write(String.format("s %f ",size));
		return this;
	}

	public PStructureSynth size(float s1,float s2,float s3) {
		file.write(String.format("s %f %f %f ",s1,s2,s3));
		return this;
	}

	public PStructureSynth s(float size) {
		size(size);
		return this;
	}

	public PStructureSynth s(float s1,float s2,float s3) {
		size(s1,s2,s3);
		return this;
	}



	public PStructureSynth matrix(float...matrixCoord) {
		if(matrixCoord.length != 9) {
			error("%s required %d parameters, got %d.",getMethodName(),matrixCoord.length,9);
			return this;
		}

		String matrix = makeParam(matrixCoord);
		file.write(String.format("m %s ",matrix));
		return this;
	}

	public PStructureSynth m(float...matrixCoord) {
		matrix(matrixCoord);
		return this;
	}

	public PStructureSynth mirrorX() {
		file.write(String.format("fx "));
		return this;
	}

	public PStructureSynth fx() {
		mirrorX();
		return this;
	}

	public PStructureSynth mirrorY() {
		file.write(String.format("fy "));
		return this;
	}

	public PStructureSynth fy() {
		mirrorY();
		return this;
	}

	public PStructureSynth mirrorZ() {
		file.write(String.format("fz "));
		return this;
	}

	public PStructureSynth fz(float size) {
		mirrorZ();
		return this;
	}

	public PStructureSynth reflect() {
		file.write(String.format("reflect "));
		return this;
	}

	public PStructureSynth hue(float value) {
		file.write(String.format("hue %f ",value));
		return this;
	}

	public PStructureSynth h(float value) {
		hue(value);
		return this;
	}

	public PStructureSynth saturation(float value) {
		file.write(String.format("sat %f ",value));
		return this;
	}

	public PStructureSynth sat(float value) {
		saturation(value);
		return this;
	}

	public PStructureSynth brightness(float value) {
		file.write(String.format("b %f ",value));
		return this;
	}

	public PStructureSynth b(float value) {
		brightness(value);
		return this;
	}

	public PStructureSynth alpha(float value) {
		file.write(String.format("a %f ",value));
		return this;
	}

	public PStructureSynth a(float value) {
		alpha(value);
		return this;
	}

	public PStructureSynth color(String color) {
		if(!color.matches(COLOR_PATTERN)) {
			error("%s requires a valid color, got %s.",getMethodName(),color);
			return this;
		}

		file.write(String.format("color %s ",color));
		return this;
	}

	public PStructureSynth blend(String color,float strength) {
		if(!color.matches(COLOR_PATTERN)) {
			error("%s requires a valid color, got %s.",getMethodName(),color);
			return this;
		}

		file.write(String.format("blend %s %f ",color,strength));
		return this;
	}

	public PStructureSynth colorRandom() {
		file.write(String.format("color random "));
		return this;
	}

	public PStructureSynth colorPool(String scheme) {
		String schemeExamples = "randomhue\ngreyscale\nimage:filename.png\nlist:orange,white,grey";
		String schemePattern = "(randomhue)|(greyscale)|(^image:.*)|(^list:.*)";
		String listPattern = "(^list:["+COLOR_PATTERN+",])+";
		if(!scheme.matches(schemePattern)) {
			error("%s got a wrong scheme.Only \n %s \n are permitted.",getMethodName(),schemeExamples);
			return this;
		}
		if(scheme.matches(listPattern)) {
			String[] listItems = scheme.split(":");
			String[] colors = listItems[1].split(",");
			for(int i=0;i<colors.length;i++) {
				if(!colors[i].matches(COLOR_PATTERN)) {
					error("Error in scheme. Color is not valid:"+colors[i]);
					return this;
				}
			}
		}
		file.write(String.format("set colorpool %s\n",scheme));
		return this;
	}

	public void comment(String txt) {
		file.write(String.format("// %s\n",txt));
	}

	public void box() {
		file.write(String.format("} box\n"));

	}

	public void grid() {
		file.write(String.format("} grid\n"));

	}

	public void sphere() {
		file.write(String.format("} sphere\n"));

	}

	public void line() {
		file.write(String.format("} line\n"));

	}

	public void triangle(float...coordinates) {
		if(coordinates.length != 9) {
			error("%s required %d parameters, got %d.",getMethodName(),coordinates.length,9);
			return;
		}

		String coords = makeParam(coordinates);

		file.write(String.format("} triangle %s\n",coords));
	}

	public void mesh() {
		file.write(String.format("} mesh\n"));

	}

	public PStructureSynth define(String varName,Object value) {
		file.write(String.format("#define %s %s\n",varName,value));
		variables.put(varName, value);
		return this;
	}
	
	public PStructureSynth define(String varName,Object value,String type,float min,float max) {
		file.write(String.format("#define %s %s (%s:%f-%f)\n",varName,value,type,min,max));
		variables.put(varName, value);
		return this;
	}

	public Object var(String varName) {
		Object value = variables.get(varName);
		if(value == null) {
			error("Variable %s doesn't exist.",varName);
			return null;
		}
		return value;
	}
}
