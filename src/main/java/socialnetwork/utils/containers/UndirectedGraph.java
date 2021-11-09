package socialnetwork.utils.containers;


import java.util.*;



/**
 * Undirected Graph data structure implementation
 * @param <T> type of the elements of the graph
 */
public class UndirectedGraph<T> {

    private Map<T, HashSet<T>> adjacencyMap = new HashMap<T, HashSet<T>>();;
    enum NodeState{
        NOT_VISITED,
        NEIGHBOURS_NOT_VISITED,
        VISITED
    }
    private int verticesNum = 0;
    private int edgesNum = 0;

    /**
     * Getter method for number of vertices
     */
    public int getVerticesNum() {
        return verticesNum;
    }

    /**
     * Getter method for number of edges
     */
    public int getEdgesNum() {
        return edgesNum;
    }

    /**
     * Default constructor
     */
    public UndirectedGraph(){

    }

    /**
     * Copy constructor that creates a new graph with the vertices and edges of other graph
     */
    public UndirectedGraph(UndirectedGraph<T> other){
        this.edgesNum = other.edgesNum;
        this.verticesNum = other.verticesNum;
        this.adjacencyMap = new HashMap<>(other.adjacencyMap);
    }

    /**
     * Constructor that creates a new graph with no edges and the given vertices
     * @param vertices - list of the elements representing the nodes of the graph
     */
    public UndirectedGraph(List<T> vertices){
        for(T vertex : vertices)
            addVertex(vertex);
    }

    /**
     * Checks if the graphs are equal by value
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UndirectedGraph)) return false;
        UndirectedGraph<?> that = (UndirectedGraph<?>) o;
        return verticesNum == that.verticesNum && edgesNum == that.edgesNum && Objects.equals(adjacencyMap, that.adjacencyMap);
    }

    /**
     * Returns hashCode of this
     */
    @Override
    public int hashCode() {
        return Objects.hash(adjacencyMap, verticesNum, edgesNum);
    }

    /**
     * Returns a set containing the neighbours of the given node
     * @param vertex node for which we want to find the neighbours
     * @return a set or null if the node doesn't exist in the graph
     */
    public Set<T> getNeighboursOf(T vertex){
        return adjacencyMap.get(vertex);
    }

    /**
     * Adds a new node in the graph
     * @param vertex node that will be added
     * @return true if the vertex was added, false if the vertex already exists
     */
    public boolean addVertex(T vertex) {
        if (adjacencyMap.containsKey(vertex))
            return false;
        adjacencyMap.put(vertex, new HashSet<>());
        verticesNum++;
        return true;
    }

    /**
     * Returns the list of nodes of the graph
     * @return list of type T elements
     */
    public List<T> getVertices(){
        return adjacencyMap.keySet().stream().toList();
    }

    /**
     * Adds edge (vertex1, vertex2) into the graph, order of the nodes in the edge being irrelevant
     * If one of the nodes doesn't exist, it will be added into the graph
     * @param vertex1 - first node of the edge
     * @param vertex2 - second node of the edge
     * @return true if the edge was added, false otherwise
     */
    public boolean addEdge(T vertex1, T vertex2){
        if(vertex1.equals(vertex2))
            return false;

        if(hasEdge(vertex1, vertex2))
            return true;

        if(!hasVertex(vertex1))
            addVertex(vertex1);

        if(!hasVertex(vertex2))
            addVertex(vertex2);

        adjacencyMap.get(vertex1).add(vertex2);
        adjacencyMap.get(vertex2).add(vertex1);
        edgesNum++;

        return true;
    }

    /**
     * Checks if the given vertex exists in the graph
     * @return true if the vertex exists in the graph, false otherwise
     */
    public boolean hasVertex(T vertex) {
        return adjacencyMap.containsKey(vertex);
    }

    /**
     * Returns the set of edges of the graph
     * @return a set of unordered pairs
     */
    public Set<UnorderedPair<T, T>> getEdges(){
        Set<UnorderedPair<T, T>> allEdges = new HashSet<>();
        for(Map.Entry<T, HashSet<T>> entry : adjacencyMap.entrySet()){
            T vertex1 = entry.getKey();

            for(T vertex2 : entry.getValue())
                allEdges.add(new UnorderedPair<>(vertex1, vertex2));
        }
        return allEdges;
    }

    /**
     * Checks if the graph is complete
     * @return true if the graph is complete, false otherwise
     */
    public boolean isComplete(){
        return edgesNum == verticesNum * (verticesNum - 1) / 2;
    }

    /**
     * Finds the number of connected components of the graph
     * @return number of connected components
     */
    public int findNumberOfConnectedComponents(){
        Map<T, NodeState> visitedMap = createVisitedMap();
        int numberOfConnectedComponents = 0;

        for(T node : getVertices()){
            if(visitedMap.get(node) == NodeState.NOT_VISITED){
                numberOfConnectedComponents++;
                runBreadthFirstSearch(node, visitedMap);
            }
        }
        return numberOfConnectedComponents;
    }

    /**
     * Checks if edge (vertex1, vertex2) exists in the graph; order in edge is irrelevant
     * @param vertex1 - first node of the edge
     * @param vertex2 - second node of the edge
     * @return true if edge exists, false otherwise
     */
    public boolean hasEdge(T vertex1, T vertex2){
        if(vertex1.equals(vertex2))
            return false;
        return adjacencyMap.containsKey(vertex1) &&
                adjacencyMap.get(vertex1).contains(vertex2) &&
                adjacencyMap.containsKey(vertex2) &&
                adjacencyMap.get(vertex2).contains(vertex1);
    }

    /**
     * Returns a list of graphs representing the connected components of this
     * @return a list of undirected graphs
     */
    public List<UndirectedGraph<T>> getConnectedComponents(){
        Map<T, NodeState> visitedMap = createVisitedMap();
        List<UndirectedGraph<T>> connectedComponents = new ArrayList<>();

        for(T node : getVertices())
            if(visitedMap.get(node) == NodeState.NOT_VISITED){
                List<T> vertices = runBreadthFirstSearch(node, visitedMap);
                UndirectedGraph<T> component = new UndirectedGraph<>(vertices);
                for(T vertex : vertices)
                    for(T neighbour : getNeighboursOf(vertex))
                        component.addEdge(vertex, neighbour);
                connectedComponents.add(component);
            }
        return connectedComponents;
    }


    /**
     * Finds the connected component with the longest walk
     * @return undirected  graph of type T; if the graph has no vertices, an empty graph will be
     * returned; if the graph is connected, a copy of this will be returned
     */
    public UndirectedGraph<T> findConnectedComponentWithLongestWalk(){
        int numberOfConnectedComponents = findNumberOfConnectedComponents();

        if(numberOfConnectedComponents == 0)
            return new UndirectedGraph<>();

        if(findNumberOfConnectedComponents() == 1)
            return new UndirectedGraph<>(this);

        List<UndirectedGraph<T>> connectedComponents = getConnectedComponents();

        UndirectedGraph<T> maxComponent = connectedComponents.get(0);
        int maxPath = maxComponent.findLongestWalk();
        for(int i = 1; i < connectedComponents.size(); i++){
            int path = connectedComponents.get(i).findLongestWalk();
            if(maxPath < path){
                maxComponent = connectedComponents.get(i);
                maxPath = path;
            }
        }
        return maxComponent;
    }

    /**
     * Finds the longest walk in this graph
     * @return length of the longest walk
     */
    public int findLongestWalk() {
        if(verticesNum >= 5 && isComplete()){
            if(verticesNum % 2 != 0)
                return verticesNum * (verticesNum - 1) / 2;
            else
                return verticesNum * (verticesNum - 2) / 2 + 1;
        }

        IntRef maxRef = new IntRef();
        maxRef.value = 0;
        IntRef walkSize = new IntRef();
        Set<UnorderedPair<T, T>> visitedEdges = new HashSet<>();
        for(var source : getVertices()){
            walkSize.value = 0;
            findLongestWalkRec(source ,visitedEdges, walkSize, maxRef);
            visitedEdges.clear();
        }
        return maxRef.value;
    }

    /**
     * Class representing a reference to an int primitive type
     */
    private static class IntRef{
        int value;
    }
    /**
     * Finds the longest walk starting from source node
     * @param source - start node of the walk
     * @param visitedEdges - set of unordered pairs representing the visited edges
     * @param sizeOfCurrentWalk - reference to an integer representing the length of the current walk
     * @param sizeOfMaxWalk - reference to an integer representing the longest walk
     */
    private void findLongestWalkRec(T source,
                                    Set<UnorderedPair<T, T>> visitedEdges,
                                    IntRef sizeOfCurrentWalk,
                                    IntRef sizeOfMaxWalk){
        for(var neighbour : getNeighboursOf(source)){
            var currentEdge = new UnorderedPair<>(source, neighbour);
            if(!visitedEdges.contains(currentEdge)){
                visitedEdges.add(currentEdge);
                int oldSize = sizeOfCurrentWalk.value;
                sizeOfCurrentWalk.value++;
                findLongestWalkRec(neighbour,
                        visitedEdges,
                        sizeOfCurrentWalk,
                        sizeOfMaxWalk);

                if(sizeOfMaxWalk.value < sizeOfCurrentWalk.value)
                    sizeOfMaxWalk.value = sizeOfCurrentWalk.value;;
                visitedEdges.remove(currentEdge);
                sizeOfCurrentWalk.value = oldSize;
            }
        }
    }

    /**
     * Creates a map between nodes and states; each node's state is set to NodeState.NOT_VISITED
     * @return a map between type T elements and NodeState
     */
    private Map<T, NodeState> createVisitedMap(){
        Map<T, NodeState> visitedMap = new HashMap<>();

        for(T node : getVertices())
            visitedMap.put(node, NodeState.NOT_VISITED);

        return visitedMap;
    }


    /**
     * BFS implementation
     * @param source start node
     * @param visitedMap map between node of type T and a state; each node has one of the states:
     *                   - NEIGHBOURS_NOT_VISITED - the node has been visited, but not his neighbours
     *                   - NOT_VISITED - the node hasn't been visited yet
     *                   - VISITED neighbours and the node have been visited
     * @return a list containing the nodes of the connected component
     */
    private List<T> runBreadthFirstSearch(T source, Map<T, NodeState> visitedMap) {
        visitedMap.put(source, NodeState.NEIGHBOURS_NOT_VISITED);
        Queue<T> nodeQueue = new ArrayDeque<>();
        nodeQueue.add(source);
        List<T> verticesOfConnectedComponent = new ArrayList<>();
        while (!nodeQueue.isEmpty()) {
            T currentNode = nodeQueue.remove();
            verticesOfConnectedComponent.add(currentNode);
            if (visitedMap.get(currentNode) == NodeState.NEIGHBOURS_NOT_VISITED)
                for (T neighbour : getNeighboursOf(currentNode))
                    if (visitedMap.get(neighbour) == NodeState.NOT_VISITED) {
                        visitedMap.put(neighbour, NodeState.NEIGHBOURS_NOT_VISITED);
                        nodeQueue.add(neighbour);
                    }
            visitedMap.put(currentNode, NodeState.VISITED);
        }
        return verticesOfConnectedComponent;
    }
}
