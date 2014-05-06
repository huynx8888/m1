/*
 * =====================================================================================
 *
 *       Filename:  tp1.cpp
 *
 *    Description:  
 *
 *        Version:  1.0
 *        Created:  03/04/2013 04:16:50 PM
 *       Revision:  1
 *       Compiler:  g++
 *         Editor:i vim 
 *         Author:  NGUYEN Van Tho 
 *   Organization: IFI - Promotion 17 
 *
 * =====================================================================================
 */


#include	<stdlib.h>
#include	<stdio.h>
#include	<unistd.h>
#include	<getopt.h>
#include	<math.h>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

#include "tp1.h"
#include "util.h"
/* 
 * ===  FUNCTION  ======================================================================
 *         Name:  main
 *  Description:  
 * =====================================================================================
 */

int
main ( int argc, char *argv[] )
{
    int line;
    char *ptr;
    int n = 0;
    point m[10];
    
    cv::namedWindow( "Display Image", CV_WINDOW_AUTOSIZE );

    Tp1 tp1 = Tp1(argv[1]); 
    if(strcmp(argv[2], "profile") == 0){
        if(argc > 3){
            line = strtol(argv[3], &ptr, 10);
        }else{
            line = 0;
        }
        cv::Mat profile = tp1.getProfile(line);
        cv::Mat withLine = tp1.getImageWithLine(line);
        imwrite( "imageWithLine.jpg", withLine);
        imwrite( "profil.jpg", profile );
        cv::imshow( "Display Image", profile);
    }else if(strcmp(argv[2], "linear") == 0){
        int x, y;
        for(int i = 3; i < argc; i = i + 2){
            x = strtol(argv[i], &ptr, 10);
            y = strtol(argv[i + 1], &ptr, 10);
            if(x < 0){
                x = 0;
            }else if(x > 255){
                x = 255;
            }
            if(y < 0){
                y = 0;
            }else if(y > 255){
                y = 255;
            }
            m[n].x = x;
            m[n++].y = y;
        }

        cv::Mat transformed = tp1.transformLinear(&m[0], n);
        imwrite( "transformed-linear.jpg", transformed);
    }else if(strcmp(argv[2], "histogram") == 0){
        cv::Mat hist = tp1.histogram();
        imwrite( "histogram.jpg", hist);
        imshow("calcHist Demo", hist);
    }else if(strcmp(argv[2], "nonlinear") == 0){
        float gamma = strtof(argv[3], &ptr);
        cv::Mat transformed = tp1.transformNonLinear(gamma);
        cv::imshow( "Display Image", transformed);
        imwrite( "transformed-nonlinear.jpg", transformed);
    }else if(strcmp(argv[2], "equalisation") == 0){
        cv::Mat transformed = tp1.equalisation();
        cv::imshow( "Display Image", transformed);
        imwrite( "equalisation.jpg", transformed);
    }else {
        eprintf("Parammeter is not enough, use ./tp1 img_name ...");
        return 1;
    }
    cv::waitKey(0);
    return EXIT_SUCCESS;
}				/* ----------  end of function main  ---------- */

Tp1::Tp1(char *imgName){
    image = cv::imread(imgName, CV_LOAD_IMAGE_UNCHANGED);
    validateLine();
}


cv::Mat Tp1::getImage(){
    return image;
}

cv::Mat Tp1::getProfile(int line){
    if(image.channels() == 1){
        return getProfileGrayScale(line);  
    }
    return getProfileRGB(line);
}

cv::Mat Tp1::getProfileGrayScale(int line){
    cv::Size imageSize = image.size();
    cv::Mat profile = cv::Mat(256, imageSize.width, CV_8UC3, cv::Scalar(0,0,0));
    uchar l1; //intensité d'un pixel
    uchar l2;
    l1 = image.at<uchar>(line, 0);
    for (int p = 0; p < imageSize.width; p++){
        l2 = image.at<uchar>(line, p + 1);
        cv::Point p1 = cv::Point(p, 255 - l1);
        cv::Point p2 = cv::Point(p + 1, 255 - l2);
        l1 = l2;
        cv::line(profile, p1, p2, cv::Scalar(255, 255, 255));
    }
    return profile;
}

cv::Mat Tp1::getProfileRGB(int line){
    cv::Size imageSize = image.size();
    cv::Mat profile = cv::Mat(256, imageSize.width, CV_8UC3, cv::Scalar(0,0,0));
    uchar r1;
    uchar r2;
    uchar g1;
    uchar g2;
    uchar b1;
    uchar b2;
    r1 = image.at<uchar>(line, 2);
    g1 = image.at<uchar>(line, 1);
    b1 = image.at<uchar>(line, 0);
    for (int p = 0; p < image.channels()*imageSize.width; p = p + 3){
        b2 = image.at<uchar>(line, p + 3);
        g2 = image.at<uchar>(line, p + 4);
        r2 = image.at<uchar>(line, p + 5);

        //p est trois fois grand que la position de point dans l'image sortie
        int pn = p /3;
        //profil pour la couleur bleu
        cv::Point p1 = cv::Point(pn, 255 - b1);
        cv::Point p2 = cv::Point(pn + 1, 255 - b2);
        cv::line(profile, p1, p2, cv::Scalar(255, 0, 0));
        b1 = b2;

        //profil pour la couleur vert 
        p1 = cv::Point(pn, 255 - g1);
        p2 = cv::Point(pn + 1, 255 - g2);
        cv::line(profile, p1, p2, cv::Scalar(0, 255, 0));
        g1 = g2;

        //profil pour la couleur rouge 
        p1 = cv::Point(pn, 255 - r1);
        p2 = cv::Point(pn + 1, 255 - r2);
        cv::line(profile, p1, p2, cv::Scalar(0, 0, 255));
        r1 = r2;
    }
    return profile;
}

cv::Mat Tp1::getImageWithLine(int line){
    cv::Size imageSize = image.size();
    cv::Mat withLine = image.clone();
    cv::Point p1 = cv::Point(0, line);
    cv::Point p2 = cv::Point(imageSize.width - 1, line);
    cv::line(withLine, p1, p2, cv::Scalar(0, 0, 0));
    return withLine;
}

uchar Tp1::getIntensity(int x, int min, int max){
    if(x > max){ 
        x = max; 
    }else if (x < min){
        x = min; 
    }   
    return x;
}

cv::Mat Tp1::transformLinear(point *p, int n){
    cv::Size imageSize = image.size();
    cv::Mat imageTransformed = image.clone();
    for (int i = 0; i < n - 1; i++){
        point p1 = p[i];
        point p2 = p[i + 1];
    }
    uchar *lut = new uchar[256];
    for (int j = 0; j < n - 1; j++){
        point p1 = p[j];
        point p2 = p[j + 1];
        //calculer les intensités pour les pixels appartenant au premier morceau
        if(j == 0){
            for (int i = 0; i < p2.x; i++){
                int v = p2.y*(i - p1.x)/(p2.x - p1.x);
                lut[i] = getIntensity(v, 0, p2.y); 
            }
        }else if (j == n - 1){
        //calculer les intensités pour les pixels appartenant au dernier morceau
            for (int i = p1.x; i < 255; i++){
                int v = p1.y + (255 - p1.y)*(i - p1.x)/(p2.x - p1.x);
                lut[i] = getIntensity(v, p1.y, 255); 
            }
        }else{
        //calculer les intensités pour les pixels appartenant au morceau au millier
            for (int i = p1.x; i < p2.x; i++){
                int v = p1.y + (p2.y - p1.y)*(i - p1.x)/(p2.x - p1.x);
                lut[i] = getIntensity(v, p1.y, p2.y); 
            }
        }
    }
    for (int i = 0; i < imageSize.width; i++){
        for (int j = 0; j < imageSize.height; j++){
            imageTransformed.at<uchar>(j, i) = lut[image.at<uchar>(j, i)]; 
        }
    }
    delete []lut;
    return imageTransformed;
}

cv::Mat Tp1::transformNonLinear(float gamma){
    cv::Size imageSize = image.size();
    cv::Mat imageTransformed = image.clone();

    //lookup table. pour optimiser la performance
    int lut[256];

    //l'intensité est normalisé dans le domaine 0 - 1 avant d'appliquer le formule
    for (int i = 0; i < 256; i++){
        lut [i] = 255*pow(i/255.0, gamma);
    }
    if(image.channels() == 1){
        for (int i = 0; i < imageSize.width; i++){
            for (int j = 0; j < imageSize.height; j++){
                imageTransformed.at<uchar>(j, i) = lut[image.at<uchar>(j, i)]; 
            }
        }
    }
    return imageTransformed;
}

cv::Mat Tp1::histogram(){

    const int histSize = 256;
    int hist[histSize];
    double histNormalised[histSize];
    int totalPixel = image.size().width*image.size().height;
    int hist_width = histSize; 
    int hist_height = 200;

    cv::Mat histImage( hist_height, hist_width, CV_8UC3, cv::Scalar(0, 0, 0));
    //initialiser
    for (int i = 0; i < histSize; i++){
        hist[i] = 0;    
        histNormalised[i] = 0;    
    }
    //calculer l'histogramme
    for (int i = 0; i < image.size().width; i++){
        for (int j = 0; j < image.size().height; j++){
            hist[image.at<uchar>(j, i)]++;
        }
    }
    double max;
    //normalisation de histogramme
    for (int i = 0; i < histSize; i++){
        histNormalised[i] = hist[i]*1.0/totalPixel;
        if(histNormalised[i] > max){
            max = histNormalised[i];
        }
    }
    
    double coefficient = (hist_height - 1)/max;
    for( int i = 1; i < histSize; i++ ){
        cv::line( histImage, cv::Point( i-1, hist_height - (uchar)(coefficient * histNormalised[i-1]) ) ,
                cv::Point( i, hist_height - (uchar)(coefficient * histNormalised[i]) ),
                cv::Scalar( 255, 255, 255), 2, 8, 0  );
    }
    return histImage;
}

uchar Tp1::maxIntensity(){
    return *std::max_element(image.begin<uchar>(),image.end<uchar>());
}
cv::Mat Tp1::equalisation(){
    cv::Mat imageTransformed = cv::Mat::zeros( image.size(), image.type() ); 
    //calcul histogram
    int hist[256];
    double histNormalised[256];
    double c[256];
    int totalPixel = image.size().width*image.size().height;
    for (int i = 0; i < 256; i++){
        hist[i] = 0;    
        histNormalised[i] = 0;    
    }
    for (int i = 0; i < image.size().width; i++){
        for (int j = 0; j < image.size().height; j++){
            hist[image.at<uchar>(j, i)]++;
        }
    }
    //normalisation
    for (int i = 0; i < 256; i++){
        histNormalised[i] = hist[i]*1.0/totalPixel;
    }
    //calculer la densité de probabilité
    for (int i = 0; i < 256; i++){
        for(int j = 0; j < i; j++){
            c[i] += histNormalised[j];
        }
    }
    //Calcul the output image 
    for (int i = 0; i < image.size().width; i++){
        for (int j = 0; j < image.size().height; j++){
            imageTransformed.at<uchar>(j, i) = 255*c[image.at<uchar>(j, i)]; 
        }
    }
    return imageTransformed;
}

void Tp1::validateLine(){
    cv::Size imageSize = image.size();
    if(line < 0 ){
        line = 0;
    }else if(line > imageSize.height){
        line = imageSize.height - 1;
    }
}
