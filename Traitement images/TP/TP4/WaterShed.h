#ifndef WATERSHED_H 
#define WATERSHED_H
 
#include <iostream>
#include <opencv2/opencv.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <vector> 

#include "Base.h"

class WaterShed : public Base 
{
    public:
        WaterShed(char *name, int threshold, char *ref);
        void setMarkers(const cv::Mat& markerImage);
        virtual cv::Mat processRGB();
        virtual cv::Mat processHSV();
        virtual cv::Mat processLAB();
	private:
//        cv::Mat markers;
        int threshold;
        float threshold2;
        cv::Mat process(cv::Mat img);
};
#endif
