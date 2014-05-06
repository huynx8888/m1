
#include <iostream>
#include <opencv2/opencv.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <stdlib.h>
#include <vector>

#include "Kmean.h"


int main(int argc, char ** argv){
    char *ptr;
    int clusters = strtol(argv[2], &ptr, 10);
    int attempts = strtol(argv[3], &ptr, 10);
    Kmean k = Kmean(argv[1], clusters, attempts, argv[4]);
    cv::Mat krgb = k.processRGB();
    cv::waitKey(0);
    cv::Mat khsv = k.processHSV();
    cv::waitKey(0);
    cv::Mat klab = k.processLAB();
    cv::waitKey(0);
    cv::imwrite("krgb.png", krgb);
    cv::imwrite("khsv.png", khsv);
    cv::imwrite("klab.png", klab);
    return 0;
}

Kmean::Kmean(char *imgName, int clust, int att, char *ref):Base(imgName, ref){
    clusters = clust;
    attempts = att;
}
/* 
cv::Mat Kmean::processRGB(){
    cv::Mat samples(image.cols * image.rows, 3, CV_32F);
    for( int y = 0; y < image.rows; y++ ){
        for( int x = 0; x < image.cols; x++ ){
            for( int z = 0; z < 3; z++){
                samples.at<float>(y + x*image.rows, z) = image.at<cv::Vec3b>(y,x)[z];
            }
        }
    }

    cv::Mat labels, centers;
    //    cv::kmeans(samples, clusters, labels, cv::TermCriteria(CV_TERMCRIT_ITER|CV_TERMCRIT_EPS, 10000, 0.0001), attempts, cv::KMEANS_PP_CENTERS, centers );
    cv::kmeans(samples, clusters, labels, 
                cv::TermCriteria(CV_TERMCRIT_ITER+CV_TERMCRIT_EPS, 10, 1.0), attempts, cv::KMEANS_PP_CENTERS, centers );

    cv::Mat dst( image.size(), image.type() );
    for( int y = 0; y < image.rows; y++ ){
        for( int x = 0; x < image.cols; x++ ){ 
            int cluster_idx = labels.at<int>(y + x*image.rows, 0);
            dst.at<cv::Vec3b>(y,x)[0] = centers.at<float>(cluster_idx, 0);
            dst.at<cv::Vec3b>(y,x)[1] = centers.at<float>(cluster_idx, 1);
            dst.at<cv::Vec3b>(y,x)[2] = centers.at<float>(cluster_idx, 2);
        }
    }
    
    cv::erode(dst, dst, cv::Mat(), cv::Point(-1,-1), 2);
    cv::dilate(dst, dst, cv::Mat(), cv::Point(-1,-1), 2);
    cv::erode(dst, dst, cv::Mat(), cv::Point(-1,-1), 2);
    cv::dilate(dst, dst, cv::Mat(), cv::Point(-1,-1), 2);

    cv::imshow("final_result", dst);
    return dst;
}
*/

cv::Mat Kmean::processRGB(){                                                                                                                                   
    return process(image);
}

cv::Mat Kmean::processHSV(){
    cv::Mat imageHSV;
    cv::cvtColor(image, imageHSV, CV_BGR2HSV); 
    return process(imageHSV);
}
cv::Mat Kmean::processLAB(){
    cv::Mat imageLAB;
    cv::cvtColor(image, imageLAB, CV_BGR2Lab); 
    return process(imageLAB);
}
cv::Mat Kmean::process(cv::Mat img){
    cv::Mat samples(img.cols * img.rows, 3, CV_32F);
    for( int y = 0; y < img.rows; y++ ){
        for( int x = 0; x < img.cols; x++ ){
            for( int z = 0; z < 3; z++){
                samples.at<float>(y + x*img.rows, z) = img.at<cv::Vec3b>(y,x)[z];
            }
        }
    }


    /*    cv::Mat samples(image.cols * image.rows, 5, CV_32F);
          for( int y = 0; y < image.rows; y++ ){
          for( int x = 0; x < image.cols; x++ ){
          samples.at<float>(y + x*image.rows, 0) = (float)x / image.cols;
          samples.at<float>(y + x*image.rows, 1) = (float)x / image.rows;
          for( int z = 2; z < 5; z++){
          samples.at<float>(y + x*image.rows, z) = image.at<cv::Vec3b>(y,x)[z];
          }
          }
          }
          */
    cv::Mat labels, centers;
    //    cv::kmeans(samples, clusters, labels, cv::TermCriteria(CV_TERMCRIT_ITER|CV_TERMCRIT_EPS, 10000, 0.0001), attempts, cv::KMEANS_PP_CENTERS, centers );
    cv::kmeans(samples, clusters, labels, cv::TermCriteria(CV_TERMCRIT_ITER+CV_TERMCRIT_EPS, 10, 1.0), attempts, cv::KMEANS_PP_CENTERS, centers );

    std::vector<cv::Mat> regions;
    for (int i = 0; i < clusters; i++){
        regions.push_back(cv::Mat::zeros(img.size(), CV_8U));
    }

    cv::Mat dst( img.size(), img.type() );
    for( int y = 0; y < img.rows; y++ ){
        for( int x = 0; x < img.cols; x++ ){ 
            int cluster_idx = labels.at<int>(y + x*img.rows, 0);
            dst.at<cv::Vec3b>(y,x)[0] = centers.at<float>(cluster_idx, 0);
            dst.at<cv::Vec3b>(y,x)[1] = centers.at<float>(cluster_idx, 1);
            dst.at<cv::Vec3b>(y,x)[2] = centers.at<float>(cluster_idx, 2);
            regions[cluster_idx].at<uchar>(y, x) = 1;
        }
    }
    
    compare(regions);
    cv::erode(dst, dst, cv::Mat(), cv::Point(-1,-1), 2);
    cv::dilate(dst, dst, cv::Mat(), cv::Point(-1,-1), 2);
    cv::erode(dst, dst, cv::Mat(), cv::Point(-1,-1), 2);
    cv::dilate(dst, dst, cv::Mat(), cv::Point(-1,-1), 2);

    cv::imshow("final_result", dst);
    return dst;
}

