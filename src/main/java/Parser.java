import java.util.ArrayList;
import java.util.*;
import java.awt.image.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;

public class Parser{
    public static void main(String[] args) {
        try{
            String filename = "image.png";
            int displayBufferSize = 256;
            int maxInstructions = 1000;
            int mult = 1;
            BufferedImage bi;
            if(args.length != 0) {
                try {
                    boolean displayHelp = false;
                    for(String i : args) {
                        if(i.equals("--help") || i.equals("-?") || i.equals("-h")) {
                            displayHelp = true;
                        }
                    }
                    if(displayHelp) {
                        System.out.println("Usage: java -jar png2mlog.jar *.png [displayBufferSize] [maxInstructions] [scale]\n\t--help - Display this dialog.\n\tdisplayBufferSize=256 - How many draw calls your processors are allowed to flush to the display.\n\tmaxInstructions=1000 - How many instructions each processor can store. \033[31mMust not be 0.\033[39m\n\tscale=1 - Integer scale for rendering your image.\n\n\033[1mSanitize your inputs. I am not responsible for this program hanging your terminal.\033[22m\n\nThis program will generate the necessary mlog code to render a given image at (0,0) in display coordinate space.\nBasedUser/png2mlog is a fork of owler0954/Image-to-code-converter-for-mindustry-6.0-logic-blocks on GitHub.");
                        return;
                    }
                } catch(Exception e) {
                    System.out.println(e);
                }
                // TODO: This is dumb.
                try {
                    try {
                        filename = args[0];
                    } catch(Exception e) {
                        filename = "image.png";
                    }
                    bi = ImageIO.read(new File(filename));
                } catch(Exception e) {
                    System.out.println("QUITTING: Expected valid RGB/RGBA image, got the following error: " + e);
                    return;
                }
                try {
                    displayBufferSize = Integer.parseInt(args[1]);
                } catch(Exception e) {
                    displayBufferSize = 256;
                }
                try {
                    maxInstructions = Integer.parseInt(args[2]);
                } catch(Exception e) {
                    maxInstructions = 1000;
                }
                try {
                    mult = Integer.parseInt(args[3]);
                } catch(Exception e) {
                    mult = 1;
                }
            } else {
                bi = ImageIO.read(new File("image.png"));
            }
            String curline;
            FileWriter interf;
            int w=bi.getWidth();
            int h=bi.getHeight();
            int[][] pictureR=new int[w][h];
            int[][] pictureG=new int[w][h];
            int[][] pictureB=new int[w][h];
            int lines=0;
            int drawCalls=0;
            int x=0; int y=0;
            int filesWritten = 0;
            interf = new FileWriter("output0.txt", false);
            File d = null;
            while(x<w){
                y=0;
                while(y<h){
                    int rgb=bi.getRGB(x,y);
                    pictureR[x][y] = (rgb >> 16) & 0xFF; 
                    pictureG[x][y] = (rgb >> 8) & 0xFF; 
                    pictureB[x][y] = (rgb) & 0xFF;
                    if((lines-1 / maxInstructions) > ((lines-2) / maxInstructions)) {
                        interf.append("drawflush display1\n");
                        interf.flush();
                        d = new File("output"+String.valueOf(filesWritten)+".txt");
                        if(d.length() > 32767) {
                            System.out.println("\033[1;31mCAUTION: \033[22;33mCurrent file output"+String.valueOf(filesWritten)+".txt has "+d.length()+"characters. You may not be able to import this mlog file into Mindustry, try a lower maxInstructions value.\033[22;39m");
                        }
                        filesWritten++;
                        interf = new FileWriter("output"+String.valueOf(filesWritten)+".txt");
                    }
                    if( (drawCalls / displayBufferSize) > ((drawCalls-1) / displayBufferSize)) {
                        interf.append("drawflush display1\n");
                        lines++;
                    }
                    interf.append("draw color "+pictureR[x][y]+" "+pictureG[x][y]+" "+pictureB[x][y]+" 255 0\n");
                    interf.append("draw rect "+x*mult+" "+(h-y-mult)*mult+" "+String.valueOf(mult)+" "+String.valueOf(mult)+" 0\n"); // two appends, for better readability
                    // side note, the reason there's h-y is because Mindustry starts its origin in the *bottom left* instead of top left, like BufferedImage.
                    // that's why we have to do this madness
                    lines += 2;
                    y++;
                }
                x++;
            }
            interf.append("drawflush display1\n"); // potential mlog leak, TODO fix
            interf.flush();
        }catch(IOException e){};
    }
}
