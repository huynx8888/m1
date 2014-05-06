
#ifndef BASE_H 
#define BASE_H 
 
#include <iostream>
#include <vector>
#include <opencv2/opencv.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

class Base 
{
    public:
        Base(char *name, char *ref);
        virtual cv::Mat processRGB();
        virtual cv::Mat processHSV();
        virtual cv::Mat processLAB();
	protected:
        cv::Mat image; 
        std::vector<cv::Mat> segs;
        void compare(std::vector<cv::Mat> regions);
};
#endif
