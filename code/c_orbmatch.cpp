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

void featuresMatching();										//�����_�}�b�`���O�֐�
Mat myEqualizeHist(const Mat& src);

int main()
{
	featuresMatching();

	waitKey(0);

	return 0;
}

void featuresMatching()
{
	int64 start = getTickCount();			//����X�^�[�g	


	//�摜�ǂݍ���
	Mat src = imread("image1");
	Mat dist = imread("image2");
	Mat out;
	vector<Mat> srcHist, distHist;

	if (!src.data || !dist.data)
	{
		printf("file not existing");
		return;
	}

	//�C�ӂ�SIZE�Ƀ��T�C�Y
	resize(src, src, cv::Size(), SIZE.first / src.cols, SIZE.second / src.rows);
	resize(dist, dist, cv::Size(), SIZE.first / dist.cols, SIZE.second / dist.rows);

	//���x���ω�
	src = myEqualizeHist(src);
	dist = myEqualizeHist(dist);

	//�O���C�X�P�[��
	cvtColor(src, src, CV_BGR2GRAY);
	cvtColor(dist, dist, CV_BGR2GRAY);

	//�����_���X�g�Ɠ����ʕϐ��錾
	vector<KeyPoint> keyPoints1, keyPoints2;
	Mat descriptor1, descriptor2;

	//ORB�����_���o
	auto orb = ORB::create();
	orb->detectAndCompute(src, Mat(), keyPoints1, descriptor1);
	orb->detectAndCompute(dist, Mat(), keyPoints2, descriptor2);

	//�����_�}�b�`���O�i�����_���ɏ��2�g�j
	auto matcher = DescriptorMatcher::create("BruteForce-Hamming");
	vector<vector<DMatch>> matches;
	matcher->knnMatch(descriptor1, descriptor2, matches, 2);

	vector<DMatch> validMatches;
	double thresholdRatio = 0.6;
	for (auto& match : matches)
	{
		//1�ʂ̓����ʂ�2�ʂ�0.6�{��菬������΁i�������قǗǂ��j�̗p
		if (match[0].distance < thresholdRatio*match[1].distance)
		{
			validMatches.push_back(match[0]);
		}
	}

	//�̗p���ꂽ�����_�̑g��10�ȏ�Ȃ�
	if (validMatches.size() > 10)
	{
		cout << validMatches.size() << endl;
		cout << "����" << endl;
	}
	else
	{
		cout << validMatches.size() << endl;
		cout << "�s����" << endl;
	}

	//drawMatches(src, keyPoints1, dist, keyPoints2, validMatches, out);
	//drawKeypoints(src, keyPoints1, src, Scalar(0, 0, 255));
	//drawKeypoints(dist, keyPoints2, dist, Scalar(0, 0, 255));

	//imshow("SRC_FEATURES", src);
	//imshow("DIST_FEATURES", dist);
	//imshow("MATCHES", out);

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

