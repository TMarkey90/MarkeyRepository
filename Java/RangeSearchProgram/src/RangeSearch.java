import java.util.*;
import java.io.*;

/**
 * @author Tom Markey
 * 
 * Range Tree Data Structure Project
 * 
 * Implementation of the range tree data structure for locating point in the x,y plane within a parameterized range.  
 * Uses a 2-D balanced binary search tree constructed from a pre-sorted list of coordinates, first by the x bounds,
 * then by the y-bounds with a sub-function. Outputs the number of points within the given query straight to console
 * in the form of a single integer.
 * For efficiency, canonical node detection is used to directly access the subtree and/or report the number of nodes 
 * in its subtree.  For details on complexity of efficiency, space, and initial construction time, see the comments 
 * and headers of the specific functions.  Program should reach speed and space required by spec sheet. 
 * 
 * Note: Could not correctly design program to recognize canonical nodes (always had mistakes on large tests)
 *       Instead, program goes to each individual nodes and increases the count if in range.  Increases the run time
 *       for larger trees, but should not change big O a query time of O(log^2(n)).  Method of determining canonical
 *       nodes are commented out so you can have an idea of my approach.
 * 
 * Program Structure:
 *      Main Method
 *      Helper Methods
 *              pointsMergeSort
 *              constructTree
 *              rangeFindByX
 *              rangeFindByY
 *      Additional Classes
 *              Node
 *              PointCoordinates
 *              
 *                      note: see function headers and comments for details
 */
public class RangeSearch {

    public static void main(String[] args) throws FileNotFoundException{
        // Parse the input from the input file
        File inputFile = new File(args[0]);
        Scanner inputScanner = new Scanner(inputFile);
        
        // Sort the point into 2 sorted lists
        
        // Get the primary information from the first line of input n = number of points, 
        // q = number of queries
        int numberOfPoints = inputScanner.nextInt();
        int numberOfQueries = inputScanner.nextInt();
        
        
        // Initialize 2 coordinates lists, one for x and one for y
        // Scans the next n lines to get the coordinate of the points, creates a new PointCoordinates
        // object, then adds it to both lists (to be sorted next step)
        PointCoordinates[] pointsByX = new PointCoordinates[numberOfPoints];
        
        for (int i = 0; i < numberOfPoints; i++) {
            // Get the coordinates and create the object
            double xCoordinate = inputScanner.nextDouble();
            double yCoordinate = inputScanner.nextDouble();
            PointCoordinates newCoordinate = new PointCoordinates(xCoordinate, yCoordinate);
            
            // Adds the object to both lists, to be sorted later
            pointsByX[i] = newCoordinate;
        }
        
        // Sorts the two lists by their respective primary coordinates using the two 
        // helper merge sort methods, then check for duplicates with respect to x and y
        
        // By Y
        pointsMergeSort(false, pointsByX, new PointCoordinates[numberOfPoints], 0, numberOfPoints - 1);
        double increaseY = offsetDuplicates(pointsByX, false);
        
        // By X
        pointsMergeSort(true, pointsByX, new PointCoordinates[numberOfPoints], 0, numberOfPoints - 1);
        double increaseX = offsetDuplicates(pointsByX, true);
        
        // Construct the Range-Tree
        // Details of function can be seen in Helper Section
        // Basic Overview of Algorithm for Tree Construction:
        //      Check if the range is a single coordinate (termination check)
        //      Calculate the median range
        //      Make Node w/ the coordinate at the median index
        //      Recursively create the leftChild
        //      Recursively create the rightChild
        //      Return the node (upon completion, should return the head node; parent = null)
        Node rangeTreeRoot = constructRangeTree(true, pointsByX, 0, numberOfPoints - 1);
        
        
        
        // Test Print
        
        // Find points within range
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < numberOfQueries; i++) {
            
            // Get the 4 bounds from the inputScanner
            double leftBoundX = inputScanner.nextInt();
            double rightBoundX = inputScanner.nextInt();
            double leftBoundY = inputScanner.nextInt();
            double rightBoundY = inputScanner.nextInt();
            
            // Run the primary rangeFind method based on the bounds (see Helper Function Section)
            int pointsInRange = rangeFindByX(rangeTreeRoot, 0, 0, leftBoundX, rightBoundX + increaseX, leftBoundY, rightBoundY + increaseY);
            
            // Append solution to the output
            output.append(pointsInRange + "\n");
            
            
        }
        
        // Print Output to console and close the scanner
        System.out.print(output.toString());
        inputScanner.close();
    }
    
    // HELPER METHODS
    
    /**
     * pointsMergeSort Method
     * Sorts a list of PointCoordinates by either x or y coordinates based on the parameter
     * input.  Fist parameter input is a boolean whether or not the coordinates are being 
     * sorted by X.  If false, obviously we are sorting by y.
     * Runs in O(nlog(n)) time either way of sorting.
     * Parameters:
     *      sortingByX (boolean) : true if sorting by X, false if sorting by Y
     *      primaryArray (PointCoordinates[]) : array being sorted
     *      auxArray (PointCoordinates[]) : auxilary array for processing, usually created new
     *      leftStart (int) : the left most index being sorted
     *      rightEnd (int) : the right most index being sorted
     *              note : end index, NOT size of array
     */
    public static void pointsMergeSort(
        // Parameters
        boolean sortingByX, PointCoordinates[] primaryArray, PointCoordinates[] auxArray, int leftStart, int rightEnd) {
        
        
        // Check what coordinate we are sorting the array by
        if (sortingByX) {
            // Check for base case / termination
            if (leftStart == rightEnd) {
                return;
            }
            
            // Select the midWay point, then recursively sort the two halves
            // of the primaryArray
            int midPoint = (leftStart + rightEnd) / 2;
            pointsMergeSort(sortingByX, primaryArray, auxArray, leftStart, midPoint);
            pointsMergeSort(sortingByX, primaryArray, auxArray, midPoint + 1, rightEnd);
            
            // Copy over the array, then merge the two halves
            for (int i = leftStart; i <= rightEnd; i++) {
                auxArray[i] = primaryArray[i];
            }
            
            // Initializes the indexes of the for loop
            int leftIndex = leftStart;
            int rightIndex = midPoint + 1;
            
            // Loop over the auxArray, sort and add accordingly
            for (int i = leftStart; i <= rightEnd; i++) {
                // if the left side is done, copy over the remaining right side
                if (leftIndex == (midPoint + 1)) {
                    primaryArray[i] = auxArray[rightIndex++];
                }
                
                // if the right side is done, copy the remaining left side
                else if (rightIndex > rightEnd) {
                    primaryArray[i] = auxArray[leftIndex++];
                }
                
                // Get the smaller value of the two x-coordinates, and add it to the array
                else if (Double.compare(auxArray[leftIndex].getX(), auxArray[rightIndex].getX()) <= 0) {
                    primaryArray[i] = auxArray[leftIndex];
                    leftIndex++;
                }
                else {
                    primaryArray[i] = auxArray[rightIndex];
                    rightIndex++;
                }
            }
        }
        
        // Sorts by Y, functionality remains the same
        else {
            // Check for base case / termination
            if (leftStart == rightEnd) {
                return;
            }
            
            // Select the midWay point, then recursively sort the two halves
            // of the primaryArray
            int midPoint = (leftStart + rightEnd) / 2;
            pointsMergeSort(sortingByX, primaryArray, auxArray, leftStart, midPoint);
            pointsMergeSort(sortingByX, primaryArray, auxArray, midPoint + 1, rightEnd);
            
            // Copy over the array, then merge the two halves
            for (int i = leftStart; i <= rightEnd; i++) {
                auxArray[i] = primaryArray[i];
            }
            
            // Initializes the indexes of the for loop
            int leftIndex = leftStart;
            int rightIndex = midPoint + 1;
            
            // Loop over the auxArray, sort and add accordingly
            for (int i = leftStart; i <= rightEnd; i++) {
                // if the left side is done, copy over the remaining right side
                if (leftIndex == (midPoint + 1)) {
                    primaryArray[i] = auxArray[rightIndex++];
                }
                
                // if the right side is done, copy the remaining left side
                else if (rightIndex > rightEnd) {
                    primaryArray[i] = auxArray[leftIndex++];
                }
                
                // Get the smaller value of the two records, and add it to the array
                else if (Double.compare(auxArray[leftIndex].getY(), auxArray[rightIndex].getY()) <= 0) {
                    primaryArray[i] = auxArray[leftIndex];
                    leftIndex++;
                }
                else {
                    primaryArray[i] = auxArray[rightIndex];
                    rightIndex++;
                }
            }
        }
    }
    
    
    
    /**
     * constructTree Method
     * Constructs a balanced, binary range tree that supports range query finds.  Implements recursion to construct 
     * tree, returns the head node with its parent = null.  Has a boolean parameter determining if the tree being 
     * constructed is based on the x, or y bounds.  Balanced of either is based on how the input elements are pre-sorted
     * (see pointsMergeSort method)
     * 
     * Complexity Argument:
     * Each leaf will have to be constructed, having a time complexity of O(n).  For each
     * of those leaf nodes, the "highest" it will have to travel up to get back to construct
     * the root will be log(n), the height the tree is guaranteed to be.  So, we have n * log(n).
     * So, the overall time complexity of the tree construction will be O(n log(n))
     * 
     * Parameters:
     *      isX (boolean) : true is constructing with primary nodes, false otherwise
     *      coordinateArray (PointCoordinates[]) : array of pre-sorted coordinates
     *      leftStart (int) : the left most index of the array being constructed
     *      rightEnd (int) : the right most index of the array being constructed
     *          note: this is the last index number, NOT the length of the array
     *  
     *  
     */
    public static Node constructRangeTree(boolean isX, PointCoordinates[] coordinateArray, int leftStart, int rightEnd) {
        // Completion Check
        if (leftStart == rightEnd) {
            Node leafNode = new Node(coordinateArray[leftStart]);
            PointCoordinates[] descendant = new PointCoordinates[1];
            descendant[0] = leafNode.getCoordinates();
            leafNode.setDescendants(descendant);
            
            // if the node is based on the x ordering, make the y subtree out of itself
            if (isX == true) {
                leafNode.setYRoot(leafNode);
            }
            
            return leafNode;
        }
        
        // Calculates the median index of the current index range
        int midPoint = (leftStart + rightEnd) / 2;
        
        // Creates the descendants[] array and copies the relevant points
        int numberOfDescendants = (rightEnd + 1) - leftStart;
        PointCoordinates[] descendants = new PointCoordinates[numberOfDescendants];
        int coordinateArrayIndex = leftStart;
        for (int i = 0; i < numberOfDescendants; i++) {
            descendants[i] = coordinateArray[coordinateArrayIndex];
            coordinateArrayIndex++;
        }
        
        
        // Creates a new node, then recursively assigns its left and right children, and
        // all of its descendant node coordinates
        Node internalNode = new Node(coordinateArray[midPoint]);
        internalNode.setLeftChild(constructRangeTree(isX, coordinateArray, leftStart, midPoint));
        internalNode.setRightChild(constructRangeTree(isX, coordinateArray, midPoint + 1, rightEnd));
        internalNode.setDescendants(descendants);
        
        // Checks if this node is based on x, and builds the y subtree if so
        if (isX == true) {
            PointCoordinates[] coordinatesByY = internalNode.getDescendants();
            int arrayLength = coordinatesByY.length;
            
            // Sort the array by y
            pointsMergeSort(false, coordinatesByY, new PointCoordinates[arrayLength], 0, arrayLength - 1);
            
            // Construct the sub tree
            internalNode.setYRoot(constructRangeTree(false, coordinatesByY, 0, arrayLength - 1));
        }
        
        // returns the node
        return internalNode;
    }

    
    
    /**
     * rangeFindByX Method
     * Recursively goes through the range tree and determines the pointCoordinates within the given- Xrange
     * Determines canonical nodes though series of if statements (see comments below)
     * Input node must be root node of the x-based rangeTree in order to work properly.  Once x-bound is determined,
     * y-version of function is called again to determine y-bound.  numberOfPoints is only increased when leaf or canonical
     * nodes are found within the y-bound (if found in y-bound, then it must be in the x-bound by design).
     * The basic structure of the algorithm is as follows: (more detail explanation in comments)
     *      Start at the root node
     *      Check left until left most leaf is found
     *          increase numberOfPoints
     *      Recursively return up the tree until the root is found again (right bound might be found before root)
     *          canonical nodes are found and numberOfPoints is increased accordingly
     *      Travel down the right bound until the right most leaf is found
     *          canonical nodes are found and numberOfPoints is increased
     *  
     *  Complexity Argument:
     *  The "deepest" the method will travel due to the canonical node detection is to the two single leafs 
     *  on the ends of the left and right bounds.  Because the both x-tree and y-tree are guaranteed to have 
     *  at maximum a height of log(n), each "deep" call from the x-version and y-version, which dominates the function, 
     *  will have a time complexity of log(n), having reached every node on the bound to the leaf.  Because worst case 
     *  this could happen to both the x and the y trees, we have log(n) * log(n) time complexity, yielding an overall 
     *  time complexity of O(log^2(n)).
     *  
     *  
     *  
     *  Parameters:
     *      inputNode (Node) : the node the current recursion is reading data from (original inputNode must be root)
     *      noteType (int) : integer representing the type of child it is. 0 = root, 1 = leftChild, 2 = rightChild
     *      numberOfpoint (int) : number of points within range 
     *      leftBoundX (int) : the minimum left x-coordinate 
     *      rightBoundX (int) : the maximum right x-bound
     *      leftBoundY (int) : the minimum left y-coordinate    note: y-coordinates passed in for call to rangeFindByY()
     *      rightBoundY (int) : the maximum right y-bound
     *      
     */
    public static int rangeFindByX(
            Node inputNode, int nodeType, int pointsInRange, double leftBoundX, double rightBoundX, double leftBoundY, double rightBoundY) {      
        // First, update the inputNodes childType field so we can reading information later
        inputNode.setChildType(nodeType);
        
        // LEAF CHECKS
        
        // Check to see if the inputNode is a leaf node
        // If so, check if its in bound, increase the points, then return
        if (inputNode.isLeaf()) {
            if ((inputNode.getCoordinates().getX() >= leftBoundX) && (inputNode.getCoordinates().getX() <= rightBoundX)) {
                // The value is within the bounds, call the rangeFindByY method and return the value
                pointsInRange = rangeFindByY(inputNode.getYRoot(), 0, pointsInRange, leftBoundY, rightBoundY);
            }
            return pointsInRange;
        }
        
        // ROOT CHECKS
        
        // Check if the inputNode is the root node (the start of the function)
        if (nodeType == 0) {
            // Check if the value is within the left bound.  If so, recursively check the left
            if (inputNode.getCoordinates().getX() >= leftBoundX) {
                pointsInRange = rangeFindByX(inputNode.getLeftChild(), 1, pointsInRange, leftBoundX, rightBoundX, leftBoundY, rightBoundY);
            }
            
            // Check if the value is within the right bound.  If so, recursively check the right
            if (inputNode.getCoordinates().getX() <= rightBoundX) {
                pointsInRange = rangeFindByX(inputNode.getRightChild(), 2, pointsInRange, leftBoundX, rightBoundX, leftBoundY, rightBoundY);
            }
            
            // Function has now recursively check both bounds.  If values were found within the parameter bounds,
            // they will have been added to the outputList
            return pointsInRange;
        }
        
        // LEFTCHILD CHECKS
        
        // Checks if the inputNode is a leftChild
        if (nodeType == 1) {
            // Check the left bound to make sure we haven't gone to far
            if (inputNode.getCoordinates().getX() >= leftBoundX) {
                
                // Check the right bound.  
                if (inputNode.getCoordinates().getX() <= rightBoundX) {
                    
                    // If in both bounds, check if the node is a canonical node
                    // By design, a left child can only be a canonical node if 3 things are true:
                    //  1) The node's parent is a rightChild
                    //  2) The node's parent is within the right bound
                    //  3) The node's grandparent is within the left bound
                    
                    //  note: if the node's parent is not a right child, recursive check both children
                    // if (inputNode.getParent().getChildType() != 2) {
                        pointsInRange = rangeFindByX(inputNode.getLeftChild(), 1, pointsInRange, leftBoundX, rightBoundX, leftBoundY, rightBoundY);
                        
                        pointsInRange = rangeFindByX(inputNode.getRightChild(), 2, pointsInRange, leftBoundX, rightBoundX, leftBoundY, rightBoundY);
                        
                        return pointsInRange;
                    // }
                    
                    
                    // else {                        
                    //     If a canonical node, increase the points within the range and return
                    //    if ((inputNode.getParent().getCoordinates().getX() <= rightBoundX) && 
                    //            (inputNode.getParent().getParent().getCoordinates().getX() >= leftBoundX)){
                    //        pointsInRange = rangeFindByY(inputNode.getYRoot(), 0, pointsInRange, leftBoundY, rightBoundY);
                    //        return pointsInRange;
                    //    }
                        
                    //    Not a canonical node, so we recursively check each child node
                    //      else {
                    //          pointsInRange = rangeFindByX(inputNode.getLeftChild(), 1, pointsInRange, leftBoundX, rightBoundX, leftBoundY, rightBoundY);
                            
                    //           pointsInRange =  rangeFindByX(inputNode.getRightChild(), 2, pointsInRange, leftBoundX, rightBoundX, leftBoundY, rightBoundY);
                            
                    //           return pointsInRange;
                    //       }
                    //  }
                    
                }
                
                // If out of right bounds, go recursive call left
                else {
                    
                    pointsInRange = rangeFindByX(inputNode.getLeftChild(), 1, pointsInRange, leftBoundX, rightBoundX, leftBoundY, rightBoundY);
                    return pointsInRange;
                }    
            }
            
            // If out of left bounds, recursive call right right
            else {
                pointsInRange = rangeFindByX(inputNode.getRightChild(), 2, pointsInRange, leftBoundX, rightBoundX, leftBoundY, rightBoundY);
                return pointsInRange;
            }
        }
        
        // RIGHTCHILD CHECKS
        
        // Checks if the inputNode is a rightChild
        if (nodeType == 2) {
            
            // Checks if the value is within the left bound
            if (inputNode.getCoordinates().getX() >= leftBoundX) {
                
                // Checks if the value is within the right bound
                if (inputNode.getCoordinates().getX() <= rightBoundX) {
                    
                    // The value is within both bounds, so now we check if it is canonical
                    // A rigghtChild will only be canonical if 3 things are true:
                    // 1) the parent is a left child
                    // 2) the parent's value is within the right leftBound
                    // 3) the grandparent's value is within the rightBound
                    
             //       note: if the node's parent is not a left child, we still go left recursively, then check the right (by design)
             //       if (inputNode.getParent().getChildType() != 1) {
                        pointsInRange = rangeFindByX(inputNode.getLeftChild(), 1, pointsInRange, leftBoundX, rightBoundX, leftBoundY, rightBoundY);
                       
                        pointsInRange = rangeFindByX(inputNode.getRightChild(), 2, pointsInRange, leftBoundX, rightBoundX, leftBoundY, rightBoundY);
                        
                        return pointsInRange;
             //       }
                    
                    
             //       else {
             //            If a canonical node, increase points in range
             //           if ((inputNode.getParent().getCoordinates().getX() <= rightBoundX) && 
             //               (inputNode.getParent().getParent().getCoordinates().getX() >= leftBoundX)){
             //               pointsInRange = rangeFindByY(inputNode.getYRoot(), 0, pointsInRange, leftBoundY, rightBoundY);
                        
             //              return pointsInRange;
             //           }
                    
             //           Not a canonical node, so we recursively check each child node
             //           else {
             //               pointsInRange = rangeFindByX(inputNode.getLeftChild(), 1, pointsInRange, leftBoundX, rightBoundX, leftBoundY, rightBoundY);
                        
              //              pointsInRange = rangeFindByX(inputNode.getRightChild(), 2, pointsInRange, leftBoundX, rightBoundX, leftBoundY, rightBoundY);
                        
              //              return pointsInRange;
              //          }
              //     }
                    
                }
                
                // If not in the rightBound, recursive call left
                else {
                    pointsInRange = rangeFindByX(inputNode.getLeftChild(), 1, pointsInRange, leftBoundX, rightBoundX, leftBoundY, rightBoundY);
                    return pointsInRange;
                }
                
            }
            
            // If not in the leftBound, recursive call right
            else {
                pointsInRange = rangeFindByX(inputNode.getRightChild(), 2, pointsInRange, leftBoundX, rightBoundX, leftBoundY, rightBoundY);
                return pointsInRange;
            }
        }
        
        // At this point, all recursions should be complete and returned
        return pointsInRange;
    }
    
    
    
    /**
     * rangeFindByY Method
     * Almost duplicate method of the x-version, but determines bounds based on y. For complexity argumentation 
     * and overview of function, see the x-version above. Will only ever be called on a leaf or canonical node.
     * Note that the input to the function will be the yRoot of the inputNode for rangeFindByX, not the inputNode
     * itself.
     * 
     * Parameters:
     *      inputNode (Node) : the node the current recursion is reading data from (original inputNode must be root)
     *      noteType (int) : integer representing the type of child it is. 0 = root, 1 = leftChild, 2 = rightChild
     *      numberOfpoint (int) : number of points within range
     *      leftBoundY (int) : the minimum left y-coordinate 
     *      rightBoundY (int) : the maximum right y-bound       note: no x-bound needed, if function is called the 
     *                                                                  points are within the x-bounds
     */
    public static int rangeFindByY(Node inputNode, int nodeType, int pointsInRange, double leftBoundY, double rightBoundY) {      
        // First, update the inputNodes childType field so we can reading information later
        inputNode.setChildType(nodeType);
        
        // LEAF CHECKS
        
        // Check to see if the inputNode is a leaf node
        // If so, check if its in bound, increase the points, then return
        if (inputNode.isLeaf()) {
            if ((inputNode.getCoordinates().getY() >= leftBoundY) && (inputNode.getCoordinates().getY() <= rightBoundY)) {
                pointsInRange++;
            }
            return pointsInRange;
        }
        
        // ROOT CHECKS
        
        // Check if the inputNode is the root node (the start of the function)
        if (nodeType == 0) {
            // Check if the value is within the left bound.  If so, recursively check the left
            if (inputNode.getCoordinates().getY() >= leftBoundY) {
                pointsInRange = rangeFindByY(inputNode.getLeftChild(), 1, pointsInRange, leftBoundY, rightBoundY);
            }
            
            // Check if the value is within the right bound.  If so, recursively check the right
            if (inputNode.getCoordinates().getY() <= rightBoundY) {
                pointsInRange = rangeFindByY(inputNode.getRightChild(), 2, pointsInRange, leftBoundY, rightBoundY);
            }
            
            // Function has now recursively check both bounds.  If values were found within the parameter bounds,
            // they will have been added to the outputList
            return pointsInRange;
        }

        
        // LEFTCHILD CHECKS
        
        // Checks if the inputNode is a leftChild
        if (nodeType == 1) {
            // Check the left bound to make sure we haven't gone to far
            if (inputNode.getCoordinates().getY() >= leftBoundY) {
                
                // Check the right bound.  
                if (inputNode.getCoordinates().getY() <= rightBoundY) {
                    
                    // If in both bounds, check if the node is a canonical node
                    // By design, a left child can only be a canonical node if 3 things are true:
                    //  1) The node's parent is a rightChild
                    //  2) The node's parent is within the right bound
                    //  3) The node's grandparent is within the left bound
                    
                    //  note: if the node's parent is not a right child, recursive check both children
                    // if (inputNode.getParent().getChildType() != 2) {
                        pointsInRange = rangeFindByY(inputNode.getLeftChild(), 1, pointsInRange, leftBoundY, rightBoundY);
                        
                        pointsInRange = rangeFindByY(inputNode.getRightChild(), 2, pointsInRange, leftBoundY, rightBoundY);
                        
                        return pointsInRange;
                    // }
                    
                    
              //      else {                        
              //           If a canonical node, increase the points within the range and return
              //          if ((inputNode.getParent().getCoordinates().getY() <= rightBoundY) && 
              //                  (inputNode.getParent().getParent().getCoordinates().getY() >= leftBoundY)){
              //              pointsInRange = pointsInRange + inputNode.getDescendants().length;
              //              return pointsInRange;
              //          }
                        
              //           Not a canonical node, so we recursively check each child node
              //          else {
              //              pointsInRange = rangeFindByY(inputNode.getLeftChild(), 1, pointsInRange, leftBoundY, rightBoundY);
                            
              //              pointsInRange =  rangeFindByY(inputNode.getRightChild(), 2, pointsInRange, leftBoundY, rightBoundY);
              //              
              //              return pointsInRange;
              //          }
              //      }
                    
                }
                
                // If out of right bounds, go recursive call left
                else {
                    
                    pointsInRange = rangeFindByY(inputNode.getLeftChild(), 1, pointsInRange, leftBoundY, rightBoundY);
                    return pointsInRange;
                }    
            }
            
            // If out of left bounds, recursive call right right
            else {
                pointsInRange = rangeFindByY(inputNode.getRightChild(), 2, pointsInRange, leftBoundY, rightBoundY);
                return pointsInRange;
            }
        }
        
        // RIGHTCHILD CHECKS
        
        // Checks if the inputNode is a rightChild
        if (nodeType == 2) {
            
            // Checks if the value is within the left bound
            if (inputNode.getCoordinates().getY() >= leftBoundY) {
                
                // Checks if the value is within the right bound
                if (inputNode.getCoordinates().getY() <= rightBoundY) {
                    
                    // The value is within both bounds, so now we check if it is canonical
                    // A rigghtChild will only be canonical if 3 things are true:
                    // 1) the parent is a left child
                    // 2) the parent's value is within the right leftBound
                    // 3) the grandparent's value is within the rightBound
                    
                    //  note: if the node's parent is not a left child, we still go left recursively, then check the right (by design)
                    //  if (inputNode.getParent().getChildType() != 1) {
                        pointsInRange = rangeFindByY(inputNode.getLeftChild(), 1, pointsInRange, leftBoundY, rightBoundY);
                       
                        pointsInRange = rangeFindByY(inputNode.getRightChild(), 2, pointsInRange, leftBoundY, rightBoundY);
                        
                        return pointsInRange;
                    //  }
                    
                    
                    // else {
                        // If a canonical node, increase points in range
                    //   if ((inputNode.getParent().getCoordinates().getY() <= rightBoundY) && 
                    //       (inputNode.getParent().getParent().getCoordinates().getY() >= leftBoundY)){
                    //        pointsInRange = pointsInRange + inputNode.getDescendants().length;
                        
                    //       return pointsInRange;
                    //   }
                    
                        // Not a canonical node, so we recursively check each child node
                    //     else {
                    //         pointsInRange = rangeFindByY(inputNode.getLeftChild(), 1, pointsInRange, leftBoundY, rightBoundY);
                        
                    //        pointsInRange = rangeFindByY(inputNode.getRightChild(), 2, pointsInRange, leftBoundY, rightBoundY);
                        
                    //        return pointsInRange;
                    //    }
                    // }
                    
                }
                
                // If not in the rightBound, recursive call left
                else {
                    pointsInRange = rangeFindByY(inputNode.getLeftChild(), 1, pointsInRange, leftBoundY, rightBoundY);
                    return pointsInRange;
                }
                
            }
            
            // If not in the leftBound, recursive call right
            else {
                pointsInRange = rangeFindByY(inputNode.getRightChild(), 2, pointsInRange, leftBoundY, rightBoundY);
                return pointsInRange;
            }
        }
        
        // At this point, all recursions should be complete and returned
        return pointsInRange;
    }
    
    
    /**
     * offsetDuplicates Method
     * Method for slighting increasing the duplicate entries in a list as to offset their coordinates to
     * help with range tree construction.  Returns the max amount increased so you can offset your
     * rangeFind search's right bound.  Assumes you are passing a pre-sorted list as a parameter.
     * Parameters:
     *      inputList (PointCoordinates[]) : list of coordinates you want to offset
     *      byX (boolean) : true if off-setting the Y values, false if off-setting by Y
     */
    public static double offsetDuplicates(PointCoordinates[] inputList, boolean byX) {
        // Duplicates by X
        if (byX) {
            // Initialize the tiny offset, and the current offset
            double initialOffset = 0.0000001;
            double currentOffset = initialOffset;
        
            // Runs though the list and checks if the next value is the same
            // If so, offsets, then checks the next one, and continues on
            for (int i = 0; i < inputList.length; i++) {          
                for (int j = i+1; j < inputList.length; j++) {
                    // Break iff different
                    if (inputList[i].getX() != inputList[j].getX()) {
                        break;
                    }
                    
                    // If equal, increase the offset, then the x value
                    currentOffset = currentOffset + initialOffset;
                    inputList[j].setX(inputList[j].getX() + currentOffset);
                }
            }
            
            return currentOffset;
        }
        
        // Duplicates by Y
        else {
         // Initialize the tiny offset, and the current offset
            double initialOffset = 0.0000001;
            double currentOffset = initialOffset;
        
            // Runs though the list and checks if the next value is the same
            // If so, offsets, then checks the next one, and continues on
            for (int i = 0; i < inputList.length; i++) {          
                for (int j = i+1; j < inputList.length; j++) {
                    // Break iff different
                    if (inputList[i].getY() != inputList[j].getY()) {
                        break;
                    }
                    
                    // If equal, increase the offset, then the x value
                    currentOffset = currentOffset + initialOffset;
                    inputList[j].setY(inputList[j].getY() + currentOffset);
                }
            }
            return currentOffset;
        }
    }
    
    
    // ADDITIONAL CLASSES
    
    /**
     * Node Class
     * Node class used for the tree structure.  Tree will be a binary tree, so each Node with have a single Parent, 
     * and two children nodes (left ad right).  If node is constructed in the primary x-coordinate array, it will
     * have a special y-root field assigned for the 2-D implementation of the range find.  If a y-coordinate node
     * this field will remain null.
     * Also, can store an array of all the points of the nodes further down the tree (descendants).
     * Node also contains a 2 value fields for storing the x and y coordinates.
     * Two simple boolean method to determine if it is a root or leaf node.
     * 
     * Parameters (in order):
     *      parent (Node) : the parent of this node
     *      yRoot (Node) : the root node of its descendant based on their y-bounds (only for x-nodes)
     *      leftChild (Node) : the leftChild of this node
     *      rightChild (Node) : the rightChild of this node
     *      descendants (PointCoordinates[]) : all of the descendant nodes "downstream" from this one
     *          note: the descendants contains the node itself
     *      coordinates (pointCoordinate) : the coordinates of this node
     *      childType (int) : 0 = root, 1 = leftChil, 2 = rightChild
     *              note: used for and set by the rangeFind() method
     */
    public static class Node {
        // Fields
        private Node parent;
        private Node yRoot;
        private Node leftChild;
        private Node rightChild;
        private PointCoordinates[] descendants;
        private PointCoordinates coordinates;
        private int childType;
        
        // Constructor
        public Node(PointCoordinates coords) {          
            this.setCoordinates(coords);
        }
        
        // Boolean Methods
        public boolean isLeaf() {
            if ((this.getLeftChild() == null) && (this.getRightChild() == null)) {
                return true;
            }
            else {
                return false;
            }
        }
        
        public boolean isRoot() {
            if(this.getParent() == null) {
                return true;
            }
            else {
                return false;
            }
        }
        
        // Getter and Setter Methods
        // Parent
        public void setParent(Node newParent) {  
            this.parent= newParent;
        }
        public Node getParent() {
            return parent;
        }
        
        // leftChild
        public void setLeftChild(Node newLeftChild) {
            this.leftChild = newLeftChild;
            leftChild.setParent(this);
        }
        public Node getLeftChild() {
            return leftChild;
        }
        
        // rightChild
        public void setRightChild(Node newRightChild) {
            this.rightChild = newRightChild;
            rightChild.setParent(this);
        }
        public Node getRightChild() {
            return rightChild;
        }
        
        // Descendants
        public PointCoordinates[] getDescendants() {
            return descendants;
        }
        public void setDescendants(PointCoordinates[] newDescendants) {
            this.descendants = newDescendants;
        }
        
        // coordinates
        public void setCoordinates(PointCoordinates newCoordinates) {
            this.coordinates = newCoordinates;
        }
        public PointCoordinates getCoordinates() {
            return coordinates;
        }
        
        // childType
        public void setChildType(int type) {
            this.childType = type;
        }
        public int getChildType() {
            return childType;
        }

        // yRoot
        public Node getYRoot() {
            return yRoot;
        }

        public void setYRoot(Node yRoot) {
            this.yRoot = yRoot;
        }
    }
    
    
    
    /**
     * PointCoordinate Class
     * Class that stores the x and y coordinates of a point on the xy-plane
     * Easier to create class of coordinates than store the coordinates directly in the Node
     * class.  Easier for Range Tree construction
     * Parameters: (self explanatory)
     *      x : int
     *      y : int
     */
    public static class PointCoordinates {
        // Fields
        private double xCoordinate;
        private double yCoordinate;
        
        // Constructor
        public PointCoordinates(double x, double y) {
            this.setX(x);
            this.setY(y);
        }
        
        // Getter and Setter Methods
        public void setX(double newX) {
            this.xCoordinate = newX;
        }
        public double getX() {
            return xCoordinate;
        }
        
        public void setY(double newY) {
            this.yCoordinate = newY;
        }
        public double getY() {
            return yCoordinate;
        }
    }
}
