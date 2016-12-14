#!/bin/bash
#delgrp <wordsToBeDeleted> <fileIn> <fileOut>
#delgrp()
#{
#	if grep -q "^$1:" $2 ; then
#		grep -v "^$1:" $2 > tmp.txt
#		cp -f tmp.txt $3
#		#rm -f tmp.txt
#	else
#		echo "NOPE"	
#	fi
#}

cat $1 | tr '[:punct:]' ' ' | tr 'A-Z' 'a-z' >tmp
sed 's/\<the\>//g' tmp | sed 's/\<br\>//g' | sed 's/\<a\>//g'|sed 's/\<or\>//g'|sed 's/\<and\>//g'|
sed 's/\<is\>//g' |sed 's/\<are\>//g' |sed 's/\<i\>//g' |sed 's/\<he\>//g'|sed 's/\<at\>//g'|sed 's/\<them\>//g'  |sed 's/\<she\>//g' |sed 's/\<it\>//g'|sed 's/\<they\>//g' > $2
rm tmp
#delgrp ../../toDelete.txt out2.txt out3.txt
#tr '\n' ' ' <out3.txt > out4.txt
