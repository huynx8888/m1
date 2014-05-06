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

#include "Harris.h"

int const maxValue = 255;
int thresholdValue = 0;
char * const windowName = "Harris";

int main(int argc, char ** argv){
    Harris harris = Harris(argv[1]);
    harris.get();
    imshow(windowName, harris.getHarris());
    cv::waitKey(0);
}

Harris::Harris(char *imgName){
    image = cv::imread(imgName, CV_LOAD_IMAGE_GRAYSCALE);
    if( image.empty() )
    {
        std::cout<<"Cannot read image file: "<< imgName<<std::endl;
        exit(1);
    }
}

void Harris::get(){
    cv::cornerHarris( image, imageHarris, BLOCK_SIZE, APERTURE, K, cv::BORDER_DEFAULT );
    cv::normalize( imageHarris, imageHarris, 0, 255, cv::NORM_MINMAX, CV_32FC1, cv::Mat() );
    convertScaleAbs( imageHarris, imageHarris);
}

cv::Mat Harris::getHarris(){
    return imageHarris;
}
