package sukamto.imagerecognition.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.HashMap;

public class Tree {

    private static HashMap<String, Node> roots = new HashMap<String, Node>();
    private static BufferedReader br;

    public static String search(String string) {
        String[] features = string.split(", ");
        if (roots.containsKey(features[0])) {
            return searchChar(features, roots.get(features[0]), 1);
        } else {
            return "Failed";
        }
    }
    private static String searchChar(String[] features, Node node, int index) {
        if (index == features.length) {
            return node.children.get(features[index]).val;
        }
        if (node.children.containsKey(features[index].trim())) {
            Log.e("searchChar", features[index]+index);
            return searchChar(features, node.children.get(features[index]), index+1);
        } else {
            Log.e("failed 2", "fail");
            return "Failed";
        }
    }

    public static void insertAllFeatures(){
        try {
            br = new BufferedReader(new FileReader("/storage/emulated/0/Documents/feature.txt"));
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                insertFeatures(currentLine);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertFeatures(String string) throws IOException {
        String[] features = string.split(",");
        if (!roots.containsKey(features[0])) {
            roots.put(features[0], new Node());
        }
        insert(features, roots.get(features[0]), 1);
    }

    private static void insert(String[] features, Node node, int index) {
        final Node nextChild;
        if (node.children.containsKey(features[index])) {
            nextChild = node.children.get(features[index]);
        } else {
            nextChild = new Node();
            nextChild.parent = node;
            nextChild.val = new String(features[index].trim());
            node.children.put(features[index], nextChild);
        }
        if (index == features.length - 1) {
            if(index == 10){
                Log.e("feature 10", node.children.get(features[index]).val);
            }
            nextChild.leaf = true;
            return;
        } else {
            insert(features, nextChild, index + 1);
        }
    }
}
