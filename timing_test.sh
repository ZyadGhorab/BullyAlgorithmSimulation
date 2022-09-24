# Timing test
./clean.sh
java -jar Bully.jar 6 5005 Normal 100 config1.txt &
java -jar Bully.jar 5 5004 Timing 100 config1.txt &
java -jar Bully.jar 4 5003 Normal 100 config1.txt &
java -jar Bully.jar 3 5002 Normal 100 config1.txt Initiator &
java -jar Bully.jar 2 5001 Normal 100 config1.txt &
java -jar Bully.jar 1 5000 Normal 100 config1.txt &
sleep 15
cat Node_*.txt >> timing_results.txt
