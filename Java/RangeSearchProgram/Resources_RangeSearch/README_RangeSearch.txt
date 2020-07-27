Range Search Project

This program is an implementation of a Range Tree data structure that supports range counting queries over a 2D point set. 
The input is a set of points in 2-dimensional space in which a data structure on these points is built to answer queries. 
For each query, a description of an axis-parallel rectangle in 2D space (a range of x and y values) is given. The program 
then uses the data structure built to answer each query. For each query, the number of points that lie inside the rectangle 
(including points on the boundary) is reported directly back to the user.


Run Instructions:
Run the RangeSearch.class file in the bin folder with a single argument.  The argument should be the file path to a .txt file 
containing the representation of the points in a 2D plane (format described below).  The solution will be printed directly to 
the user.


Input Format:
The input file will be a .txt file and must have the following format: 

1) The first line of input file will contain two space separated integers N and Q, where 1 <= N <= 10,000 and 
1 <= Q <= 1,000.  Note that N is the number of points and Q is the number of queries.

2) N lines follow, with each line describing a point. Each line contains 2 space separated integers x and y, where 
0 <= x, y <= 1,000,000.  Tese are the x and y coordinates of the corresponding point. It is possible that multiple points 
lie at the same coordinates.

3) Lastly, Q lines follow, with each line describing a query. Each query line contains 4 space separated integers xmin,
xmax, ymin, and ymax, where 0 <= xmin, xmax, ymin, ymax <= 1,000,000. It is guaranteed that xmin <= xmax and ymin <= ymax. 
These 4 values describe the boundaries of the query rectangle.


Output Format:
Output is Q lines, with the i'th line giving the answer to the i'th query. Each line contains a single integer, the number 
of query points that lie within or on the boundary of the specified rectangular prism.  So for example, the integer on the
second line of the output will the the number of points that lie within the second range query. More formally put, output is
the number of input points (x,y,z) such that xmin <= x <= xmax, and ymin <= y <= ymax, zmin <= z <= zmax. Output is printed
directly to the user.

Example Input: 

5 2
0 0
10 10
5 10
0 5
10 10 
0 5 0 5
5 10 10 1000

Example Output:

2 
3

References and Acknowledgments:
Two resources were used in this project:
1.  	Notes by David Mount: http://www.cs.umd.edu/~mount/754/Lects/754lects.pdf
2.	Computational Geometry: Algorithms and Applications, Mark de Berg, Otfried Cheong, Mark van Krevald, Mark Overmars.

This program was orginially written as the solution to a probloem in a Virginia Tech Data Structures class.  But, the
original input and output specificationas have been altered slightly to make it more user freindly. All code written 
is original, and please feel free to use this if it help you in any way.
