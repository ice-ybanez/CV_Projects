package assignment;

import java.util.LinkedList;

public class GraphAdjList extends AbstractGraph {
    private record Edge(int destination, double weight) {}
    private final LinkedList<Edge>[] neighbours;
    // adjacency-lists representation of the graph

    public GraphAdjList(int noOfVertices, boolean directed) {
        super(noOfVertices, directed);
        // initializes adjacency-list graph
        neighbours = new LinkedList[noOfVertices];

        for (int i = 0; i < noOfVertices; i++) {
            neighbours[i] = new LinkedList<>();
        }
    }

    public void addEdge(int source, int destination, double weight) {
        // adds additional edge to graph
        neighbours[source].add(new Edge(destination, weight));

        if (!directed) { // if graph is undirected, add reverse edge
            neighbours[destination].add(new Edge(source, weight));
        }
    }

    public void removeEdge(int source, int destination) {
        // removes edge from graph between source and destination
        for (int i = 0; i < neighbours[source].size(); i++) {

            if (neighbours[source].get(i).destination == destination) {
                neighbours[source].remove(i);
                break;
            }
        }
        if (!directed) { // if graph is undirected, remove reverse edge
            for (int i = 0; i < neighbours[destination].size(); i++) {
                if (neighbours[destination].get(i).destination == source) {
                    neighbours[destination].remove(i);
                    break;
                }
            }
        }
    }

    public double getWeight(int source, int destination) {
        // returns weight of the edge
        // returns Double.NaN if no edge exists
        for (int i = 0; i < neighbours[source].size(); i++) {
            Edge edge = neighbours[source].get(i);
            if (edge.destination == destination) {
                return edge.weight;
            }
        }
        return Double.NaN;
    }

    public int[] getNeighbours(int vertex) {
        // returns the num of all adjacent vertices(neighbours) as an array
        int[] result = new int[neighbours[vertex].size()];

        for (int i = 0; i < neighbours[vertex].size(); i++) {
            result[i] = neighbours[vertex].get(i).destination;
        }
        return result;
    }

    public int getDegree(int vertex) {
        // returns degree of given vertex
        if (directed) {
            int degree = 0;
            for(int i = 0; i < neighbours.length; i++) { // loop through adjacency lists
                for (int j = 0; j < neighbours[i].size(); j++) { // loop through edges in each list
                    if (neighbours[i].get(j).destination == vertex) {
                        degree++;
                    }
                }
            }
            return degree + neighbours[vertex].size(); // sum of in-degree and out-degree
        }
        else {
            return neighbours[vertex].size(); // degree is adjacency list size
        }
    }

    public boolean isPath(int[] nodes) {
        // checks if sequence of nodes is on a path or not
        for (int i = 0; i < nodes.length - 1; i++) {
            boolean found = false;
            for (int j = 0; j < neighbours[nodes[i]].size(); j++) {
                if (neighbours[nodes[i]].get(j).destination == nodes[i + 1]) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public int getNoOfEdges() {
        // returns number of edges of the graph
        int count = 0;
        for(int i = 0; i < neighbours.length; i++) {
            count += neighbours[i].size();
        }
        // if the graph is directed, return count
        if(directed){
            return count;
        }
        // if undirected, divide by 2 to avoid double counting
        else {
            return count / 2;
        }
    }
}
