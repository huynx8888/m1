/*
 * =====================================================================================
 *
 *       Filename:  Sobel.c
 *
 *    Description:  
 *
 *        Version:  1.0
 *        Created:  04/08/2013 04:44:12 PM
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  NGUYEN Van Tho (), thonv133@gmail.com
 *   Organization:  
 *
 * =====================================================================================
*/
#include <iostream>
#include <opencv2/opencv.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <stdlib.h>

#include "Prewitt.h"
#include "Util.h"

int const maxValue = 255;
int thresholdValue = 0;
char * const windowName = "Prewitt";

int main(int argc, char ** argv){
    Prewitt prewitt = Prewitt(argv[1]);
    prewitt.get();
    char *ptr;
    thresholdValue = strtol(argv[2], &ptr, 10);

    imshow(windowName, prewitt.getPrewitt());
//    cv::waitKey(0);
//    prewitt.applyThreshold(thresholdValue);
    prewitt.save(thresholdValue);
//    cv::waitKey(0);
}

Prewitt::Prewitt(char *imgName){
    image = cv::imread(imgName, CV_LOAD_IMAGE_GRAYSCALE);
    if( image.empty() )
    {
        std::cout<<"Cannot read image file: "<< imgName<<std::endl;
        exit(1);
    }
    //initialiser les kernel
    kernelX = cv::Mat(KERNEL_SIZE, KERNEL_SIZE, CV_32F);
    kernelY = cv::Mat(KERNEL_SIZE, KERNEL_SIZE, CV_32F);
    //Pour kernel = 3
    for(int j = 0; j < KERNEL_SIZE; j++){
        kernelX.at<float>(j, 0) = -1.0;
        kernelX.at<float>(j, 1) = 0.0;
        kernelX.at<float>(j, 2) = 1.0;
        kernelY.at<float>(0, j) = -1.0;
        kernelY.at<float>(1, j) = 0.0;
        kernelY.at<float>(2, j) = 1.0;
    }
    name = (char *)malloc(80*sizeof(char));
    memset(name, '\0', 80);
    name = strncpy(name, imgName, strlen(imgName));
}

void Prewitt::get(){
    //cvtColor(image, image, CV_RGB2GRAY);
    cv::GaussianBlur( image, image, cv::Size(3,3), 0, 0, cv::BORDER_DEFAULT );
    cv::Mat gradX, gradY;
    
    filter( image , gradX, 0 );
    cv::convertScaleAbs( gradX, gradX );
    filter( image , gradY, 1 );
    cv::convertScaleAbs( gradY, gradY );
    cv::addWeighted( gradX, 0.5, gradY, 0.5, 0, imagePrewitt);
}
//0 -> x
//1 -> y
void Prewitt::filter(cv::Mat src, cv::Mat & dst, int grad){
    cv::Point anchor = cv::Point( -1, -1 );
    if(grad == 0){
        cv::filter2D(src, dst, DDEPTH, kernelX, anchor, DELTA, cv::BORDER_DEFAULT ); 
    }else{
        cv::filter2D(src, dst, DDEPTH, kernelY, anchor, DELTA, cv::BORDER_DEFAULT ); 
    }
}

cv::Mat Prewitt::getPrewitt(){
    return imagePrewitt;
}

void Prewitt::save(int threshold){
    if(threshold == 0){
        cv::imwrite(getName(PREFIX, name, threshold), imagePrewitt);
    }else{
        cv::Mat img = applyThreshold(threshold);
        cv::imwrite(getName(PREFIX, name, threshold), img);
    }
}


cv::Mat Prewitt::applyThreshold(int thresholdValue){
    cv::Mat im;
    //    cv::threshold(imagePrewitt, im, thresholdValue, maxValue, CV_THRESH_OTSU+CV_THRESH_BINARY);                                 
    //cv::threshold(imagePrewitt, im, thresholdValue, maxValue, CV_THRESH_BINARY);  
    im = myThreahold(imagePrewitt, thresholdValue);
    imshow( windowName, im);
    return im;
}
