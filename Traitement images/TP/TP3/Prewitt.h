#ifndef SOBEL_H 
#define SOBEL_H
 
#include <iostream>
#include <opencv2/opencv.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"


#define PREFIX "imgPre/"
class Prewitt 
{
    public:
        Prewitt(char *name);
        void get();
        cv::Mat getPrewitt();
        void filter(cv::Mat src, cv::Mat &dst, int direction);
        cv::Mat applyThreshold(int threshold);
        void save(int);
	private:
        cv::Mat image; 
        cv::Mat imagePrewitt;
        char * name;
        cv::Mat kernelX;
        cv::Mat kernelY;
        int static const DDEPTH = CV_16S;
        int static const DELTA = 0;
        int static const KERNEL_SIZE = 3;
        int static const SCALE = 1;
};
#endif
