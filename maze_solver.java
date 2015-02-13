package MP1;

import java.lang.String;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Queue;
import java.util.LinkedList;
import java.lang.StringBuilder;
import java.util.Deque;

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
        Node parent;

        public Node(int x, int y, Node parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }

    }

    private static Node maze_parser(boolean[][] visited, cell[][] maze) {

        //variable to hold starting position
        Node start = null;

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
                    else if (s.charAt(i) == '.')
                        maze[rows][i] = cell.END;
                        // space
                    else
                        maze[rows][i] = cell.SPACE;
                }
                rows++;
            }
        } catch (FileNotFoundException error) {
            error.printStackTrace();
        }
        return start;
    }

    private static Node dfs(boolean[][] visited, cell[][] maze, Node start) {

        Deque<Node> stack = new LinkedList<Node>();

        stack.push(start);

        Node end = null;

        while (!stack.isEmpty()) {
            Node cur = stack.pop();
            visited[cur.x][cur.y] = true;

            //found end point
            if (maze[cur.x][cur.y] == cell.END) {
                end = cur;
                return end;
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
        return end;
    }

    private static Node bfs(boolean[][] visited, cell[][] maze, Node start) {

        Queue<Node> q = new LinkedList<Node>();

        q.add(start);

        Node end = null;

        while (!q.isEmpty()) {
            Node cur = q.remove();
            visited[cur.x][cur.y] = true;

            //found end point
            if (maze[cur.x][cur.y] == cell.END) {
                end = cur;
                return end;
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
        return end;
    }

    public static void main(String[] args) {

        /* The 2D arrays work for the small maze.
           For now, just manually enter the size of the mazes..lol
         */

        boolean[][] visited = new boolean[10][22];
        cell[][] maze = new cell[10][22];
        Node start = null;
        Node end = null;
        start = maze_parser(visited, maze);


        // to test bfs or dfs, change the function call below.

        if (start != null){
            end = dfs(visited, maze, start);

            //trace back execution
            if (end != null) {
                while(end.parent != null){
                    end = end.parent;
                    maze[end.x][end.y] = cell.DOT;
                }

            }
        }

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
