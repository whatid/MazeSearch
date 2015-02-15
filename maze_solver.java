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

/**
 * File created on 2/7/15. Maze solver program. Input is a maze in text file format.
 */
public class maze_solver {


    public enum cell {
        WALL, SPACE, START, END, DOT
    }

    private static class Node {
        int x;
        int y;
        int path_cost, estimated_cost, total_cost;
        Node parent;

        public Node(int x, int y, Node parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }

    }

    private static class Pair {
        private final Node start;
        private final Node end;

        public Pair(Node start, Node end) {
            this.start = start;
            this.end = end;
        }
    }

    private static Pair maze_parser(boolean[][] visited, cell[][] maze) {

        //variable to hold return values
        Pair result;
        Node start = null;
        Node end = null;

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

        /* System.out.println(System.getProperty("user.dir")); */
        File file = new File("/Users/fwirjo/IdeaProjects/CS440_MP1/src/MP1/smallMaze.txt");

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
                        end = new Node(rows, i, null);
                        maze[rows][i] = cell.END;
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

        result = new Pair(start, end);

        return result;
    }

    private static void dfs(boolean[][] visited, cell[][] maze, Node start) {

        Deque<Node> stack = new LinkedList<Node>();

        stack.push(start);

        Node end = null;

        while (!stack.isEmpty()) {
            Node cur = stack.pop();
            visited[cur.x][cur.y] = true;

            //found end point
            if (maze[cur.x][cur.y] == cell.END) {
                end = cur;
                while(end.parent != null){
                    end = end.parent;
                    maze[end.x][end.y] = cell.DOT;
                }
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

        Node end = null;

        while (!q.isEmpty()) {
            Node cur = q.remove();
            visited[cur.x][cur.y] = true;

            //found end point
            if (maze[cur.x][cur.y] == cell.END) {
                end = cur;
                while(end.parent != null){
                    end = end.parent;
                    maze[end.x][end.y] = cell.DOT;
                }
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

    private static void greedy_bfs(Node start, Node end, cell [][] maze, boolean [][] visited) {
        Comparator<Node> comp = new EstimatedCost();
        PriorityQueue<Node> q = new PriorityQueue<Node>(1000, comp);
        q.add(start);
        start.estimated_cost = heuristic(start, end);

        while (!q.isEmpty()) {
            Node cur = q.remove();
            visited[cur.x][cur.y] = true;

            if (maze[cur.x][cur.y] == cell.END) {
                while(cur.parent != null){
                    cur = cur.parent;
                    maze[cur.x][cur.y] = cell.DOT;
                }
                return;
            }
            else {
                if (cur.x + 1 < maze.length) {
                    if (maze[cur.x + 1][cur.y] != cell.WALL && !visited[cur.x + 1][cur.y]) {
                        Node temp = new Node(cur.x + 1, cur.y, cur);
                        temp.estimated_cost = heuristic(cur, end);
                        q.add(temp);
                    }
                }
                if (cur.x - 1 >= 0) {
                    if (maze[cur.x - 1][cur.y] != cell.WALL && !visited[cur.x - 1][cur.y]) {
                        Node temp = new Node(cur.x + 1, cur.y, cur);
                        temp.estimated_cost = heuristic(cur, end);
                        q.add(temp);
                    }
                }
                if (cur.y + 1 < maze[0].length) {
                    if (maze[cur.x][cur.y + 1] != cell.WALL && !visited[cur.x][cur.y + 1]) {
                        Node temp = new Node(cur.x + 1, cur.y, cur);
                        temp.estimated_cost = heuristic(cur, end);
                        q.add(temp);
                    }
                }
                if (cur.y - 1 >= 0) {
                    if (maze[cur.x][cur.y - 1] != cell.WALL && !visited[cur.x][cur.y - 1]) {
                        Node temp = new Node(cur.x + 1, cur.y, cur);
                        temp.estimated_cost = heuristic(cur, end);
                        q.add(temp);
                    }
                }

            }
        }
    }

    private static void A_star (Node start, Node end, cell [][] maze, boolean[][] visited) {

        Comparator<Node> comp = new TotalCost();
        PriorityQueue<Node> q = new PriorityQueue<Node>(1000, comp);
        q.add(start);

        start.path_cost = 0;
        start.estimated_cost = heuristic(start,end);
        start.total_cost = start.path_cost + start.estimated_cost;

        while (!q.isEmpty()) {

            // remove from open set and mark visited
            Node cur = q.remove();
            visited[cur.x][cur.y] = true;


            if (maze[cur.x][cur.y] == cell.END) {
                while(cur.parent != null){
                    cur = cur.parent;
                    maze[cur.x][cur.y] = cell.DOT;
                }
                return;
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

        boolean[][] visited = new boolean[10][22];
        cell[][] maze = new cell[10][22];
        Pair result = maze_parser(visited, maze);
        Node start = result.start;
        Node end = result.end;

        // to test bfs or dfs, call the corresponding function call below.

        greedy_bfs(start, end, maze, visited);

        //print to console

        StringBuilder output = new StringBuilder();

        int rows = 0;

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
                        output.append(".");
                        break;
                    case SPACE:
                        output.append(" ");
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
