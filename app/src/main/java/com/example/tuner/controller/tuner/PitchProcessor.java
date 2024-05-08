package com.example.tuner.controller.tuner;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchDetector;


/**
 * Modified copy of PitchProcessor from TarsosDSP allowing a pitch detector object to be passed in.
 * Is responsible to call a pitch estimation algorithm. It also calculates progress.
 * The underlying pitch detection algorithm must implement the {@link PitchDetector} interface.
 */
public class PitchProcessor implements AudioProcessor {

    /**
     * The underlying pitch detector;
     */
    private final PitchDetector detector;

    private final PitchDetectionHandler handler;

    public PitchProcessor(PitchDetector detector, PitchDetectionHandler handler) {
        this.detector = detector;
        this.handler = handler;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        float[] audioFloatBuffer = audioEvent.getFloatBuffer();

        PitchDetectionResult result = detector.getPitch(audioFloatBuffer);


        handler.handlePitch(result,audioEvent);
        return true;
    }

    @Override
    public void processingFinished() {
    }
}
