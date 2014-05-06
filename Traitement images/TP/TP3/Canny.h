
#ifndef CANNY_H 
#define CANNY_H
 
#include <iostream>
#include <opencv2/opencv.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

#define PREFIX "imgCan/"

class Canny 
{
    public:
        Canny(char *name);
        void get(int);
        cv::Mat applyThreshold(int);
        cv::Mat getCanny();
        void save(int);
	private:
        cv::Mat image; 
        cv::Mat imageCanny;
        char *name;
        int static const RATIO = 3;
        int static const KERNEL_SIZE = 3;
};
#endif
