# RealTime-AudioVisualizer-Histogram
This is a small JavaFX desktop application for real-time audio visualization (histogram) built from scratch.

## Overview
The application supports visualization and playback of .wav files, or it can directly visualize microphone input.
<br> The audio is visually represented with a histogram - bars on the X axis represent frequencies ranging from 43Hz up to 22050Hz, the Y axis represents the amplitude (strength) of frequencies.

![Alt text](images/pic1.png?raw=true "")
![Alt text](images/pic2.png?raw=true "")


## Implementation details<br>
### 1. Calculating the Mel filter bank
The visualizer mimics the human auditory system which is logarithmic. This means that as the frequencies get higher we can detect fewer changes in sound (We can detect far more differences at lower frequencies). It is carefully calculated which frequencies are shown in the visualizer so that the sound is represented in the same way we hear it. After the frequencies are calculated a triangulation filter is applied to help capture the energy at each critical frequency band and give a rough approximation of the spectrum shape.

### 2. Reading raw audio data slices (from .wav file or microphone) and (if .wav) writing to output (speakers) - playing audio
After the Mel filter bank is created, audio processing can begin.
### 3. Unpacking raw data into samples and applying the Hamming window function
Because we are taking fixed slices of audio we use a window function to smooth out the transition between two slices. This helps emphasize the key characteristics of each time slice.
### 4. Processing samples and drawing the Histogram
FFT (Fast Fourier transform) decomposes the sequence of samples (sound wave) into components of different frequencies (base harmonics - elementary sound waves). Using the Mel filter bank we choose which components to use for each frequency band (Bar on the X axis). After choosing and calculating the magnitude for all 64 frequency bands (Bars), corresponding rectangles representing frequency bands (Bars) are being drawn and scaled.
<br>Small lines on top of each bar on the graph represent the maximum magnitude of the corresponding frequency band during a session.<br><br>

## Sidenote
This was a fun little project I had done in my spare time at the start of the 5th semester (start of the 3rd year of collage), influenced by studying audio processing for the course - Speech recognition at the Faculty of Computer Science in Belgrade.

## Download
You can download the .jar files [here](downloads).<br>
To run the AudioVisualizerPlayer.jar it must be within the same folder as the audioFiles folder.

## Contributors
- Stefan Ginic - <stefangwars@gmail.com>
