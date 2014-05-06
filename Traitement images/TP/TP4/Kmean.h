
#ifndef KMEAN_H 
#define KMEAN_H
 
#include <iostream>
#include <opencv2/opencv.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

#include "Base.h"

class Kmean : public Base 
{
    public:
        Kmean(char *name, int clusters, int attempts, char *ref);
        void setMarkers(const cv::Mat& markerImage);
        virtual cv::Mat processRGB();
        virtual cv::Mat processHSV();
        virtual cv::Mat processLAB();
	private:
//        cv::Mat markers;
        int clusters;
        int attempts;
        cv::Mat process(cv::Mat img);
};
#endif
