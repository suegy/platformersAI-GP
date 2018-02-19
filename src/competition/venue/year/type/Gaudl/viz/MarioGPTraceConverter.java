package competition.venue.year.type.Gaudl.viz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;


public class MarioGPTraceConverter extends JFrame{

	
	// copied from ObjectXYIs class
	public static final int CanBreak = -4;
	public static final int Coin = -3;
	public static final int Enemy = -2;
	public static final int FireFlower = -1;
	public static final int Mushroom = 0;
	public static final int Princess = 1;
	public static final int Air = 2;
	public static final int Walkable = 3;
	public static final int Unkown = 4;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Pattern IfPattern = Pattern.compile("^if(?<X>.+)$");//then, else
	public static Pattern EqualsPattern = Pattern.compile("^Equals(?<X>.+)$"); //sep by ","
	public static Pattern ObjectXYPattern = Pattern.compile("^objectAtXY (?<X>.+)$"); //sep by ";"
	public static Pattern SubPattern2 = Pattern.compile("^sub\\[(?<X>.+)\\]$"); //sep by "-->"
	//public static Pattern ObjectXYPattern = Pattern.compile("^\\(objectAtXY (?<x>.+?); (?<y>.+?)\\)$");
	public static Pattern AndPattern = Pattern.compile("^(?<obj1>.+?) && (?<obj2>.+?)$"); //sep by "&&"
	public static Pattern ParentPattern = Pattern.compile("^\\((?<X>.+)\\)$");

	String[] tree_representation;
	String[] output;
	int nodecounter;
	BufferedWriter writer;

	public MarioGPTraceConverter(){

		
		//reader.testInit();
		
		String[][] data = readFilesUsingFileChooser();
		
		
		for (int i = 0; i< data.length; i++){
			String name = data[i][0].substring(data[i][0].lastIndexOf(File.separator)+1);//[filepath.length-1];
			String output = data[i][0].substring(0,data[i][0].lastIndexOf("."))+"_viz.gv";
			String result = startGraphing(data[i][1], name);
			try {
				writer = new BufferedWriter(new FileWriter(output));
				writer.write(result);
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void testInit() {
		tree_representation = new String[1];
		tree_representation[0] = "if((Equals((objectAtXY 3; 3), (objectAtXY (objectAtXY (objectAtXY (objectAtXY (objectAtXY 3; 4); 1); 3); 4); (objectAtXY (objectAtXY 2; -2); 3))))) then ((if((isTall && isTall)) then ((if((Equals((objectAtXY 3; 4), (objectAtXY (objectAtXY 3; 4); 1)))) then (longJump) else((sub[right --> longJump])))) else((if(isTall) then ((sub[longJump --> right])) else((sub[right --> longJump])))))) else((if((Equals((objectAtXY (objectAtXY 1; (objectAtXY (objectAtXY (objectAtXY -2; 4); 1) (objectAtXY 1; 4))); 1), (objectAtXY (objectAtXY (objectAtXY 3; -3); 4); 1)))) then ((if(isTall) then ((sub[longJump --> right])) else((if((Equals((objectAtXY (objectAtXY (objectAtXY (objectAtXY 3; 3); 4) (objectAtXY (objectAtXY (objectAtXY -2; 4); 1); (objectAtXY 1; 4))); 4), (objectAtXY (objectAtXY (objectAtXY 2; -2); 3); 1)))) then (right) else((if((Equals((objectAtXY (objectAtXY (objectAtXY (objectAtXY 3; 3); 4); (objectAtXY (objectAtXY (objectAtXY -2; 4); 1); (objectAtXY 1; 4))); 4), (objectAtXY (objectAtXY 3; 4); 1)))) then (longJump) else((sub[longJump --> right])))))))) else((if(isTall) then ((if((isTall && (Equals((objectAtXY -2; (objectAtXY -2; 2)), (objectAtXY 3; 4))))) then ((if(isTall) then (longJump) else(longJump))) else(right))) else(right)))))";
		//tree_representation[0] = "if((Equals((objectAtXY 3; 3), 3))) then (LongJump) else(nothing)";
		tree_representation[0] = "if((Equals((objectAtXY 3; 3), (objectAtXY (objectAtXY (objectAtXY (objectAtXY (objectAtXY 3; 4); 1); 3); 4) (objectAtXY (objectAtXY 2; -2); 3))))) then ((if((isTall && isTall)) then ((if((Equals((objectAtXY 3; 4), (objectAtXY (objectAtXY 3; 4); 1)))) then (longJump) else((sub[right --> longJump])))) else((if(isTall) then ((sub[longJump --> right])) else((sub[right --> longJump])))))) else((if((Equals((objectAtXY (objectAtXY 1; (objectAtXY (objectAtXY (objectAtXY -2; 4); 1); (objectAtXY (objectAtXY (objectAtXY -2; 4); 1); (objectAtXY 1; 4));)); 1), (objectAtXY (objectAtXY (objectAtXY 3; -3); 4); 2)))) then ((if(isTall) then ((sub[longJump --> right])) else((if((Equals((objectAtXY (objectAtXY (objectAtXY (objectAtXY (objectAtXY (objectAtXY 3; 4); 4); (objectAtXY (objectAtXY (objectAtXY -2; 4); 1); (objectAtXY 1; 4))); 4); (objectAtXY (objectAtXY (objectAtXY -2; 4); 1); (objectAtXY (objectAtXY (objectAtXY 0; 4); 1); (objectAtXY (objectAtXY (objectAtXY 3; 4); 1); 3)))); 4), (objectAtXY (objectAtXY (objectAtXY 2; -2); 3); 1)))) then (right) else((if((Equals((objectAtXY (objectAtXY (objectAtXY (objectAtXY 3; 3); 4); (objectAtXY (objectAtXY (objectAtXY 1; (objectAtXY (objectAtXY 3; -3); 4)); 1); 1)); 4), (objectAtXY (objectAtXY 3; 4); 1)))) then (longJump) else((sub[(sub[longJump --> right]) --> right])))))))) else((if(isTall) then ((if((isTall && (Equals((objectAtXY -2; (objectAtXY -2; 2)), (objectAtXY 3; 4))))) then ((if(isTall) then ((sub[longJump --> right])) else(longJump))) else(right))) else(right)))))";
		startGraphing(tree_representation[0],"0");

	}
	public String startGraphing(String input,String name) {
		String output = String.format("digraph %s { \n",name);
		nodecounter = 0;
		output += reformatToGraphViz(input.trim(),"node");
		output += "}";

		//System.out.println(output);
		return output;
	}
	public String reformatToGraphViz(String input, String mynode){
		Matcher m = ParentPattern.matcher(input);
		String[] sol = new String[2];
		while(m.matches()) {
			input = m.group("X");
			m = ParentPattern.matcher(input);
		}
		String data = "";
		m = IfPattern.matcher(input);


		if (m.matches()) {
			//topmost node for this subgraph is supposed to be node counter 
			// parent node number is not known
			data = String.format("node%s [label=\"%s\",shape=\"diamond\"]\n", mynode, "if");
			sol = subRecursiveString(m.group("X"), '(', ')',"");
			data += reformatToGraphViz(sol[0],mynode+"1");
			data += String.format("node%s -> node%s [label=\"condition\"]\n", mynode, mynode+"1");

			if (sol[1].startsWith("then")){
				sol[1]=sol[1].substring(sol[1].indexOf("then")+4).trim();
				sol = subRecursiveString(sol[1], '(', ')',"");
				data += reformatToGraphViz(sol[0],mynode+"2");
				data += String.format("node%s -> node%s [label=\"then\"]\n", mynode, mynode+"2");
			}
			if (sol[1].startsWith("else")){
				sol[1]=sol[1].substring(sol[1].indexOf("else")+4).trim();
				data += reformatToGraphViz(sol[1],mynode+"3");
				data += String.format("node%s -> node%s [label=\"else\"]\n", mynode, mynode+"3");
			}
			return data;
		}

		m = SubPattern2.matcher(input);
		if (m.matches()) {
			data = String.format("node%s [label=\"%s\",shape=\"rectangle\"]\n", mynode, "Sub");
			sol = subRecursiveString(m.group("X"), '(', ')',"-->");
			data += reformatToGraphViz(sol[0],mynode+"1");
			data += String.format("node%s -> node%s \n", mynode, mynode+"1");

			data += reformatToGraphViz(sol[1],mynode+"2");
			data += String.format("node%s -> node%s;\n", mynode, mynode+"2");

			return data;
		}

		m = ObjectXYPattern.matcher(input);
		if (m.matches()) {
			data = String.format("node%s [label=\"%s\",shape=\"rectangle\"]\n", mynode, "objectAtXY");
			sol = subRecursiveString(m.group("X"), '(', ')',",");
			data += reformatToGraphViz(sol[0],mynode+"1");
			data += String.format("node%s -> node%s [label=\"x\"]\n", mynode, mynode+"1");

			sol = subRecursiveString(sol[1], '(', ')',",");
			data += reformatToGraphViz(sol[0],mynode+"2");
			data += String.format("node%s -> node%s [label=\"y\"]\n", mynode, mynode+"2");

			data += reformatToGraphViz(sol[1],mynode+"3");
			data += String.format("node%s -> node%s [label=\"Object\"]\n", mynode, mynode+"3");

			return data;
		}

		m = EqualsPattern.matcher(input);
		if (m.matches()) {
			data = String.format("node%s [label=\"%s\",shape=\"rectangle\"]\n", mynode, "==");
			sol = subRecursiveString(m.group("X"), '(', ')',",");
			data += reformatToGraphViz(sol[0],mynode+"1");
			data += String.format("node%s -> node%s \n", mynode, mynode+"1");

			data += reformatToGraphViz(sol[1],mynode+"2");
			data += String.format("node%s -> node%s;\n", mynode, mynode+"2");

			return data;
		}

		m = AndPattern.matcher(input);
		if (m.matches()) {
			data = String.format("node%s [label=\"%s\",shape=\"rectangle\"]\n", mynode, "&&");
			sol = subRecursiveString(input, '(', ')',"&&");
			data += reformatToGraphViz(sol[0],mynode+"1");
			data += String.format("node%s -> node%s; \n", mynode, mynode+"1");

			data += reformatToGraphViz(sol[1],mynode+"2");
			data += String.format("node%s -> node%s;\n", mynode, mynode+"2");

			return data;
		}

		if (input.length() > 0) {
			data = String.format("node%s [label=\"%s\",shape=\"circle\"]\n", mynode, input);
			return data;
		}

		return "";
	}



	/**
	 * opening a UI to read multiple JGAP GP configurations for a mario agent
	 * @return a list of objects containing the actual file as first argument and the content of the file in String format as the second argument
	 */
	public String[][] readFilesUsingFileChooser(){
		String[][] gPtrees;
		File[] gPcode = new File[0];

		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Genetic Algorithm Output", "txt", "gp");
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileFilter(filter);

		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			gPcode = chooser.getSelectedFiles();
		}

		gPtrees = new String[gPcode.length][2];
		for(int i = 0; i < gPcode.length; i++) {
			gPtrees[i][0] =  gPcode[i].getAbsolutePath();
			gPtrees[i][1] =  readAll(gPcode[i]);
		}

		return gPtrees;
	}


	/*
	PRIVATE SECTION
	 */
	private String readAll(File data){
		String result = "";
		try {
			BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(data),StandardCharsets.UTF_8));
			
			String line  = buff.readLine();
			while (line != null) {
				result += line+"\n";
				line  = buff.readLine();
			}
			buff.close();
		} catch (IOException e) {
			System.err.println("provided file not readable");
			return "";
		}

		return result;

	}
	
	private String whatIsObjectID(String id){
		int result=4;
		
		try {result = Integer.valueOf(id);} 
		catch (NumberFormatException e){}
		switch (result) {
		case CanBreak:
			return "CanBreak";
		case Coin:
			return "Coin";
		case Enemy:
			return "Enemy";
		case FireFlower:
			return "FireFlower";
		case Mushroom:
			return "Mushroom";
		case Princess:
			return "Princess";
		case Air:
			return "Air";
		case Walkable:
			return "Walkable";
		default:
			return "Unknown";
		}
		
	}

	/**
	 * return two strings one containing the subString and the rest separated by evenly occurance of a special char
	 * @param child
	 * @param parenOpen
	 * @param parenClose
	 * @param separator
	 * @return
	 */
	private String[] subRecursiveString(String child,char parenOpen, char parenClose, String separator){
		String[] result = new String[2];
		child = removeOuterEncapsulation(child, parenOpen, parenClose);

		if (separator != "" && itemBeforeParentheses(child, separator, parenOpen)){
			result[0] = child.substring(0, child.indexOf(separator)).trim();
			result[1] = child.substring(child.indexOf(separator)+separator.length()).trim();
		} else {
			result[0] = encapsulatedString(child, parenOpen, parenClose).trim();
			result[1] = child.substring(child.indexOf(result[0])+result[0].length()+1).trim();
			/*if (separator != "")
					result[1] = result[1].substring(result[1].indexOf(separator)+1).trim();*/
		}

		return result;
	}

	private boolean itemBeforeParentheses(String input, String itemSep, char parenOpen){
		return (input.indexOf(itemSep) < input.indexOf(parenOpen) || !input.contains(""+parenOpen))? true : false;
	}

	private String encapsulatedString(String input,char parenOpen, char parenClose){
		int counter = 0;
		input = removeOuterEncapsulation(input, parenOpen, parenClose);
		for (int i = input.indexOf(parenOpen);i<input.length();i++){
			if (input.charAt(i) == parenOpen)
				counter++;
			if (input.charAt(i) == parenClose)
				counter--;
			if (counter==0)
				return input.substring(input.indexOf(parenOpen), i+1);

		}
		return input;
	}

	private String removeOuterEncapsulation(String input,char parenOpen, char parenClose){
		int counter = 0;
		input = input.trim();

		if (input.charAt(0) != parenOpen)
			return input;

		for (int i = input.indexOf(parenOpen);i<input.length();i++){
			if (input.charAt(i) == parenOpen)
				counter++;
			if (input.charAt(i) == parenClose)
				counter--;
			if (counter==0)
				return (i<input.length()-1) ? input: input.substring(1,input.length()-1);
		}
		return input;
	}

	/*
	MAIN SECTION
	 */
	public static void main(String[] args){
		MarioGPTraceConverter reader = new MarioGPTraceConverter();

		reader = null;
		System.exit(NORMAL);
	}

}
