# AudioVisualizer-RealTime-Histogram
This is a small JavaFX desktop application for real-time audio visualization (histogram) built from scratch.

## Overview
The application supports visualization and playback of .wav files, or it can directly visualize microphone input.
<br> The audio is visually represented with a histogram - bars on the X axis represent frequencies ranging from 43Hz up to 22050Hz, the Y axis represents the amplitude (strength) of frequencies.

![Alt text](images/pic3.png?raw=true "")
![Alt text](images/pic4.png?raw=true "")


## Implementation details<br>
### 1. Calculating the Mel filter bank
The visualizer mimics the human auditory system, which is logarithmic in its nature. This means that as the frequencies get higher we can detect fewer changes in sound. That is why the Mel scale is used to select which frequencies are shown in the visualizer and how their amplitudes are calculated. The Mel filter bank is an array of logarithmically spaced frequencies, the filters are triangular which means the first filter starts at index 0, has a center at index 1 and ends at index 2, the second filter starts at index 1, has a center at 2 and ends at 3 etc. Since there are 64 bars (frequency bands) in the histogram, 64 filters are needed -> there are 66 frequencies in the filter bank array.

![Alt text](images/melbank.png?raw=true "10 filter example")

### 2. Reading raw audio data slices (from .wav file or microphone) and (if .wav) writing to output (speakers) - playing audio
After the Mel filter bank is created, audio processing can begin.
### 3. Unpacking raw data into samples and applying the Hamming window function
Because we are taking fixed slices of audio we use a window function to smooth out the transition between two slices. This helps emphasize the key characteristics of each time slice.
### 4. Processing samples and drawing the Histogram
FFT (Fast Fourier transform) decomposes the sequence of samples (sound wave) into components of different frequencies (base harmonics - elementary sound waves). Using the Mel filter bank we know which components to use for computing the magnitude of each frequency band (bar on the X axis). After computing all 64 magnitudes, corresponding rectangles representing frequency bands (bars) are drawn and appropriately scaled.
<br>Small lines on top of the bars on the graph represent the maximum magnitudes of the corresponding frequency bands during a session.<br><br>

## Sidenote
This was a small side project I had done in my spare time at the start of the 5th semester (start of the 3rd year of college), influenced by studying audio processing for the course - Speech recognition at the Faculty of Computer Science in Belgrade.

## Download
You can download the .jar files [here](downloads/AudioVisualizerHistogram.zip).<br>
To run the AudioVisualizerPlayer.jar it must be within the same folder as the audioFiles folder.

## Contributors
- Stefan Ginic - <stefangwars@gmail.com>
