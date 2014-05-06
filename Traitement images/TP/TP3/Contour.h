#ifndef CONTOUR_H 
#define CONTOUR_H

#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/highgui/highgui.hpp"
#include <stdlib.h>
#include <stdio.h>

class Contour{
    public:
        Contour(cv::Mat img);
        cv::Mat contourDetect();
	private:
        cv::Mat image; 
        int threshold;
};
#endif
