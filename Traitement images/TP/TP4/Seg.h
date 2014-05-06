#ifndef SEG_H
#define SEG_H

#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <vector>

#include <opencv2/opencv.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

std::vector<cv::Mat> getSeg(char *file){
    std::string sLine = "";
    std::string fileName = "";
    int segment = 0;
    int width = 0;
    int height = 0;
    int gray = 0;

    std::ifstream infile;
    infile.open(file);

    //parse header
    while (!infile.eof())
    {
        getline(infile, sLine);
        int found = -1;
        if( (found = sLine.find("image")) != std::string::npos){
            fileName = sLine.substr(found + 6, sLine.length() - found - 6); 
        }else if ( (found = sLine.find("width")) != std::string::npos){
            std::istringstream ( sLine.substr(found + 5, sLine.length() - found - 5) ) >> width; 
        }else if ( (found = sLine.find("height")) != std::string::npos){
            std::istringstream ( sLine.substr(found + 6, sLine.length() - found - 6) ) >> height; 
        }else if ((found = sLine.find("segments")) != std::string::npos){
            std::string s = sLine.substr(found + 8, sLine.length() - found - 8);
            std::istringstream ( s ) >> segment; 
        }else if ((found = sLine.find("gray")) != std::string::npos){
            std::istringstream ( sLine.substr(found + 4, sLine.length() - found - 4) ) >> gray; 
        }else if ((found = sLine.find("data")) != std::string::npos){
            break;
        }
    }

    std::vector <cv::Mat> segs; 
    for (int i = 0; i < segment; i++){
        segs.push_back(cv::Mat(height, width, CV_8U, cv::Scalar(0)));
    }

    //parse data
    while (!infile.eof())
    {
        getline(infile, sLine);
        std::istringstream iss (sLine);
        int segId;
        int row;
        int col1;
        int col2;
        iss >> segId;
        iss >> row;
        iss >> col1;
        iss >> col2;
        cv::Point p1(col1, row); 
        cv::Point p2(col2, row); 
        cv::line(segs[segId], p1, p2, 1);
    }
    return segs;                   
}

#endif
