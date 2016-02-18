package it.jaschke.alexandria.ui.camera;

import android.Manifest;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.google.android.gms.common.images.Size;

import java.io.IOException;
import java.lang.reflect.Field;

public class CameraSourcePreview extends ViewGroup {
  private static final String LOG_TAG = CameraSourcePreview.class.getSimpleName();

  private Context mContext;
  private SurfaceView mSurfaceView;
  private boolean mStartRequested;
  private boolean mSurfaceAvailable;
  private CameraSource mCameraSource;

  private GraphicOverlay mOverlay;

  public CameraSourcePreview(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    mStartRequested = false;
    mSurfaceAvailable = false;

    mSurfaceView = new SurfaceView(context);
    mSurfaceView.getHolder().addCallback(new SurfaceCallback());
    addView(mSurfaceView);
  }

  @RequiresPermission(Manifest.permission.CAMERA)
  public void start(CameraSource cameraSource) throws IOException, SecurityException {
    if (cameraSource == null) {
      stop();
    }

    mCameraSource = cameraSource;

    if (mCameraSource != null) {
      mStartRequested = true;
      startIfReady();
    }
  }

  @RequiresPermission(Manifest.permission.CAMERA)
  public void start(CameraSource cameraSource, GraphicOverlay overlay)
    throws IOException, SecurityException {
    mOverlay = overlay;
    start(cameraSource);
  }

  public void stop() {
    if (mCameraSource != null) {
      mCameraSource.stop();
    }
  }

  public void release() {
    if (mCameraSource != null) {
      mCameraSource.release();
      mCameraSource = null;
    }
  }

  @RequiresPermission(Manifest.permission.CAMERA)
  private void startIfReady() throws IOException, SecurityException {
    if (mStartRequested && mSurfaceAvailable) {
      mCameraSource.start(mSurfaceView.getHolder());
      if (mOverlay != null) {
        Size size = mCameraSource.getPreviewSize();
        int min = Math.min(size.getWidth(), size.getHeight());
        int max = Math.max(size.getWidth(), size.getHeight());
        if (isPortraitMode()) {
          // Swap width and height sizes when in portrait, since it will be rotated by
          // 90 degrees
          mOverlay.setCameraInfo(min, max, mCameraSource.getCameraFacing());
        } else {
          mOverlay.setCameraInfo(max, min, mCameraSource.getCameraFacing());
        }
        mOverlay.clear();
      }
      mStartRequested = false;
    }
  }

  private class SurfaceCallback implements SurfaceHolder.Callback {
    @Override
    public void surfaceCreated(SurfaceHolder surface) {
      mSurfaceAvailable = true;
      try {
        startIfReady();
      } catch (SecurityException se) {
        Log.e(LOG_TAG,"Do not have permission to start the camera", se);
      } catch (IOException e) {
        Log.e(LOG_TAG, "Could not start camera source.", e);
      }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surface) {
      mSurfaceAvailable = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }
  }

  private static boolean cameraFocus(@NonNull CameraSource cameraSource, @NonNull String focusMode) {
    Field[] declaredFields = CameraSource.class.getDeclaredFields();

    for (Field field : declaredFields) {
      if (field.getType() == Camera.class) {
        field.setAccessible(true);
        try {

          Camera camera = (Camera) field.get(cameraSource);
          if (camera != null) {
            Camera.Parameters params = camera.getParameters();
            params.setFocusMode(focusMode);
            camera.setParameters(params);
            return true;
          }
          return false;
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }

        break;
      }
    }

    return false;
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    int width = 420;
    int height = 240;
    if (mCameraSource != null) {
      Size size = mCameraSource.getPreviewSize();
      if (size != null) {
        width = size.getWidth();
        height = size.getHeight();
      }
    }
    Log.d(LOG_TAG, "Width: " + width);
    Log.d(LOG_TAG, "Height: " + height);

    // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
    if (isPortraitMode()) {
      int tmp = width;
      //noinspection SuspiciousNameCombination
      width = height;
      height = tmp;
    }

    final int layoutWidth = right - left;
    final int layoutHeight = bottom - top;

    // Computes height and width for potentially doing fit width.
    int childWidth = layoutWidth;
    int childHeight = (int)(((float) layoutWidth / (float) width) * height);

    // If height is too tall using fit width, does fit height instead.
    if (childHeight > layoutHeight) {
      //childHeight = layoutHeight;
      //childWidth = (int)(((float) layoutHeight / (float) height) * width);
    }

    for (int i = 0; i < getChildCount(); ++i) {
      getChildAt(i).layout(0, 0, childWidth, childHeight);
    }

    try {
      startIfReady();
    } catch (SecurityException se) {
      Log.e(LOG_TAG, "Do not have permission to start the camera", se);
    } catch (IOException e) {
      Log.e(LOG_TAG, "Could not start camera source.", e);
    }
  }

  private boolean isPortraitMode() {
    int orientation = mContext.getResources().getConfiguration().orientation;
    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
      return false;
    }
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
      return true;
    }

    Log.d(LOG_TAG, "isPortraitMode returning false by default");
    return false;
  }
}