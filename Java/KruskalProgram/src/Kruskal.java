import java.io.*;
import java.lang.*;
import java.util.*;

/**
 * Kruskla's Algorithm Project
 * @author Tom Markey
 * 
 * Implementation of Kruskal's algorithm using a Union-Find data structure.
 * The program reads in all the edges, sorts them using an internal merge sort method, and
 * then implements Kruskal's algorithm to find the weight of the minimal spanning tree (MST).  
 * Algorithm completes in worst case O(mlog(n)) time (see below for further explanation)
 * 
 * For complexity explanations in notes, n = vertexes, m = edges.
 * Sections of program:
 *      Main Method:
 *          Read in edges from input string
 *          Sort edges according to weight (least to greatest)
 *          Implement Kruskal's algorithm to compute minimum spanning tree weight
 *          
 *      Helper Method Section:
 *          Union
 *          Find
 *          edgeMergeSort
 *          
 *      Additional Classes Section:
 *          Node
 *          Component
 *          Edge
 *  
 *
 *          To Do:
 *              Implement return file of actual MST graph (same format as input)
 *              Determine new runtime after optional return file
 * 
 */
public class Kruskal {

    public static void main(String[] args) throws FileNotFoundException {
        // Initialize the Scanner off the input file
        File inputFile = new File(args[0]);
        Scanner inputScanner = new Scanner(inputFile);
        
        // Read first line to determine number of edges and vertexes
        int numberOfVertexes = inputScanner.nextInt();
        int numberOfEdges = inputScanner.nextInt();
        
        
        // Initialize list of edges to be sorted, the vertex list, and the components list
        // NOTE: the index of the lists storing the vertexes and components are off by -1
        //       So vertex 3 will be stored at vertexList[2] 
        Edge[] edgesList = new Edge[numberOfEdges];
        Node[] vertexList = new Node[numberOfVertexes];
        Component[] componentList = new Component[numberOfVertexes];
        
        
        // Loop over edges (done in O(m) time) and do the following:
        //      if the source or dest vertexes have not been created, create them
        //          create the corresponding component object as well
        //      add the vertexes and component to the respective lists
        //      create the new edge and add it to the list
        for (int i = 0; i < numberOfEdges; i++) {
            // Checks if the either the source or destination vertexes have been
            // created and added to the vertexList and componentList
            int source = inputScanner.nextInt();
            if (vertexList[source - 1] == null) {
                Node sourceVertex = new Node(source);
                vertexList[source - 1] = sourceVertex;
                componentList[source - 1] = new Component(vertexList[source - 1]);
            }
            
            int destination = inputScanner.nextInt();
            if (vertexList[destination - 1] == null) {
                Node destVertex = new Node(destination);
                vertexList[destination - 1] = destVertex;
                componentList[destination - 1] = new Component(vertexList[destination - 1]);
            }
            
            // Creates the new Edges and adds it to the list
            long edgeWeight = inputScanner.nextInt();
            Edge newEdge = new Edge(vertexList[source - 1], vertexList[destination - 1], edgeWeight);
            edgesList[i] = newEdge;
        }
        
        
        // Sort edges using mergeSort, by weight. Done in O(log(m)) time
        edgesMergeSort(edgesList, new Edge[numberOfEdges], 0, numberOfEdges - 1);
        
        
        // Implementation Kruskal's Algorithm
        // Explanation:
        // Now that the edges are sorted, we loop over the edgesList, joining the disjoint components 
        // when necessary, making sure no cycles occur.  Components with a smaller size will be joined
        // to the larger component, keeping the depth of the tree log(n).  When components are of
        // equal size, the vertex with the lower number will be kept as the root of the component.
        // Once a component has reach a size equal to the number of vertexes, a minimal spanning
        // tree has be constructed, and the weight of that component is returned to the console
        // At worst, the algorithm will take O(mlog(n)) time.  O(m) time for the
        // for loop over the edges, and at max O(log(n)) time for each Find() operation.
        // All Union() operations are done in constant O(1) time, not really affecting the overall run time.
        
        
        // Loops over the edges array.  At worst takes O(m) time
        for (int i = 0; i < numberOfEdges; i++) {
            
            // Find the component each vertex belongs to on the edge and assigns to a variable
            // Note the index offsets (number - 1)
            // Each Find() operation take O(log(n)) time, because of the component construction
            int sourceComponentIndex = Find(edgesList[i].getSource()) - 1;
            int destComponentIndex = Find(edgesList[i].getDestination()) - 1;
            
            
            // Checks if the source and dest vertexes are in the same component (cycle check)
            // if the two component indexes are the same, Find() returned the same value so the two
            // vertexes are in the same component
            if (sourceComponentIndex != destComponentIndex) {
                
                // Determines which component is smaller in size, which will be the first 
                // parameter in the Union() method (see Helper section)
                // Note that if the two sizes are equal, the destVertex is arbitrarily chosen to
                // remain the rootNode 
                if (componentList[sourceComponentIndex].getSize() <= componentList[destComponentIndex].getSize()) {
                    
                    // Unions the two components, and adds the weight of the new edge to the component
                    Union(componentList[sourceComponentIndex], componentList[destComponentIndex]);
                    componentList[destComponentIndex].setWeight(
                            componentList[destComponentIndex].getWeight() + edgesList[i].getWeight());
                    
                    // Checks if the new component has the same size as total vertexes.  
                    // If so, we print the component's weight to console and terminate the program
                    if (componentList[destComponentIndex].getSize() == numberOfVertexes) {
                        System.out.print(componentList[destComponentIndex].getWeight());
                        break;
                    }
                }
                
                
                else {
                    // Unions the two components, and adds the weight of the new edge to the component
                    Union(componentList[destComponentIndex], componentList[sourceComponentIndex]);
                    componentList[sourceComponentIndex].setWeight(
                        componentList[sourceComponentIndex].getWeight() + edgesList[i].getWeight());
                    
                 // Checks if the new component has the same size as total vertexes.  
                    // If so, we print the component's weight to console and terminate the program
                    if (componentList[sourceComponentIndex].getSize() == numberOfVertexes) {
                        System.out.print(componentList[sourceComponentIndex].getWeight());
                        break;
                    }
                }
            }
            
        }
        // For loop over edges is complete

    }
    
    // HELPER METHODS
    
    /**
     * Union Method
     * Takes two components as parameters, and merges the two.  This method ALWAYS merges
     * the first parameter into the second.  That is, upon completion the parent of the rootNode
     * of componentA will be the rootNode of componentB.
     * Also updates the size and weight of componentB
     * Completed in constant time, or O(1)
     */
     public static void Union(Component componentA, Component componentB) {
         // Reassign the parent of rootNode A
         componentA.getRootNode().setParent(componentB.getRootNode());
         
         // Updates the size and weight of componentB
         componentB.setSize(componentA.getSize() + componentB.getSize());
         componentB.setWeight(componentA.getWeight() + componentB.getWeight());
     }
    
    /**
     * Find Method
     * Iterative implementation that returns the number of the component 
     * the vertex parameter belongs to.
     * The method will run in time equal to the number of layers "deep" the parameter vertex
     * is in its component. Implements path compression after component root vertex is found. 
     * Because of the Union method implementation and Kruskal's algorithmic structure, the component 
     * will never be more than log(n) layers deep.  So, worst case this method will run in O(log(n)) time
     * 
     * Note: this method returns an int representing the component number (index + 1), not the component itself
     */
    public static int Find(Node vertex) {
        // Initializes the loop vertex that is checked in the while loop
        Node rootVertex = vertex;
        
        // While loop that starts at the parameter vertex, and continues until the
        // root node is found
        while (rootVertex.getParent() != null) {
            rootVertex = rootVertex.getParent();
        }
        
        // Compress the path just taken by assigning the parent of each vertex
        // visited to be the rootNode of the component
        Node currentVertex = vertex;
        while (currentVertex.getParent() != null) {
            Node nextVertex = currentVertex.getParent();
            currentVertex.setParent(rootVertex);
            currentVertex = nextVertex;
        }
        
        return currentVertex.getNumber();
    }
    
    
    /**
     * Merge Sort function for the edgesList
     * Used before the Kruskal's implementation to ensure the edges are in ascending
     * order by weight.  Source and dest vertexes are disregarded by this helper method, only
     * the weight of the edge matters
     * Basic structure taken for the internalMergeSort() method from Project 1, only uses array of
     * edges rather than byte arrays
     */
    public static void edgesMergeSort(Edge[] edgeArray, Edge[] auxArray, int leftStart, int rightEnd) {
        // Check for base case/ termination
        if (leftStart == rightEnd) {
            return;
        }
        
        // Select the midWay point, then recursively sort the two halves
        // of the inputArray
        int midPoint = (leftStart + rightEnd) / 2;
        edgesMergeSort(edgeArray, auxArray, leftStart, midPoint);
        edgesMergeSort(edgeArray, auxArray, midPoint + 1, rightEnd);
        
        // Copy over the array, then merge the two halves
        for (int i = leftStart; i <= rightEnd; i++) {
            auxArray[i] = edgeArray[i];
        }
        
        // Initializes the indexes of the for loop
        int leftIndex = leftStart;
        int rightIndex = midPoint + 1;
        
        // Loop over the auxArray, sort and add accordingly
        for (int i = leftStart; i <= rightEnd; i++) {
            // if the left side is done, copy over the remaining right side
            if (leftIndex == (midPoint + 1)) {
                edgeArray[i] = auxArray[rightIndex++];
            }
            
            // if the right side is done, copy the remaining left side
            else if (rightIndex > rightEnd) {
                edgeArray[i] = auxArray[leftIndex++];
            }
            
            // Get the smaller value of the two records, and add it to the array
            else if (Long.compare(auxArray[leftIndex].getWeight(), auxArray[rightIndex].getWeight()) <= 0) {
                edgeArray[i] = auxArray[leftIndex];
                leftIndex++;
            }
            else {
                edgeArray[i] = auxArray[rightIndex];
                rightIndex++;
            }
        }
    }



    // ADDITIONAL CLASSES

    /**
     *  Node Class
     * Acts as a vertex in the Union-Find structure.  Has two fields, one
     * representing the number of the vertex (or name), and the other being the
     * vertex's parent node.  If the vertex does not have a parent, this field is
     * null
     * Fields:
     *      number : the identifying number of the node
     *      parent : the parent node of this node
     */
    public static class Node {
        // The vertex number, or the "name" of the vertex
        public int number;
        
        // The parent of the vertex.  If the vertex has no parent, then parent = null
        public Node parent = null;
        
        // Constructor for a new vertex object
        public Node(int vertexNumber) {
            this.setNumber(vertexNumber);
        }
        
        // Getter / Setter methods, returns / sets the fields
        public int getNumber() {
            return number;
        }
        public void setNumber(int newNumber) {
            number = newNumber;
        }
        
        
        public Node getParent() {
            return parent;
        }
        public void setParent(Node newParent) {
            parent = newParent;
        }
    }

    
    /**
     * Component Class
     * Represents the tree the vertex is currently in.  This is what is returned
     * when the Find operation is used.  Hold three fields, the parent node of the 
     * representative, the total size or number of vertexes in the component,
     * and the total weight of all the edges in the tree.
     * NOTE: weight is is long to avoid overflow issues
     * Fields:
     *      rootNode : the head, or root, of the component
     *      size : the number of nodes currently in the component
     *      weight : the sum of the weight of all edges in the component
     */
    public static class Component {
        // Note: the parent Node is the "root" of the tree, or the node that represents
        //       all other nodes within the component.  What Find(v) returns
        public Node rootNode;
        public int size;
        public long weight;
        
        // Constructor for new Representative object
        public Component(Node parentNode) {
            rootNode = parentNode;
            size = 1;
            weight = 0;
        }
        
        // Getter / Setter Methods, returns / sets said fields
        public Node getRootNode() {
            return rootNode;
        }
        public void setRootNode(Node newParent) {
            rootNode = newParent;
        }
        
        public int getSize() {
            return size;   
        }
        public void setSize(int newSize) {
         size = newSize;   
        }
        
        public long getWeight() {
            return weight;
        }
        public void setWeight(long newWeight) {
            weight = newWeight;
        }
    }
    
    
    /**
     * Edges Class
     * The edge object, used in constructing the disjoint tree sets of edges
     * and vertexes.  Has three fields: a source vertex, a destination vertex, 
     * and the weight of the edge.  Note, the source and destination vertexes
     * are only in that ordered because of how they are parsed from the input
     * (first vertex is source, second vertex is destination)
     * Fields:
     *      sourceVertex : the source node       note: source and dest vertexes are based 
     *      destVertex : the destination node            off of the order read from input
     *      weight : the weight of the edge
     *      
     */
    public static class Edge {
        public Node sourceVertex;
        public Node destVertex;
        public long weight;
        
        // Constructor for a new edge object
        public Edge(Node source, Node dest, long weight) {
            this.setSource(source);
            this.setDestVertex(dest);
            this.setWeight(weight);
        }
        
        // Getter / Setter Methods
        public Node getSource() {
            return sourceVertex;
        }
        public void setSource(Node newSource) {
            sourceVertex = newSource;
        }
        
        public Node getDestination() {
            return destVertex;
        }
        public void setDestVertex(Node newDest) {
            destVertex = newDest;
        }
        
        public long getWeight() {
            return weight;
        }
        public void setWeight(long newWeight) {
            weight = newWeight;
        }
    }
}