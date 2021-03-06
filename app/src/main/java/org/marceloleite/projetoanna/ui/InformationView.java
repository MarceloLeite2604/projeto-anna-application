package org.marceloleite.projetoanna.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.marceloleite.projetoanna.R;
import org.marceloleite.projetoanna.utils.Log;
import org.w3c.dom.Text;

/**
 * A generic {@link LinearLayout} to show information about the application.
 */
public class InformationView extends LinearLayout {

    /**
     * A tag to identify this class' messages on log.
     */
    private static final String LOG_TAG = InformationView.class.getSimpleName();

    /*
     * Enables messages of this class to be shown on log.
     */
    static {
        Log.addClassToLog(LOG_TAG);
    }

    /**
     * The progress bar shown when the application is waiting for a process to conclude.
     */
    private ProgressBar progressBar;

    /**
     * The text view which shows an information to the user.
     */
    private TextView textView;

    /**
     * Constructor.
     *
     * @param context The context of the application which the linear layout should be created.
     * @param message Message to show on view.
     */
    public InformationView(@NonNull Context context, String message) {
        this(context, message, true);
    }

    /**
     * Constructor.
     *
     * @param context         The context of the application which the linear layout should be created.
     * @param message         Message to show on view.
     * @param showProgressBar Inform true if the progress bar must be shown on view or false to hidden it.
     */
    public InformationView(@NonNull Context context, String message, boolean showProgressBar) {
        super(context);

        View.inflate(context, R.layout.information, this);
        this.textView = findViewById(R.id.information_text);
        this.textView.setText(message);
        this.progressBar = findViewById(R.id.information_progressbar);
        showProgressBar(showProgressBar);
    }

    public void setMessage(String informationText) {
        textView.setText(informationText);
    }

    /**
     * Shows of hides the progress bar on the view.
     *
     * @param show True if the progress bar must be shown. False if it should be hidden.
     */
    public void showProgressBar(boolean show) {
        int value = View.GONE;
        if (show) {
            value = View.VISIBLE;
        }
        progressBar.setVisibility(value);
    }
}
