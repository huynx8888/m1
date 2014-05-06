CFLAGS = `pkg-config --cflags opencv` -Wall -g
LIBS = `pkg-config --libs opencv` -lfftw3 -lm
All:
	g++ $(CFLAGS) $(LIBS) tp2.cpp tp2.h main.cpp -o tp2 
#% : %.cpp
#	g++ $(CFLAGS) $(LIBS) -o $@ $<
