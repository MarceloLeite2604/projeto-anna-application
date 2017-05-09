package org.marceloleite.projetoanna.mixer;

import android.os.AsyncTask;

import org.marceloleite.projetoanna.MainActivity;

import java.io.File;
import java.io.IOException;

/**
 * Created by Marcelo Leite on 03/05/2017.
 */

public class MixerAsyncTask extends AsyncTask<MixerAsyncTaskParameters, Integer, File> {

    private static final String LOG_TAG = MixerAsyncTask.class.getSimpleName();

    private MainActivity mainActivity;

    public MixerAsyncTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected File doInBackground(MixerAsyncTaskParameters... mixerAsyncTaskParametersArray) {

        File mixedFile = null;
        for (int counter = 0; counter < mixerAsyncTaskParametersArray.length; counter++) {
            MixerAsyncTaskParameters mixerAsyncTaskParameters = mixerAsyncTaskParametersArray[counter];
            File movieFile = mixerAsyncTaskParameters.getMovieFile();
            File audioFile = mixerAsyncTaskParameters.getAudioFile();
            try {
                mixedFile = Mixer.mixAudioAndVideo(audioFile, movieFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mixedFile;
    }

    @Override
    protected void onPostExecute(File file) {
        mainActivity.mixConcluded(file);
        super.onPostExecute(file);
    }
}
