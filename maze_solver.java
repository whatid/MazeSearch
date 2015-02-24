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
        WALL, SPACE, START, END, DOT, SPAN, NUMBER     //use these name to describe the maze
    }

    private static class Node {
        int x;
        int y;
        int path_cost, estimated_cost, total_cost, distance /* distance traversed to reach up to N other nodes */;
        Node parent;
        char val;

        public Node(int x, int y, Node parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }

        public Node(int x, int y, char val) {
            this.x = x;
            this.y = y;
            this.val = val;
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
        Vector<Node> storage = new Vector<Node>();    //storge all the dots we need in part3, can also store on node so out code can run for pat 1 and 2

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
        File file = new File("/Users/donezio/IdeaProjects/MazeSearch/big.txt");

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
        Deque<Node> stack = new LinkedList<Node>();       //linkedlist as a stack

        stack.push(start);                            //add the first node to the stack

        Node end = null;

        while (!stack.isEmpty()) {                       //while there is still nodes on the stack
            Node cur = stack.pop();                      //pop the node
            expanded_nodes++;
            visited[cur.x][cur.y] = true;               //marked it as visited


           //a piece of code to print the expanded path

           // if ( maze[cur.x][cur.y] != cell.END  ){
           //     maze[cur.x][cur.y] = cell.SPAN;
           // }



            //found end point
            if (maze[cur.x][cur.y] == cell.DOT) {
                end = cur;
                while(end.parent != null){
                    end = end.parent;                        //find back its  parent and then print the path
                    maze[end.x][end.y] = cell.DOT;
                    path_cost++;                              //path cost plus one
                }
                maze[end.x][end.y] = cell.START;              //get back the start point
                System.out.println("path cost: " + path_cost);
                System.out.println("expanded nodes: " + expanded_nodes);
                return;
            } else {

                //check all four neighbors,if it is inside the maze, not visited and not a wall, we will process it
                if (cur.x + 1 < maze.length) {
                    if (maze[cur.x + 1][cur.y] != cell.WALL && !visited[cur.x + 1][cur.y]) {
                        stack.push(new Node(cur.x + 1, cur.y, cur));       //push on stack
                    }
                }
                if (cur.x - 1 >= 0) {
                    if (maze[cur.x - 1][cur.y] != cell.WALL && !visited[cur.x - 1][cur.y]) {
                        stack.push(new Node(cur.x - 1, cur.y, cur));          //push on stack
                    }
                }
                if (cur.y + 1 < maze[0].length) {
                    if (maze[cur.x][cur.y + 1] != cell.WALL && !visited[cur.x][cur.y + 1]) {
                        stack.push(new Node(cur.x, cur.y + 1, cur));          //push on stack
                    }
                }
                if (cur.y - 1 >= 0) {
                    if (maze[cur.x][cur.y - 1] != cell.WALL && !visited[cur.x][cur.y - 1]) {
                        stack.push(new Node(cur.x, cur.y - 1, cur));          //push on stack
                    }
                }
            }
        }
    }

    private static void bfs(boolean[][] visited, cell[][] maze, Node start) {

        Queue<Node> q = new LinkedList<Node>();      //queue to store the node we are going to use

        q.add(start);                            //add the start point as the first node

        int path_cost = 0;                       //initial path cost
        int expanded_nodes = 0;                  //initial expanded nodes
        Node end = null;                         //end node is NULL at first

        while (!q.isEmpty()) {
            Node cur = q.remove();                 //we work on the first node
            expanded_nodes++;
            visited[cur.x][cur.y] = true;          //marked as visited


         //we try to print out the dot expanded first but then change it
           // if ( maze[cur.x][cur.y] != cell.END  ){
           //     maze[cur.x][cur.y] = cell.SPAN;
           // }



            //found end point
            if (maze[cur.x][cur.y] == cell.DOT) {     //if this is a dot
                end = cur;                            //then it is a end point
                while(end.parent != null){             //this while loop finds the parent of the node and then print the path
                    end = end.parent;
                    maze[end.x][end.y] = cell.DOT;
                    path_cost++;                      //increase path cost
                }
                maze[end.x][end.y] = cell.START;       //print back the start point P
                //two lines for output
                System.out.println("path cost:" + path_cost);
                System.out.println("expanded nodes: " + expanded_nodes);
                return;
            } else {                                  //if we didn't find the end point

                //check all four neighbors, if it is inside the maze and not a wall and not a visited node
                //we add it to the queue then process it next time//
                if (cur.x + 1 < maze.length) {
                    if (maze[cur.x + 1][cur.y] != cell.WALL && !visited[cur.x + 1][cur.y]) {
                        q.add(new Node(cur.x + 1, cur.y, cur));    //add to queue
                    }
                }
                if (cur.x - 1 >= 0) {
                    if (maze[cur.x - 1][cur.y] != cell.WALL && !visited[cur.x - 1][cur.y]) {
                        q.add(new Node(cur.x - 1, cur.y, cur));   // add to queue
                    }
                }
                if (cur.y + 1 < maze[0].length) {
                    if (maze[cur.x][cur.y + 1] != cell.WALL && !visited[cur.x][cur.y + 1]) {
                        q.add(new Node(cur.x, cur.y + 1, cur));   //add to queue
                    }
                }
                if (cur.y - 1 >= 0) {
                    if (maze[cur.x][cur.y - 1] != cell.WALL && !visited[cur.x][cur.y - 1]) {
                        q.add(new Node(cur.x, cur.y - 1, cur));    //add to queue
                    }
                }
            }
        }
    }

//heuristic funtion
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



    private static void greedy_bfs(Node start, Vector<Node> storage, cell [][] maze, boolean [][] visited) {   //storgae only contains on node for greedy_bfs
        Comparator<Node> comp = new EstimatedCost();
        PriorityQueue<Node> q = new PriorityQueue<Node>(1000, comp);         //use a priority queue
        int path_cost = 0;
        int expanded_nodes = 0;
        start.estimated_cost = heuristic(start, storage.elementAt(0));       //use the heuristic function to get the estimated cost
        q.add(start);                                                         //add start to process

        while (!q.isEmpty()) {

            // removes the node with the lowest heuristic value
            Node cur = q.remove();
            expanded_nodes++;
            //mark as visited
            visited[cur.x][cur.y] = true;
          //  if ( maze[cur.x][cur.y] != cell.END  ){
          //      maze[cur.x][cur.y] = cell.SPAN;
          //  }




            // found end point
            if (maze[cur.x][cur.y] == cell.DOT) {    //if we found the solution
                while(cur.parent != null){
                    cur = cur.parent;                   //get back its parents
                    maze[cur.x][cur.y] = cell.DOT;       //print the path
                    path_cost++;
                }
                maze[cur.x][cur.y] = cell.START;
                System.out.println("path cost: " + path_cost);          //out put on screen
                System.out.println("expanded nodes: " + expanded_nodes);
                return;
            }
            else {

                // check neighbors, if it is inisde the maze, not a wall, not visited, add to queue;

                if (cur.x + 1 < maze.length) {
                    if (maze[cur.x + 1][cur.y] != cell.WALL && !visited[cur.x + 1][cur.y]) {
                        Node temp = new Node(cur.x + 1, cur.y, cur);
                        temp.estimated_cost = heuristic(temp, storage.elementAt(0));  //calculate the cost
                        q.add(temp);
                    }
                }
                if (cur.x - 1 >= 0) {
                    if (maze[cur.x - 1][cur.y] != cell.WALL && !visited[cur.x - 1][cur.y]) {
                        Node temp = new Node(cur.x - 1, cur.y, cur);
                        temp.estimated_cost = heuristic(temp, storage.elementAt(0)); //calculate the cost
                        q.add(temp);
                    }
                }
                if (cur.y + 1 < maze[0].length) {
                    if (maze[cur.x][cur.y + 1] != cell.WALL && !visited[cur.x][cur.y + 1]) {
                        Node temp = new Node(cur.x, cur.y + 1, cur);
                        temp.estimated_cost = heuristic(temp, storage.elementAt(0));      //calculate the cost
                        q.add(temp);
                    }
                }
                if (cur.y - 1 >= 0) {
                    if (maze[cur.x][cur.y - 1] != cell.WALL && !visited[cur.x][cur.y - 1]) {
                        Node temp = new Node(cur.x, cur.y - 1, cur);
                        temp.estimated_cost = heuristic(temp, storage.elementAt(0));   //calculate the cost
                        q.add(temp);
                    }
                }

            }
        }
    }

    private static Vector<Node> A_star (Node start, Vector<Node> storage, cell [][] maze, boolean[][] visited) {

        Comparator<Node> comp = new TotalCost();
        PriorityQueue<Node> q = new PriorityQueue<Node>(1000, comp);     //use a priority queue to store the node we need
        q.add(start);                                                    //add it to queue
        int path_cost = 0;
        int expanded_nodes = 0;
        start.path_cost = 0;
        boolean startNode = true;
        int index = 0;
        Vector<Node> lol = new Vector<Node>();

        int idx = 0;
        char letter = 'a';                                                  //letter starts in lowercase a
       // Node alt = start;

        int min = 0;
        boolean first = true;
        //use this for loop to find the closest node of all the nodes we need to visit
        Node nextGoal = null;
        for (int i = 0; i < storage.size(); i++) {
            int temp = start.path_cost + heuristic(start, storage.elementAt(i));              //heuristic of the A* funtion
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

        Node end = nextGoal;                                       //we get the closest node as the next end point

     //   System.out.println("start-x: " + start.x + ", start-y: " + start.y);

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
               // System.out.println("goal-x: " + cur.x + ", goal-y: " + cur.y);
                maze[cur.x][cur.y] = cell.NUMBER;
                lol.add(new Node(cur.x, cur.y, letter++));

                Node tempnode = cur;
                //traceback from goal node to start node
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
             //   System.out.println("tempnode coords " + "x: " + tempnode.x + "y: " + tempnode.y);
              //  System.out.println("curnode coords " + "x: " + cur.x + "y: " + cur.y);
                if (storage.size() == 0) {
                    System.out.println("path cost: " + path_cost);
                    System.out.println("expanded nodes: " + expanded_nodes);
                    return lol;
                } else {
                    for (int a = 0; a < visited.length; a++) {
                        for (int b = 0; b < visited[a].length; b++)
                            visited[a][b] = false;

                    }

                    //set current node to the new start node//
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
                    q.clear();       //after we reach a node, delete everything insde the queue and redo a*
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

                // check all four neighbors   if it is not a wall, it is inside the maze, and unvisited, we add it to process
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

        return lol;
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


      Vector<Node> p = A_star(start, storage, maze, visited);
      // greedy_bfs(start, storage, maze, visited);
      //bfs(visited, maze, start);
       // dfs(visited, maze, start);




        //print to console

        StringBuilder output = new StringBuilder();

        int rows = 0;
        //char letter = 'a';


        //print out different char according to what's inside the maze
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
                        for (int i = 0; i < p.size(); i++) {
                            if (p.elementAt(i).x == rows && p.elementAt(i).y == y) {
                                output.append(p.elementAt(i).val);
                                break;
                            }

                        }
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
