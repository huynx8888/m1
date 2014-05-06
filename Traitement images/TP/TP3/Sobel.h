#ifndef SOBEL_H 
#define SOBEL_H
 
#include <iostream>
#include <opencv2/opencv.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

#define PREFIX "imgSo/"
class Sobel 
{
    public:
        Sobel (char *name);
        void get();
        cv::Mat applyThreshold(int);
        cv::Mat getSobel();
        void save(int);
	private:
        cv::Mat image; 
        char * name;
        cv::Mat imageSobel;
        int static const DDEPTH = CV_16S;
        int static const DELTA = 0;
        int static const KERNEL_SIZE = 3;
        int static const SCALE = 1;
};
#endif
