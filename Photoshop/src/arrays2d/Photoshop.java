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
				temp=pixels[i]; //set temp variable to current pixel color
				pixels[i]=pixels[pixels.length-1-i]; //change pixel color to pixel in the same place on the right half of the screen
				pixels[pixels.length-1-i]=temp; //sets the right pixel to the original current pixel color
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
        	for (int j=0; j<pixels[i].length; j++) {
        		double t=distance(pixels[i][j], colorList[0]); int index=0;
        		for (int k=1; k<colorList.length; k++) {
        			if (distance(pixels[i][j], colorList[k])<t) {
        				t=distance(pixels[i][j], colorList[k]);
        				index=k;
        			}
        		}
        		pixels[i][j]=colorList[index];
        	}
        }
    }
    
    public void kmeans() {
    	for (int count=0; count<300; count++) {
	    	int prev=0; 
	    	Color[][] cList=new Color[9][2];
	    	Color[][][] temp=new Color[pixels.length][pixels[0].length][2];
	    	
	    	for (int i=0; i<cList.length; i++) {
	    		cList[i][0]=pixels[pixels.length/((i+3)/3)-1][pixels[0].length/(i%3+1)-1];
	    	}
	    	
	    	for (int i=0; i<temp.length; i++) {
	    		for (int j=0; j<temp[i].length; j++) {
	    			double t=distance(pixels[i][j], cList[0][0]); int index=0;
	    			for (int k=1; k<cList.length; k++) {
	    				t=distance(pixels[i][j], cList[k][0]);
	    				index=k;
	    			}
	    			temp[i][j][0]=cList[index][0];
	    			temp[i][j][1]=pixels[i][j];
	    		}
	    	}
	    	int[][] avg=new int[9][4];
	    	for (int i=0; i<temp.length; i++) {
	    		for (int j=0; j<temp.length; j++) {
	    			for (int k=0; k<cList.length; k++) {
	    				if (temp[i][j][0]==cList[k][0]) {
	    					avg[k][0]+=temp[i][j][1].getRed();
	    					avg[k][1]+=temp[i][j][1].getGreen();
	    					avg[k][2]+=temp[i][j][1].getBlue();
	    					avg[k][3]++;
	    					break;
	    				}
	    			}
	    		}
	    	}
	    	int c=0;
	    	for (int i=0; i<cList.length; i++) {
	    		if (avg[i][3]!=0)
	    			cList[i][1]=new Color(avg[i][0]/avg[i][3], avg[i][1]/avg[i][3], avg[i][2]/avg[i][3]);
	    		else 
	    			cList[i][1]=Color.black;
	    		if (distance(cList[i][0],cList[i][1])<10) {
	    			c++;
	    		}
	    	}
	    	if (c==cList.length) break;
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
			temp[i]=new Color[pixels[i].length];
        	for (int j=0; j<pixels[i].length; j++) { //goes through every pixel
        		int r=0,g=0,b=0;
        		if (i>=n&&i<pixels.length-n&&j>=n&&j<pixels[i].length-n) {
	        		for (int k=n; k>=-n; k--) {
	        			for (int x=n; x>=-n; x--) {
	        				r+=pixels[i-k][j-x].getRed();
	        				g+=pixels[i-k][j-x].getGreen();
	        				b+=pixels[i-k][j-x].getBlue();
	        			}
	        		}
	        		temp[i][j]=new Color((int)(r/Math.pow(2*n+1,2)), (int)(g/Math.pow(2*n+1,2)), (int)(b/Math.pow(2*n+1,2)));
        		}
        		
        		else {
        			System.out.println(i+" "+j);
        			int x, y=1;
        			for (x=Math.max(0, i-n); x<Math.min(pixels.length, i+n); x++) {
        				for (y=Math.max(0, j-n); y<Math.min(pixels[i].length, j+n); y++) {
        					r+=pixels[x][y].getRed();
							g+=pixels[x][y].getGreen();
							b+=pixels[x][y].getBlue();
        				}
        			}
        			int x1=Math.max(0, i-n), y1=Math.max(0, j-n);
        			int div=Math.abs((x-x1)*(y-y1));
        			temp[i][j]=new Color(r/div, g/div, b/div);
        		}
        		
//        		else {
//        			int i1=i, i2=i-(pixels.length-1), j1=j, j2=j-(pixels[i].length-1); 
//        			
//        			
//        			if (i1<n) { //top edge
//        				if (j1<n) { //left corner
//        					for (int x=i-i1; x<i+n; x++) {
//        						for (int y=j-j1; y<j+n; y++) {
//        							r+=pixels[x][y].getRed();
//        							g+=pixels[x][y].getGreen();
//        							b+=pixels[x][y].getBlue();
//        						} 
//            				}
//        					int iMin=Math.min(i1, i2*-1), jMin=Math.min(j1,j2*-1);
//                			int div=(n+1+Math.min(Math.abs(iMin), n))*(n+1+Math.min(Math.abs(jMin), n));
//            				temp[i][j]=new Color(r/div, g/div, b/div);
//        				}
//        				else if (j2>-n&&j2<=0) { //right corner
//        					for (int x=i-i1; x<i+n; x++) {
//        						for (int y=j-j2; y>j-n; y--) {
//        							r+=pixels[x][y].getRed();
//        							g+=pixels[x][y].getGreen();
//        							b+=pixels[x][y].getBlue();
//        						} 
//            				}
//        					int iMin=Math.min(i1, i2*-1), jMin=Math.min(j1,j2*-1);
//                			int div=(n+1+Math.min(Math.abs(iMin), n))*(n+1+Math.min(Math.abs(jMin), n));
//            				temp[i][j]=new Color(r/div, g/div, b/div);
//        				}
//        				else {
//        					for (int x=i-i1; x<i+n; x++) {
//        						for (int y=j+n; y>j-n; y--) {
//        							r+=pixels[x][y].getRed();
//        							g+=pixels[x][y].getGreen();
//        							b+=pixels[x][y].getBlue();
//        						} 
//        					}
//        					int iMin=Math.min(i1, i2*-1), jMin=Math.min(j1,j2*-1);
//                			int div=(n+1+Math.min(Math.abs(iMin), n))*(n+1+Math.min(Math.abs(jMin), n));
//            				temp[i][j]=new Color(r/div, g/div, b/div);
//        				}
//        			}
//        			
//        			else if (i2>-n){ //bottom edge
//    					if (j1<n) { //left corner
//    						for (int x=i-i2; x>i-n; x--) {
//    							for (int y=j-j1; y<j+n; y++) {
//        							r+=pixels[x][y].getRed();
//        							g+=pixels[x][y].getGreen();
//        							b+=pixels[x][y].getBlue();
//        						} 
//    						}
//                			int div=(n+1-i2)*(n+1+j1);
//            				temp[i][j]=new Color(r/div, g/div, b/div);
//        				}
//        				else if (j2>-n&&j2<=0){ //right corner
//        					for (int x=i-i2; x>i-n; x--) {
//		    					for (int y=j-j2; y>j-n; y--) {
//									r+=pixels[x][y].getRed();
//									g+=pixels[x][y].getGreen();
//									b+=pixels[x][y].getBlue();
//								} 
//        					}
//                			int div=(n+1-i2)*(n+1-j2);
//            				temp[i][j]=new Color(r/div, g/div, b/div);
//        				}
//        				else {
//        					for (int x=i-i2; x>i-n; x--) {
//		    					for (int y=j+n; y>j-n; y--) {
//									r+=pixels[x][y].getRed();
//									g+=pixels[x][y].getGreen();
//									b+=pixels[x][y].getBlue();
//								} 
//        					}
//                			int div=(n+1-i2)*(2*n+1);
//            				temp[i][j]=new Color(r/div, g/div, b/div);
//        				}
//        			}
//        			
//        			else {
//        				if (j1<n) { //left edge
//        					for (int x=i+n; x>i-n; x--) {
//        						for (int y=j-j1; y<j+n; y++) {
//        							r+=pixels[x][y].getRed();
//        							g+=pixels[x][y].getGreen();
//        							b+=pixels[x][y].getBlue();
//        						} 
//        					}
//        					int iMin=Math.min(i1, i2*-1), jMin=Math.min(j1,j2*-1);
//                			int div=(n+1+Math.min(Math.abs(iMin), n))*(n+1+Math.min(Math.abs(jMin), n));
//            				temp[i][j]=new Color(r/div, g/div, b/div);
//        				}
//        				else if (j2>-n&&j2<=0){ //right edge
//        					for (int x=i+n; x>i-n; x--) {
//        						for (int y=j-j2; y>j-n; y--) {
//									r+=pixels[x][y].getRed();
//									g+=pixels[x][y].getGreen();
//									b+=pixels[x][y].getBlue();
//								} 
//        					}
//        					int iMin=Math.min(i1, i2*-1), jMin=Math.min(j1,j2*-1);
//                			int div=(n+1+Math.min(Math.abs(iMin), n))*(n+1+Math.min(Math.abs(jMin), n));
//            				temp[i][j]=new Color(r/div, g/div, b/div);
//        				}
//        			}
//        		}
        	} 
        }
		for (int i=0; i<pixels.length; i++) {
        	for (int j=0; j<pixels[i].length; j++) {
//        		pixels[i][j]=temp[i][j] == null ? new Color(255,255,255): temp[i][j];
        		pixels[i][j]=temp[i][j];
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
        			for (int x=j-1; x<=j+1; x++) {
        				if (k==i&&x==j) {
        					r1=pixels[k][x].getRed()*8;
            				g1=pixels[k][x].getGreen()*8;
            				b1=pixels[k][x].getBlue()*8;
            				continue;
        				}
        				r+=pixels[k][x].getRed();
        				g+=pixels[k][x].getGreen();
        				b+=pixels[k][x].getBlue();
        			}
        			temp[i][j]=new Color((Math.max(Math.min(r1+r*-1, 255),0)), (Math.max(Math.min(g1+g*-1, 255),0)), (Math.max(Math.min(b1+b*-1, 255),0)));
        		}
        	}
        }
        for (int i=1; i<pixels.length-1; i++) {
        	for (int j=1; j<pixels[i].length-1; j++) {
        		pixels[i][j]=temp[i][j];
        	}
        }
    }
    
    public void sharpen() {
        outputName = "sharpened_" + outputName;
        Color[][]temp=new Color[pixels.length][]; 
        for (int i=1; i<pixels.length-1; i++) {
			temp[i]=new Color[pixels[i].length];
        	for (int j=1; j<pixels[i].length-1; j++) { //goes through every pixel
        		int r=0,g=0,b=0;
        		r+=pixels[i-1][j].getRed();
        		g+=pixels[i-1][j].getGreen();
        		b+=pixels[i-1][j].getBlue();
        		r+=pixels[i+1][j].getRed();
        		g+=pixels[i+1][j].getGreen();
        		b+=pixels[i+1][j].getBlue();
        		r+=pixels[i][j-1].getRed();
        		g+=pixels[i][j-1].getGreen();
        		b+=pixels[i][j-1].getBlue();
        		r+=pixels[i][j+1].getRed();
        		g+=pixels[i][j+1].getGreen();
        		b+=pixels[i][j+1].getBlue();
        		r*=-1;
        		g*=-1;
        		b*=-1;
        		r+=pixels[i][j].getRed()*5;
        		g+=pixels[i][j].getGreen()*5;
        		b+=pixels[i][j].getBlue()*5;
    			temp[i][j]=new Color((Math.max(Math.min(r, 255),0)), (Math.max(Math.min(g, 255),0)), (Math.max(Math.min(b, 255),0)));
        	}
        }
        for (int i=1; i<pixels.length-1; i++) {
        	for (int j=1; j<pixels[i].length-1; j++) {
        		pixels[i][j]=temp[i][j];
        	}
        }
    }
    
    public void rotate() {
    	 outputName = "rotated_" + outputName;
         Color[][]temp=new Color[pixels[0].length][pixels.length]; 
         for (int i=0; i<temp.length; i++) {
        	 for (int j=0; j<temp[i].length; j++) {
        		 
        	 }
         }
    }
   
    
    // *************** DON'T MESS WITH THE BELOW CODE **************** //
    
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
		
	        BufferedImage new_image = new BufferedImage(image.getWidth(),
	                        image.getHeight(), BufferedImage.TYPE_INT_ARGB);
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