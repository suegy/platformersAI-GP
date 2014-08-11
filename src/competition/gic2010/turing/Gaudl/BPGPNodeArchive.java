package competition.gic2010.turing.Gaudl;

import java.util.TreeMap;

public class BPGPNodeArchive {
	
	private BPGPNodeArchive(){}
	
	private static BPGPNodeArchive instance;
	private TreeMap<MarioCommand, V>
	
	public static BPGPNodeArchive getArchive(){
		if (instance instanceof BPGPNodeArchive)
			return instance;
		
		instance = new BPGPNodeArchive();
		
		return instance;
	}

}
