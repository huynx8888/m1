#include <iostream>
#include <vector>
#include <opencv2/opencv.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include <stdlib.h>

#include "Base.h"
#include "Seg.h"



Base::Base(char *imgName, char *ref){
    image = cv::imread(imgName, CV_LOAD_IMAGE_UNCHANGED);
    if( image.empty() )
    {
        std::cout<<"Cannot read image file: "<< imgName<<std::endl;
        exit(1);
    }
    segs = getSeg(ref);
}

void Base::compare(std::vector<cv::Mat> regions){

/*      for(std::vector<cv::Mat>::iterator r = regions.begin(); r != regions.end(); ++r) {
        std::cout<<"M "<<++i<<",";
        cv::Mat reg = *r;
cv::imshow("xxx", reg); 
cv::waitKey(0);
        for(std::vector<cv::Mat>::iterator s = segs.begin(); s != regions.end(); ++s) {
            int n = 0;
            std::cout<<++j<<",";
            cv::Mat seg = *s;
            for (int x = 0; x < reg.rows; x++){
                for (int y = 0; y < reg.cols; y++){
                    if(reg.at<uchar>(x, y) == 1 && seg.at<uchar>(x, y) == 1){
                        n++;
                    }
                }
            }
            std::cout<<n<<std::endl;
        }
    }
*/
    std::cout<<"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"<<std::endl;
    for(size_t i = 0; i < regions.size(); ++i){
        std::cout<<"---------------------------------------------------------------------------------"<<std::endl;
        std::cout<<"M "<<i<<",";
        for(size_t j = 0; j < segs.size(); ++ j) {
            int n = 0;
            for (int x = 0; x < regions[i].rows; x++){
                for (int y = 0; y < regions[i].cols; y++){
                    if(regions[i].at<uchar>(x, y) == 1 && segs[j].at<uchar>(x, y) == 1){
                        n++;
                    }
                }
            }
            std::cout<<n<<" & ";
        }
        std::cout<<std::endl;
    }
}

    cv::Mat Base::processRGB(){}
    cv::Mat Base::processHSV(){}
    cv::Mat Base::processLAB(){}

