package assignment;

import java.util.ArrayList;
import java.util.List;

public class GraphAdjMatrix extends AbstractGraph {
    private final double[][] adjMatrix;
    // adjacency-matrix representation

    public GraphAdjMatrix(int noOfVertices, boolean directed) {
        super(noOfVertices, directed);
        // initializes the adjacency-matrix
        adjMatrix = new double[noOfVertices][noOfVertices];

        // sets all entries in matrix to Double.NaN to indicate no edge exists (initially)
        for (int i = 0; i < noOfVertices; i++) {
            for (int j = 0; j < noOfVertices; j++) {
                adjMatrix[i][j] = Double.NaN;
            }
        }
    }

    public void addEdge(int source, int destination, double weight) {
        // adds additional edge to graph
        adjMatrix[source][destination] = weight;

        if (!directed) {    // if graph is undirected, add reverse edge
            adjMatrix[destination][source] = weight;
        }
    }

    public void removeEdge(int source, int destination) {
        // removes edge from graph between source and destination
        adjMatrix[source][destination] = Double.NaN;

        if (!directed) {    // if undirected, remove reverse edge
            adjMatrix[destination][source] = Double.NaN;
        }
    }

    public double getWeight(int source, int destination) {
        // returns weight of the edge
        // returns Double.NaN if no edge exists
        // ^from the initialisation of the adjacency matrix on line 6
        return adjMatrix[source][destination];
    }

    public int[] getNeighbours(int vertex) {
        // returns the num of all adjacent vertices(neighbours) as an array
        List<Integer> neighbours = new ArrayList<>();

        // iterate through row linked to vertex to find all connected vertices
        for (int i = 0; i < noOfVertices; i++) {
            if (!Double.isNaN(adjMatrix[vertex][i])) {
                neighbours.add(i);
            }
        }
        // return list of neighbours that have been converted to array
        return neighbours.stream().mapToInt(i -> i).toArray();
    }

    public int getDegree(int vertex) {
        // return degree of given vertex
        int degree = 0;

        // iterate through row of vertex and count what is NOT a NaN
        for (int i = 0; i < noOfVertices; i++) {
            if (!Double.isNaN(adjMatrix[vertex][i])) {
                degree++;
            }
            // count reverse edge of undirected graph
            if (!directed && !Double.isNaN(adjMatrix[i][vertex]) && i != vertex) {
                degree++;
            }
        }
        return degree;
    }

    public boolean isPath(int[] nodes) {
        // check if sequence of nodes is on a path or not
        for (int i = 0; i < nodes.length - 1; i++) {
            if (Double.isNaN(adjMatrix[nodes[i]][nodes[i + 1]])) {
                return false;   // means no edge exists between nodes
            }
        }
        return true;    // means all nodes are connected by edges
    }

    public int getNoOfEdges() {
        // returns num of edges of graph
        // initialise count of edges
        int count = 0;

        // iterate through matrix to count what is NOT a NaN
        for (int i = 0; i < noOfVertices; i++) {
            for (int j = 0; j < noOfVertices; j++) {
                if (!Double.isNaN(adjMatrix[i][j])) {
                    count++;
                }
            }
        }
        // if the graph is directed, return count
        if(directed){
            return count;
        }
        // if undirected, divide by 2 to avoid double counting
        // implemented this as in undirected graphs, each edge is counted twice, once for each direction
        else {
            return count / 2;
        }
    }
}
