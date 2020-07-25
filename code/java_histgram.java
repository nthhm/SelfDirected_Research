import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class Source {
		
	final double[] SIZE = {400,400};
	
	public void histgram() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		long start = System.currentTimeMillis();		//�@����X�^�[�g
		
		Mat src = Imgcodecs.imread("image1");
		Mat dist = Imgcodecs.imread("image2");		//�@�摜����
		
		if (src== null || dist == null) {
		      System.out.println("file not existing");
		      System.exit(0);
		    }
		
		Imgproc.resize(src,src,new Size(400,400));
		Imgproc.resize(dist,dist,new Size(400,400));	
		Imgcodecs.imwrite("test1-1.jpg", src);			// �o�͉摜�̕ۑ�
		Imgcodecs.imwrite("test2-1.jpg", dist);			// �o�͉摜�̕ۑ�
		
		src= myEqualizeHist(src);
		dist= myEqualizeHist(dist);
		
		Imgproc.cvtColor(src,src, Imgproc.COLOR_RGB2GRAY);
		Imgproc.cvtColor(dist,dist, Imgproc.COLOR_RGB2GRAY);
		
		List<Double> histList = new ArrayList<Double>();
		List<Mat> srchist = new ArrayList<Mat>();
		List<Mat> disthist = new ArrayList<Mat>();
		srchist.add(src);
		disthist.add(dist);
		Mat hist1 = new Mat();
		Mat hist2 = new Mat();
		MatOfInt histsize = new MatOfInt(256);
		float[] range= {0.0f,256.0f};
		MatOfFloat ranges = new MatOfFloat(range);
		Imgproc.calcHist( srchist, new MatOfInt(0), new Mat(),hist1, histsize, ranges);
		Imgproc.calcHist( disthist, new MatOfInt(0), new Mat(),hist2, histsize, ranges);
	
		 histList.add(Imgproc.compareHist(hist1, hist2, 0));
		 
		 System.out.println(histList);
	        if(histList.get(0)>=0.8) {
	        	System.out.println("����");
	        }
	        else {
	        	System.out.println("�s����");
	        }
		long end = System.currentTimeMillis();
        System.out.println((end - start)  + "ms");		//�@����I��
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
    System.out.println("�����J�n");
    
    	new Source().histgram();
    	
    System.out.println("�����I��");
  }
}

*