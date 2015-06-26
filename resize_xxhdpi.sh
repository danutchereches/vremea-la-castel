#!/bin/sh

#convert -crop -resize 200x200 ./sample_0.jpg ./drawable-xhdpi/sample_0.png

DENSITY=xhdpi
SIZE=200


resize() {
    convert -define png:size=200x200 ./sample_$I.jpg -thumbnail ${SIZE}x${SIZE}^ -gravity center -extent ${SIZE}x${SIZE} ./drawable-$DENSITY/sample_$I.png
}


FILES=`find ./res/drawable-xxhdpi/ -type f -printf "%f\n"`

for f in $FILES
do
	echo -n "Resizing $f ."
	convert ./res/drawable-xxhdpi/$f -adaptive-resize 66.66% ./res/drawable-xhdpi/$f
	echo -n '.'
	convert ./res/drawable-xxhdpi/$f -adaptive-resize 50% ./res/drawable-hdpi/$f
	echo -n '.'
	convert ./res/drawable-xxhdpi/$f -adaptive-resize 33.33% ./res/drawable-mdpi/$f
	echo -n '.'
	convert ./res/drawable-xxhdpi/$f -adaptive-resize 25% ./res/drawable-ldpi/$f
	echo ' done.'
done
