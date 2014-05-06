#include <iostream>
#include <opencv2/opencv.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <stdlib.h>
#include <vector>

#include "WaterShed.h"


int main(int argc, char ** argv){
    char *ptr;
    int thres = strtol(argv[2], &ptr, 10);
    WaterShed ws = WaterShed(argv[1], thres, argv[3]);
    cv::Mat wrgb = ws.processRGB();
    cv::waitKey(0);
    cv::Mat whsv = ws.processHSV();
    cv::waitKey(0);
    cv::Mat wlab = ws.processLAB();
    cv::waitKey(0);
    cv::imwrite("wrgb.png", wrgb);
    cv::imwrite("whsv.png", whsv);
    cv::imwrite("wlab.png", wlab);
    return 0;
}

WaterShed::WaterShed(char *imgName, int thres, char *ref):Base(imgName, ref){
    threshold = thres;
}


/*cv::Mat WaterShed::processRGB(){
    cv::Mat binary;
    cv::cvtColor(image, binary, CV_BGR2GRAY);
    cv::threshold(binary, binary, threshold, 255, cv::THRESH_BINARY);

    // Perform the distance transform algorithm
    cv::Mat dist;
    cv::distanceTransform(binary, dist, CV_DIST_L2, 3);
    // Normalise dist
    cv::normalize(dist, dist, 0, 1., cv::NORM_MINMAX);

    // Threshold to obtain the peaks
    // This will be the markers for the foreground objects
    cv::threshold(dist, dist, .2, 1., CV_THRESH_BINARY);


    cv::Mat dist_8u;
    dist.convertTo(dist_8u, CV_8U);

    // Find total markers
    std::vector<std::vector<cv::Point> > contours;

    cv::findContours(dist_8u, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);
    int ncomp = contours.size();

    // Create the marker image
    cv::Mat markers = cv::Mat::zeros(dist.size(), CV_32SC1);

        // Draw the foreground markers
    for (int i = 0; i < ncomp; i++){
        cv::drawContours(markers, contours, i, cv::Scalar::all(i+1), -1);
    }

    // Draw the background marker
    cv::circle(markers, cv::Point(5,5), 3, CV_RGB(255,255,255), -1);

    //initialise random colors
    cv::RNG rng( 0xFFFFFFFF );

    std::vector <cv::Vec3b> colors;
    for (int i = 0; i < ncomp; i++){
        int c1 = rng.uniform(0,255);
        int c2 = rng.uniform(0,255);
        int c3 = rng.uniform(0,255);

        colors.push_back(cv::Vec3b(c1, c2, c3));
    }

    // Perform the watershed algorithm
    cv::watershed(image, markers);

    // Create the result image
    cv::Mat dst = cv::Mat::zeros(markers.size(), CV_8UC3);

    for (int i = 0; i < markers.rows; i++){
        for (int j = 0; j < markers.cols; j++){
            int index = markers.at<int>(i,j);
            if (index > 0 && index <= ncomp)
                dst.at<cv::Vec3b>(i,j) = colors[index-1];
            else
                dst.at<cv::Vec3b>(i,j) = cv::Vec3b(0,0,0);
        }
    }

    cv::imshow("final_result", dst);
    return dst;
}
*/

cv::Mat WaterShed::processRGB(){
    return process(image);
}
cv::Mat WaterShed::processHSV(){
    cv::Mat imageHSV;
    cv::cvtColor(image, imageHSV, CV_BGR2HSV); 
    return process(imageHSV);
}
cv::Mat WaterShed::processLAB(){
    cv::Mat imageLAB;
    cv::cvtColor(image, imageLAB, CV_BGR2Lab); 
    return process(imageLAB);
}
cv::Mat WaterShed::process(cv::Mat img){
    cv::Mat binary;
    cv::cvtColor(image, binary, CV_BGR2GRAY);
    cv::threshold(binary, binary, threshold, 255, cv::THRESH_BINARY);
    // Perform the distance transform algorithm
    cv::Mat dist;
    cv::distanceTransform(binary, dist, CV_DIST_L2, 3);
    // Normalise dist
    cv::normalize(dist, dist, 0, 1., cv::NORM_MINMAX);

    // Threshold to obtain the peaks
    // This will be the markers for the foreground objects
    cv::threshold(dist, dist, .4, 1., CV_THRESH_BINARY);

    cv::Mat dist_8u;
    dist.convertTo(dist_8u, CV_8U);

    // Find total markers
    std::vector<std::vector<cv::Point> > contours;

    cv::findContours(dist_8u, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);
    int ncomp = contours.size();

    // Create the marker image
    cv::Mat markers = cv::Mat::zeros(dist.size(), CV_32SC1);

        // Draw the foreground markers
    for (int i = 0; i < ncomp; i++){
        cv::drawContours(markers, contours, i, cv::Scalar::all(i+1), -1);
    }

    // Draw the background marker
    cv::circle(markers, cv::Point(5,5), 3, CV_RGB(255,255,255), -1);

    //initialise random colors
    cv::RNG rng( 0xFFFFFFFF );

    std::vector <cv::Vec3b> colors;
    std::vector<cv::Mat> regions;
    for (int i = 0; i < ncomp; i++){
        int c1 = rng.uniform(0,255);
        int c2 = rng.uniform(0,255);
        int c3 = rng.uniform(0,255);

        colors.push_back(cv::Vec3b(c1, c2, c3));
        regions.push_back(cv::Mat::zeros(markers.size(), CV_8U));
    }
    //background
    regions.push_back(cv::Mat::zeros(markers.size(), CV_8U));

    // Perform the watershed algorithm
    cv::watershed(img, markers);

    // Create the result image
    cv::Mat dst = cv::Mat::zeros(markers.size(), CV_8UC3);

    for (int i = 0; i < markers.rows; i++){
        for (int j = 0; j < markers.cols; j++){
            int index = markers.at<int>(i,j);
            if (index > 0 && index <= ncomp){
                dst.at<cv::Vec3b>(i,j) = colors[index-1];
                regions[index -1 ].at<uchar>(i, j) = 1;
            }else{
                dst.at<cv::Vec3b>(i,j) = cv::Vec3b(0,0,0);
                regions[ncomp].at<uchar>(i, j) = 1;
            }
        }
    }

    compare(regions);
    cv::imshow("final_result", dst);
    return dst;
}
