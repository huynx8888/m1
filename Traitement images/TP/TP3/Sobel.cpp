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

#include "Sobel.h"
#include "Util.h"

int const maxValue = 255;
int thresholdValue = 0;
char * const windowName = "Sobel";

int main(int argc, char ** argv){
    Sobel sobel = Sobel(argv[1]);
    sobel.get();
    char *ptr;
    thresholdValue = strtol(argv[2], &ptr, 10);

    /*  imgSobel = sobel.getSobel();
        cv::createTrackbar( "Value",
        windowName, &thresholdValue,
        maxValue, applyThreshold);
        applyThreshold(0,0);
        while(true)
        {
        int c;
        c = cv::waitKey( 20 );
        if( (char)c == 27 ){
            break; 
        }
    }
 */   
    imshow(windowName, sobel.getSobel());
   // cv::waitKey(0);
    //sobel.applyThreshold(thresholdValue);
    sobel.save(thresholdValue);
    //cv::waitKey(0);
}

Sobel::Sobel(char *imgName){
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

void Sobel::get(){
    //cvtColor(image, image, CV_RGB2GRAY);
    cv::GaussianBlur( image, image, cv::Size(3,3), 0, 0, cv::BORDER_DEFAULT );
    cv::Mat gradX, gradY;
    cv::Sobel( image , gradX, DDEPTH, 1, 0, 3, SCALE, DELTA, cv::BORDER_DEFAULT );
    cv::convertScaleAbs( gradX, gradX );
    cv::Sobel( image , gradY, DDEPTH, 0, 1, 3, SCALE, DELTA, cv::BORDER_DEFAULT );
    cv::convertScaleAbs( gradY, gradY );
    cv::addWeighted( gradX, 0.5, gradY, 0.5, 0, imageSobel);
}

cv::Mat Sobel::getSobel(){
    return imageSobel;
}

cv::Mat Sobel::applyThreshold(int thresholdValue){
    cv::Mat im;
    im = myThreahold(imageSobel, thresholdValue);
    //cv::threshold(imageSobel, im, thresholdValue, maxValue, CV_THRESH_BINARY);
    return im;
}
void Sobel::save(int threshold){
    if(threshold == 0){
        cv::imwrite(getName(PREFIX, name, threshold), imageSobel);
    }else{
        cv::Mat img = applyThreshold(threshold);
        std::cout<<getName(PREFIX, name, threshold);
        cv::imwrite(getName(PREFIX, name, threshold), img);
    }
}
