import java.util.*;
import java.io.*;

/**
 * Dijkstra's Shortest Path Project
 * @author Tom Markey
 * 
 * This program is an implementation of Dijkstra's algorithm, computing the shortest path
 * between vertex 1 and vertex N (the last vertex listed in the input) in a connected, directed
 * graph.  See README in resources folder for a description of the input format. 
 * For complexity arguments, n = number of vertexes, while m = edges.
 * Construction of the graph takes O(n+m) time.  Because of PriorityQueue implementation, 
 * Dijkstra's runs with a time complexity of O(mlog(n)).
 * 
 * Sections of Program:
 *      Main Method
 *          reads in the input and constructs the graph
 *          adds the point in the graph to a priority queue
 *          apply Dijkstra's Algorithm based on distance on the x,y,z plane
 *          Print distance from point 1 to point n
 *      Helper Methods
 *          getDistanceBetween()
 *      Additional Classes
 *          Vertex
 *          Location
 *          DistanceComparator
 *
 *  To Do:
 * 		Create optional return of file describing actual shortest path
 *  	Determine the new runtime efficiency after optional return addition
**/

public class Dijkstra {

    public static void main(String[] args) throws FileNotFoundException {
        //  Reader and Set-Up Section

        // Initialize the input scanner
        File inputFile = new File(args[0]);
        Scanner inputScanner = new Scanner(inputFile);
        
        // Reads the first line for number of vertexes(n) and edges(m)
        int numberOfVertexes = inputScanner.nextInt();
        int numberOfEdges = inputScanner.nextInt();
        
        // Initializes the lists in memory of vertexes
        Vertex[] vertexList = new Vertex[numberOfVertexes];
        
        
        // Reads next n lines for Vertex, initializing the object and getting the 
        // location of the point in space.  Adding Neighbors is done at next step
        // Efficiency of O(n)
        for (int i = 0; i < numberOfVertexes; i++) {
            
            // Reads in line from the input and initializes the corresponding variables
            double xCoordinate = inputScanner.nextDouble();
            double yCoordinate = inputScanner.nextDouble();
            double zCoordinate = inputScanner.nextDouble();
            
            // Creates the Vertex object and assigns it to the vertexList
            // Note: the vertex's number is its list index + 1
            Vertex newVertex = new Vertex((i + 1), new Location(xCoordinate, yCoordinate , zCoordinate));
            vertexList[i] = newVertex;
            
        }
        
        // Reads the next m lines for adding neighbors to the Vertexes       
        // Efficiency of O(m)
        for (int i = 0; i < numberOfEdges; i++) {
            
            // Reads in line from the input and initializes the corresponding variables
            int sourceVertexIndex = inputScanner.nextInt() - 1;
            int destinationVertexIndex = inputScanner.nextInt() - 1;
            
            // Adds the neighbor to the source vertex, effectively creating an edge
            vertexList[sourceVertexIndex].addNeighbor(vertexList[destinationVertexIndex]);
        }
        // Efficiency up until this point should be O(n + m), with the creation of Vertexes
        // and the addition of their neighbors dominating the runtime
        
        
        // Now, we loop over the vertexList and add each Vertex to a PriorityQueue, ordered
        // by distance from Vertex 1.  This will then be used in Dijkstra's Algorithm
        // Efficiency of O(n)
        Comparator<Vertex> comparator = new DistanceComparator();
        PriorityQueue<Vertex> vertexPriorityQueue = new PriorityQueue<Vertex>(numberOfVertexes, comparator);
        
        for (int i = 0; i< numberOfVertexes; i++) {
            vertexPriorityQueue.add(vertexList[i]);
        }
 
        
        // Implementation of Dijkstra's Algorithm:
        // We start by selecting the first Vertex in the PriorityQueue, which to begin will
        // be Vertex 1.  We process all of the vertex's neighbors, updating their distances from
        // Vertex 1 as we go.  Once all a vertex's neighbors have been processed, we put the vertex
        // in the visited list.  Once the PriorityQueue is empty, we have all the shortest paths from
        // Vertex 1 to all other Vertexs, including the Nth vertex.  This Nth vertex's distance
        // from Vertex 1 is then printed to console.
        // This algorithm basically performs a BFS on the constructed graph.  So, the time complexity
        // is O(n + m), scanning each vertex and its edges once
        
        // Initialize the visited list
        int vertexesVisited = 0;
        
        // for loop that runs over each vertex, implementing Dijsktra's Algorithm on the distance 
        // from its neighbors and distance from Vertex 1
        while (vertexesVisited != numberOfVertexes) {
            // Grabs the current vertex for processing its neighbors
            
            Vertex currentVertex = vertexPriorityQueue.peek();
            
            // Grabs the shortest distance neighbor and updates its distance, if needed
            Vertex currentNeighbor = currentVertex.getNeighbor();
            
            // Checks if the vertex has any neighbors that have not been visited
            if (currentNeighbor != null) {
                double distanceToNeighbor = getDistanceBetween(currentVertex, currentNeighbor);
                
                if (currentVertex.getDistanceFromStart() + distanceToNeighbor < currentNeighbor.getDistanceFromStart()) {
                    currentNeighbor.setDistanceFromStart(currentVertex.getDistanceFromStart() + distanceToNeighbor);
                    
                    // Adds the updated, lowered distance Vertex to the PriorityQueue, acting as
                    // a quasi decrease-key method
                    vertexPriorityQueue.add(currentNeighbor);
                }
            }
            
            // if null, then there are no more neighbors, so we can mark the vertex as visited and increase out counter
            else {
                // All neighbors and their distances have been processed at this point, so we add the vertex
                // to the visited list
                vertexPriorityQueue.poll();
                vertexesVisited++; 
            }
        }
        
        // While loop has finished, so we return the final distance variable of the Nth vertex
        // in the vertexList.
        System.out.println(vertexList[numberOfVertexes - 1].getDistanceFromStart());
    }
    
    // Helper Methods
    
    // Helper Methods
    
    /**
     * getDistanceBetween Method
     * Method that calculates and returns the distance between two Vertex objects.  
     * Formula is 3-D Pythagorean Formula:      for Locations Vertexs A and B
     *          D = sqrt[ (destX - sourceX)^2 + (destY - sourceY)^2 + (destZ - sourceZ)^2 ] 
     */
     public static double getDistanceBetween(Vertex source, Vertex destination) {
         // Performs calculations and returns values
         return Math.sqrt( ( ( (destination.getCoordinates().getX() - source.getCoordinates().getX() ) * (destination.getCoordinates().getX() - source.getCoordinates().getX()) ) 
                           + ( (destination.getCoordinates().getY() - source.getCoordinates().getY() ) * (destination.getCoordinates().getY() - source.getCoordinates().getY()) )
                           + ( (destination.getCoordinates().getZ() - source.getCoordinates().getZ() ) * (destination.getCoordinates().getZ() - source.getCoordinates().getZ()) ) )
                         );
     }
    
    
    // Additional Classes
    
    /**
     * Vertex Class
     * A Node-like abstraction of a vertex in the graph.  Contains an identifying number, and 
     * a Location object representing the vertex's position on the x,y,z plane.  
     * Fields:
     *      number : identifying number
     *      coordinates : Location of the vertex on the x,y,z plane
     *      neighbors : the neighboring Vertexs able to receive messages
     *      distanceFromStart : the shortest distance to vertex 1.  Initialized as 0 for vertex 1,
     *                          and MAX for all other Vertexs
     */  
    public static class Vertex {
        // Fields
        public int number;
        public Location coordinates;
        public PriorityQueue<Vertex> neighbors;
        public double distanceFromStart;
        
        
        // Constructor Method
        public Vertex(int IDNumber, Location position) {
            this.setNumber(IDNumber);
            this.setCoordinates(position);
            neighbors = new PriorityQueue<Vertex>(new DistanceComparator());
            
            // Checks if this is vertex 1
            if (IDNumber == 1) {
                this.setDistanceFromStart(0);
            }
            
            // Otherwise
            else {
                this.setDistanceFromStart(Double.MAX_VALUE);
            }
        }
        
        
        // Getters and Setters for the fields
        
        // Number
        public void setNumber(int newNumber) {
            number = newNumber;
        }
        public int getNumber() {
            return number;
        }
        
        
        // Coordinates
        public void setCoordinates(Location newCoordinates) {
            coordinates = newCoordinates;
        }
        public Location getCoordinates() {
            return coordinates;
        }
        
        
        // Neighbors
        public void addNeighbor(Vertex newNeghbor) {
            neighbors.add(newNeghbor);
        } 
        public Vertex getNeighbor() {     // removes and returns first neighbor in queue
            return neighbors.poll();
        }
        
        
        // Distance from start
        public void setDistanceFromStart(double newDistance) {
            distanceFromStart = newDistance;
        }
        public double getDistanceFromStart() {
            return distanceFromStart;
        }
    }
    
    
    /**
     * Location Class
     * Contains the three coordinates representing points on the x,y,z Euclidean coordinate system.
     * Fields:
     *      x : x coordinate
     *      y : y coordinate
     *      z : z coordinate
     */
    public static class Location {
        // Fields
        private double x;
        private double y;
        private double z;
        
        // Constructor Method
        public Location(double xCord, double yCord, double zCord) {
            this.setX(xCord);
            this.setY(yCord);
            this.setZ(zCord);
        }
        
        // Getter and Setter Methods
        // X
        public void setX(double newX) {
            x = newX;
        }
        public double getX() {
            return x;
        }
        
        // Y
        public void setY(double newY) {
            y = newY;
        }
        public double getY() {
            return y;
        }
        
        // Z
        public void setZ(double newZ) {
            z = newZ;
        }
        public double getZ() {
            return z;
        }
    }
    
    /**
     * DistanceComparator class 
     * Used for the ordering of the PriorityQueue used in Dijsktra's Implementation
     * Compares the distance from vertex 1 in the form of a double
     * @Override: compare() 
     */
    public static class DistanceComparator implements Comparator<Vertex> {
        // @override
        public int compare(Vertex A, Vertex B) {
            if (A.getDistanceFromStart() < B.getDistanceFromStart() ) {
                return -1;
            }
            if (A.getDistanceFromStart() > B.getDistanceFromStart()) {
                return 1;
            }
            return 0;
        }
    }
    
}