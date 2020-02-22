package com.github.walterfan;

// Java program to count all paths from a source
// to a destination.
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

// This class represents a directed graph using
// adjacency list representation
// This code is contributed by shubhamjd.

class Graph {

    // No. of vertices
    private int V;

    // Array of lists for
    // Adjacency List
    // Representation
    private LinkedList<Integer> adj[];

    @SuppressWarnings("unchecked")
    Graph(int v)
    {
        V = v;
        adj = new LinkedList[v];
        for (int i = 0; i < v; ++i)
            adj[i] = new LinkedList<>();
    }

    // Method to add an edge into the graph
    void addEdge(int v, int w)
    {

        // Add w to v's list.
        adj[v].add(w);
    }


    // A recursive method to count
    // all paths from 'u' to 'd'.
    int countPathsUtil(int u, int d,
                       boolean visited[],
                       int pathCount)
    {

        // Mark the current node as
        // visited and print it
        visited[u] = true;

        // If current vertex is same as
        // destination, then increment count
        if (u == d)
        {
            pathCount++;
        }

        // Recur for all the vertices
        // adjacent to this vertex
        else
        {
            Iterator<Integer> i = adj[u].listIterator();
            while (i.hasNext())
            {
                int n = i.next();
                if (!visited[n])
                {
                    pathCount = countPathsUtil(n, d,
                            visited,
                            pathCount);
                }
            }
        }

        visited[u] = false;
        return pathCount;
    }

    // Returns count of
    // paths from 's' to 'd'
    int countPaths(int s, int d)
    {

        // Mark all the vertices
        // as not visited
        boolean visited[] = new boolean[V];
        Arrays.fill(visited, false);

        // Call the recursive method
        // to count all paths
        int pathCount = 0;
        pathCount = countPathsUtil(s, d,
                visited,
                pathCount);
        return pathCount;
    }


}



public class CountPaths {
    // Driver Code
    public static void main(String args[])
    {
        Graph g = new Graph(4);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(0, 3);
        g.addEdge(2, 0);
        g.addEdge(2, 1);
        g.addEdge(1, 3);

        int s = 2, d = 3;
        System.out.println(g.countPaths(s, d));
    }
}
