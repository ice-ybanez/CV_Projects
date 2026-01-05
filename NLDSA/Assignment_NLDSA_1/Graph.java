package assignment;

public interface Graph {
    int getNoOfVertices();
    int getNoOfEdges();

    void addEdge(int source, int destination, double weight);
    void removeEdge(int source, int destination);

    double getWeight(int source, int destination);
    int[] getNeighbours(int vertex);
    int getDegree(int vertex);
    boolean isPath(int[] nodes);
}
