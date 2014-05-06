/*
 * =====================================================================================
 *
 *       Filename: Laplacian.cpp 
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

#include "Laplacian.h"
#include "Util.h"

int const maxValue = 255;
int thresholdValue = 0;
char * const windowName = "Laplacien";

int main(int argc, char ** argv){
    Laplacian laplacian = Laplacian(argv[1]);
    laplacian.get();
    char *ptr;
    thresholdValue = strtol(argv[2], &ptr, 10);

    imshow(windowName, laplacian.getLaplacian());
    //cv::waitKey(0);
    laplacian.save(thresholdValue);
//        laplacian.applyThreshold(thresholdVaLue);
//    cv::waitKey(0);
}

Laplacian::Laplacian(char *imgName){
    image = cv::imread(imgName, CV_LOAD_IMAGE_GRAYSCALE);
    if( image.empty() )
    {
        std::cout<<"Cannot read image file: "<< imgName<<std::endl;
        exit(1);
    }
    name = (char *)malloc(80*sizeof(char));
    memset(name, '\0', 80);
    name = strncpy(name, imgName, strlen(imgName));
}

void Laplacian::get(){
    //cvtColor(image, image, CV_RGB2GRAY);
    cv::GaussianBlur( image, image, cv::Size(3,3), 0, 0, cv::BORDER_DEFAULT );
    cv::Laplacian( image, imageLaplacian, DDEPTH, KERNEL_SIZE, SCALE, DELTA, cv::BORDER_DEFAULT );
    cv::convertScaleAbs( imageLaplacian, imageLaplacian );
}

cv::Mat Laplacian::getLaplacian(){
    return imageLaplacian;
}
void Laplacian::save(int threshold){
    if(threshold == 0){
        cv::imwrite(getName(PREFIX, name, threshold), imageLaplacian);
    }else{
        cv::Mat img = applyThreshold(threshold);
        std::cout<<getName(PREFIX, name, threshold);
        cv::imwrite(getName(PREFIX, name, threshold), img);
    }
}

cv::Mat Laplacian::applyThreshold(int thresholdValue){
    cv::Mat im;
    //cv::threshold(imageLaplacian, im, thresholdValue, maxValue, CV_THRESH_BINARY);
    im = myThreahold(imageLaplacian, thresholdValue);
    imshow( windowName, im);
    return im;
}

