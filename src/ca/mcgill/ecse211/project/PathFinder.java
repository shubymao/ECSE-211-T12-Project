package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * This class implements the atomic path-finding functions that allow the robot to compute
 * the optimal path to a desired grid coordinate. It also contains a representation of the real-world 
 * competition grip through its map attribute. These atomic helper functions will be used to create the 
 * different compound navigation methods (i.e. the search zone traversal algorithm and the algorithm to
 * carry the stranded back to the starting corner).
 * 
 * @author charlesbourbeau
 *
 */
public class PathFinder {

	/**
	 * Enumeration for the status of the grid.
	 * @author Shuby
	 *
	 */
	private enum Grid {
		Availible, SearchZoneNotVisited, SearchZoneVisited, Obsticles
	}

	/**
	 * The map for searching or traversing.
	 */
	private Grid[][] map;
	
	/**
	 * The coordinate of the start tile.
	 */
	private int[] start;
	
	/**
	 * The coordinate of the tunnel head tile.
	 */
	private int[] tunnelHead;
	
	/**
	 * The coordinate of the tunnel tail tile.
	 */
	private int[] tunnelTail;
	
	/**
	 * The coordinate of the lower left tile of the search zone.
	 */
	private int[] searchZoneLL;
	
	/**
	 * The coordinate of the upper right tile of the search zone.
	 */
	private int[] searchZoneUR;

	PathFinder(boolean green, int[] tnGLL, int[] tnGUR, int[] tnRLL, int[] tnRUR, int[] gLL, int[] gUR, int[] rLL,
			int[] rUR, int[] ilLL, int[] ilUR, int[] szGLL, int[] szGUR, int[] szRLL, int[] szRUR) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 15; j++) {
				map[i][j] = Grid.Obsticles;
			}
		}
		configureTunnel(green, tnGLL, tnGUR, tnRLL, tnRUR);
		initStart(green);
		if (green) {
			initMap(tnGLL, tnGUR, gLL, gUR, ilLL, ilUR, szGLL, szGUR);
		} else {
			initMap(tnRLL, tnRUR, rLL, rUR, ilLL, ilUR, szRLL, szRUR);
		}
	}

	/**
	 * Configure the tunnel entry parameter such as position and angle to enter and
	 * exit the tunnel.
	 * 
	 * @param green coordinate in tile unit
	 * @param tnGLL lower left green tunnel coordinate in tile unit
	 * @param tnGUR upper right green tunnel coordinate in tile unit
	 * @param tnRLL lower left red tunnel coordinate in tile unit
	 * @param tnRUR upper right red tunnel coordinate in tile unit
	 */
	private void configureTunnel(boolean green, int[] tnGLL, int[] tnGUR, int[] tnRLL, int[] tnRUR) {
		if (green) {
			tunnelHead = new int[3];
			tunnelHead[0] = tnGLL[0];
			tunnelHead[1] = (int) (tnGLL[1] - 1);
			tunnelHead[2] = 0;
			tunnelTail = new int[3];
			tunnelTail[0] = (int) (tnGUR[0] - 1);
			tunnelTail[1] = tnGUR[1];
			tunnelTail[2] = 180;
		} else {
			tunnelHead = new int[2];
			tunnelHead[0] = (int) (tnRLL[0] - 1);
			tunnelHead[1] = tnRLL[1];
			tunnelHead[2] = 90;
			tunnelTail = new int[2];
			tunnelTail[0] = tnRUR[0];
			tunnelTail[1] = (int) (tnRUR[1] - 1);
			tunnelTail[2] = 270;
		}
	}

	/**
	 * Initialize the start location and angle based on the team color.
	 * 
	 * @param green a boolean specifying whether the robot is green or red (false)
	 */
	private void initStart(boolean green) {
		start = new int[3];
		if (green) {
			start[0] = GREEN_START[0];
			start[1] = GREEN_START[1];
			start[2] = GREEN_START[2];
		} else {
			start[0] = RED_START[0];
			start[1] = RED_START[1];
			start[2] = RED_START[2];
		}
	}

	/**
	 * Initialize the map of the course later used for traversal and path finding.
	 * 
	 * @param tnLL tunnel coordinate lower left
	 * @param tnUR tunnel coordinate upper right
	 * @param izLL initial zone coordinate lower left
	 * @param izUR initial zone coordinate upper right
	 * @param ilLL island zone coordinate lower left
	 * @param ilUR island zone coordinate upper right
	 * @param szLL search zone coordinate lower left
	 * @param szUR search zone coordinate upper right
	 */
	private void initMap(int[] tnLL, int[] tnUR, int[] izLL, int[] izUR, int[] ilLL, int[] ilUR, int[] szLL,
			int[] szUR) {
		this.searchZoneLL = szLL;
		this.searchZoneUR = szUR;
		map = new Grid[9][15];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 15; j++) {
				if (i >= ilLL[0] && i < ilUR[0] && j >= ilLL[1] && j < ilUR[1]) {
					map[i][j] = Grid.Availible;
				}
				if (i >= szLL[0] && i < szUR[0] && j >= szLL[1] && j < szUR[1]) {
					map[i][j] = Grid.SearchZoneNotVisited;
				}
				if (i >= tnLL[0] && i < tnUR[0] && j >= tnLL[1] && j < tnUR[1]) {
					map[i][j] = Grid.Availible;
				}
			}
		}
	}

	/**
	 * Return the start coordinates and angle.
	 * 
	 * @return the start coordinate and angle.
	 */
	public int[] getStartCord() {
		return start;
	}

	/**
	 * return a path to get from tunnel head to the tunnel angle.
	 * 
	 * @return the path to head of the tunnel.
	 */
	public int[][] getPathToTunnelHead() {
		int[][] paths = new int[2][2];
		paths[0] = new int[] { start[0], tunnelHead[1] };
		paths[1] = new int[] { tunnelHead[0], tunnelHead[1] };
		return paths;
	}

	/**
	 * Return the coordinate and angle of tunnel head to reach to tunnel tail.
	 * 
	 * @return tunnel head coordinate and angle to get to tunnel tail.
	 */
	public int[] getTunnelHead() {
		return tunnelHead;
	}

	/**
	 * Return the coordinate and angle of tunnel tail to reach to tunnel head.
	 * 
	 * @return tunnel tail coordinate and angle to get to tunnel head.
	 */
	public int[] getTunnelTail() {
		return tunnelTail;
	}

	/**
	 * Register the obstacle based on the correct coordinate and angle that faced a
	 * block.
	 * 
	 * @param x coordinate of the vehicle (in tile unit)
	 * @param y coordinate of the vehicle (in tile unit)
	 * @param angle of the vehicle facing
	 * @return whether the obstacle was registered.
	 */
	public boolean registerObstacle(int x, int y, int angle) {
		x += (angle == 90) ? 1 : (angle == 270) ? -1 : 0;
		y += (angle == 0) ? 1 : (angle == 180) ? -1 : 0;
		if (x < 0 || x >= 15 || y < 0 || y >= 15)
			return false;
		map[y][x] = Grid.Obsticles;
		return true;
	}

	/**
	 * @param x coordinate of the vehicle (in tile unit)
	 * @param y coordinate of the vehicle (in tile unit)
	 * @return the path to the closest corner
	 */
	public ArrayList<int[]> getPathToClosestCorner(int x, int y) {
		int tX = Math.abs(x - searchZoneLL[0]) < Math.abs(x - searchZoneUR[0]) ? searchZoneLL[0] : searchZoneUR[0];
		int tY = Math.abs(y - searchZoneLL[1]) < Math.abs(y - searchZoneUR[1]) ? searchZoneLL[1] : searchZoneUR[1];
		;
		return aStar(x, y, tX, tY, false);
	}

	/**
	 * get the path to the next unvisited grid location for the robot to traverse.
	 * @param x coordinate of the vehicle (in tile unit)
	 * @param y coordinate of the vehicle (in tile unit)
	 * @return the path to the next coordinate to visit.
	 */
	public ArrayList<int[]> getNextSearchCordinate(int x, int y) {
		map[y][x] = Grid.SearchZoneVisited;
		return aStar(x, y, -1, -1, true);
	}

	/**
	 * The a* algorithm to find the shortest path to the target.
	 * 
	 * @param x coordinate of the vehicle (in tile unit)
	 * @param y coordinate of the vehicle (in tile unit)
	 * @param tX target x coordinate (in tile unit)
	 * @param tY target t coordinate (in tile unit)
	 * @param search a tag that determine whether the algorithm will be searching or path finding. 
	 * @return the shortest path to the target coordinate.
	 */
	private ArrayList<int[]> aStar(int x, int y, int tX, int tY, boolean search) {
		if (x >= 15 || x < 0 || y >= 9 || y < 0 || map[y][x] == Grid.Obsticles)
			return null;
		boolean[][] visited = new boolean[9][15];
		HashMap<Integer, Integer> parent = new HashMap<>();
		int[][] dirs = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };
		PriorityQueue<int[]> pq = new PriorityQueue<>(new Comparator<int[]>() {
			@Override
			public int compare(int[] a, int[] b) {
				return a[2] - b[2];
			}
		});
		pq.add(new int[] { x, y, search ? 0 : dist(x, y, tX, tY) * 4, 0 });
		while (!pq.isEmpty()) {
			int[] cur = pq.poll();
			int curX = cur[0];
			int curY = cur[1];
			int move = cur[3];
			if ((!search && curX == tX && curY == tY) || (search && map[curY][curX] == Grid.SearchZoneNotVisited)) {
				return getPathFromParent(curX, curY, parent);
			}
			visited[curY][curX] = true;
			int count = 0;
			for (int[] dir : dirs) {
				int nx = (int) (curX + dir[0]);
				int ny = (int) (curY + dir[1]);
				if (!valid(nx, ny, visited))
					continue;
				int cost = (search ? move + 1 : dist(nx, ny, tX, tY) + move + 1) * 4 + count;
				pq.offer(new int[] { nx, ny, cost, move + 1 });
				parent.put(nx * 9 + ny, curX * 9 + curY);
				count++;
			}
		}
		return null;
	}

	/**
	 * @param tX
	 * @param tY
	 * @param parent
	 * @return The path based on the parent map.
	 */
	private ArrayList<int[]> getPathFromParent(int tX, int tY, HashMap<Integer, Integer> parent) {
		ArrayList<int[]> res = new ArrayList<>();
		int curX = tX;
		int curY = tY;
		while (parent.containsKey(curX * 15 + curY)) {
			res.add(new int[] { curX, curY });
			int id = parent.get(curX * 15 + curY);
			curX = id / 9;
			curY = id % 9;
		}
		Collections.reverse(res);
		return res;
	}

	/**
	 * @param x
	 * @param y
	 * @param visited
	 * @return whether the current coordinate is valid.
	 */
	private boolean valid(int x, int y, boolean[][] visited) {
		if (x >= 15 || x < 0 || y >= 9 || y < 0 || map[y][x] == Grid.Obsticles || visited[y][x])
			return false;
		return true;
	}

	/**
	 * @param x
	 * @param y
	 * @param tx
	 * @param ty
	 * @return The euclidian distance from the target.
	 */
	private int dist(int x, int y, int tx, int ty) {
		return (int) (Math.abs(x - tx) + Math.abs(y - ty));
	}
}
