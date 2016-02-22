package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.service.FetchScoresService;

public class ScoresWidgetProvider extends AppWidgetProvider {

  public static final String LOG_TAG =
      ScoresWidgetProvider.class.getSimpleName();

  @Override
  public void onDeleted(Context context, int[] appWidgetIds) {
    Log.d(LOG_TAG, "onDeleted");
    super.onDeleted(context, appWidgetIds);
  }

  @Override
  public void onDisabled(Context context) {
    Log.d(LOG_TAG, "onDisabled");
    super.onDisabled(context);
  }

  @Override
  public void onEnabled(Context context) {
    Log.d(LOG_TAG, "onEnabled");
    super.onEnabled(context);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d(LOG_TAG, "onReceive");

    super.onReceive(context, intent);

    Log.d(LOG_TAG, intent.getAction());

    if (intent.getAction().equals(FetchScoresService.DATA_UPDATED)) {
      AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

      int[] appWidgetIds = appWidgetManager
          .getAppWidgetIds(new ComponentName(context, getClass()));

      appWidgetManager.notifyAppWidgetViewDataChanged(
          appWidgetIds,
          R.id.football_scores
      );
    }
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                       int[] appWidgetIds) {

    Log.d(LOG_TAG, "onUpdate");
    for (int appWidgetId : appWidgetIds) {
      Log.d(LOG_TAG, "AppWidgetId: " + appWidgetId);
      RemoteViews views =
          new RemoteViews(context.getPackageName(), R.layout.widget_scores);

      // Create an Intent to launch MainActivity
      Intent intent = new Intent(context, MainActivity.class);

      PendingIntent pendingIntent =
          PendingIntent.getActivity(context, 0, intent, 0);

      views.setOnClickPendingIntent(R.id.widget, pendingIntent);

      views.setRemoteAdapter(
          R.id.football_scores,
          new Intent(context, ScoresWidgetService.class)
      );

      views.setEmptyView(R.id.football_scores, R.id.empty_view);

      appWidgetManager.updateAppWidget(appWidgetId, views);

    }

  }
}
