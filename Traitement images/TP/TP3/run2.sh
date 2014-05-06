#! /bin/sh
for f in images/*; do 
    #threshold = 0 10 .. 200
        ./canny $f 0
        ./laplacian $f 0
        ./prewitt $f 0 
        ./sobel $f 0 
done
