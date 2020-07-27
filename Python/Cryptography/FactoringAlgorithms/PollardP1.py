"""
Pollard P-1 Fractoring Algorithm

Las Vegas Style factoring algorithm implemented in python.  This algorithm may 
fail to find a factor, but if a factor is found its accuracy is 100% 
(no possibile false positives).  
User freindly version that does not prints steps to the prompt.  For more 
detailed information displayed change displayPrompt = True

Basic Outline:
    input: n, the integer you want factored
    output: promt displays the factor found, or if no factor was found
            additional information displayed if displayPrompt = True
    
    a = base
    for j = 2 to Bound B
        do a = a^j (mod n)
        d = gcd(a-1, n)
        if 1 < d < n
            d is a factor of n
        else continue loop
    loop ends, return failure

To Do:
    Prove test works mathematically in comments
    Create desktop executable
    
@author: Tom Markey
"""
import math
# INITIAL VALUES
# n = integer you want to be factored 
# B =  bound for the number of itterations for the algorithm
# a = base for the test
# displauSteps is a boolean to display more detail printed to the prompt if True
n = 15770708441
B = 175
a = 2
displayPrompt = False


# for final printing purposes
factN = 0, print()

# Print the intital values for display
if displayPrompt == True:
    print()
    print("---- Initial Values -----")
    print("n = ", n)
    print("B = ", B)
    print("a = ", a)
    print()
    print()

# for loop initialized
if displayPrompt == True:
    print("----- STARTING TEST -----")
j = 2
factorFound = False

for j in range(2, B+1):
    # a = a^j (mod n)
    a = pow(a, j, n)
    
    # d = gcd(a-1, n)
    d = math.gcd(a-1, n)
    if displayPrompt == True: 
        print()
        print("----------")
        print("Iteration number ", j-1)
        print()
        print("Values: ")
        print("a = ", a)
        print("d = ", d)
    
    # check if  1 < d < n
    if (1 < d) & (d < n):
        if displayPrompt == True: 
            print()
            print(d, " is a factor of ", n, ".  Factor found!")
            print()
        factorFound = True
        factN = d
        break
    
    # if check was fasle, so no factor this iteration
if displayPrompt == True:
       print()
       print("No factor found this iteration")
       print()
if displayPrompt == True:
    print("----- END OF TEST -----")
    print()
    print()

if factorFound == True:
    print("Test has found that", factN,"is a factor of", n)
    print(n, " is composite.")
else:
    print("Test has completed without finding a factor for", n)
    print(n, " might be a prime. Increase B for further testing.")


