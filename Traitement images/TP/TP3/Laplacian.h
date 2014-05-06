
#ifndef LAPLACIAN_H 
#define LAPLACIAN_H
 
#include <iostream>
#include <opencv2/opencv.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

#define PREFIX "imgLap/"
class Laplacian 
{
    public:
        Laplacian (char *name);
        void get();
        cv::Mat applyThreshold(int);
        cv::Mat getLaplacian();
        void save(int);
	private:
        cv::Mat image; 
        cv::Mat imageLaplacian;
        char *name;
        int static const DDEPTH = CV_16S;
        int static const DELTA = 0;
        int static const KERNEL_SIZE = 3;
        int static const SCALE = 1;
};
#endif
