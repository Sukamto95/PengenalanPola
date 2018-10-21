package sukamto.imagerecognition.model;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import java.util.ArrayList;

public class MainModel {
    public static int[] ARR_RED = new int[256];
    public static int[] ARR_GREEN = new int[256];
    public static int[] ARR_BLUE = new int[256];
    public static int[] ARR_GRAY = new int[256];
    public static double[] kum_red = new double[256];
    public static double[] kum_green = new double[256];
    public static double[] kum_blue = new double[256];
    public static double[] kum_gray = new double[256];
    public static int[] ekual_red = new int[256];
    public static int[] ekual_green = new int[256];
    public static int[] ekual_blue = new int[256];
    public static int[] ekual_gray = new int[256];
    public static int[] smooth_red;
    public static int[] smooth_green;
    public static int[] smooth_blue;
    public static int[] smooth_gray;
    public static final int REQUEST_CAPTURE_IMAGE = 100;
    public static final int REQUEST_SELECT_IMAGE = 200;
    public static Bitmap imageBitmap = null;
    public static Bitmap grayBitmap = null;
    public static Bitmap trBitmap = null;
    public static Bitmap trGrayBitmap = null;

    public static void setBitmap(Bitmap bitmap){
        imageBitmap = bitmap;
    }

    public static void setGrayBitmap(Bitmap bitmap){
        grayBitmap = bitmap;
    }

    public static void setTrBitmap(Bitmap bitmap){
        trBitmap = bitmap;
    }

    public static void setTrGrayBitmap(Bitmap bitmap){
        trGrayBitmap = bitmap;
    }

    public static Bitmap getImageBitmap(){
        return imageBitmap;
    }

    public static Bitmap getGrayBitmap(){
        return grayBitmap;
    }

    public static Bitmap getTrBitmap(){
        return trBitmap;
    }

    public static Bitmap getTrGrayBitmap(){
        return trGrayBitmap;
    }

    public static void countColor(){
        for(int i=0;i<=255;i++){
            ARR_RED[i]=0;
            ARR_GREEN[i]=0;
            ARR_BLUE[i]=0;
            ARR_GRAY[i]=0;
        }
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();
        grayBitmap = imageBitmap.copy(imageBitmap.getConfig(), true);
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                int pixel = imageBitmap.getPixel(y,x);
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = (pixel) & 0xff;
                int gray = (r+g+b)/3;
                grayBitmap.setPixel(y,x,Color.rgb(gray, gray, gray));

                ARR_RED[r]=ARR_RED[r]+1;
                ARR_GREEN[g]=ARR_GREEN[g]+1;
                ARR_BLUE[b]=ARR_BLUE[b]+1;
                ARR_GRAY[gray]=ARR_GRAY[gray]+1;
            }
        }
    }

    public static void ekualisasi(double w){
        kum_red = new double[256];
        kum_green = new double[256];
        kum_blue = new double[256];
        kum_gray = new double[256];
        ekual_red = new int[256];
        ekual_green = new int[256];
        ekual_blue = new int[256];
        ekual_gray = new int[256];
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();
        int[] pix = new int[width * height];
        int[] refGray = new int[256];
        int[] refRed = new int[256];
        int[] refGreen = new int[256];
        int[] refBlue = new int[256];
        imageBitmap.getPixels(pix, 0, width, 0, 0, width, height);
        recKumulatif(0, w);
        for(int i = 0 ; i < 256 ; i++){
            int kGray = (int) Math.round((kum_gray[i]*(Math.pow(2,8)-1))/(width*height));
            ekual_gray[kGray] += ARR_GRAY[i];
            refGray[i] = kGray;

            int kRed = (int) Math.round((kum_red[i]*(Math.pow(2,8)-1))/(width*height));
            ekual_red[kRed] += ARR_RED[i];
            refRed[i] = kRed;

            int kGreen = (int) Math.round((kum_green[i]*(Math.pow(2,8)-1))/(width*height));
            ekual_green[kGreen] += ARR_GREEN[i];
            refGreen[i] = kGreen;

            int kBlue = (int) Math.round((kum_blue[i]*(Math.pow(2,8)-1))/(width*height));
            ekual_blue[kBlue] += ARR_BLUE[i];
            refBlue[i] = kBlue;
        }
        trBitmap = imageBitmap.copy(imageBitmap.getConfig(), true);
        trGrayBitmap = imageBitmap.copy(imageBitmap.getConfig(), true);
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                int pixel = imageBitmap.getPixel(y,x);
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = (pixel) & 0xff;
                int gray = (r+g+b)/3;
                trBitmap.setPixel(y,x, Color.rgb(refRed[r], refGreen[g], refBlue[b]));
                trGrayBitmap.setPixel(y,x,Color.rgb(refGray[gray], refGray[gray], refGray[gray]));
            }
        }
    }

    public static void recKumulatif(int x, double w){
        if(x == 0){
            kum_gray[x] = (double)ARR_GRAY[x];
            kum_red[x] = (double)ARR_RED[x];
            kum_green[x] = (double)ARR_GREEN[x];
            kum_blue[x] = (double)ARR_BLUE[x];
            recKumulatif(x+1, w);
        } else if(x == 255){
            kum_gray[x] = Math.round(w*ARR_GRAY[x]+kum_gray[x-1]);
            kum_red[x] = Math.round(w*ARR_RED[x]+kum_red[x-1]);
            kum_green[x] = Math.round(w*ARR_GREEN[x]+kum_green[x-1]);
            kum_blue[x] = Math.round(w*ARR_BLUE[x]+kum_blue[x-1]);
            return;
        } else{
            kum_gray[x] = Math.round(w*ARR_GRAY[x]+kum_gray[x-1]);
            kum_red[x] = Math.round(w*ARR_RED[x]+kum_red[x-1]);
            kum_green[x] = Math.round(w*ARR_GREEN[x]+kum_green[x-1]);
            kum_blue[x] = Math.round(w*ARR_BLUE[x]+kum_blue[x-1]);
            recKumulatif(x+1, w);
        }
    }

    public static void recKumulatif(int[] arr_red, int[] arr_green,
                             int[] arr_blue, int x, double w){
        if(x == 0){
            kum_red[x] = (double)arr_red[x];
            kum_green[x] = (double)arr_green[x];
            kum_blue[x] = (double)arr_blue[x];
            recKumulatif(arr_red, arr_green, arr_blue,x+1, w);
        } else if(x == 255){
            kum_red[x] = Math.round(w*arr_red[x]+kum_red[x-1]);
            kum_green[x] = Math.round(w*arr_green[x]+kum_green[x-1]);
            kum_blue[x] = Math.round(w*arr_blue[x]+kum_blue[x-1]);
            return;
        } else{
            kum_red[x] = Math.round(w*arr_red[x]+kum_red[x-1]);
            kum_green[x] = Math.round(w*arr_green[x]+kum_green[x-1]);
            kum_blue[x] = Math.round(w*arr_blue[x]+kum_blue[x-1]);
            recKumulatif(arr_red, arr_green, arr_blue,x+1, w);
        }
    }

    public static Bitmap[] getSmoothingImage() {
        smooth_red = new int[256];
        smooth_green = new int[256];
        smooth_blue = new int[256];
        smooth_gray = new int[256];
        Bitmap colorResult = imageBitmap.copy(imageBitmap.getConfig(), true);
        Bitmap grayResult = imageBitmap.copy(imageBitmap.getConfig(), true);
        int count;
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();
        int sumR, sumG, sumB, sumGr;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                count = 0;
                sumR = 0;
                sumG = 0;
                sumB = 0;
                sumGr = 0;
                for (int a = -1; a <= 1; a++) {
                    for (int b = -1; b <= 1; b++) {
                        if (i + a >= 0 && i + a < width && j + b >= 0 && j + b < height) {
                            int pixel = imageBitmap.getPixel(i+a,j+b);
                            int red = (pixel >> 16) & 0xff;
                            int green = (pixel >> 8) & 0xff;
                            int blue = (pixel) & 0xff;
                            int gray = (red+green+blue)/3;
                            sumR += red;
                            sumG += green;
                            sumB += blue;
                            sumGr += gray;
                            count++;
                        }
                    }
                }
                int avgR = sumR/count;
                int avgG = sumG/count;
                int avgB = sumB/count;
                int avgGray = (avgR+avgG+avgB)/3;

                smooth_red[avgR] += 1;
                smooth_green[avgG] += 1;
                smooth_blue[avgB] += 1;
                smooth_gray[avgGray] += 1;
                colorResult.setPixel(i,j, Color.rgb(avgR, avgG, avgB));
                grayResult.setPixel(i,j, Color.rgb(avgGray, avgGray, avgGray));
            }
        }

        return new Bitmap[]{colorResult, grayResult};
    }

    public static Bitmap getScaledBitmap(Bitmap bitmap, int rotate){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = width;
        int newHeight = height;
        int count = 0;
        while(newWidth > 1000){
            newWidth /= 2;
            count++;
        }
        for(int i = 0 ; i < count ; i++){
            newHeight /= 2;
        }
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        matrix.postRotate(rotate);
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return bitmapResult;
    }

    public static Bitmap getBinaryImage(Bitmap bitmap, int threshold) {
        Bitmap result = bitmap.copy(bitmap.getConfig(), true);

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                int pixel = bitmap.getPixel(i,j);
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = (pixel) & 0xff;
                int gray = (r+g+b)/3;
                if (gray < threshold) {
                    result.setPixel(i,j, Color.rgb(0,0,0));
                } else {
                    result.setPixel(i,j, Color.rgb(255,255,255));
                }
            }
        }
        return result;
    }

    public static String[] checkNumber(Bitmap bitmap) {
        Bitmap image = bitmap.copy(bitmap.getConfig(), true);
        String result = "";
        String chains = "";
        int[] color;
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                color = SupportModel.getPixelColor(image, i, j);
                if (color[3] == 0) {
                    String chain = getChainCode(image, i, j);
                    chains = chains + chain + "\n";
                    result = result + translate(chain);
                    floodFill(image, i, j);
                }
            }
        }
        return new String[] {result, chains};
    }

    public static String[] checkNumber2(Bitmap bitmap) {
        int[] color;
        String chain;
        Bitmap image = bitmap.copy(bitmap.getConfig(), true);
        StringBuilder result = new StringBuilder();
        StringBuilder chains = new StringBuilder();
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                color = SupportModel.getPixelColor(image, i, j);
                if (color[3] == 0) {
                    chain = getChainCode(image, i, j);
                    chains.append(String.format("%s\n\n", chain));
                    result.append(String.format("%d ", translate2(chain)));
                    floodFill(image, i, j);
                }
            }
        }
        return new String[]{result.toString(), chains.toString()};
    }

    private static String getChainCode(Bitmap bitmap, int x, int y) {
        int a = x;
        int b = y;
        int[] next;
        int source = 6;
        String chainCode = "";
        do {
            next = getNextPixel(bitmap, a, b, source);
            a = next[0];
            b = next[1];
            source = (next[2] + 4) % 8;
            chainCode = chainCode + next[2];
        }
        while (!(a == x && b == y));
        return chainCode;
    }

    private static int[] getNextPixel(Bitmap bitmap, int x, int y, int source) {
        int a, b, target = source;
        int[][] points = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
        do {
            target = (target + 1) % 8;
            a = x + points[target][0];
            b = y + points[target][1];
        }
        while (SupportModel.getPixelColor(bitmap, a, b)[3] == 255);
        return new int[]{a, b, target};
    }

    private static int translate(String chain) {
        double[][] ratio = {
                {0.250, 0.075, 0.098, 0.075, 0.250, 0.075, 0.098, 0.075},
                {0.329, 0.079, 0.074, 0.000, 0.361, 0.053, 0.095, 0.005},
                {0.108, 0.161, 0.172, 0.044, 0.134, 0.112, 0.243, 0.022},
                {0.143, 0.098, 0.158, 0.105, 0.132, 0.098, 0.169, 0.094},
                {0.186, 0.146, 0.090, 0.005, 0.328, 0.005, 0.232, 0.005},
                {0.161, 0.060, 0.218, 0.067, 0.147, 0.070, 0.211, 0.063},
                {0.189, 0.086, 0.133, 0.103, 0.163, 0.086, 0.159, 0.077},
                {0.211, 0.091, 0.201, 0.000, 0.201, 0.105, 0.183, 0.004},
                {0.175, 0.095, 0.132, 0.101, 0.164, 0.101, 0.132, 0.095},
                {0.168, 0.090, 0.147, 0.086, 0.181, 0.086, 0.142, 0.095}
        };
        int result = 0;
        double max = 0;
        double[] direction = new double[8];
        for (int i = 0; i < chain.length(); i++) {
            direction[Character.getNumericValue(chain.charAt(i))]++;
        }
        for (int i = 0; i < 8; i++) {
            direction[i] = direction[i] / chain.length();
        }
        for (int i = 0; i < 10; i++) {
            double res = 0;
            for (int j = 0; j < 8; j++) {
                res = res + ratio[i][j] * direction[j];
            }
            res = res / getVectorLength(ratio[i]) / getVectorLength(direction);
            if (res > max) {
                max = res;
                result = i;
            }
        }
        return result;
    }

    public static int translate2(String chain) {
        ArrayList<Integer> count;
        String simpleChain = getSimpleChain(chain);
        if (simpleChain.equals("2460")) {
            count = getCountChain(chain);
            if((double)count.get(0) / count.get(1) <= 0.2){
                return 1;
            }
            else{
                return 0;
            }
        } else if (simpleChain.equals("246424602060")) {
            count = getCountChain(chain);
            if (count.get(1) > count.get(count.size() - 3)) {
                return 2;
            } else {
                return 5;
            }
        } else if (simpleChain.equals("246020602060")) {
            return 3;
        } else if (simpleChain.equals("2420246060")) {
            return 4;
        } else if (simpleChain.equals("24642460")) {
            return 6;
        } else if (simpleChain.equals("246060")) {
            return 7;
        } else if (simpleChain.equals("24602060")) {
            return 9;
        } else {
            return -1;
        }
    }

    public static String getSimpleChain(String chain) {
        if (chain.length() < 2) {
            return chain;
        }
        char current;
        char last = chain.charAt(0);
        StringBuilder result = new StringBuilder();
        result.append(last);
        for (int i = 1; i < chain.length(); i++) {
            current = chain.charAt(i);
            if (current != last && Character.getNumericValue(current) % 2 == 0) {
                last = chain.charAt(i);
                result.append(last);
            }
        }
        return result.toString();
    }

    public static ArrayList<Integer> getCountChain(String chain) {
        if (chain.length() < 2) {
            return new ArrayList<>();
        }
        ArrayList<Integer> list = new ArrayList<>();
        char current;
        char last = chain.charAt(0);
        int counter = 1;
        for (int i = 1; i < chain.length(); i++) {
            current = chain.charAt(i);
            if (Character.getNumericValue(current) % 2 == 0) {
                if (current != last) {
                    list.add(counter);
                    last = chain.charAt(i);
                    counter = 1;
                } else {
                    counter++;
                }
            }
        }
        list.add(counter);
        return list;
    }

    private static double getVectorLength(double[] vector) {
        double sum = 0;
        for (int i = 0; i < 8; i++) {
            sum = sum + vector[i] * vector[i];
        }
        return Math.sqrt(sum);
    }

    private static void floodFill(Bitmap bitmap, int x, int y) {
        int[] color = SupportModel.getPixelColor(bitmap, x, y);
        if (color[3] != 255) {
            bitmap.setPixel(x,y,Color.rgb(255,255,255));
            floodFill(bitmap, x - 1, y);
            floodFill(bitmap, x + 1, y);
            floodFill(bitmap, x, y - 1);
            floodFill(bitmap, x, y + 1);
        }
    }

    public static String[] checkSkeletonManual(Bitmap bitmap) {
        Bitmap image = bitmap.copy(bitmap.getConfig(), true);
        String chains = "";
        String skeletonCode = "";
        int[] color;
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                color = SupportModel.getPixelColor(image, i, j);
                if (color[3] == 0) {
                    String[] code = getSkeletonChain(image, i, j);
                    String chain = code[0];
                    skeletonCode += code[1]+"\n";
                    chains = chains + chain + "\n";
                    floodFill(image, i, j);
                }
            }
        }
        return new String[] {chains, skeletonCode};
    }

    private static String[] getSkeletonChain(Bitmap bitmap, int x, int y) {
        int a = x;
        int b = y;
        int[] next;
        int source = 6;
        int skeletonX, skeletonY;
        String skeletonCode = "";
        String chainCode = "";
        do {
            next = getNextSkeletonPixel(bitmap, a, b, source);
            a = next[0];
            b = next[1];
            source = (next[2] + 4) % 8;
            skeletonX = next[3];
            skeletonY = next[4];
            if(skeletonX != 0 && skeletonY != 0){
                skeletonCode += skeletonX + ","+skeletonY + "; ";
            }
            chainCode = chainCode + next[2];
        } while (!(a == x && b == y));
        return new String[]{chainCode,skeletonCode};
    }

    private static int[] getNextSkeletonPixel(Bitmap bitmap, int x, int y, int source) {
        int a, b, target = source;
        int skeletonX;
        int skeletonY;
        int[][] points = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
        do {
            target = (target + 1) % 8;
            a = x + points[target][0];
            b = y + points[target][1];
        }
        while (SupportModel.getPixelColor(bitmap, a, b)[3] == 255);
        int[] skeleton = getSkeletonPixel(bitmap, a, b, target);
        skeletonX = skeleton[0];
        skeletonY = skeleton[1];
        return new int[]{a, b, target, skeletonX, skeletonY};
    }

    private static int[] getSkeletonPixel(Bitmap bitmap, int x, int y, int source) {
        int a = x;
        int b = y;
        int target = (source + 2) % 8;
        int count = 0;
        int[][] points = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
        do {
            a += points[target][0];
            b += points[target][1];
            count++;
        }
        while (SupportModel.getPixelColor(bitmap, a, b)[3] == 0 && count < 80);
        if(count >=80){
            return new int[]{0,0};
        }
        int centerX = (a + x) / 2;
        int centerY = (b + y) / 2;
        return new int[]{centerX, centerY};
    }

    public static Bitmap getSkeletonBitmap(Bitmap bitmap, String skeletonCodes){
        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        String[] arrSkeletonCode = skeletonCodes.split("\n");
        for(int i = 0 ; i < arrSkeletonCode.length; i++){
            String[] skeletonCode = arrSkeletonCode[i].split("; ");
            for(int j = 0 ; j < skeletonCode.length ; j++){
                String[] index = skeletonCode[j].split(",");
                bmp.setPixel(Integer.parseInt(index[0]),Integer.parseInt(index[1]),Color.rgb(0,0,0));
            }
        }
        return bmp;
    }

    public static Bitmap getZhangSuenBitmap(Bitmap bitmap) {
        ArrayList<Integer> satisfyX = null;
        ArrayList<Integer> satisfyY = null;
        int[][] bitmapArr = SupportModel.getBitmapArray(bitmap);
        int nChange;
        int step = 0;
        //int count = 1;
        do{
            nChange = 0;
            satisfyX = new ArrayList<>();
            satisfyY = new ArrayList<>();
            for (int i = 0; i < bitmapArr.length; i++) {
                for (int j = 0; j < bitmapArr[0].length; j++) {
                    if ((bitmapArr[i][j] == 0) && (i > 0 && i < bitmapArr.length - 1 && j > 0 && j < bitmapArr[0].length - 1)) {
                        boolean isSatisfy = isSatisfy(bitmapArr, i, j, (step+1)%2);
                        if(isSatisfy){
                            nChange ++;
                            satisfyX.add(i);
                            satisfyY.add(j);
                        }
                    }
                }
            }
            bitmapArr = getSatisfyArray(bitmapArr, satisfyX, satisfyY);
            step = (step + 1) % 2;
            //Log.e("step "+count,nChange+"");
            //count++;
        } while(nChange>0);
        Bitmap image = SupportModel.arrToBitmap(bitmapArr);
        return image;
    }

    private static boolean isSatisfy(int[][] arr, int x, int y, int step) {
        int a, b, target = 7;
        int ap = 0;
        int bp = 0;
        int currentColor;
        int lastColor = 0;
        int[] neighbourColor = new int[8];
        int[][] points = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
        for(int i = 0 ; i < 9 ; i++){
            target = (target + 1) % 8;
            a = x + points[target][0];
            b = y + points[target][1];
            currentColor = arr[a][b];
            neighbourColor[i%8] = currentColor;
            if(currentColor == 0 && lastColor == 1){
                ap++;
            }
            if(currentColor == 0 && i < 8){
                bp++;
            }
            lastColor = currentColor;
        }
        if(bp >= 2 && bp <= 6){
            if(ap == 1){
                if(step == 1){
                    if(neighbourColor[0] == 1 || neighbourColor[2] == 1 || neighbourColor[4] == 1){
                        if(neighbourColor[2] == 1 || neighbourColor[4] == 1 || neighbourColor[6] == 1){
                            return true;
                        } else{
                            return false;
                        }
                    } else{
                        return false;
                    }
                } else{
                    if(neighbourColor[0] == 1 || neighbourColor[2] == 1 || neighbourColor[6] == 1){
                        if(neighbourColor[0] == 1 || neighbourColor[4] == 1 || neighbourColor[6] == 1){
                            return true;
                        } else{
                            return false;
                        }
                    } else{
                        return false;
                    }
                }
            } else{
                return false;
            }
        } else{
            return false;
        }
    }

    private static int[][] getSatisfyArray(int[][] arr, ArrayList<Integer> satisfyX, ArrayList<Integer> satisfyY){
        for(int i = 0 ; i < satisfyX.size() ; i++){
            arr[satisfyX.get(i)][satisfyY.get(i)] = 1;
        }
        return arr;
    }

    public static StringBuffer getSkeletonFeature(Bitmap bitmap) {
        int count;
        int[] border, border2;

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int size = height * width;
        int[] pixels = new int[size];
        int[] pixelsa = new int[size];
        StringBuffer stringBuffer = new StringBuffer();

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.getPixels(pixelsa, 0, width, 0, 0, width, height);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if ((pixels[i + j * width] & 0x000000ff) != 255) {
                    border = SupportModel.floodFill(pixels, i, j, width);

                    do {
                        count = SupportModel.zhangSuenStep(pixelsa, border[0], border[1], border[2], border[3], width);
                    }
                    while (count != 0);

                    border2 = SupportModel.getNewBorder(pixelsa, border[0], border[1], border[2], border[3], width);
                    SkeletonFeature sf = SupportModel.extractFeature(pixelsa, border2[0], border2[1], border2[2], border2[3], width);

                    stringBuffer.append(String.format("%d, %b, %b, %b, %b, %b, %b, %b, %b, %b\r\n",
                            sf.endpoints.size(),
                            sf.hTop, sf.hMid, sf.hBottom,
                            sf.vLeft, sf.vMid, sf.vRight,
                            sf.lTop, sf.lMid, sf.lBottom));
                }
            }
        }
        return stringBuffer;
    }

    public static String recognitionFromFeature(String string){
        String[] features = string.split("\n");
        String result = "";
        for(int i = 0 ; i < features.length ; i++){
            String[] feature = features[i].split(", ");
            String endpoint = feature[0].trim();
            String htop = feature[1].trim();
            String hmid = feature[2].trim();
            String hbot = feature[3].trim();
            String vleft = feature[4].trim();
            String vmid = feature[5].trim();
            String vright = feature[6].trim();
            String ltop = feature[7].trim();
            String lmid = feature[8].trim();
            String lbot = feature[9].trim();
            if(endpoint.equals("0")){
                if(vleft.equals("true")){
                    if(lmid.equals("true")){
                        result += "D ";
                    } else if(lmid.equals("false")){
                        result += "B ";
                    }
                } else if(lmid.equals("false")){
                    result += "8 ";
                } else {
                    result += "O ";
                }
            } else if(endpoint.equals("1")){
                if(vleft.equals("true")){
                    result += "P ";
                } else if(hmid.equals("true")){
                    result += "e ";
                }  else if(vright.equals("true")){
                    result += "g ";
                } else if(lbot.equals("true")){
                    result += "6 ";
                } else if(ltop.equals("true")){
                    result += "9 ";
                }
            } else if(endpoint.equals("2")){
                if(hmid.equals("true")){
                    result += "R ";
                } else if(htop.equals("true") && hbot.equals("true")){
                    if(hbot.equals("true")){
                        result += "Z ";
                    } else{
                        result += "7 ";
                    }
                } else if(hbot.equals("true")){
                    if(vleft.equals("true")){
                        result += "L ";
                    } else{
                        result += "A ";
                    }
                } else if(vleft.equals("true")){
                    if(vright.equals("true")){
                        result += "U ";
                    } else if(ltop.equals("true")){
                        result += "p ";
                    } else{
                        result += "b ";
                    }
                } else if(lbot.equals("true")){
                    if(vright.equals("true")){
                        result += "d ";
                    } else{
                        result += "a ";
                    }
                } else if(lmid.equals("true")){
                    result += "Q ";
                } else if(ltop.equals("true")){
                    result += "q ";
                } else if(vright.equals("true")){
                    result += "I ";
                } else {
                    result += "C";
                }
            } else if(endpoint.equals("3")){
                if(vmid.equals("true")){
                    result += "T ";
                } else if(lmid.equals("true")){
                    result += "4 ";
                } else if(htop.equals("true")){
                    if(hbot.equals("true")){
                        if(hmid.equals("true")) {
                            result += "E ";
                        } else {
                            result += "z ";
                        }
                    } else{
                        if(vleft.equals("true")){
                            result += "F ";
                        } else{
                            result += "5 ";
                        }
                    }
                } else if(hbot.equals("true")){
                    result += "2 ";
                } else if(vleft.equals("true")){
                    if(vright.equals("false")){
                        result += "r ";
                    } else{
                        result += "M ";
                    }
                } else if(vright.equals("true")){
                    result += "1 ";
                } else {
                    result += "Y ";
                }
            } else if(endpoint.equals("4")){
                if(hmid.equals("true")){
                    result += "H ";
                } else if(vmid.equals("true")){
                    if(vleft.equals("true")){
                        result += "m ";
                    } else{
                        result += "t ";
                    }
                } else if(vleft.equals("false")){
                    result += "x ";
                } else if(htop.equals("true")){
                    result += "f ";
                } else if(vright.equals("true")){
                    result += "N ";
                } else{
                    result += "K ";
                }
            } else if(endpoint.equals("5")){
                result += "W ";
            }

        }
        return result;
    }

//    public static String recognitionFromFeature(String string){
//        Tree.insertAllFeatures();
//
//        return Tree.search(string);
//    }
}
