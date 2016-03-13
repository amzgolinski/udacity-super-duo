package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
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
    super.onDeleted(context, appWidgetIds);
  }

  @Override
  public void onDisabled(Context context) {
    super.onDisabled(context);
  }

  @Override
  public void onEnabled(Context context) {
    super.onEnabled(context);
  }

  @Override
  public void onReceive(Context context, Intent intent) {

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

    for (int appWidgetId : appWidgetIds) {
      RemoteViews views =
          new RemoteViews(context.getPackageName(), R.layout.widget_scores);

      // Create an Intent to launch MainActivity
      Intent mainActivity = new Intent(context, MainActivity.class);

      PendingIntent pendingIntent =
          PendingIntent.getActivity(context, 0, mainActivity, 0);
      views.setOnClickPendingIntent(R.id.widget, pendingIntent);

      views.setRemoteAdapter(
          R.id.football_scores,
          new Intent(context, ScoresWidgetService.class)
      );

      Intent test = new Intent(context, MainActivity.class);
      PendingIntent clickPendingIntentTemplate =
          TaskStackBuilder.create(context)
              .addNextIntentWithParentStack(test)
              .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

      views.setPendingIntentTemplate(
          R.id.football_scores,
          clickPendingIntentTemplate);

      views.setEmptyView(R.id.football_scores, R.id.empty_view);

      appWidgetManager.updateAppWidget(appWidgetId, views);

    }

  }
}
