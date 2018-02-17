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
			double cellSizee, ColFlag flag) {
	
		//this.cellSize = cellSize;//TODO
		
		//A create grid of cells covering the area
		int noOfColumns = (maxX - minX) / cellSize+1;//TODO: reject bad input (like 0 rows, -2 columns)			
		int noOfRows = (maxY - minY) / cellSize+1;   //instead of "+1"s here :P
		
			
		List<Collidable>[][] cells = new ArrayList[noOfColumns][noOfRows];
		int[][]count = new int[noOfColumns][noOfRows];//at the moment used just for debugging
		//instantiation of Lists
		for (int x = 0; x < noOfColumns; x++) {
			for (int y = 0; y < noOfRows; y++) {
				cells[x][y] = new ArrayList<Collidable>();
			}
		}
		
		//B assign collidables to cells according to their positions
		for (Collidable c: collidables) {
			nextCollidable:
			for (int x = 0; x < noOfColumns; x++) {
				if (c.getPosition().getX() < minX + cellSize * (x+1)) {//found a column
					for (int y = 0; y < noOfRows; y++) {
						if (c.getPosition().getY() <  minY + cellSize * (y+1)) {//found a row
							cells[x][y].add(c);
							
							break nextCollidable;//collidable allocated to its cell - allocate the next one
						}
					}
				}
			}		
		}
		
		//DEBUG
		int totCount = 0;
		StringBuilder allocations = new StringBuilder("\n");
		for (int y = 0; y < noOfRows; y++) {
			for (int x = 0; x < noOfColumns; x++) {
				allocations.append(cells[x][y].size() + "\t");
				totCount+=cells[x][y].size();
			}
			allocations.append("\n");
		}			
		System.out.println(allocations.append("\n + total: " + totCount));
		
		
		//process(cells,  minX,  maxX,  minY,  maxY, cellSize,  flag);
		
		//C test for collisions
		//set-up 2 utility methods for addNeighborsToCell
		U.setLimitsWrapX(0, noOfColumns-1);
		U.setLimitsWrapY(0, noOfRows-1);
		for (int x = 0; x < noOfColumns; x++) {
			for (int y = 0; y < noOfRows; y++) {
				//dispatch spatially limited batches for collDet
				collider.doCollisions(addNeighborsToCell(cells, x, y),//TODO: only centre cell can be subj & obj. 
						minX, maxX, minY, maxY, 1, flag);
			}
			
		}
	}
	
	private List<Collidable> addNeighborsToCell (List<Collidable>[][] cells, int x, int y) {
		
		List<Collidable> result = new ArrayList<>();
		
		result.addAll(cells[U.x(x-1)][U.y(y-1)]);//top row
		result.addAll(cells[U.x(x)][U.y(y-1)]);
		result.addAll(cells[U.x(x+1)][U.y(y-1)]);
		result.addAll(cells[U.x(x-1)][U.y(y)]);//middle row
		result.addAll(cells[U.x(x)][U.y(y)]);
		result.addAll(cells[U.x(x+1)][U.y(y)]);
		result.addAll(cells[U.x(x-1)][U.y(y+1)]);//bottom row
		result.addAll(cells[U.x(x)][U.y(y+1)]);
		result.addAll(cells[U.x(x+1)][U.y(y+1)]);
		
		return result;
	}
	
	
	
	
	//only stage param is used, rest passed to doCollisions
	private void process (List<Collidable>[][] cells, int minX, int maxX, int minY, int maxY,
			double cellSize, ColFlag flag) {
		//Scanner s = new Scanner(System.in);
		
		int width = cells.length;
		int height = cells[0].length;
		//System.out.println("in process width: " + width + " height "+ height);
				
		U.setLimitsWrapX(0, width-1);
		U.setLimitsWrapY(0, height-1);
		
		for (int y = 0; y < height; y++) {		
			for (int x = 0; x < width; x++) {
				
				//tested[x][y] = true;//TODO
				List<Collidable> result = new ArrayList<>();
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
				
				result.addAll(cells[U.x(x-1)][U.y(y-1)]);//top row
				result.addAll(cells[U.x(x)][U.y(y-1)]);
				result.addAll(cells[U.x(x+1)][U.y(y-1)]);
				result.addAll(cells[U.x(x-1)][U.y(y)]);//middle row
				result.addAll(cells[U.x(x)][U.y(y)]);
				result.addAll(cells[U.x(x+1)][U.y(y)]);
				result.addAll(cells[U.x(x-1)][U.y(y+1)]);//bottom row
				result.addAll(cells[U.x(x)][U.y(y+1)]);
				result.addAll(cells[U.x(x+1)][U.y(y+1)]);
				
				collider.doCollisions(result, minX, maxX, minY, maxY, 1, flag);				
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
