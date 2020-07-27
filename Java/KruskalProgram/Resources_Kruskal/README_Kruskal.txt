Kruskal’s MST Project

This program accepts a file representing an undirected, connected, and weighted graph with M edges and N vertices as input,
and outputs the weight of the minimum spanning tree of that graph using Kruskal’s algorithms, with an implementation of a 
union-find data structure that uses path compression.  For time complexity information, see program code.

Run Instructions:
Run the Kruskal.class file in the bin folder with a single argument.  The argument should be the file path to a .txt file 
containing the representation of the undirected graph (format described below).  The solution will be printed directly to 
the user.

Input Format:
The input file must have the following format: 

1) The first line of input will contain 2 space-separated integers, 1 <= N <= 100,000 and 0 <= M <= 500,000. Here, N is 
the number of vertices in the input graph and M is the number of edges. The vertices of the graph will be numbered 1 to N. 

2) After the first line, M lines follow. Each of these M lines contains three space-separated integers u, v and w, where 
1 <= u, v <= N and 1 <= w <= 1,000,000. Each such line indicates that there is an edge between the vertex u and the vertex v.
The graph will be simple and undirected: each edge will be between a unique pair of vertices. 

The input graph is guaranteed to be connected, so a spanning tree must exist. 

Output Format:
Output a single integer on a single line: the weight of the minimum spanning tree.  This is done using a single call of
System.out.println().


To Do:
Return a fgile with the same format as input, representing the MST who's weight was printed to the user
Make above file optional for the user.


References and Acknowledgments:
This program was orginially written as an assignment in a Virginia Tech Data structures class, but the orgiginal input
and output specifications have been altered slightly to make it more general and universal. All code written is original,
but please feel free to use this is it help you in any way.