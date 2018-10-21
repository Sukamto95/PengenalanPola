package sukamto.imagerecognition.model;

import java.util.HashMap;
import java.util.Set;

public class Node {
    public Boolean leaf = false;
    public HashMap<String, Node> children = new HashMap<String, Node>();
    public Node parent;
    public String val = "";

}
