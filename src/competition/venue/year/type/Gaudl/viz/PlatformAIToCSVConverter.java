package competition.venue.year.type.Gaudl.viz;

import org.platformer.benchmark.platform.engine.level.Level;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.Vector;
import java.util.zip.ZipFile;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;


public class PlatformAIToCSVConverter extends JFrame {


    /**
     *
     */
    private static final long serialVersionUID = -5600604690876755916L;

    /**
     * opening a UI to read MarioAI zip files and extract the level and mario actions
     *
     * @return a list of objects containing
     */
    public String[][] readFilesUsingFileChooser() {
        Vector<ZipFile> logs = new Vector<ZipFile>();
        Vector<String> marioData = new Vector<>();
        Vector<Vector<Integer>> marioActions = new Vector<Vector<Integer>>();
        Vector<Level> levels = new Vector<Level>();

        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "MarioAi Log files", "zip");
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileFilter(filter);

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            for (File file : chooser.getSelectedFiles()) {
                try {
                    logs.add(new ZipFile(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Scanner scan = null;
        for (ZipFile zip : logs) {
            marioData.add(zip.getName());
            if (zip.getEntry("actions.act") != null) {
                try {
                    scan = new Scanner(zip.getInputStream(zip.getEntry("actions.act")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Byte act;
                Vector<Integer> actions = new Vector<Integer>();
                while ((act = scan.nextByte()) != null) {
                    actions.add(act.intValue());
                }
                marioActions.add(actions);
            }
            if (zip.getEntry("level.lvl") != null) {
                ObjectInputStream ois = null;
                try {
                    ois = new ObjectInputStream(zip.getInputStream(zip.getEntry("level.lvl")));
                    Level lvl = (Level) ois.readObject();
                    levels.add(lvl);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    //return gPtrees;


    public static void main(String[] args) {

    }

}
