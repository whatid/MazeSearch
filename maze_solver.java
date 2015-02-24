//package MP1;

import java.lang.String;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Queue;
import java.util.LinkedList;
import java.lang.StringBuilder;
import java.util.Deque;
import java.util.PriorityQueue;
import java.lang.Math;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

/**
 * File created on 2/7/15. Maze solver program. Input is a maze in text file format.
 */
public class maze_solver {


    public enum cell {
        WALL, SPACE, START, END, DOT, SPAN
    }

    private static class Node {
        Position pos;
        int path_cost, estimated_cost, total_cost;
        ArrayList<Position> collected, uncollected; // Hold path's uncollected dots
        Node parent;

        public Node(int x, int y) {
            pos = new Position(x,y);
            parent = null;
        }

        public Node(int x, int y, Node parent) {
            pos = new Position(x,y);
            this.parent = parent;
        }

        @Override
        public String toString(){
            return String.format("NodeInfo:\nPosition: (%d, %d)\nPath Cost: %d\nTotal Cost: %d\nUncollected Size: %d\n",
                                pos.x, pos.y, path_cost, total_cost, uncollected.size());
        }

        private void print(){
            System.out.println(this);
        }
    }

    // Implements a MST with Kruskal's algorithm
    private static class MinimumSpanningTree{
        ArrayList<Position> v;
        ArrayList<Edge> e;

        public MinimumSpanningTree(ArrayList<Position> v){
            this.v = v;
            //this.e = reduce(v, createEdges(v));
            this.e = buildMST(v);
        }

        class Edge implements Comparable<Edge>{
            Position v1; // Index of v1 in v
            Position v2; // Index of v2 in v
            int weight;

            public Edge(Position a, Position b){
                v1 = a;
                v2 = b;
                weight = manhattanDist(a, b);
            }

            @Override
            public int compareTo(Edge other){
                if(this.weight == other.weight)
                    return 0;

                return (this.weight > other.weight) ? 1 : -1;
            }

            @Override
            public String toString(){
                return String.format("(%d, %d) to (%d, %d)\tweight: %d.",
                                    v1.x, v1.y, v2.x, v2.y, weight);
            }
        }

        public ArrayList<Edge> buildMST(ArrayList<Position> v){
            if(v.isEmpty())
                System.out.println("ERROR1");

            PriorityQueue<Edge> pq = new PriorityQueue<Edge>();
            for(int i = 0; i < v.size(); i++){
                for(int j = 1; j < v.size(); j++){
                    Edge newEdge = new Edge(v.get(i), v.get(j));
                    pq.add(newEdge);
                    //String s = new String().format("(i,j) = (%d,%d)", i, j);
                    //System.out.println(s);
                }
            }
            // Edge asdf = pq.peek();
            // System.out.println(asdf.toString());

            // Keep track of connected components (start all unconnected)
            HashMap<Position, Set<Position>> kForest = new HashMap<Position, Set<Position>>();
            for(Position vertex : v){
                Set<Position> connected = new HashSet<Position>();
                connected.add(vertex);
                kForest.put(vertex, connected);
            }

            ArrayList<Edge> edgesMST = new ArrayList<Edge>();
            // Add the shortest edges until all vertices are connected
            while(true){
                if(pq.peek() == null)
                    System.out.println("ERROR2");
                Edge testEdge = pq.poll();
                Set<Position> connectedToV1 = kForest.get(testEdge.v1);
                Set<Position> connectedToV2 = kForest.get(testEdge.v2);

                // Vertices are already connected
                if(connectedToV1.equals(connectedToV2))
                    continue;

                // Connect vertices
                edgesMST.add(testEdge);
                connectedToV1.addAll(connectedToV2);
                for(Position vertex : connectedToV1){
                    kForest.put(vertex, connectedToV1);
                }
                if(connectedToV1.size() == v.size())
                    break;
            }
            return edgesMST;
        }

        private void print(){
            for(Edge edge : e){
                System.out.println(edge);
            }
        }

        private int weight(){
            int w = 0;
            for(Edge edge: e){
                w += edge.weight;
            }
            return w;
        }
    }

    private static class Position {
        int x;
        int y;

        public Position(int x, int y){
            this.x = x;
            this.y = y;
        }

        //@Override
        public boolean equalTo(Position other){
            if(this.x == other.x && this.y == other.y)
                return true;

            else return false;
        }
    }

    // Maze to be populated by maze_parser and passed as argument to searches
    private static class Maze{
        cell[][] maze;
        int[][] visited;
        /* Could add an array that counts how many times a dot has been collected
            This would be used in the heuristic function to give a heavier weight
            to dots that haven't been collected before.
        */
        //private ArrayList<int> timesCollected;
        private final Position start;
        private ArrayList<Position> dots;
        private HashMap<Set<Position>, MinimumSpanningTree> dotsToMST;

        public Maze(cell[][] maze, int[][] visited, Position start, ArrayList<Position> dots,
                    HashMap<Set<Position>, MinimumSpanningTree> hm){
            this.maze = maze;
            this.visited = visited;
            this.start = start;
            this.dots = dots;
            this.dotsToMST = hm;
        }

        @Override
        public String toString(){
            return String.format("MazeInfo:\nStart: (%d, %d)\nnumDots: %d",
                                start.x, start.y, dots.size());
        }

        private void print(){
            System.out.println(this);
        }

    }

    //private static Pair maze_parser(boolean[][] visited, cell[][] maze) {
    private static Maze maze_parser() {
        /*
           Note about finding maze text files:
           Use the commented out code below to find file relative to your own directory.
           The string currently used is my the location of the file on my computer
         */

        System.out.println(System.getProperty("hhhahha"));
        File file = new File("/Users/James/Documents/Programming/GitHub/MazeSearch/trickySearch.txt");

        // Maze info to fill
        cell[][] maze = new cell[7][20];           // Manually change size
        int[][] visited = new int[7][20];  // Manually change size
        Position start = null;
        ArrayList<Position> dots = new ArrayList<Position>();
        HashMap<Set<Position>, MinimumSpanningTree> dotsToMST = new HashMap<Set<Position>, MinimumSpanningTree>();

        // initialize visited array
        for (int i = 0; i < visited.length; i++) {
            for (int j = 0; j < visited[i].length; j++)
                visited[i][j] = 0;
        }

        try {
            Scanner it = new Scanner(file);
            int rows = 0;
            while (it.hasNextLine()) {

                String s = it.nextLine();
                for (int i = 0; i < s.length(); i++) {
                    // wall
                    if (s.charAt(i) == '%')
                        maze[rows][i] = cell.WALL;
                    // starting point P
                    else if (s.charAt(i) == 'P') {
                        start = new Position(rows, i);
                        maze[rows][i] = cell.START;
                    }
                    // dots
                    else if (s.charAt(i) == '.') {
                        //end = new Node(rows, i, null);
                        maze[rows][i] = cell.DOT;
                        dots.add(new Position(rows, i)); // Return all dots
                    }
                    // space
                    else
                        maze[rows][i] = cell.SPACE;
                }
                rows++;
            }
        } catch (FileNotFoundException error) {
            error.printStackTrace();
        }

        return new Maze(maze, visited, start, dots, dotsToMST);
    }
/*
    private static void dfs(boolean[][] visited, cell[][] maze, Node start) {

        int path_cost = 0;
        int expanded_nodes = 0;
        Deque<Node> stack = new LinkedList<Node>();

        stack.push(start);

        Node end = null;

        while (!stack.isEmpty()) {
            Node cur = stack.pop();
            expanded_nodes++;
            visited[cur.pos.x][cur.pos.y] = true;

            if ( maze[cur.pos.x][cur.pos.y] != cell.END  ){
                maze[cur.pos.x][cur.pos.y] = cell.SPAN;
            }



            //found end point
            if (maze[cur.pos.x][cur.pos.y] == cell.END) {
                end = cur;
                while(end.parent != null){
                    end = end.parent;
                    maze[end.x][end.y] = cell.DOT;
                    path_cost++;
                }
                maze[end.x][end.y] = cell.START;
                System.out.println("path cost: " + path_cost);
                System.out.println("expanded nodes: " + expanded_nodes);
                return;
            } else {

                //check all four neighbors
                if (cur.pos.x + 1 < maze.length) {
                    if (maze[cur.pos.x + 1][cur.pos.y] != cell.WALL && !visited[cur.pos.x + 1][cur.pos.y]) {
                        stack.push(new Node(cur.pos.x + 1, cur.pos.y, cur));
                    }
                }
                if (cur.pos.x - 1 >= 0) {
                    if (maze[cur.pos.x - 1][cur.pos.y] != cell.WALL && !visited[cur.pos.x - 1][cur.pos.y]) {
                        stack.push(new Node(cur.pos.x - 1, cur.pos.y, cur));
                    }
                }
                if (cur.pos.y + 1 < maze[0].length) {
                    if (maze[cur.pos.x][cur.pos.y + 1] != cell.WALL && !visited[cur.pos.x][cur.pos.y + 1]) {
                        stack.push(new Node(cur.pos.x, cur.pos.y + 1, cur));
                    }
                }
                if (cur.pos.y - 1 >= 0) {
                    if (maze[cur.pos.x][cur.pos.y - 1] != cell.WALL && !visited[cur.pos.x][cur.pos.y - 1]) {
                        stack.push(new Node(cur.pos.x, cur.pos.y - 1, cur));
                    }
                }
            }
        }
    }

    private static void bfs(boolean[][] visited, cell[][] maze, Node start) {

        Queue<Node> q = new LinkedList<Node>();

        q.add(start);

        int path_cost = 0;
        int expanded_nodes = 0;
        Node end = null;

        while (!q.isEmpty()) {
            Node cur = q.remove();
            expanded_nodes++;
            visited[cur.pos.x][cur.pos.y] = true;

            if ( maze[cur.pos.x][cur.pos.y] != cell.END  ){
                maze[cur.pos.x][cur.pos.y] = cell.SPAN;
            }



            //found end point
            if (maze[cur.pos.x][cur.pos.y] == cell.END) {
                end = cur;
                while(end.parent != null){
                    end = end.parent;
                    maze[end.x][end.y] = cell.DOT;
                    path_cost++;
                }
                maze[end.x][end.y] = cell.START;
                System.out.println("path cost:" + path_cost);
                System.out.println("expanded nodes: " + expanded_nodes);
                return;
            } else {

                //check all four neighbors
                if (cur.pos.x + 1 < maze.length) {
                    if (maze[cur.pos.x + 1][cur.pos.y] != cell.WALL && !visited[cur.pos.x + 1][cur.pos.y]) {
                        q.add(new Node(cur.pos.x + 1, cur.pos.y, cur));
                    }
                }
                if (cur.pos.x - 1 >= 0) {
                    if (maze[cur.pos.x - 1][cur.pos.y] != cell.WALL && !visited[cur.pos.x - 1][cur.pos.y]) {
                        q.add(new Node(cur.pos.x - 1, cur.pos.y, cur));
                    }
                }
                if (cur.pos.y + 1 < maze[0].length) {
                    if (maze[cur.pos.x][cur.pos.y + 1] != cell.WALL && !visited[cur.pos.x][cur.pos.y + 1]) {
                        q.add(new Node(cur.pos.x, cur.pos.y + 1, cur));
                    }
                }
                if (cur.pos.y - 1 >= 0) {
                    if (maze[cur.pos.x][cur.pos.y - 1] != cell.WALL && !visited[cur.pos.x][cur.pos.y - 1]) {
                        q.add(new Node(cur.pos.x, cur.pos.y - 1, cur));
                    }
                }
            }
        }
    }
*/
    private static int manhattanDist (Position a, Position b){
        return (Math.abs(a.x - b.x) + Math.abs(a.y - b.y));
    }

    private static int heuristic (Node cur, Maze m) {
        // 1. Manhattan Distance for a single dot endpoint:
        if(m.dots.size() == 1)
            return manhattanDist(cur.pos, m.dots.get(0));

        // 2. Heuristic for multiple dots:
        // h(n) = distance to nearest dot + weight of MST of all uncollected dots
        int distToNearest = 1000;

        if(cur.uncollected.isEmpty())
            return 0;

        if(cur.uncollected.size() == 1)
            return manhattanDist(cur.pos, cur.uncollected.get(0));

        for(Position dot : cur.uncollected){
            distToNearest = (manhattanDist(cur.pos, dot) < distToNearest)
                            ? manhattanDist(cur.pos, dot) : distToNearest;
        }

        Set<Position> uncollectedSet = new HashSet<Position>(cur.uncollected);

        if(!m.dotsToMST.containsKey(uncollectedSet)){
            // Need to add this set to the HashMap
            MinimumSpanningTree newMST = new MinimumSpanningTree(cur.uncollected);
            m.dotsToMST.put(uncollectedSet, newMST);
        }

        return distToNearest + m.dotsToMST.get(uncollectedSet).weight();

    }

    public static class TotalCost implements Comparator<Node> {
        @Override
        public int compare(Node first, Node second) {
            if (first.total_cost < second.total_cost) {
                return -1;
            }
            else if (first.total_cost > second.total_cost) {
                return 1;
            }
            else
                return 0;
        }
    }

    public static class EstimatedCost implements Comparator<Node> {
        @Override
        public int compare(Node first, Node second) {
            if (first.estimated_cost < second.estimated_cost) {
                return -1;
            }
            else if (first.estimated_cost > second.estimated_cost) {
                return 1;
            }
            else
                return 0;
        }
    }

    // expands node that has the lowest value of the heuristic function.
/*
    private static void greedy_bfs(Node start, Node end, cell [][] maze, boolean [][] visited) {
        Comparator<Node> comp = new EstimatedCost();
        PriorityQueue<Node> q = new PriorityQueue<Node>(1000, comp);
        int path_cost = 0;
        int expanded_nodes = 0;
        start.estimated_cost = heuristic(start, end);
        q.add(start);

        while (!q.isEmpty()) {

            // removes the node with the lowest heuristic value
            Node cur = q.remove();
            expanded_nodes++;
            //mark as visited
            visited[cur.pos.x][cur.pos.y] = true;
            if ( maze[cur.pos.x][cur.pos.y] != cell.END  ){
                maze[cur.pos.x][cur.pos.y] = cell.SPAN;
            }




            // found end point
            if (maze[cur.pos.x][cur.pos.y] == cell.END) {
                while(cur.parent != null){
                    cur = cur.parent;
                    maze[cur.pos.x][cur.pos.y] = cell.DOT;
                    path_cost++;
                }
                maze[cur.pos.x][cur.pos.y] = cell.START;
                System.out.println("path cost: " + path_cost);
                System.out.println("expanded nodes: " + expanded_nodes);
                return;
            }
            else {

                // check neighbors, add to queue;

                if (cur.pos.x + 1 < maze.length) {
                    if (maze[cur.pos.x + 1][cur.pos.y] != cell.WALL && !visited[cur.pos.x + 1][cur.pos.y]) {
                        Node temp = new Node(cur.pos.x + 1, cur.pos.y, cur);
                        temp.estimated_cost = heuristic(temp, end);
                        q.add(temp);
                    }
                }
                if (cur.pos.x - 1 >= 0) {
                    if (maze[cur.pos.x - 1][cur.pos.y] != cell.WALL && !visited[cur.pos.x - 1][cur.pos.y]) {
                        Node temp = new Node(cur.pos.x - 1, cur.pos.y, cur);
                        temp.estimated_cost = heuristic(temp, end);
                        q.add(temp);
                    }
                }
                if (cur.pos.y + 1 < maze[0].length) {
                    if (maze[cur.pos.x][cur.pos.y + 1] != cell.WALL && !visited[cur.pos.x][cur.pos.y + 1]) {
                        Node temp = new Node(cur.pos.x, cur.pos.y + 1, cur);
                        temp.estimated_cost = heuristic(temp, end);
                        q.add(temp);
                    }
                }
                if (cur.pos.y - 1 >= 0) {
                    if (maze[cur.pos.x][cur.pos.y - 1] != cell.WALL && !visited[cur.pos.x][cur.pos.y - 1]) {
                        Node temp = new Node(cur.pos.x, cur.pos.y - 1, cur);
                        temp.estimated_cost = heuristic(temp, end);
                        q.add(temp);
                    }
                }

            }
        }
    }
*/
    //private static void A_star (Node start, Node end, cell [][] maze, boolean[][] visited) {
    private static void A_star (Maze m) {

        Comparator<Node> comp = new TotalCost();
        PriorityQueue<Node> q = new PriorityQueue<Node>(1000, comp);

        Node start = new Node(m.start.x, m.start.y);
        start.uncollected = new ArrayList<Position>(m.dots);
        cell[][] maze = m.maze;
        int[][] visited = m.visited;

        q.add(start);
        int path_cost = 0;
        int expanded_nodes = 0;
        start.path_cost = 0;
        start.total_cost = start.path_cost + heuristic(start, m);

        int maxVisited = 50;

        while (!q.isEmpty()) {

            // remove from open set and mark visited
            Node cur = q.poll();

            //cur.print();

            expanded_nodes++;
            visited[cur.pos.x][cur.pos.y] = visited[cur.pos.x][cur.pos.y] + 1;

            /*
            if ( maze[cur.pos.x][cur.pos.y] != cell.DOT ){
                maze[cur.pos.x][cur.pos.y] = cell.SPAN;
            }
            */

            // Check should change to uncollected.isEmpty()
            if(cur.uncollected.isEmpty()){
                while(cur.parent != null){
                    int dotCount = 0;

                    boolean isDot = false;
                    for(Position p : m.dots){
                        if(cur.pos.equalTo(p)){
                            dotCount++;
                            isDot = true;
                            break;
                        }
                    }
                    maze[cur.pos.x][cur.pos.y] = (isDot) ? cell.SPAN : cell.DOT;
                    path_cost++;
                    cur = cur.parent;
                }
                maze[cur.pos.x][cur.pos.y] = cell.START;
                System.out.println("path cost: " + path_cost);
                System.out.println("expanded nodes: " + expanded_nodes);
                return;
            }

            // check all four neighbors
            if (cur.pos.x + 1 < maze.length) {
                if (maze[cur.pos.x + 1][cur.pos.y] != cell.WALL && visited[cur.pos.x + 1][cur.pos.y] < maxVisited) {
                    Node temp = new Node(cur.pos.x + 1, cur.pos.y, cur);
                    temp.path_cost = cur.path_cost + 1;
                    temp.uncollected = new ArrayList<Position>();
                    for(Position p : cur.uncollected){
                        if(!p.equalTo(temp.pos))
                            temp.uncollected.add(p);
                    }
                    // if(maze[cur.pos.x + 1][cur.pos.y] == cell.DOT)
                    //     temp.uncollected.remove(temp.pos);
                    temp.total_cost = temp.path_cost + heuristic(temp, m);
                    q.add(temp);
                }
            }
            if (cur.pos.x - 1 >= 0) {
                if (maze[cur.pos.x - 1][cur.pos.y] != cell.WALL && visited[cur.pos.x - 1][cur.pos.y] < maxVisited) {
                    Node temp = new Node(cur.pos.x - 1, cur.pos.y, cur);
                    temp.path_cost = cur.path_cost + 1;
                    temp.uncollected = new ArrayList<Position>();
                    for(Position p : cur.uncollected){
                        if(!p.equalTo(temp.pos))
                            temp.uncollected.add(p);
                    }
                    temp.total_cost = temp.path_cost + heuristic(temp, m);
                    q.add(temp);
                }
            }
            if (cur.pos.y + 1 < maze[0].length) {
                if (maze[cur.pos.x][cur.pos.y + 1] != cell.WALL && visited[cur.pos.x][cur.pos.y + 1] < maxVisited) {
                    Node temp = new Node(cur.pos.x, cur.pos.y + 1, cur);
                    temp.path_cost = cur.path_cost + 1;
                    temp.uncollected = new ArrayList<Position>();
                    for(Position p : cur.uncollected){
                        if(!p.equalTo(temp.pos))
                            temp.uncollected.add(p);
                    }
                    temp.total_cost = temp.path_cost + heuristic(temp, m);
                    q.add(temp);
                }
            }
            if (cur.pos.y - 1 >= 0) {
                if (maze[cur.pos.x][cur.pos.y - 1] != cell.WALL && visited[cur.pos.x][cur.pos.y - 1] < maxVisited) {
                    Node temp = new Node(cur.pos.x, cur.pos.y - 1, cur);
                    temp.path_cost = cur.path_cost + 1;
                    temp.uncollected = new ArrayList<Position>();
                    for(Position p : cur.uncollected){
                        if(!p.equalTo(temp.pos))
                            temp.uncollected.add(p);
                    }
                    temp.total_cost = temp.path_cost + heuristic(temp, m);
                    q.add(temp);
                }
            }
        }
    }

    public static void main(String[] args) {

        Maze m = maze_parser();

        // Some tests
        /*
        for(Position p : m.dots){
            System.out.println(String.format("(%d, %d)", p.x, p.y));
        }

        MinimumSpanningTree mst = new MinimumSpanningTree(m.dots);
        mst.print();

        m.print();
        */
        // To test search algorithms call the corresponding function below.
        // test one at a time......

        A_star(m);
        //greedy_bfs(m);
        //bfs(m);
        //dfs(m);

        //print to console

        StringBuilder output = new StringBuilder();

        int rows = 0;

        while (rows < m.maze.length) {
            for (int y = 0; y < m.maze[0].length; y++) {
                switch (m.maze[rows][y]) {
                    // Temporarily changed for easier visual
                    case DOT:
                        output.append(" . "); //"."
                        break;
                    case WALL:
                        output.append("[%]"); //"%"
                        break;
                    case START:
                        output.append(" P "); //"P"
                        break;
                    case END:
                        output.append(" E "); //"E"
                        break;
                    case SPACE:
                        output.append("   "); //" "
                        break;
                    case SPAN:
                        output.append(" * "); //"*"
                        break;
                    default:
                        break;
                }
            }
            System.out.println(output);
            output.setLength(0);
            rows++;
        }
    }
}
