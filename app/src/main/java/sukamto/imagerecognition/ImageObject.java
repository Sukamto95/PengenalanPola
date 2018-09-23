package sukamto.imagerecognition;

import java.util.ArrayList;

public class ImageObject {
    ArrayList<Integer> listX;
    ArrayList<Integer> listY;

    public ImageObject(){
        listX = new ArrayList<>();
        listY = new ArrayList<>();
    }

    public ArrayList<Integer> getListX() {
        return listX;
    }

    public ArrayList<Integer> getListY() {
        return listY;
    }
}
