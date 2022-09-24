# bully-algorithm-simulation

This is a simulation of the bully algorithm for leader election in a distributed system.

## How to run as simple console application

1. Clone the repository
2. Run `.\clean.sh` to clean the created files for the previous run if exists
3. Run `Main.java` in the src folder to start the GUI (not completed yet)
4. the output will be separate files for each node process
5. you can concatenate all the files to see the output of the whole system with `cat Node_*.txt >> normal_results.txt`
6. The output will be in the root directory of the repository in the file `normal_results.txt`
7. After closing the GUI, the process will be terminated to avoid dangling processes

