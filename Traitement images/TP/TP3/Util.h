#include<string.h>
#include<stdlib.h>
#include<stdio.h>

#include <opencv2/opencv.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"


char *getName(char *prefix, char *name, int threshold){
    char *out = (char*) malloc(sizeof(char) * 100);
    memset(out, '\0', 100);
    char *point = strchr(name, '.');
    if(point != NULL){
        *point = '-';
    }
    out = strncpy(out, prefix, strlen(prefix));
    out = strncat(out, name, strlen(name));
    char t[4];
    snprintf(t, 4, "%d", threshold);
    out = strncat(out, t, strlen(t));
    out = strncat(out, ".jpg", strlen(".jpg"));
    return out;
}

cv::Mat myThreahold(cv::Mat src, int threshold){
    cv::Mat dst = cv::Mat(src.size(), src.type());
    for(int i = 0; i < src.cols; i++){
        for(int j = 0; j < src.rows; j++){
            if(src.at<uchar>(j,i) < threshold){
                dst.at<uchar>(j,i) = 255;
            }else{
                dst.at<uchar>(j,i) = 0;
            }
        }
    }
    
    return dst;
}
