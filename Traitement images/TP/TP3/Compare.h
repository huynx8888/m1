

#ifndef COMPARE_H 
#define COMPARE_H
 
#include <iostream>
#include <opencv2/opencv.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

#define PREFIX "imgCan/"

class Compare 
{
    public:
        Compare(char *file1, char * file2);
        void print();
	private:
        cv::Mat image1; 
        cv::Mat image2; 
        int calculPixels(cv::Mat image);
        int calculCorrectPixels();
        int detect;
        int reference;
        int correct;
        int failPositive;
        int failNegative;
        float p;
        float tfp;
        float tfn;
};
#endif
