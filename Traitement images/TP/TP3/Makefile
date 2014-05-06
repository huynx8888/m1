CFLAGS = `pkg-config --cflags opencv` -Wall -g
LIBS = `pkg-config --libs opencv` -lm
All:
	g++ $(CFLAGS) $(LIBS) Sobel.cpp Sobel.h Util.h -o sobel
	g++ $(CFLAGS) $(LIBS) Laplacian.cpp Laplacian.h Util.h -o laplacian
	g++ $(CFLAGS) $(LIBS) Canny.cpp Canny.h Util.h -o canny 
	g++ $(CFLAGS) $(LIBS) Prewitt.cpp Prewitt.h Util.h -o prewitt 
	g++ $(CFLAGS) $(LIBS) Compare.cpp Compare.h -o compare 
sobel:
	g++ $(CFLAGS) $(LIBS) Sobel.cpp Sobel.h -o sobel
laplace:
	g++ $(CFLAGS) $(LIBS) Laplacian.cpp Laplacian.h -o laplacian 
canny:
	g++ $(CFLAGS) $(LIBS) Canny.cpp Canny.h -o canny 
harris:
	g++ $(CFLAGS) $(LIBS) Harris.cpp Harris.h -o harris 
prewitt:
	g++ $(CFLAGS) $(LIBS) Prewitt.cpp Prewitt.h Util.h -o prewitt 
compare:
	g++ $(CFLAGS) $(LIBS) Compare.cpp Compare.h -o compare 
clean:
	rm -f canny compare sobel laplacian prewitt
