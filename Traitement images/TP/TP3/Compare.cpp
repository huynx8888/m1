/*
 * =====================================================================================
 *
 *       Filename:  Compare.cpp
 *
 *    Description:  
 *
 *        Version:  1.0
 *        Created:  04/21/2013 11:57:31 AM
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  NGUYEN Van Tho (), thonv133@gmail.com
 *   Organization:  
 *
 * =====================================================================================
 */


#include <iostream>
#include <iomanip>
#include <opencv2/opencv.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <stdlib.h>
#include <stdio.h>

#include "Compare.h"

int const maxValue = 0;
char * const windowName = "Test";

int main(int argc, char ** argv){
    //std::cout<<argv[1]<<"   "<<argv[2]<<std::endl;
    Compare compare = Compare(argv[1], argv[2]);
}

Compare::Compare(char *file1, char *file2){
    image1 = cv::imread(file1, CV_LOAD_IMAGE_UNCHANGED);
    if( image1.empty() )
    {
        std::cout<<"Cannot read image file: "<< file1 <<std::endl;
        exit(1);
    }
    image2 = cv::imread(file2, CV_LOAD_IMAGE_UNCHANGED);
    if( image2.empty() )
    {
        std::cout<<"Cannot read image file: "<< file2 <<std::endl;
        exit(1);
    }
//    cv::threshold(image2, image2, 0, maxValue, CV_THRESH_BINARY_INV);

    //imshow(windowName, image2);
    //cv::waitKey(0);
    //imshow(windowName, image1);
    //cv::waitKey(0);
    detect = calculPixels(image1);
    reference = calculPixels(image2);
    correct = calculCorrectPixels();
//    failPositive = detect - correct;
//    failNegative = reference - correct;
    float t = correct + failPositive + failNegative;
    p = correct / t;
    tfp = failPositive / t;
    tfn = failNegative / t;
    print();
}


int Compare::calculPixels(cv::Mat image){
//std::cout<<"---------------------------"<<std::endl;
    int n = 0;
    for(int i = 0; i < image.cols; i++){
        for(int j = 0; j < image.rows; j++){
//printf("%d  ", image.at<uchar>(j,i));
//std::cout<<image.at<uchar>(j,i)<<"   ";
           if(image.at<uchar>(j,i) == 0){
               n++;
           }
        }
    }
    return n;
}

int Compare::calculCorrectPixels(){
    failPositive = detect;
    failNegative = reference;
    int n = 0;
    cv::Mat im2;
    cv::copyMakeBorder(image2, im2, 1, 1, 1, 1, cv::BORDER_CONSTANT, cv::Scalar::all(255));

    for(int i = 0; i < image1.cols; i++){
        for(int j = 0; j < image1.rows; j++){
            if(image1.at<uchar>(j,i) == 0){
                if(im2.at<uchar>(j + 1,i + 1) == 0){
                    failNegative--; 
                    failPositive--;
                    n++;
                    im2.at<uchar>(j + 1,i + 1) = 255;
                }else{
                    int found = 0;
                    for(int k = i; k <= i + 2; k++){
                        for(int l = j; l <= j + 2; l++){
                            if(im2.at<uchar>(l,k) == 0){
                                failNegative--; 
                                failPositive--;
                                im2.at<uchar>(l,k) = 255;
                                n++;
                                found = 1;
                                break;
                            }
                        }
                        if(found){
                            break;
                        }
                    }
                }
            }
        }
    }
    return n;
}


void Compare::print(){
//    std::cout<<"-----------------------------------------"<<std::endl;

//    std::cout<<"Contours détectés: "<<detect<<std::endl;
//    std::cout<<"Contours détectés: "<<detect<<std::endl;
//    std::cout<<"Contours référence: "<<reference<<std::endl;
//    std::cout<<"Performance: "<<p<<std::endl;
//    std::cout<<"Taux de Faux Positifs: "<<tfp<<std::endl;
//    std::cout<<"Taux de Faux Négatifs: "<<tfn<<std::endl;
//    std::cout<<"Pour rappor: " << std::fixed << std::setprecision( 3 ) << p << " - " << tfp << " - "<< tfn <<std::endl;
    std::cout<<std::fixed << std::setprecision( 3 ) << p << " - " << tfp << " - "<< tfn <<" & ";

}
