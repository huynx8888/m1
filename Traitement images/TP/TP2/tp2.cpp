/*
 * =====================================================================================
 *
 *       Filename:  tp2.c
 *
 *    Description:  
 *
 *        Version:  1.0
 *        Created:  03/18/2013 04:46:54 PM
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  YOUR NAME (), 
 *   Organization:  
 *
 * =====================================================================================
 */
#include "opencv2/core/core.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/highgui/highgui.hpp"

#include <math.h>
#include <stdio.h>
# include <fftw3.h>
#include "tp2.h"

Tp2::Tp2 (char *imgName){
//    image = cv::imread(imgName, CV_LOAD_IMAGE_UNCHANGED);
    image = cv::imread(imgName, CV_LOAD_IMAGE_GRAYSCALE);
    if( image.empty() )
    {                                                                                  
        printf("Cannot read image file: %s\n", imgName) ;
        exit(1);
    }
}

cv::Mat Tp2::getDft(){
    cv::Mat imgMagnitude = getRawMagnitude(image);
    cv::Mat spectrum = getSpectrum(imgMagnitude);
    cv::normalize(spectrum, spectrum, 0, 1, CV_MINMAX);
    cv::imshow("Spectre", spectrum);
    to8U(spectrum);
    cv::imwrite("images/spectre.jpg", spectrum);
    cv::Mat imgComplex = getDft(image);
    cv::Mat planes[2];
    cv::split(imgComplex, planes);
    
//    lowPassFilter(planes[0], planes[1], 0.5);

    cv::Mat inverse = inverseDft(planes[0], planes[1]);
    cv::normalize(inverse, inverse, 0, 1, CV_MINMAX);
//    cv::imshow("Inverse", inverse);
    to8U(inverse);
    cv::imwrite("images/inverse.jpg", inverse);
    return imgMagnitude;
}

cv::Mat Tp2::lowPassFilter(float pad){
    cv::Mat imgMagnitude = getRawMagnitude(image);
    cv::Mat spectrum = getSpectrum(imgMagnitude);
    cv::Mat imgComplex = getDft(image);
    cv::Mat planes[2];
    cv::split(imgComplex, planes);
    
    lowPassFilter(planes[0], planes[1], pad);

    cv::Mat inverse = inverseDft(planes[0], planes[1]);
    
    cv::imshow("Inverse", inverse);
    return inverse;
}

cv::Mat Tp2::getRawMagnitude(cv::Mat img){
    cv::Mat planes[2];
    cv::Mat imgComplex = getDft(img);
    split(imgComplex, planes);
    //stocker l'image complexe pour inverse dft 
//    cv::Mat imgMagnitude = cv::Mat(img.rows, img.cols, CV_32F, cv::Scalar(0, 0, 0));
    cv::Mat imgMagnitude;
    cv::magnitude(planes[0], planes[1], imgMagnitude);
    
    return imgMagnitude;
}

void Tp2::lowPassFilter(cv::Mat &imgReal, cv::Mat &imgImaginary, float rad){
    cv::Mat lowReal = cv::Mat::zeros(imgReal.size(), CV_32F); 
    cv::Mat lowImaginary = lowReal.clone(); 

    int rows = imgReal.rows;
    int cols = imgReal.cols;
    int radius = getRadius(cols, rows, rad);
    int r2 = radius * radius;

    for(int i = 0; i < radius; i++){
        for(int j = 0; j < radius; j++){
            if(i*i + j*j <= r2){
                lowReal.at<float>(j,i) = imgReal.at<float>(j,i);
                lowImaginary.at<float>(j,i) = imgImaginary.at<float>(j,i);

                lowReal.at<float>(j, cols - i - 1) = imgReal.at<float>(j, cols - i - 1);
                lowImaginary.at<float>(j, cols - i - 1) = imgImaginary.at<float>(j, cols - i - 1);

                lowReal.at<float>(rows - j - 1,i) = imgReal.at<float>(rows - j - 1, i);
                lowImaginary.at<float>(rows - j - 1,i) = imgImaginary.at<float>(rows - j - 1, i);

                lowReal.at<float>(rows - j - 1, cols - i - 1) = imgReal.at<float>(rows - j - 1, cols - i - 1);
                lowImaginary.at<float>(rows - j -1, cols - i - 1) = imgImaginary.at<float>(rows - j - 1, cols - i - 1);
            }
        }
    }
    imgReal = lowReal;
    imgImaginary = lowImaginary;
}

void Tp2::highPassFilter(cv::Mat &imgReal, cv::Mat &imgImaginary, float rad){
    int rows = imgReal.rows;
    int cols = imgReal.cols;
    int radius = getRadius(cols, rows, rad);
    int r2 = radius * radius;

    for(int i = 0; i <= radius; i++){
        for(int j = 0; j <= radius; j++){
            if(i*i + j*j <= r2){
                imgReal.at<float>(j,i) = 0;
                imgImaginary.at<float>(j,i) = 0;

                imgReal.at<float>(j, cols - i - 1) = 0;
                imgImaginary.at<float>(j, cols - i - 1) = 0;

                imgReal.at<float>(rows - j - 1, i) = 0;
                imgImaginary.at<float>(rows - j - 1, i) = 0;

                imgReal.at<float>(rows - j - 1, cols - i - 1) = 0;
                imgImaginary.at<float>(rows - j - 1, cols - i - 1) = 0;
            }
        }
    }
}
//le filtre butterworth
void Tp2::bandRejectFilter(cv::Mat &imgReal, cv::Mat &imgImaginary, float low, float high){
    int rows = imgReal.rows;
    int cols = imgReal.cols;
    float h = getRadius(cols, rows, high); 
    float l = getRadius(cols, rows, low); 
    float high2 = h*h;
    float low2 = l*l;
    for(int i = 0; i < cols; i++){
        for(int j = 0; j < rows; j++){
            int dj = (j < rows / 2) ? j : rows - j;
            int di = (i < cols / 2) ? i : cols - i;
            float dist2 = dj*dj + di*di;
            if(dist2 > low2 && dist2 < high2){
                imgReal.at<float>(j,i) = 0;
                imgImaginary.at<float>(j,i) = 0;
            }
        }
    }
}

void Tp2::toggleCentrique(cv::Mat &img){
    int cx = img.cols/2;
    int cy = img.rows/2;
    cv::Mat q0(img, cv::Rect(0, 0, cx , cy));   // Top-Left - Create a ROI per quadrant
    cv::Mat q1(img, cv::Rect(cx, 0, cx, cy));  // Top-Right
    cv::Mat q2(img, cv::Rect(0, cy, cx, cy));  // Bottom-Left
    cv::Mat q3(img, cv::Rect(cx, cy, cx, cy)); // Bottom-Right

    cv::Mat tmp;                           // swap quadrants (Top-Left with Bottom-Right)
    q0.copyTo(tmp);
    q3.copyTo(q0);
    tmp.copyTo(q3);

    q1.copyTo(tmp);                    // swap quadrant (Top-Right with Bottom-Left)
    q2.copyTo(q1);
    tmp.copyTo(q2);
}

void Tp2::sin2(cv::Mat &imgReal, cv::Mat &imgImaginary){
    int rows = imgReal.rows;
    int cols = imgReal.cols;
    float max = 0; 
    int maxX = 0;
    int maxY = 0;
    int r = 3;
    int r2 = r * r;
    toggleCentrique(imgReal);
    toggleCentrique(imgImaginary);
    for(int i = 0; i < cols; i++){
        for(int j = 0; j < rows; j++){
            float m = imgReal.at<float>(j,i)*imgReal.at<float>(j,i) + imgImaginary.at<float>(j,i) * imgImaginary.at<float>(j,i);
            if(max < m && !(i == cols/2 && j == rows/2)){
                max = m;
                maxX = i;
                maxY = j;
            }
        }
    }

        imgReal.at<float>(maxY, maxX) = 0;
        imgImaginary.at<float>(maxY, maxX) = 0;
        imgReal.at<float>(cols - maxY - 1, cols - maxX - 1) = 0;
        imgImaginary.at<float>(maxY - maxY - 1, cols - maxX - 1) = 0;

    for (int i = 0; i <= r; i++){
        for(int j = 0; j <= r; j++){
            if(i*i + j*j <= r2){
                imgReal.at<float>(maxY -j , maxX - i) = 0;
                imgImaginary.at<float>(maxY -j , maxX - i) = 0;
                imgReal.at<float>(maxY +j , maxX + i) = 0;
                imgImaginary.at<float>(maxY +j , maxX + i) = 0;

                imgReal.at<float>(maxY -j , maxX + i) = 0;
                imgImaginary.at<float>(maxY -j , maxX + i) = 0;
                imgReal.at<float>(maxY +j , maxX - i) = 0;
                imgImaginary.at<float>(maxY +j , maxX - i) = 0;

                imgReal.at<float>(cols - maxY + j , cols - maxX + i) = 0;
                imgImaginary.at<float>(cols - maxY +j , cols - maxX + i) = 0;
                imgReal.at<float>(cols - maxY - j , cols - maxX - i) = 0;
                imgImaginary.at<float>(cols - maxY -j , cols - maxX - i) = 0;

                imgReal.at<float>(cols - maxY + j , cols - maxX - i) = 0;
                imgImaginary.at<float>(cols - maxY +j , cols - maxX - i) = 0;
                imgReal.at<float>(cols - maxY - j , cols - maxX + i) = 0;
                imgImaginary.at<float>(cols - maxY -j , cols - maxX + i) = 0;
            }
        }
    }

    toggleCentrique(imgReal);
    toggleCentrique(imgImaginary);

}
//sinusoidal filter, Butterworth band rejected
void Tp2::sinusoidalFilter(cv::Mat &imgReal, cv::Mat &imgImaginary, float low, float high, float power){
    int rows = imgReal.rows;
    int cols = imgReal.cols;
    float h = getRadius(cols, rows, high); 
    float l = getRadius(cols, rows, low); 
    float center = (h + l)/2;
    float width = h - l;
    float freq = width/2;
    //traiter le 1 & 4e quadrant
    for(int i = 0; i < cols; i++){
        for(int j = 0; j < rows; j++){
            int dj = (j < rows / 2) ? j : rows - j;
            int di = (i < cols / 2) ? i : cols - i;
            float dist = sqrt(dj*dj + di*di) - center;
            float filter = 1 - 1/(1 + pow(dist*dist/freq*freq, power));
            //traiter le 1 & 4e quadrant
            imgReal.at<float>(j, i) *= filter;
            imgImaginary.at<float>(j, i) *= filter;
        }
    }
}

cv::Mat Tp2::fftw(cv::Mat img){
    img.convertTo(img, CV_32F);
    int rows = img.rows;
    int cols = img.cols;
    fftw_complex *out, *in;
    fftw_plan plan_forward;
    in = (fftw_complex*) fftw_malloc(sizeof(fftw_complex) * rows * cols);
    out = (fftw_complex*) fftw_malloc(sizeof(fftw_complex) * rows * cols);

    for (int i = 0; i < rows; i++){
        for(int j = 0; j < cols; j++){
            in[i*cols+j][0] = img.at<float>(j,i);
            in[i*cols+j][1] = 0; 
            printf("%f", in[i*rows+j][0]);
        }
    }
    plan_forward = fftw_plan_dft_2d(cols, rows, in, out, FFTW_FORWARD, FFTW_ESTIMATE);
    fftw_execute ( plan_forward );
    cv::Mat imgMagnitude = cv::Mat::zeros(img.size(), CV_32F); 
    for (int i = 0; i < rows; i++){
        for (int j = 0; j < cols; j++ ){
            float real = out[i*rows+j][0];
            float imag = out[i*rows+j][1]; 
            float mag = sqrt(real*real + imag*imag);
            imgMagnitude.at<float>(j, i) = mag;
            printf("%f", imgMagnitude.at<float>(j, i));
        }
    }

    imgMagnitude = getSpectrum(imgMagnitude);
    fftw_destroy_plan ( plan_forward );

    fftw_free ( in );
    fftw_free ( out );
    return imgMagnitude;
}

cv::Mat Tp2::getDft(cv::Mat img){
    if(img.empty()){
        perror("????");
        exit(1);
    }
//    img = cv::imread("images/images.jpe", CV_LOAD_IMAGE_GRAYSCALE);
//    int M = cv::getOptimalDFTSize( img.rows );                                             
//    int N = cv::getOptimalDFTSize( img.cols );
//    cv::Mat padded;
//    cv::copyMakeBorder(img, padded, 0, M - img.rows, 0, N - img.cols, cv::BORDER_CONSTANT, cv::Scalar::all(0));
//    cv::Mat planes[] = {cv::Mat_<float>(padded), cv::Mat::zeros(padded.size(), CV_32F)};
//    cv::Mat imgComplex;
//    merge(planes, 2, imgComplex); 

    cv::Mat imgComplex;
//    cv::dft(cv::Mat_<float>(img), imgComplex, cv::DFT_COMPLEX_OUTPUT);
    cv::dft(cv::Mat_<float>(img), imgComplex, cv::DFT_COMPLEX_OUTPUT);
    return imgComplex;
}

cv::Mat Tp2::getSpectrum(cv::Mat img){
    img += cv::Scalar::all(1);                    // switch to logarithmic scale
    log(img, img);
//    img = img(cv::Rect(0, 0, img.cols & -2, img.rows & -2)); 
    toggleCentrique(img);

    // viewable image form (float between values 0 and 1).
    return img;

}

cv::Mat Tp2::inverseDft(cv::Mat imgReal, cv::Mat imgImaginary){
    cv::Mat imgComplex;
    cv::Mat planes[] = {imgReal, imgImaginary};
    cv::merge(planes, 2, imgComplex);
    cv::Mat imgInverse;
    cv::dft(imgComplex, imgInverse, cv::DFT_INVERSE | cv::DFT_SCALE | cv::DFT_REAL_OUTPUT);
//    imgComplex = imgComplex(cv::Rect(0, 0, imgComplex.cols & -2, imgComplex.rows & -2)); 
    return imgInverse;
}

cv::Mat Tp2::getImage(){
    return image;
}

void Tp2::getInfo(){
    printf("Width: %d\n", image.cols);
    printf("Height: %d\n", image.rows);
}

float Tp2::getRadius(int cols, int rows, float rad){
    int min = cols;
    if(rows < cols){
        min = rows;
    }
    return rad*min/2;
}

void Tp2::to8U(cv::Mat &img){
    int rows = img.rows;
    int cols = img.cols;
    cv::normalize(img, img, 0, 1, CV_MINMAX); // Transform the matrix with float values into a
    for (int i = 0; i < cols; i++){
        for (int j = 0; j < rows; j++ ){
            img.at<float>(j, i) *= 255;
        }
    }
}

