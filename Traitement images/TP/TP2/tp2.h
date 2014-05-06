#ifndef TP2_H
#define TP2_H
 
#include <iostream>
#include <opencv2/opencv.hpp>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

#define COMPLEX_MAGNITUDE_FILE_NAME "image_complex_magnitude_raw.jpg"
#define COMPLEX_PHASE_FILE_NAME "image_complex_phase_raw.jpg"

class Tp2
{
    public:
        Tp2 (char *imgName);
        cv::Mat getDft(); 
//        cv::Mat getInverDft();
        cv::Mat getInverDft(char *magnitude, char *phase);
        cv::Mat getSpectrum(cv::Mat img);
        cv::Mat getRawMagnitude(cv::Mat img);
        cv::Mat inverseDft(cv::Mat imgReal, cv::Mat imgImaginary);
        cv::Mat getDft(cv::Mat img);
        void lowPassFilter(cv::Mat &imgReal, cv::Mat &imgImaginary, float rad);
        cv::Mat lowPassFilter(float pad);
        void highPassFilter(cv::Mat &imgReal, cv::Mat &imgImaginary, float rad);
        void bandRejectFilter(cv::Mat &imgReal, cv::Mat &imgImaginary, float low, float high);
        void sinusoidalFilter(cv::Mat &imgReal, cv::Mat &imgImaginary, float low, float high, float power);
        void sin2(cv::Mat &imgReal, cv::Mat &imgImaginary);
        cv::Mat fftw(cv::Mat img);
        cv::Mat getImage();
        void getInfo();
        void to8U(cv::Mat &img);
	private:
        float getRadius(int cols, int rows, float rad);
        void toggleCentrique(cv::Mat &img);
        cv::Mat image; 
};
#endif
