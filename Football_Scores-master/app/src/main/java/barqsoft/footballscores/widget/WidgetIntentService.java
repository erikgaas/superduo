package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by erik on 7/13/15.
 */
public class WidgetIntentService extends IntentService{

    public static final String[] FOOTBALL_COLUMNS = {
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL
    };

    private static final int INDEX_HOME = 0;
    private static final int INDEX_AWAY = 1;
    private static final int INDEX_HOME_GOALS = 2;
    private static final int INDEX_AWAY_GOALS = 3;

    public WidgetIntentService() {
        super("WidgetIntentService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v("Where are here", "where we want to be");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                FootballWidgetProvider.class));

/*        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());*/

        Uri matchUri = DatabaseContract.BASE_CONTENT_URI;
        Cursor data = getContentResolver().query(
                matchUri,
                FOOTBALL_COLUMNS,
                null, null, DatabaseContract.scores_table.DATE_COL + " DESC");

        if (data == null) {
            Log.v("WidgetProvider", "no data returned. What gives?");
            return;
        }

        if (!data.moveToFirst()) {
            Log.v("WidgetProvider", "no data first returned. What gives?");
            data.close();
            return;
        }
        String homeTeam = data.getString(INDEX_HOME);
        String awayTeam = data.getString(INDEX_AWAY);
        String homeGoals = data.getString(INDEX_HOME_GOALS);
        String awayGoals = data.getString(INDEX_AWAY_GOALS);
        data.close();

        Log.v(homeTeam, homeGoals);


        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.widget_layout;

            RemoteViews views = new RemoteViews(this.getPackageName(),
                    layoutId);
            views.setTextViewText(R.id.home_team, homeTeam);
            views.setTextViewText(R.id.away_team, awayTeam);
            views.setTextViewText(R.id.home_team_goals, homeGoals);
            views.setTextViewText(R.id.away_team_goals, awayGoals);



            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);


        }

    }
}
