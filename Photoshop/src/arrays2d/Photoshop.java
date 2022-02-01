package arrays2d;
// Photoshop program that can run several manipulations on 
// an image
// filler code by Mr. David

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class Photoshop extends Component {

	// the name of the output file. will be determined by which methods are called
    private String outputName;
    
    // the 2d array of colors representing the image
    private Color[][] pixels;
    
    // the width and height of the image 
    private int w,h;
    

    // this method increases each color's rgb value by a given amount.
    // don't forget that rgb values are limited to the range [0,255]
    public void brighten(int amount) {
        outputName = "brightened_" + outputName;
        for (int i=0; i<pixels.length; i++) {
        	for (int j=0; j<pixels[i].length; j++) { //goes through every pixel
        		pixels[i][j]=new Color(Math.max(Math.min(pixels[i][j].getRed()+amount, 255),0),Math.max(Math.min(pixels[i][j].getGreen()+amount, 255),0),Math.max(Math.min(pixels[i][j].getBlue()+amount, 255),0));
        	} //sets each pixel rgb value to +=amount with limits of >=0 and <=255
        }
    }
    
    // flip an image either horizontally or vertically.
    public void flip(boolean horizontally) {
        outputName = (horizontally?"h":"v") + "_flipped_" + outputName;
		if (horizontally) { //check horizontal or vertical flip
			Color temp=new Color(0);
			for(int i=0;i<pixels.length;i++) { //goes through all rows
				for(int j=0;j<pixels[i].length/2;j++) { //goes through half the columns 
					temp=pixels[i][j]; //stores current pixel as a temp color
					pixels[i][j]=pixels[i][pixels[i].length-1-j]; //replaces the current pixel with the pixel in the same place on the bottom half of the screen
					pixels[i][pixels[i].length-1-j]=temp; //sets the bottom pixel to the original current pixel color
				}
			}
		}
		else { //vertical flip
			Color[] temp=new Color[pixels[0].length];
			for(int i=0;i<pixels.length/2;i++) { //go through half the rows
				temp=pixels[i]; //set temp to be row i
				pixels[i]=pixels[pixels.length-1-i]; //sets row i to the row across from row i
				pixels[pixels.length-1-i]=temp; //sets the row that was changed to be row i (from temp)
			}
		}
    }
    
    // negates an image
    // to do this: subtract each pixel's rgb value from 255 
    // and use this as the new value
    public void negate() {
        outputName = "negated_" + outputName;
        for (int i=0; i<pixels.length; i++) {
        	for (int j=0; j<pixels[i].length; j++) { //goes through every pixel
        		pixels[i][j]=new Color(255-pixels[i][j].getRed(), 255-pixels[i][j].getGreen(), 255-pixels[i][j].getBlue());
        	} //sets each pixel rgb value 255-r, 255-g, 255-b
        }
    }
    
    // this makes the image 'simpler' by redrawing it using only a few colors
    // to do this: for each pixel, find the color in the list that is closest to
    // the pixel's rgb value. 
    // use this predefined color as the rgb value for the changed image.
    public void simplify() {
		// the list of colors to compare to. Feel free to change/add colors
		Color[] colorList = {Color.BLUE, Color.RED,Color.ORANGE, Color.MAGENTA,
                Color.BLACK, Color.WHITE, Color.GREEN, Color.YELLOW, Color.CYAN};
        outputName = "simplified_" + outputName;
        for (int i=0; i<pixels.length; i++) {
        	for (int j=0; j<pixels[i].length; j++) { //goes through every pixel
        		double t=distance(pixels[i][j], colorList[0]); int index=0;
        		for (int k=1; k<colorList.length; k++) { //goes through color list
        			if (distance(pixels[i][j], colorList[k])<t) { //finds the minimum distance between all colors
        				t=distance(pixels[i][j], colorList[k]); //sets new distance
        				index=k; //saves index of color in the colorList
        			}
        		}
        		pixels[i][j]=colorList[index]; //sets the pixel to be the closest color
        	}
        }
    }
    
    public void kmeans(int n) { //takes n as input and produces an image with n colors that are averaged multiple times over all the colors in the cluster
    	outputName = "kmeans_" + outputName;
    	if (n<=0) return; //if color number isn't positive it kills the method to avoid errors
    	int prev=0; //initializes prev which will be used to keep track of count later on
    	Color[][] cList=new Color[2][n]; //creates a 2 by n 2d array of colors to alternate between them every cycle as a color list
    	Color[][][] temp=new Color[pixels.length][pixels[0].length][2]; //creates a 3d array that again has 2 different templates for pixels 
    	int ind=0;
    	for (int i=0; i<(int)Math.sqrt(n); i++) {
    		for (int j=0; j<(int)Math.sqrt(n); j++, ind++) {
    			cList[0][ind]=pixels[(int)(i*(pixels.length/Math.sqrt(n)))][(int)(j*(pixels[0].length/Math.sqrt(n)))];
    		} //splits the image into a square grid and gets the next smallest square amount of colors
    	}
    	while (ind<n) {
    		cList[0][ind]=new Color((int)(Math.random()*255), (int) (Math.random()*255),(int) (Math.random()*255)); ind++;
    	} //sets the rest of the colors to random values 
    	
    	for (int i=0; i<temp.length; i++) {
    		for (int j=0; j<temp[i].length; j++) {
    			temp[i][j][0]=pixels[i][j];
    		} //makes temp [][][0]=pixels[][]
    	}
    	int c=0;
    	for (int count=0; c!=cList[0].length; count++) { //runs until all colors are only less than 1 distance away from their previous color 
    		System.out.print(count+" "); //prints the count of the for loop to check how long it takes and if it's still running
	    	
    		c=0;
	    	for (int i=0; i<temp.length; i++) {
	    		for (int j=0; j<temp[i].length; j++) { //goes through every pixel in the temp array 
	    			double t=distance(temp[i][j][count%2], cList[0][0]); int index=0; //takes the distance of temp[i][j] and alternates between the first and second set of temp pixels
					for (int k=1; k<cList[count%2].length; k++) { //takes the corresponding color list
	    				if (distance(temp[i][j][count%2], cList[count%2][k])<t) { //finds minimum distance between the pixel and the color list 
		    				t=distance(temp[i][j][count%2], cList[count%2][k]); //sets new minimum distance
		    				index=k; //save index
	    				}
	    			}
	    			temp[i][j][(count+1)%2]=temp[i][j][count%2]; //saves the next set of temp to be the last set of temp
	    			temp[i][j][count%2]=cList[count%2][index]; //saves the colors each pixel is closest to
	    		}
	    	}
	    	int[][] avg=new int[n][4]; //creates array to keep averages 
	    	for (int i=0; i<temp.length; i++) {
	    		for (int j=0; j<temp[i].length; j++) {
	    			for (int k=0; k<cList[0].length; k++) { //goes through color list 
	    				if (temp[i][j][count%2]==cList[count%2][k]) { //checks which color the pixel is 
	    					avg[k][0]+=temp[i][j][(count+1)%2].getRed(); //adds to the green blue and red value
	    					avg[k][1]+=temp[i][j][(count+1)%2].getGreen();
	    					avg[k][2]+=temp[i][j][(count+1)%2].getBlue();
	    					avg[k][3]++; //adds count to that color
	    					break; //goes to next pixel
	    				}
	    			}
	    		}
	    	}
	    	for (int i=0; i<cList[0].length; i++) {
	    		if (avg[i][3]!=0) //sets color list to new colors that are the averages of the clusters if there is at least 1 color in the list
	    			cList[(count+1)%2][i]=new Color(avg[i][0]/avg[i][3], avg[i][1]/avg[i][3], avg[i][2]/avg[i][3]); 
	    		else //sets color to black if there are no values in the cluster
	    			cList[(count+1)%2][i]=Color.black;
	    		if (distance(cList[count%2][i],cList[(count+1)%2][i])<1) //checks if previous color and current color are less than 1 distance away
	    			c++;
	    	}
    		prev=count; //sets variable to keep track of which set of pixels to use
    	}
    	
    	for (int i=0; i<pixels.length; i++) {
    		for (int j=0; j<pixels[i].length; j++) {
    			pixels[i][j]=temp[i][j][prev%2]; //fills in pixels with the new n colors 
    		}
    	}
    }
    
    // optional helper method (recommended) that finds the 'distance' 
    // between two colors.
    // use the 3d distance formula to calculate
    public double distance(Color c1, Color c2) {	
		return Math.sqrt(Math.pow((c1.getRed()-c2.getRed()),2)+Math.pow((c1.getGreen()-c2.getGreen()),2)+Math.pow((c1.getBlue()-c2.getBlue()),2));	
    } 
    
    // this blurs the image
    // to do this: at each pixel, sum the 8 surrounding pixels' rgb values 
    // with the current pixel's own rgb value. 
    // divide this sum by 9, and set it as the rgb value for the blurred image
    public void blur(int n) { //n=blur degree
		outputName = "blurred_" + outputName;
		Color[][]temp=new Color[pixels.length][]; 
		for (int i=0; i<pixels.length; i++) { 
			temp[i]=new Color[pixels[i].length]; //sets each temp row to be the same length as pixels[i]
        	for (int j=0; j<pixels[i].length; j++) { //goes through every pixel
        		int r=0,g=0,b=0;
        		if (i>=n&&i<pixels.length-n&&j>=n&&j<pixels[i].length-n) { //checks if the pixel isn't on the edge
	        		for (int k=n; k>=-n; k--) {
	        			for (int x=n; x>=-n; x--) { //sums values of n neighbors from the pixel on each side (2n+1 by 2n+1 square)
	        				r+=pixels[i-k][j-x].getRed();
	        				g+=pixels[i-k][j-x].getGreen();
	        				b+=pixels[i-k][j-x].getBlue();
	        			}
	        		}
	        		temp[i][j]=new Color((int)(r/Math.pow(2*n+1,2)), (int)(g/Math.pow(2*n+1,2)), (int)(b/Math.pow(2*n+1,2))); //pixel value after being "blurred" (divides sum by 2n+1^2)
        		}
        		
        		else { //if it's on the edge of the image
        			int x = 0, y=0;
        			for (x=Math.max(0, i-n); x<Math.min(pixels.length, i+n); x++) { //goes to the max distance of n or the last thing in bounds
        				for (y=Math.max(0, j-n); y<Math.min(pixels[i].length, j+n); y++) {
        					r+=pixels[x][y].getRed();
							g+=pixels[x][y].getGreen();
							b+=pixels[x][y].getBlue();
        				} 
        			}
        			int x1=Math.max(0, i-n), y1=Math.max(0, j-n); //gets values of the side lengths
        			int div=(x-x1)*(y-y1); //sets the division factor to the side length x by side length y
        			temp[i][j]=new Color(r/div, g/div, b/div); //divides each sum by the size of their square
        		}
        	}
        }
		for (int i=0; i<pixels.length; i++) {
        	for (int j=0; j<pixels[i].length; j++) {
        		pixels[i][j]=temp[i][j]; //sets the pixels to the blurred version
        	}
		}
	}
    
    // this highlights the edges in the image, turning everything else black. 
    // to do this: at each pixel, sum the 8 surrounding pixels' rgb values. 
    // now, multiply the current pixel's rgb value by 8, then subtract the sum.
    // this value is the rgb value for the 'edged' image
    public void edge() {
        outputName = "edged_" + outputName;
        Color[][]temp=new Color[pixels.length][]; 
        for (int i=1; i<pixels.length-1; i++) {
			temp[i]=new Color[pixels[i].length];
        	for (int j=1; j<pixels[i].length-1; j++) { //goes through every pixel
        		int r=0,g=0,b=0, r1=0, g1=0, b1=0;
        		for (int k=i-1; k<=i+1; k++) { 
        			for (int x=j-1; x<=j+1; x++) { //draws 3x3 square around curr pixel
        				if (k==i&&x==j) { //if it's the center (current) pixel, set it to a separate value and multiply it by 8
        					r1=pixels[k][x].getRed()*8;
            				g1=pixels[k][x].getGreen()*8;
            				b1=pixels[k][x].getBlue()*8;
            				continue; //skip to next pixel
        				}
        				r+=pixels[k][x].getRed(); //sum all the rgb of other pixels
        				g+=pixels[k][x].getGreen();
        				b+=pixels[k][x].getBlue();
        			}
        			temp[i][j]=new Color((Math.max(Math.min(r1+r*-1, 255),0)), (Math.max(Math.min(g1+g*-1, 255),0)), (Math.max(Math.min(b1+b*-1, 255),0))); //uses edge formula for new color values
        		}
        	}
        }
        for (int i=1; i<pixels.length-1; i++) {
        	for (int j=1; j<pixels[i].length-1; j++) {
        		pixels[i][j]=temp[i][j]; //sets to edged values
        	}
        }
    }
    
    public void sharpen() { //sharpens image using sharpen kernel (-1, -1, +5, -1, -1)
        outputName = "sharpened_" + outputName;
        Color[][]temp=new Color[pixels.length][]; //creates temp array to hold new values
        for (int i=1; i<pixels.length-1; i++) {
			temp[i]=new Color[pixels[i].length];
        	for (int j=1; j<pixels[i].length-1; j++) { //goes through every pixel
        		int r=0,g=0,b=0;
        		r+=pixels[i-1][j].getRed(); g+=pixels[i-1][j].getGreen(); b+=pixels[i-1][j].getBlue(); 
        		r+=pixels[i+1][j].getRed(); g+=pixels[i+1][j].getGreen(); b+=pixels[i+1][j].getBlue();
        		r+=pixels[i][j-1].getRed(); g+=pixels[i][j-1].getGreen(); b+=pixels[i][j-1].getBlue();
        		r+=pixels[i][j+1].getRed(); g+=pixels[i][j+1].getGreen(); b+=pixels[i][j+1].getBlue();
        		r*=-1; g*=-1; b*=-1; //sums pixels 1 above, 1 left, 1 right, 1 below and multiples by -1
        		r+=pixels[i][j].getRed()*5;
        		g+=pixels[i][j].getGreen()*5;
        		b+=pixels[i][j].getBlue()*5; //gets current pixel and multiplies by 5
    			temp[i][j]=new Color((Math.max(Math.min(r, 255),0)), (Math.max(Math.min(g, 255),0)), (Math.max(Math.min(b, 255),0))); //new value using sharpen kernel formula
        	}
        }
        for (int i=1; i<pixels.length-1; i++) {
        	for (int j=1; j<pixels[i].length-1; j++) {
        		pixels[i][j]=temp[i][j]; //sets pixels to sharpened values
        	}
        }
    }
    
    public void rotate() { //rotates image 90 degrees clockwise
    	 outputName = "rotated_" + outputName;
         Color[][]temp=pixels; //creates a temp array that contains the original image
         pixels=new Color[temp[0].length][temp.length]; //sets pixels to new dimensions
         for (int i=0; i<pixels.length; i++) {
        	 for (int j=temp.length-1, k=0; j>=0; j--, k++) {
        		 pixels[i][k]=temp[j][i];
        	 } //goes through every column from bottom to top and places it from left to right on pixel's corresponding row
         }
    }
   
    
    // *************** I MESSED WITH THE BELOW CODE :D **************** //
    
    // feel free to check it out, but don't change it unless you've consulted 
    // with Mr. David and understand what the code's doing
    
    

    public void run() {
    	JFileChooser fc = new JFileChooser();
//		File workingDirectory = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+ "Images");
//		fc.setCurrentDirectory(workingDirectory);
		fc.showOpenDialog(null);
		File my_file = fc.getSelectedFile();
		if (my_file == null)
			System.exit(-1);
		
		// reads the image file and creates our 2d array
        BufferedImage image;
		try {
			image = ImageIO.read(my_file);
		
	        create_pixel_array(image);
			outputName = my_file.getName();
			
			// runs the manipulations determined by the user
			System.out.println("Enter the manipulations you would like to run on the image.\nYour "
					+ "choices are: brighten, flip, negate, blur, edge, or simplify.\nEnter each "
					+ "manipulation you'd like to run, then type in 'done'.");
			Scanner in = new Scanner(System.in);
			String action = in.next().toLowerCase();
			while (!action.equals("done")) {
	    			try {
		    			if (action.equals("brighten")) {
		    				System.out.println("enter an amount to increase the brightness by");
		    				int brightness = in.nextInt();
		        			Method m = getClass().getDeclaredMethod(action, int.class);
		        			m.invoke(this, brightness);
		    			}
		    			else if (action.equals("flip")) {
		    				System.out.println("enter \"h\" to flip horizontally, anything else to flip vertically.");
		        			Method m = getClass().getDeclaredMethod(action, boolean.class);
		        			m.invoke(this, in.next().equals("h"));
		    			}
		    			else if (action.equals("blur")) {
		    				System.out.println("enter amount of blur");
		    				int blurness = in.nextInt();
		        			Method m = getClass().getDeclaredMethod(action, int.class);
		        			m.invoke(this, blurness);
		    			}
		    			else if (action.equals("kmeans")) {
		    				System.out.println("enter number of colors for kmeans");
		    				int kmeansness = in.nextInt();
		        			Method m = getClass().getDeclaredMethod(action, int.class);
		        			m.invoke(this, kmeansness);
		    			}
		    			else if (action.equals("get")) {
		    				System.out.println("enter number of colors");
		    				int number = in.nextInt();
		        			Method m = getClass().getDeclaredMethod(action, int.class);
		        			m.invoke(this, number);
		    			}
		    			else {
		        			Method m = getClass().getDeclaredMethod(action);
		        			m.invoke(this, new Object[0]);
		    			}
		    			System.out.println("done. enter another action, or type 'done'");
	    			}
	    			catch (NoSuchMethodException e) {
	    				System.out.println("not a valid action, try again");
	    			} catch (IllegalAccessException e) {e.printStackTrace();System.exit(1);} 
	    			catch (IllegalArgumentException e) {e.printStackTrace();System.exit(1);}
	    			catch (InvocationTargetException e) {e.printStackTrace();System.exit(1);}
	    			
	    			action = in.next().toLowerCase();
	    		} 
			
	        in.close();
	        BufferedImage new_image = new BufferedImage(pixels[0].length,
                    pixels.length, BufferedImage.TYPE_INT_ARGB); //declares new_image as a new buffered image with new dimensions after actions/user inputs
	        // turns our 2d array of colors into a new png file
	        create_new_image(new_image);
	        File output_file = new File("Images/" + outputName);
	        ImageIO.write(new_image, "png", output_file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
		
    
    public void create_pixel_array(BufferedImage image) {
        w = image.getWidth();
        h = image.getHeight();
        pixels = new Color[h][];
        for (int i = 0; i < h; i++) {
            pixels[i] = new Color[w];
            for (int j = 0; j < w; j++) {
                pixels[i][j] = new Color(image.getRGB(j,i));
            }
        }
    }

    public void create_new_image(BufferedImage new_image) {
    	w=new_image.getWidth(); 
    	h=new_image.getHeight(); //sets width and height variables to new image width and height
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
            		new_image.setRGB(j, i, pixels[i][j].getRGB());
            }
        }
    }

    public static void main(String[] args) {
		new Photoshop();
	}

    public Photoshop() {
		run();
    }
}