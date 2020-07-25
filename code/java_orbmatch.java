import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class Source {

	final double[] SIZE = {400,400};
	
	public void featuresMatching() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		long start = System.currentTimeMillis();		//　測定スタート

		Mat src = Imgcodecs.imread("image1");
		Mat dist = Imgcodecs.imread("image2");		//　画像入力
		Mat out = new Mat();

		if (src== null || dist == null) {
		      System.out.println("file not existing");
		      System.exit(0);
		    }

		Imgproc.resize(src,src,new Size(400,400));
		Imgproc.resize(dist,dist,new Size(400,400));

		src= myEqualizeHist(src);
		dist= myEqualizeHist(dist);

		Imgproc.cvtColor(src,src, Imgproc.COLOR_RGB2GRAY);
		Imgproc.cvtColor(dist,dist, Imgproc.COLOR_RGB2GRAY);

		MatOfKeyPoint keyPoint1 = new MatOfKeyPoint();
		MatOfKeyPoint keyPoint2 = new MatOfKeyPoint();
		Mat descriptor1 = new Mat();
		Mat descriptor2 = new Mat();

		//ORB特徴点検出
		ORB orb = ORB.create();
		orb.detectAndCompute(src, new Mat(), keyPoint1, descriptor1);
		orb.detectAndCompute(dist, new Mat(), keyPoint2, descriptor2);

		//特徴点マッチング（特徴点毎に上位2組）
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
		List<MatOfDMatch> matches = new ArrayList<MatOfDMatch>();
		matcher.knnMatch(descriptor1, descriptor2, matches, 2);


		List<DMatch> validMatches = new ArrayList<DMatch>();
		double thresholdRatio = 0.6;

	    for(MatOfDMatch match: matches){

	    	//1位の特徴量が2位の0.6倍より小さければ（小さいほど良い）採用
			if (match.toArray()[0].distance < thresholdRatio*match.toArray()[1].distance)
			{
				validMatches.add(match.toArray()[0]);
			}

	    }

	  //採用された特徴点の組が10個以上なら
		if (validMatches.size() > 10)
		{
			System.out.println(validMatches.size());
			System.out.println("正解");
		}
		else
		{
			System.out.println(validMatches.size());
			System.out.println("不正解");
		}

		MatOfDMatch validMatches2 = new MatOfDMatch();
		 validMatches2.fromList(validMatches);
		Scalar color = new Scalar(0, 0, 255);
		Features2d.drawMatches(src, keyPoint1, dist, keyPoint2, validMatches2, out);
		Features2d.drawKeypoints(src, keyPoint1, src, color,Features2d.DRAW_RICH_KEYPOINTS);
		Features2d.drawKeypoints(dist, keyPoint2, dist, color,Features2d.DRAW_RICH_KEYPOINTS);

		Imgcodecs.imwrite("test2-1.jpg", src);
		Imgcodecs.imwrite("test2-2.jpg", dist);
		Imgcodecs.imwrite("test3.jpg", out);

		long end = System.currentTimeMillis();
        System.out.println((end - start)  + "ms");		//　測定終了

	}

	public Mat myEqualizeHist(final Mat src) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat hsv= new Mat();
		Mat out= new Mat();
		List<Mat> channels = new ArrayList<Mat>(3);

		Imgproc.cvtColor(src,hsv, Imgproc.COLOR_RGB2HSV);
		Core.split(hsv,channels);
		Mat channels2 = channels.get(1);
		Imgproc.equalizeHist(channels2, channels2);
		channels.set(1,channels2);
		Core.merge(channels,hsv);
		Imgproc.cvtColor(hsv,out, Imgproc.COLOR_HSV2RGB);
		return out;

	}

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.out.println("処理開始");

    	new Source().featuresMatching();

    System.out.println("処理終了");
	}
}