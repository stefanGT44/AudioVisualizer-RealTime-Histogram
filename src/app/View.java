package app;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class View extends Stage{
	
	private static View instance;
	
	public static final int BAR_NUM = 64;
	
	public static Rectangle[] rec, peaks;
	public static boolean test = false;
	
	public static double[] buffer = new double[BAR_NUM];
	public static double[] bufferDecrease = new double[BAR_NUM];
	public static double[] magnitudes = new double[BAR_NUM];
	
	public static double frequencyBins[];
	
	public static Recorder recorder;
	
	public static String song = "Daft Punk.wav";
	public static String path = "audioFiles\\";
	
	public static View get(){
		if (instance == null)
			new View();
		return instance;
	}
	
	private View(){
		instance = this;
		initializeWindow();
		calculateBins();
		if (Main.mode.equals("player"))
			startPlaying();
		else
			startRecording();
	}
	
	private void startPlaying(){
		Player player = new Player();
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				player.start();
				finished();
				System.exit(0);
			}
			
		});
		
		thread.start();
	}
	
	private void startRecording() {
		recorder = new Recorder();

		Thread recordThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					System.out.println("Start recording...");
					recorder.start();
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}

		});
		recordThread.start();
	}
	
	//constructing frequency bins
	private void calculateBins(){
		double maxFreq = 22050;
		double time = (Player.DEF_BUFFER_SAMPLE_SZ/2)/maxFreq;
		double minFreq = 1/time;
		
		frequencyBins = new double[BAR_NUM + 1];
		frequencyBins[0] = minFreq;
		frequencyBins[frequencyBins.length-1] = maxFreq;
		
		minFreq = melTransform(minFreq);
		maxFreq = melTransform(maxFreq);
		
		double amount = (maxFreq - minFreq)/BAR_NUM;
		
		/*
		Mel's scale is logarithmic so we can set the distances between
		frequencies to be linear, once we convert the values back 
		from Mel's scale we get logarithmic distances between frequencies 
		the distance increases as the frequencies increase
		which corresponds to how humans hear sound
		(we can detect fewer differences in higher frequencies) 
		*/
		
		for (int i = 1; i < frequencyBins.length-1; i++){
			frequencyBins[i] = iMelTransform(minFreq + i * amount);
		}
		
		
		//triangulation
		frequencyBins[0] = 0;
		int index = 1;
		for (int i = 2; i < Player.DEF_BUFFER_SAMPLE_SZ/2; i++){
			double freq = i / time;
			if (freq >= frequencyBins[index]){
				frequencyBins[index++] = i-1;
			}
			if (index==(BAR_NUM+1)) break;
		}
		frequencyBins[frequencyBins.length-1] = Player.DEF_BUFFER_SAMPLE_SZ/2-1;
	}
	
	private double melTransform(double freq){
		return 1125 * Math.log(1 + freq/(float)700);
	}
	
	private double iMelTransform(double freq){
		return 700 * (Math.pow(Math.E, freq/(float)1125) - 1);
	}
	
	public static void drawSpectrum2(float samples[]){
		Complex data[] = new Complex[samples.length];
		for (int i = 0; i < samples.length; i++){
			data[i] = new Complex(samples[i], 0);
		}
		Complex niz[] = FFT.fft(data);
		
		int k = 0;
		for (int i = 0; i < magnitudes.length; i++){
			
			int startIndex = (int)frequencyBins[k];
			int endIndex = (int)frequencyBins[k+1];
			
			double maxFreq =  0;
			
			for (int j = startIndex; j < endIndex; j++){
				double freq = niz[j].re() * niz[j].re() + niz[j].im() * niz[j].im();
				if (freq > maxFreq) maxFreq = freq;
			}
			
			magnitudes[i] = 20 * Math.log10(maxFreq);
			k++;
			
			// default 0.5 & 1.8
			
			if (magnitudes[i] > buffer[i]){
				buffer[i] = magnitudes[i];
				bufferDecrease[i] = 0.8;
			}
			
			if (magnitudes[i] < buffer[i]){
				buffer[i] -= bufferDecrease[i];
				bufferDecrease[i] *= 1.2;
			}
			
		}
		
		double scale = 150/110.0;
		for (int i = 0; i < magnitudes.length; i++){
			rec[i].setHeight((int)(buffer[i] * scale));
			rec[i].setY(149-rec[i].getHeight());
			if (peaks[i].getY()>rec[i].getY()-1){
				peaks[i].setY(rec[i].getY()-1);
			}
		}
	}
	
	public static void finished(){
		int animCounter = 0;
		int finishedCounter = 0;
		while (true){
			animCounter++;
			finishedCounter = 0;
			if (animCounter%1000000==0){
			animCounter = 0;
			for (int j = 0; j < rec.length; j++){
				if (rec[j].getHeight()<=1){
					finishedCounter++;
					continue;
				} else{
				rec[j].setHeight(rec[j].getHeight()-1);
				rec[j].setY(149-rec[j].getHeight());
				}
			}
			if (finishedCounter >= rec.length-1) break;
			}
		}
		for (int i = 0; i < rec.length; i++){
			rec[i].setHeight(1);
			rec[i].setY(149);
		}
		animCounter = 0;
		finishedCounter = 0;
		while (true){
			animCounter++;
			finishedCounter = 0;
			if (animCounter%1000000==0){
			animCounter = 0;
			for (int j = 0; j < rec.length; j++){
				if (peaks[j].getY()>=148){
					finishedCounter++;
					continue;
				} else{
					peaks[j].setY(peaks[j].getY()+1);
				}
			}
			if (finishedCounter >= peaks.length-1) break;
			}
		}
		for (int i = 0; i < peaks.length; i++)
			peaks[i].setY(148);
	}
	
	public void initializeWindow(){
		Pane pane = new Pane();
		
		pane.setStyle("-fx-background-color: #252a33;");
		pane.setMaxSize(512, 150);
		pane.setMinSize(512, 150);
		
		int c1[] = {26, 115, 216};
		int c2[] = {167, 0, 244};
		
		int c3[] = {0, 250, 255};
		int c4[] = {252, 132, 255};
		
		rec = new Rectangle[BAR_NUM];
		peaks = new Rectangle[BAR_NUM];
		
		for (int i = 0; i < rec.length; i++){
			int pos = i * 8 + 2;
			Rectangle r = new Rectangle(pos, 149, 4, 1);
			
			DropShadow borderGlow = new DropShadow();
			borderGlow.setColor(Color.rgb(lerp(c3[0], c4[0], i/(float)rec.length),
					lerp(c3[1], c4[1], i/(float)rec.length),
					lerp(c3[2], c4[2], i/(float)rec.length)));
			borderGlow.setWidth(5);
			borderGlow.setHeight(5);
			
			r.setEffect(borderGlow);
			
			r.setFill(Color.rgb(lerp(c1[0], c2[0], i/(float)rec.length),
					lerp(c1[1], c2[1], i/(float)rec.length),
					lerp(c1[2], c2[2], i/(float)rec.length)));
			rec[i] = r;
			
			pane.getChildren().add(r);
			
			DropShadow peakGlow = new DropShadow();
			peakGlow.setColor(Color.rgb(lerp(c3[0], c4[0], i/(float)rec.length),
					lerp(c3[1], c4[1], i/(float)rec.length),
					lerp(c3[2], c4[2], i/(float)rec.length)));
			peakGlow.setWidth(20);
			peakGlow.setHeight(20);
			
			Rectangle peak = new Rectangle(pos, 148, 4, 1);
			peak.setEffect(peakGlow);
			peak.setFill(Color.rgb(lerp(c1[0], c2[0], i/(float)rec.length),
					lerp(c1[1], c2[1], i/(float)rec.length),
					lerp(c1[2], c2[2], i/(float)rec.length)));
			
			peaks[i] = peak;
			
			pane.getChildren().add(peak);
		}
		
		this.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				Player.running = false;
				Recorder.running = false;
			}
		});
		
		Scene scene = new Scene(pane);
		this.setScene(scene);
		if (Main.mode.equals("player"))
			this.setTitle("Now playing: " + song);
		else
			this.setTitle("Recording...");
		this.show();
	}
	
	public static int lerp(int a, int b, double x){
		return (int)(a + (b - a) * x);
	}

}
