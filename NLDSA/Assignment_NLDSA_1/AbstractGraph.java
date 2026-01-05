package assignment;

public abstract class AbstractGraph implements Graph {
    protected final int noOfVertices;
    protected final boolean directed;

    protected AbstractGraph(int noOfVertices, boolean directed) {
        this.noOfVertices = noOfVertices;
        this.directed = directed;
    }

    public int getNoOfVertices() {
        return noOfVertices;
    }

    public enum GraphImpl { AdjList, AdjMatrix }
    public static Graph create(GraphImpl impl, int noOfVertices, boolean directed)
    {
        return switch (impl) {
            case AdjList -> new GraphAdjList(noOfVertices, directed);
            case AdjMatrix -> new GraphAdjMatrix(noOfVertices, directed);
        };
    }

    private static void print(Graph G) {
        System.out.println("noOfVertices = " + G.getNoOfVertices() + ", noOfEdges = " + G.getNoOfEdges());
        for (int i=0; i<G.getNoOfVertices(); i++) {
            System.out.print(i + "(deg=" + G.getDegree(i)+ "): ");
            for (int j : G.getNeighbours(i)) {
                System.out.print("->" + j + "(weight=" + G.getWeight(i, j) + ") ");
            }
            System.out.println();
        }
        System.out.println();
        for (int i=0; i<G.getNoOfVertices(); i++) {
            int[] neighbours = G.getNeighbours(i);
            for (int j=0; j<G.getNoOfVertices(); j++) {
                boolean isNeighbour = false;
                for (int neighbour : neighbours) {
                    if (neighbour == j) {
                        isNeighbour = true;
                        break;
                    }
                }
                if (isNeighbour) {
                    System.out.printf("%2d ", (int)G.getWeight(i, j));
                }
                else {
                    System.out.print(" x ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args)
    {
        double[][] A =  {
                { 0, 1, 0, 0, 1 },
                { 0, 1, 1, 0, 0 },
                { 0, 0, 0, 1, 1 },
                { 1, 0, 1, 0, 1 },
                { 0, 1, 0, 0, 0 }
        };
        for (boolean directed : new boolean[] {true, false}) {
            for (GraphImpl impl : GraphImpl.values()) {
                System.out.println("implementation = " + impl + ", directed=" + directed);
                Graph G = create(impl, A.length, directed);
                for (int i=0; i<A.length; i++) {
                    for (int j=0; j<A[i].length; j++) {
                        if (A[i][j] != 0) {
                           G.addEdge(i, j, i*j);
                        }
                    }
                }
                G.addEdge(0, 2, -1);
                System.out.println("is path = " + G.isPath(new int[] { 0, 1, 4, 2, 0} ));
                print(G);
                G.removeEdge(0, 2);
                System.out.println("is path = " + G.isPath(new int[] { 0, 1, 4, 2, 0} ));
                print(G);
            }
        }
    }
}
