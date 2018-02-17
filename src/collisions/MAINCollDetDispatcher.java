package collisions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import data.Blob;
import utils.U;

//divide the list into spatially determined related chunks
public class MAINCollDetDispatcher {

	
	int cellcount = 10;
	int cellSize = 500;
	
	List<Collidable> collidables;
	
	ColliderMAIN collider;
	
	public MAINCollDetDispatcher() {
		super();
		collider = new ColliderMAIN();
	}


	public void process(List<Collidable> collidables, int minX, int maxX, int minY, int maxY,
			double radiusFactor, ColFlag flag) {
				
		int spanX = maxX - minX;
		int width = spanX / cellSize + 1;
		
		
		int spanY = maxY - minY;
		int height = spanY / cellSize + 1;
		
//		System.out.println("spanX=" +spanX + "Xcells=" + width +
//				" spanY=" +spanY + "Ycells=" + height);
		
		List<Collidable>[][] stage = new ArrayList[width][height];
		int[][]count = new int[width][height];
		int totCount = 0;
		//instantiation
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				stage[x][y] = new ArrayList<Collidable>();
			}
		}
			
		for (Collidable c: collidables) {
			next:
			for (int x = 0; x < width; x++) {
				if (c.getPosition().getX() < minX + cellSize * (x+1)) {//found a column
					for (int y = 0; y < height; y++) {//TODO: for Y go in opposite direction (sky is the limit?)
						if (c.getPosition().getY() <  minY + cellSize * (y+1)) {//found a row
							stage[x][y].add(c);
							count[x][y]++;
							totCount++;
							break next;
						}
					}
				}
			}		
		}
				
//		StringBuilder allocations = new StringBuilder(totCount + ": \n");
//		for (int y = 0; y < height; y++) {
//			for (int x = 0; x < width; x++) {
//				allocations.append(count[x][y] + "\t");
//			}
//			allocations.append("\n");
//		}			
//		System.out.println(allocations);
		
		process(stage,  minX,  maxX,  minY,  maxY, radiusFactor,  flag);
	}
	
	//only stage param is used, rest passed to doCollisions
	private void process (List<Collidable>[][] stage, int minX, int maxX, int minY, int maxY,
			double radiusFactor, ColFlag flag) {
		//Scanner s = new Scanner(System.in);
		
		int width = stage.length;
		int height = stage[0].length;
		//System.out.println("in process width: " + width + " height "+ height);
				
		U.setLimitsWrapX(0, width-1);
		U.setLimitsWrapY(0, height-1);
		
		for (int y = 0; y < height; y++) {		
			for (int x = 0; x < width; x++) {
				
				//tested[x][y] = true;//TODO
				List<Collidable> lump = new ArrayList<>();
				//System.out.println("lump.size(): " + lump.size());
				
//				System.out.println("For x: " + x +", y: " +  y);
//				
//				System.out.print(U.x(x-1) + ", " + U.y(y-1));//top row
//				System.out.print(U.x(x) + ", " + U.y(y-1));//top row
//				System.out.println(U.x(x+1) + ", " + U.y(y-1) +" - top row");//top row
//				System.out.print(U.x(x-1) + ", " + U.y(y));//middle row
//				System.out.print(U.x(x) + ", " + U.y(y));//middle row
//				System.out.println(U.x(x+1) + ", " + U.y(y) +" - middle row");//middle row
//				System.out.print(U.x(x-1) + ", " + U.y(y+1));//middle row
//				System.out.print(U.x(x) + ", " + U.y(y+1));//middle row
//				System.out.println(U.x(x+1) + ", " + U.y(y+1) +" - bottom row");//middle row				
				
				lump.addAll(stage[U.x(x-1)][U.y(y-1)]);//top row
				lump.addAll(stage[U.x(x)][U.y(y-1)]);
				lump.addAll(stage[U.x(x+1)][U.y(y-1)]);
				lump.addAll(stage[U.x(x-1)][U.y(y)]);//middle row
				lump.addAll(stage[U.x(x)][U.y(y)]);
				lump.addAll(stage[U.x(x+1)][U.y(y)]);
				lump.addAll(stage[U.x(x-1)][U.y(y+1)]);//bottom row
				lump.addAll(stage[U.x(x)][U.y(y+1)]);
				lump.addAll(stage[U.x(x+1)][U.y(y+1)]);
				
				collider.doCollisions(lump, minX, maxX, minY, maxY, radiusFactor, flag);				
			}
		}
		
	}
	
	
	
	
	private int findY(int height, int minY, Collidable c) {
		for (int y = 0; y < height; y++) {
			if (c.getPosition().getY() <  minY + cellSize * (y+1)) {
				return y;
			}
		}
		return -1;//not found!
	}
}
