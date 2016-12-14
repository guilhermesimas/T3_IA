#!/bin/bash
#Script to join a bunch o textfiles into a single one

cd part1-pos/pos1
mkdir out
COUNTER=0
for file in ./*.txt; do
	../testScript.sh $file out/$COUNTER.txt
	#rm $COUNTER.txt
	let COUNTER=COUNTER+1	
done
echo "DONE POS1"
let COUNTER=0
cd ../pos2
mkdir out
for file in ./*.txt; do
	../testScript.sh $file out/$COUNTER.txt
	#rm $COUNTER.txt
	let COUNTER=COUNTER+1
done
echo "DONE POS2"
let COUNTER=0
cd ../pos3
mkdir out
for file in ./*.txt; do
	../testScript.sh $file out/$COUNTER.txt
	#rm $COUNTER.txt
	let COUNTER=COUNTER+1
done
echo "DONE POS3"
let COUNTER=0
cd ../pos4
mkdir out
for file in ./*.txt; do
	../testScript.sh $file out/$COUNTER.txt
	#rm $COUNTER.txt
	let COUNTER=COUNTER+1
done
echo "DONE POS4"

