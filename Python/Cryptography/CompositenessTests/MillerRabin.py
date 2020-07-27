"""
Miller-Rabin Compositeness Test

Takes an input n, which is a odd integer > 2, and determines if the candidate
integer is prime using the Miller-Rabin Compositeness Test.  Returns "Yes" if
cadidate is composite, "No" is candiadte is liekly prime.  Note that M-R is 
a yes-biased Monte Carlo algorithm for complositeness, so no false positives 
are possible, but false negatives are possible.

Basic Outline:
    input: n you want to test
    output: if n is composite or a strong or weak pseudoprime
    
    n - 1 = 2^k * m, for some k and m
    select random base a, 0 < a < n
    b = a^m (mod n)
    if b = 1 (mod n), n is prime
    for i = 0 to k-1
        if b = -1 (mod n)
            return n is likely prime
        else b = b^2 (mod n)
    for loop ends, return n is composite


For user freindly version that only displays results, set displayToconsole = False
For more detail on steps and vaules displayed, set displayToconsole = True

To Do:
    Prove mathematically in comments
    implement repeat b optimization
    create desktop executable
    
@author: Tom Markey
"""
import random


# input candidate integer, number of itteration of randomized a's you want
n = 15770708441
testIter = 100
displayToconsole = False

# loop switches, do not change
composite = True
prime = False
# loop switches, do not change


j = 1
for j in range(1, testIter+1):    
    # randomized a is selected
    a = random.randint(0, n-1)
    
    # integers k and m are solved for, such that n-1 = 2^k * m
    k = 0 
    h = n-1 
    m = h  
    while h % 2 == 0:
        h = h/2
        m = h
        k = k+1
    
    m = int(m)
    
    # Display initial values on the console
    if displayToconsole == True:
        print()
        print("Initial Values of iteration number ", j, ":")
        print()
        print("n = ", n)
        print("a = ",a)
        print("k = ",k)
        print("m = ", m)
        print()
    
    # integer b is calculated and initially tested for primality
    # check the math behind this for %100 primality accuracy 
    b = pow(a, m, n)
    if b == 1:
        if displayToconsole == True:
            print()
            print("M-R Test failed: a^m = 1 (mod n)"),
            print("Integer n is prime.") 
            print()
            print("---------- END OF ITERATION ", j, " ----------")
        composite = False
        prime = True
        break
    
    # For loop test commences
    else:
        i = 0
        for i in range(0, k):
            b = pow(a, pow(2,i)*m, n) 
            
            # if this statement is true, M-R failed
            if (b % n == n-1):
                if displayToconsole == True:
                    print()
                    print("Loop number:", i+1)
                    print("No, interger is likely prime")
                    print("Exit the loop")
                    print()
                composite = False
                break
            
            # increase the loop counter
            elif composite == True:
                if displayToconsole == True:
                    print()
                    print("Loop number:", i+1)
                    print('b : ', b)
                    print("No factor found yet")
                    print()
                i = i+1
    
    # Loop finishes, so candidate must be composite
    if composite == True:
        if displayToconsole == True:
            print()
            print("Iteration ", j)
            print("Yes, integer n is composite using ", a, "as a base.")
    
    elif composite == False:
        if displayToconsole == True:
            print()
            print("Iteration ", j)
            print("M-R Test failed: b = -1 (mod n)")
            print("Integer is likely prime.  Possible False positive")
            print()
       
    if displayToconsole == True:   
        print()
        print("---------- END OF ITERATION ", j, " ----------")
   
    # increase counter for main testIter loop  
    j = j+1
if displayToconsole == True: 
    print()
    print("---------- END OF TEST ----------")
    print()
    print()

# Endinng Test results printed to the console
if composite == True:
    print()
    print("M-R Test passed, with a total of" ,j-1 , "iterations.")
    print("Hence, n must be composite.")
    print()
if (composite == False) and (prime == False):
    print()
    print("M-R Test failed after running ", j, " times.") 
    if j > 100:
        print("n is considered to be a strong pseudoprime to all a's selected.")
    else:
        print("n is considered to be a weak pseudoprime to all a's selected.")
    print()
    
if (composite == False) and (prime == True):
    print()
    print("M-R Test failed after running ", j, " times.")
    print("On iteration ", j, " we had a^m = 1 (mod n)"),
    print("Integer n must be prime.") 
    print()