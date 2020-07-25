#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/opencv.hpp>
#include <opencv2/features2d/features2d.hpp>
#include<iostream>
#include<vector>
#include<sstream>
#include<iterator>
#include<algorithm>
#include<fstream>
#include<string>

using namespace cv;
using namespace std;

static const pair<double, double> SIZE = make_pair(400, 400);

void histgram();												//�q�X�g�O�����֐�
Mat myEqualizeHist(const Mat& src);
vector<Mat> myCalcHist(const Mat& src);
void dispHist(const string windowName, const vector<Mat>& histList);

int main()
{
	histgram();

	waitKey(0);

	return 0;
}

void histgram()
{
	int64 start = getTickCount();			//����X�^�[�g


	Mat src = imread("image1");
	Mat dist = imread("image2");		//�摜����
	vector<Mat> srcHist, distHist;

	if (!src.data || !dist.data)
	{
		printf("file not existing");
		return;
	}

	resize(src, src, cv::Size(), SIZE.first / src.cols, SIZE.second / src.rows);
	resize(dist, dist, cv::Size(), SIZE.first / dist.cols, SIZE.second / dist.rows);
	
	src = myEqualizeHist(src);
	dist = myEqualizeHist(dist);					//Mat�^�ϐ��ɔ�ԁ�

	cvtColor(src, src, CV_BGR2GRAY);
	cvtColor(dist, dist, CV_BGR2GRAY);
	
	Mat hist1, hist2;
	int histSize = 256;
	float range[] = { 0.0f,256.0f };
	const float* histRange = range;
	calcHist(&src, 1, 0, Mat(), hist1, 1, &histSize, &histRange, true, false);
	calcHist(&dist, 1, 0, Mat(), hist2, 1, &histSize, &histRange, true, false);

	cout << compareHist(hist1, hist2, CV_COMP_CORREL) << endl;

	int64 end = getTickCount();
	double elapsedMsec = (end - start) * 1000 / getTickFrequency();
	cout << elapsedMsec << "ms" << endl;
}
	
	Mat myEqualizeHist(const Mat& src)
{
	Mat hsv, out;
	vector<Mat> channels(3);

	cvtColor(src, hsv, CV_BGR2HSV);
	split(hsv, channels);
	equalizeHist(channels[2], channels[2]);
	merge(channels, hsv);
	cvtColor(hsv, out, CV_HSV2BGR);

	return out;
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	