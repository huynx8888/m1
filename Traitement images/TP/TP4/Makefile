CFLAGS = `pkg-config --cflags opencv` -Wall -g
LIBS = `pkg-config --libs opencv` -lm
All:
	g++ $(CFLAGS) $(LIBS) Base.cpp Base.h WaterShed.h WaterShed.cpp Seg.h -o watershed 
	g++ $(CFLAGS) $(LIBS) Base.cpp Base.h Kmean.h Kmean.cpp Seg.h -o kmeans 
watershed:
	g++ $(CFLAGS) $(LIBS) Base.cpp Base.h WaterShed.h WaterShed.cpp Seg.h -o watershed 
kmeans:
	g++ $(CFLAGS) $(LIBS) Base.cpp Base.h Kmean.h Kmean.cpp Seg.h -o kmeans 
seg:
	g++  $(CFLAGS) $(LIBS) SegToPng.cpp -o seg2png
clean:
	rm -f watershed kmeans seg2png 
