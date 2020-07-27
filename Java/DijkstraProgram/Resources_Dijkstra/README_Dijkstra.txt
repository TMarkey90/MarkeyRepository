Dijkstra Shortest Path Project

This is an implementation Dijkstra’s shortest path algorithm on a directed graph formed from points in 3D space. 
The input a set of N points in 3-dimensional space labeled 1 to N, connected together within a directed graph.  
Every edge has a weight equal to the Euclidean distance between its endpoints. This program computes the shortest 
path from point 1 to point N on the input graph, and reports it back to the user.


Run Instructions:
Run the Dijkstra.class file in the bin folder with a single argument.  The argument should be the file path to a .txt file containing
the representation of the directed graph (format described below).  The solution will be printed directly to the user.


Input Format:
The program has a public static void main(String[] args) method.  The value at args[0] will be the path to a .txt
file containing the directed graph representation.  This .txt file will have the following format: 

1)  The first line of input will contain two, space-separated integers, 1 <= N <= 100,000, and 1 <= M <= 500,000.
 N is the number of points in euclidean space (vertexes) and M is the connections between the points (edges).
 Note that this graph is directed.

2)  N lines follow, with line k describing the vertex labeled k. Each of these N lines will contain 3 
space separated integers: -1,000,000 <= x,y,z <= 1,000,000. The triplet (x,y,z) describes the coordinates of the
vertex in 3 dimensional space.

3)  M lines follow, each describing a directed edge. Each line contains 2 space separated integers, 1 <= u and
 v <= N, where u and v are vertex labels. Each line indicates that there sia na edge between vertex u  and vertex v. 
The weight of any edge can be computed as the Euclidean distance between u and v. 

Output Format:
The output is a single floating point number on a single line: the minimum distance required to travel from vertex 1 
to vertex N.  This is produced by a single call to System.out.println() to output the answer directly to the user. 
Note that form the program to function correctly, it must be guaranteed that there is a valid path from vertex 1 to 
vertex N in the graph. 

References and Acknowledgments:
This program was written to complete an assignment for the Data Structures and Algorithms (CS 3114) class at Virginia Tech.  
All code is original.

*Addition of optional shortest path returned was not for the orginial assignment.  Done to make a little more user friendly.

To Do:

Create return file describing the actual shortest path, not just the minimum distance.