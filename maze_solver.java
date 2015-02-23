package MP1;

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
import java.util.Vector;

/**
 * File created on 2/7/15. Maze solver program. Input is a maze in text file format.
 */
public class maze_solver {


    public enum cell {
        WALL, SPACE, START, END, DOT, SPAN, NUMBER
    }

    private static class Node {
        int x;
        int y;
        int path_cost, estimated_cost, total_cost, distance /* distance traversed to reach up to N other nodes */;
        Node parent;

        public Node(int x, int y, Node parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }

    }

    private static class Pair {
        private final Node start;
        private Vector<Node> dots = null;

        public Pair(Node start, Vector<Node> storage) {
            this.start = start;
            this.dots = storage;
        }
    }

    private static Pair maze_parser(boolean[][] visited, cell[][] maze) {

        //variable to hold return values
        Pair result;
        Node start = null;
        Vector<Node> storage = new Vector<Node>();

        // initialize visited array
        for (int i = 0; i < visited.length; i++) {
            for (int j = 0; j < visited[i].length; j++)
                visited[i][j] = false;
        }

        /*
           Note about finding maze text files:
           Use the commented out code below to find file relative to your own directory.
           The string currently used is my the location of the file on my computer
         */

        //System.out.println(System.getProperty("user.dir"));
        File file = new File("/Users/fwirjo/IdeaProjects/MazeSearch/bigSearch.txt");

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
                        start = new Node(rows, i, null);
                        maze[rows][i] = cell.START;
                    }
                    // ending point .
                    else if (s.charAt(i) == '.') {
                        storage.add(new Node(rows, i, null));
                        maze[rows][i] = cell.DOT;
                        // space
                    }
                    else
                        maze[rows][i] = cell.SPACE;
                }
                rows++;
            }
        } catch (FileNotFoundException error) {
            error.printStackTrace();
        }

        result = new Pair(start, storage);

        return result;
    }

    private static void dfs(boolean[][] visited, cell[][] maze, Node start) {

        int path_cost = 0;
        int expanded_nodes = 0;
        Deque<Node> stack = new LinkedList<Node>();

        stack.push(start);

        Node end = null;

        while (!stack.isEmpty()) {
            Node cur = stack.pop();
            expanded_nodes++;
            visited[cur.x][cur.y] = true;

            if ( maze[cur.x][cur.y] != cell.END  ){
                maze[cur.x][cur.y] = cell.SPAN;
            }



            //found end point
            if (maze[cur.x][cur.y] == cell.END) {
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
                if (cur.x + 1 < maze.length) {
                    if (maze[cur.x + 1][cur.y] != cell.WALL && !visited[cur.x + 1][cur.y]) {
                        stack.push(new Node(cur.x + 1, cur.y, cur));
                    }
                }
                if (cur.x - 1 >= 0) {
                    if (maze[cur.x - 1][cur.y] != cell.WALL && !visited[cur.x - 1][cur.y]) {
                        stack.push(new Node(cur.x - 1, cur.y, cur));
                    }
                }
                if (cur.y + 1 < maze[0].length) {
                    if (maze[cur.x][cur.y + 1] != cell.WALL && !visited[cur.x][cur.y + 1]) {
                        stack.push(new Node(cur.x, cur.y + 1, cur));
                    }
                }
                if (cur.y - 1 >= 0) {
                    if (maze[cur.x][cur.y - 1] != cell.WALL && !visited[cur.x][cur.y - 1]) {
                        stack.push(new Node(cur.x, cur.y - 1, cur));
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
            visited[cur.x][cur.y] = true;

            if ( maze[cur.x][cur.y] != cell.END  ){
                maze[cur.x][cur.y] = cell.SPAN;
            }



            //found end point
            if (maze[cur.x][cur.y] == cell.END) {
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
                if (cur.x + 1 < maze.length) {
                    if (maze[cur.x + 1][cur.y] != cell.WALL && !visited[cur.x + 1][cur.y]) {
                        q.add(new Node(cur.x + 1, cur.y, cur));
                    }
                }
                if (cur.x - 1 >= 0) {
                    if (maze[cur.x - 1][cur.y] != cell.WALL && !visited[cur.x - 1][cur.y]) {
                        q.add(new Node(cur.x - 1, cur.y, cur));
                    }
                }
                if (cur.y + 1 < maze[0].length) {
                    if (maze[cur.x][cur.y + 1] != cell.WALL && !visited[cur.x][cur.y + 1]) {
                        q.add(new Node(cur.x, cur.y + 1, cur));
                    }
                }
                if (cur.y - 1 >= 0) {
                    if (maze[cur.x][cur.y - 1] != cell.WALL && !visited[cur.x][cur.y - 1]) {
                        q.add(new Node(cur.x, cur.y - 1, cur));
                    }
                }
            }
        }
    }


    private static int heuristic (Node cur, Node end) {
        //manhattan distance
        return (Math.abs(cur.x - end.x) + Math.abs(cur.y - end.y));
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
            visited[cur.x][cur.y] = true;
            if ( maze[cur.x][cur.y] != cell.END  ){
                maze[cur.x][cur.y] = cell.SPAN;
            }




            // found end point
            if (maze[cur.x][cur.y] == cell.DOT) {
                while(cur.parent != null){
                    cur = cur.parent;
                    maze[cur.x][cur.y] = cell.DOT;
                    path_cost++;
                }
                maze[cur.x][cur.y] = cell.START;
                System.out.println("path cost: " + path_cost);
                System.out.println("expanded nodes: " + expanded_nodes);
                return;
            }
            else {

                // check neighbors, add to queue;

                if (cur.x + 1 < maze.length) {
                    if (maze[cur.x + 1][cur.y] != cell.WALL && !visited[cur.x + 1][cur.y]) {
                        Node temp = new Node(cur.x + 1, cur.y, cur);
                        temp.estimated_cost = heuristic(temp, end);
                        q.add(temp);
                    }
                }
                if (cur.x - 1 >= 0) {
                    if (maze[cur.x - 1][cur.y] != cell.WALL && !visited[cur.x - 1][cur.y]) {
                        Node temp = new Node(cur.x - 1, cur.y, cur);
                        temp.estimated_cost = heuristic(temp, end);
                        q.add(temp);
                    }
                }
                if (cur.y + 1 < maze[0].length) {
                    if (maze[cur.x][cur.y + 1] != cell.WALL && !visited[cur.x][cur.y + 1]) {
                        Node temp = new Node(cur.x, cur.y + 1, cur);
                        temp.estimated_cost = heuristic(temp, end);
                        q.add(temp);
                    }
                }
                if (cur.y - 1 >= 0) {
                    if (maze[cur.x][cur.y - 1] != cell.WALL && !visited[cur.x][cur.y - 1]) {
                        Node temp = new Node(cur.x, cur.y - 1, cur);
                        temp.estimated_cost = heuristic(temp, end);
                        q.add(temp);
                    }
                }

            }
        }
    }

    private static void A_star (Node start, Vector<Node> storage, cell [][] maze, boolean[][] visited) {

        Comparator<Node> comp = new TotalCost();
        PriorityQueue<Node> q = new PriorityQueue<Node>(1000, comp);
        q.add(start);
        int path_cost = 0;
        int expanded_nodes = 0;
        start.path_cost = 0;
        boolean startNode = true;
        int index = 0;

        int idx = 0;

       // Node alt = start;

        int min = 0;
        boolean first = true;
        Node nextGoal = null;
        for (int i = 0; i < storage.size(); i++) {
            int temp = start.path_cost + heuristic(start, storage.elementAt(i));
            if (first) {
                min = temp;
                first = false;
                nextGoal = new Node(storage.elementAt(i).x, storage.elementAt(i).y, null);
                index = i;
            }
            if (temp < min) {
                min = temp;
                nextGoal = new Node(storage.elementAt(i).x, storage.elementAt(i).y, null);
                index = i;
            }
        }

        Node end = nextGoal;

        System.out.println("start-x: " + start.x + ", start-y: " + start.y);

        // check if there are more dots to the left or the right of starting position

        //boolean startNode = true;
        start.total_cost = start.path_cost + heuristic(start, end);

        while (!q.isEmpty()) {

            // remove from open set and mark visited
            Node cur = q.remove();
            expanded_nodes++;
            visited[cur.x][cur.y] = true;

            if (maze[cur.x][cur.y] == cell.DOT) {
                //System.out.println(storage.size());
                System.out.println("goal-x: " + cur.x + ", goal-y: " + cur.y);
                maze[cur.x][cur.y] = cell.NUMBER;

                Node tempnode = cur;
                while (tempnode.parent != null) {
                    tempnode = tempnode.parent;
                    if (maze[tempnode.x][tempnode.y] == cell.NUMBER || maze[tempnode.x][tempnode.y] == cell.START) {
                        path_cost++;
                        continue;
                    }
                    else {
                        maze[tempnode.x][tempnode.y] = cell.SPAN;
                        path_cost++;
                    }

                }
                if (startNode == true) {
                    maze[tempnode.x][tempnode.y] = cell.START;
                    startNode = false;
                }
                storage.remove(index);
                System.out.println("tempnode coords " + "x: " + tempnode.x + "y: " + tempnode.y);
                System.out.println("curnode coords " + "x: " + cur.x + "y: " + cur.y);
                if (storage.size() == 0) {
                    System.out.println("path cost: " + path_cost);
                    System.out.println("expanded nodes: " + expanded_nodes);
                    return;
                } else {
                    for (int a = 0; a < visited.length; a++) {
                        for (int b = 0; b < visited[a].length; b++)
                            visited[a][b] = false;
                    }
                    min= 0;
                    first = true;
                    nextGoal = null;
                    cur.path_cost = 0;
                    cur.parent = null;
                    index = 0;
                    for (int i = 0; i < storage.size(); i++) {
                        int temp = cur.path_cost + heuristic(cur, storage.elementAt(i));
                        if (first) {
                            min = temp;
                            first = false;
                            nextGoal = new Node(storage.elementAt(i).x, storage.elementAt(i).y, null);
                            index = i;
                        }
                        if (temp < min) {
                            min = temp;
                            nextGoal = new Node(storage.elementAt(i).x, storage.elementAt(i).y, null);
                            index = i;
                        }
                    }
                    end = nextGoal;
                    cur.total_cost = cur.path_cost + heuristic(cur, end);
                    //System.out.println("new_start-x: " + cur.x + ", new_start-y: " + cur.y);
                    //if (cur.parent == null) System.out.println("wtf");
                    //System.out.println("new_start-x: " + (cur.parent).x + ", new_start-y: " + (cur.parent).y);
                    //alt = cur;
                    q.clear();
                    q.add(cur);
                    continue;
                }
                /*
                if (finish) {
                    while (cur.parent != null) {
                        cur = cur.parent;
                        if (maze[cur.x][cur.y] == cell.NUMBER) {
                            path_cost++;
                            continue;
                        }
                        else {
                            maze[cur.x][cur.y] = cell.DOT;
                            path_cost++;
                        }

                    }
                }
                */
            }
            else {

                // check all four neighbors
                if (cur.x + 1 < maze.length) {
                    if (maze[cur.x + 1][cur.y] != cell.WALL && !visited[cur.x + 1][cur.y]) {
                        Node temp = new Node(cur.x + 1, cur.y, cur);
                        temp.path_cost = cur.path_cost + 1;
                        temp.total_cost = temp.path_cost + heuristic(temp, end);
                        q.add(temp);
                    }
                }
                if (cur.x - 1 >= 0) {
                    if (maze[cur.x - 1][cur.y] != cell.WALL && !visited[cur.x - 1][cur.y]) {
                        Node temp = new Node(cur.x - 1, cur.y, cur);
                        temp.path_cost = cur.path_cost + 1;
                        temp.total_cost = temp.path_cost + heuristic(temp, end);
                        q.add(temp);
                    }
                }
                if (cur.y + 1 < maze[0].length) {
                    if (maze[cur.x][cur.y + 1] != cell.WALL && !visited[cur.x][cur.y + 1]) {
                        Node temp = new Node(cur.x, cur.y + 1, cur);
                        temp.path_cost = cur.path_cost + 1;
                        temp.total_cost = temp.path_cost + heuristic(temp, end);
                        q.add(temp);
                    }
                }
                if (cur.y - 1 >= 0) {
                    if (maze[cur.x][cur.y - 1] != cell.WALL && !visited[cur.x][cur.y - 1]) {
                        Node temp = new Node(cur.x, cur.y - 1, cur);
                        temp.path_cost = cur.path_cost + 1;
                        temp.total_cost = temp.path_cost + heuristic(temp, end);
                        q.add(temp);
                    }
                }

            }
        }


    }

    public static void main(String[] args) {

        /* The 2D arrays work for the small maze.
           For now, just manually enter the size of the mazes..lol
         */

        boolean[][] visited = new boolean[15][31]; //10,22    //11/30   //5/20
        cell[][] maze = new cell[15][31];
        Pair result = maze_parser(visited, maze);
        Node start = result.start;
        Vector<Node> storage = result.dots;


        // To test search algorithms call the corresponding function below.
        // test one at a time......


      A_star(start, storage, maze, visited);
      // greedy_bfs(start, end, maze, visited);
      //bfs(visited, maze, start);
      //  dfs(visited, maze, start);

        //print to console

        StringBuilder output = new StringBuilder();

        int rows = 0;
        char letter = 'a';

        while (rows < maze.length) {
            for (int y = 0; y < maze[0].length; y++) {
                switch (maze[rows][y]) {
                    case DOT:
                        output.append(".");
                        break;
                    case WALL:
                        output.append("%");
                        break;
                    case START:
                        output.append("P");
                        break;
                    case END:
                        output.append("E");
                        break;
                    case NUMBER:
                        output.append(letter++);
                        break;
                    case SPACE:
                        output.append(" ");
                        break;
                    case SPAN:
                        output.append("*");
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
