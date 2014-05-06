#ifndef HARRIS_H 
#define HARRIS_H
 
#include <iostream>
#include <opencv2/opencv.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"


class Harris 
{
    public:
        Harris(char *name);
        void get();
        cv::Mat applyThreshold(int);
        cv::Mat getHarris();
	private:
        cv::Mat image; 
        cv::Mat imageHarris;
        int static const DDEPTH = CV_16S;
        int static const DELTA = 0;
        int static const BLOCK_SIZE = 2;
        int static const APERTURE = 3;
        int static const SCALE = 1;
        int static const K = 0.04;
};
#endif
