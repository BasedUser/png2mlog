import java.util.ArrayList;
import java.util.*;
import java.awt.image.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;

public class Parser{
    public static void main(String[] args) {
        try{
            String curline;
            FileWriter interf;
            BufferedImage bi = ImageIO.read( new File( "image.png" ) );
            int w=bi.getWidth();
            int h=bi.getHeight();
            int[][] pictureR=new int[w][h];
            int[][] pictureG=new int[w][h];
            int[][] pictureB=new int[w][h];
            int pixels=0;
            int x=0; int y=0;
            int filesWritten = 0;
            interf = new FileWriter("output0.txt", false);
            while(x<w){
                y=0;
                while(y<h){
                    int rgb=bi.getRGB(x,y);
                    pictureR[x][y] = (rgb >> 16) & 0xFF; 
                    pictureG[x][y] = (rgb >> 8) & 0xFF; 
                    pictureB[x][y] = (rgb ) & 0xFF;
                    if( (pixels / 999) > ((pixels-1) / 999)) {
                        interf.append("drawflush display1\n");
                        interf.flush();
                        filesWritten++;
                        interf = new FileWriter("output"+String.valueOf(filesWritten)+".txt");
                    }
                    interf.append("draw color "+pictureR[x][y]+" "+pictureG[x][y]+" "+pictureB[x][y]+" 255 0\n");
                    interf.append("draw rect "+x+" "+(h-y)+" 1 1 0\n"); // two appends, for better readability
                    // side note, the reason there's h-y is because Mindustry starts its origin in the *bottom left* instead of top left, like BufferedImage.
                    // that's why we have to do this madness
                    pixels++;
                    y++;
                }
                x++;
            }
            interf.flush();
        }catch(IOException e){};
    }
}
