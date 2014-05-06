#! /bin/sh
for f in images/*; do 
    for t in {0..80..10}; do
        echo $f $t; 
        ./canny $f $t
        ./laplacian $f $t
        ./prewitt $f $t
        ./sobel $f $t
    done
done
