package competition.venue.year.type.Gaudl.gp;

import org.jgap.gp.impl.ProgramChromosome;

import java.util.List;
import java.util.TreeMap;

public class BPGPNodeArchive {
	
	private BPGPNodeArchive(){}
	
	private static BPGPNodeArchive instance;
    private TreeMap<MarioCommand, List<Integer>> commandArchive;
    private TreeMap<Integer, ProgramChromosome> chromosomeArchive;
    private TreeMap<Integer, Integer> chromosomeScore;
	
	public static BPGPNodeArchive getArchive(){
		if (instance instanceof BPGPNodeArchive)
			return instance;
		
		instance = new BPGPNodeArchive();
		
		return instance;
	}

}
