/*
 * =====================================================================================
 *
 *       Filename:  SegToPng.cpp
 *
 *    Description:  
 *
 *        Version:  1.0
 *        Created:  05/07/2013 11:23:33 PM
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  NGUYEN Van Tho (), thonv133@gmail.com
 *   Organization:  
 *
 * =====================================================================================
 */

#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <vector>

#include <opencv2/opencv.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

int main(int argc, char ** argv){
    std::string sLine = "";
    std::string fileName = "";
    int segment = 0;
    int width = 0;
    int height = 0;
    int gray = 0;

    std::ifstream infile;
    infile.open(argv[1]);

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

    //initialise image
    cv::Mat img(height, width, CV_8UC3, cv::Scalar(0));
    //initialise random colors
    cv::RNG rng( 0xFFFFFFFF );
    //initialise vector of colors
    std::vector <cv::Scalar> colors; 
    if(gray){
        for (int i = 0; i < segment; i++){
            int c = rng.uniform(0,255);
            colors.push_back(cv::Scalar(c));
        }
    }else{
        for (int i = 0; i < segment; i++){
            int c1 = rng.uniform(0,255);
            int c2 = rng.uniform(0,255);
            int c3 = rng.uniform(0,255);

            colors.push_back(cv::Scalar(c1, c2, c3));
        }
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
        cv::line(img, p1, p2, colors.at(segId));
    }
    cv::imshow("test", img);
    //save image
    std::string f("humain/"); 
    f.append(fileName);
    f.append(".png");
    //f.replace(f.end() - 3, f.end(), "png");
    cv::imwrite(f, img);
    infile.close();
    cv::waitKey(0);
    return 0;                   
} 
