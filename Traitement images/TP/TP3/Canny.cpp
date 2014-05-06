/*
 * =====================================================================================
 *
 *       Filename:  Canny.cpp
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

#include "Canny.h"
#include "Util.h"

int const maxValue = 255;
int lowThreashold= 0;
char * const windowName = "Canny";

int main(int argc, char ** argv){
    Canny canny = Canny(argv[1]);
    char *ptr;
    lowThreashold= strtol(argv[2], &ptr, 10);
    canny.get(lowThreashold);

    imshow(windowName, canny.getCanny());
    canny.save(lowThreashold);
    //cv::waitKey(0);
}

Canny::Canny(char *imgName){
    image = cv::imread(imgName, CV_LOAD_IMAGE_GRAYSCALE);
    if( image.empty() )
    {
        std::cout<<"Cannot read image file: "<< imgName<<std::endl;
        exit(1);
    }
    imageCanny.create(image.size(), image.type());
    name = (char *)malloc(80*sizeof(char));
    memset(name, '\0', 80);
std::cout<<imgName<<std::endl;
    name = strncpy(name, imgName, strlen(imgName));
}

void Canny::get(int lowThreashold){
    //blur image
    cv::Mat blurred;
    //cv::blur( image, blurred, cv::Size(3,3) );
    cv::GaussianBlur( image, blurred, cv::Size(3,3), 0, 0, cv::BORDER_DEFAULT);
    //cvtColor(image, image, CV_RGB2GRAY);
    std::cout<<"low" << lowThreashold<<std::endl;
    cv::Canny(blurred, blurred, lowThreashold, lowThreashold*RATIO, KERNEL_SIZE); 
    imageCanny = cv::Scalar::all(0);
    image.copyTo( imageCanny, blurred);
    //threahold image
    //cv::threshold(imageCanny, imageCanny, lowThreashold, maxValue, CV_THRESH_BINARY);
    imageCanny = myThreahold(imageCanny, lowThreashold);
}

cv::Mat Canny::getCanny(){
    return imageCanny;

}
void Canny::save(int threshold){
    char *path = getName(PREFIX, name, threshold);
std::cout<<path<<std::endl;
    cv::imwrite(path, imageCanny);
    free(path);
}
