package com.dji.sdk.sample.camera;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;

import com.dji.sdk.sample.R;
import com.dji.sdk.sample.common.BaseThreeBtnView;
import com.dji.sdk.sample.common.DJISampleApplication;
import com.dji.sdk.sample.common.Utils;
import com.dji.sdk.sample.utils.DJIModuleVerificationUtil;

import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.FileOutputStream;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import dji.sdk.Camera.DJICamera;
import dji.sdk.Camera.DJICameraSettingsDef;
import dji.sdk.Camera.DJIMedia;
import dji.sdk.base.DJIBaseComponent;
import dji.sdk.base.DJIError;

import dji.sdk.Gimbal.DJIGimbal;

/**
 * Created by dji on 16/1/6.
 */
public class RecordVideoView extends BaseThreeBtnView {

    Timer timer = new Timer();
    private final Context context;
    private long timeCounter = 0;
    private long hours = 0;
    private long minutes = 0;
    private long seconds = 0;
    private boolean recording = false;
    String time = "";
    public RecordVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        middleBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        DJISampleApplication.getProductInstance().getGimbal().resetGimbal(
                new DJIBaseComponent.DJICompletionCallback() {

                    @Override
                    public void onResult(DJIError error) {

                    }
                });
        DJISampleApplication.getProductInstance().getGimbal().setGimbalWorkMode(DJIGimbal.DJIGimbalWorkMode.FreeMode, new DJIBaseComponent.DJICompletionCallback() {
            @Override
            public void onResult(DJIError error) {
            }
        });
        if (DJIModuleVerificationUtil.isCameraModuleAvailable()) {
            DJISampleApplication.getProductInstance().getCamera().setCameraMode(
                    DJICameraSettingsDef.CameraMode.RecordVideo,
                    new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            Utils.setResultToToast(getContext(), "SetCameraMode to recordVideo");
                        }
                    }
            );
        }
        DJISampleApplication.getProductInstance().getCamera().setDJICameraGeneratedNewMediaFileCallback(
                new DJICamera.CameraGeneratedNewMediaFileCallback() {

                    @Override
                    public void onResult(DJIMedia djiMedia) {
                        Utils.setResultToToast(getContext(), "Index: " + djiMedia.getFileName());
                        try {

                            DateFormat df = new SimpleDateFormat("yyyyMMdd");

                            // Get the date today using Calendar object.
                            Date today = Calendar.getInstance().getTime();

                            String todayDate = df.format(today);
                            final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/osmoLog/" + todayDate);

                            if (!dir.exists())
                            {
                                dir.mkdirs();

                            }
                            File file = new File(dir, djiMedia.getFileNameWithoutExtension() + ".txt");

                            DJIGimbal.DJIGimbalAttitude djiAttitude;
                            djiAttitude = DJISampleApplication.getProductInstance().getGimbal().getAttitudeInDegrees();
                            FileOutputStream fOut = new FileOutputStream(file);
                            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                            myOutWriter.append(String.valueOf(djiAttitude.pitch));
                            myOutWriter.close();
                            fOut.close();

                        } catch (Exception e) {
                            Utils.setResultToToast(getContext(), e.getMessage());
                        }

                    }
                });

    }

    protected void onDetachedToWindow() {
        super.onDetachedFromWindow();

        if (DJIModuleVerificationUtil.isCameraModuleAvailable()) {
            DJISampleApplication.getProductInstance().getCamera().setCameraMode(
                    DJICameraSettingsDef.CameraMode.ShootPhoto,
                    new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            Utils.setResultToToast(getContext(), "SetCameraMode to shootPhoto");
                        }
                    }
            );
        }
    }

    @Override
    protected int getLeftBtnTextResourceId() {
        return R.string.record_video_start_record;
    }

    @Override
    protected int getRightBtnTextResourceId() {
        return R.string.record_video_stop_record;
    }

    @Override
    protected int getMiddleBtnTextResourceId() {
        return R.string.shoot_single_photo;
    }

    @Override
    protected int getInfoResourceId() {
        return R.string.record_video_initial_time;
    }

    @Override
    protected void getLeftBtnMethod() {

        Utils.setResultToText(context, mTexInfo, "00:00:00");
        if (DJIModuleVerificationUtil.isCameraModuleAvailable()) {
            DJISampleApplication.getProductInstance().getCamera().startRecordVideo(
                    new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            //success so, start recording
                            if (null == djiError) {
                                Utils.setResultToToast(getContext(), "Start record");
                                timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        timeCounter = timeCounter + 1;
                                        hours = TimeUnit.MILLISECONDS.toHours(timeCounter);
                                        minutes = TimeUnit.MILLISECONDS.toMinutes(timeCounter) - (hours * 60);
                                        seconds = TimeUnit.MILLISECONDS.toSeconds(timeCounter) - ((hours * 60 * 60) + (minutes * 60));
                                        time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                                        Utils.setResultToText(context, mTexInfo, time);
                                    }
                                }, 0, 1);
                                recording = true;
                            }

                        }
                    }
            );
        }

    }

    @Override
    protected void getRightBtnMethod() {

        if (DJIModuleVerificationUtil.isCameraModuleAvailable()) {
            DJISampleApplication.getProductInstance().getCamera().stopRecordVideo(
                    new DJIBaseComponent.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            Utils.setResultToToast(getContext(), "StopRecord");
                            Utils.setResultToText(context, mTexInfo, "00:00:00");
                            timer.cancel();
                            timeCounter = 0;
                            recording = false;
                        }
                    }
            );
        }
    }


    @Override
    protected void getMUpBtnMethod() {
        if (!recording) {

            DJIGimbal.DJIGimbalAngleRotation mPitch;
            DJIGimbal.DJIGimbalAngleRotation mRoll;
            DJIGimbal.DJIGimbalAngleRotation mYaw;

            mPitch = new DJIGimbal.DJIGimbalAngleRotation(true, 1.0f, DJIGimbal.DJIGimbalRotateDirection.CounterClockwise);
            mRoll = new DJIGimbal.DJIGimbalAngleRotation(true, 0.0f, DJIGimbal.DJIGimbalRotateDirection.CounterClockwise);
            mYaw = new DJIGimbal.DJIGimbalAngleRotation(true, 0.0f, DJIGimbal.DJIGimbalRotateDirection.CounterClockwise);
            DJISampleApplication.getProductInstance().getGimbal().rotateGimbalByAngle(DJIGimbal.DJIGimbalRotateAngleMode.RelativeAngle, mPitch, mRoll, mYaw, new DJIBaseComponent.DJICompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                }
            });
            DJISampleApplication.getProductInstance().getGimbal().setGimbalWorkMode(DJIGimbal.DJIGimbalWorkMode.FreeMode, new DJIBaseComponent.DJICompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                }
            });
        }
        else{Utils.setResultToToast(getContext(), "can't move while recording");}

    }

    @Override
    protected void getMDownBtnMethod() {
        if (!recording) {

            DJIGimbal.DJIGimbalAngleRotation mPitch;
            DJIGimbal.DJIGimbalAngleRotation mRoll;
            DJIGimbal.DJIGimbalAngleRotation mYaw;

            mPitch = new DJIGimbal.DJIGimbalAngleRotation(true, 5.0f, DJIGimbal.DJIGimbalRotateDirection.Clockwise);
            mRoll = new DJIGimbal.DJIGimbalAngleRotation(true, 0.0f, DJIGimbal.DJIGimbalRotateDirection.CounterClockwise);
            mYaw = new DJIGimbal.DJIGimbalAngleRotation(true, 0.0f, DJIGimbal.DJIGimbalRotateDirection.CounterClockwise);
            DJISampleApplication.getProductInstance().getGimbal().rotateGimbalByAngle(DJIGimbal.DJIGimbalRotateAngleMode.RelativeAngle, mPitch, mRoll, mYaw, new DJIBaseComponent.DJICompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                }
            });
            DJISampleApplication.getProductInstance().getGimbal().setGimbalWorkMode(DJIGimbal.DJIGimbalWorkMode.FreeMode, new DJIBaseComponent.DJICompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                }
            });
        }
        else{Utils.setResultToToast(getContext(), "can't move while recording");}
    }


    @Override
    protected void getMLeftBtnMethod() {
        if (!recording) {

            DJIGimbal.DJIGimbalAngleRotation mPitch;
            DJIGimbal.DJIGimbalAngleRotation mRoll;
            DJIGimbal.DJIGimbalAngleRotation mYaw;

            mPitch = new DJIGimbal.DJIGimbalAngleRotation(true, 0.0f, DJIGimbal.DJIGimbalRotateDirection.CounterClockwise);
            mRoll = new DJIGimbal.DJIGimbalAngleRotation(true, 0.0f, DJIGimbal.DJIGimbalRotateDirection.CounterClockwise);
            mYaw = new DJIGimbal.DJIGimbalAngleRotation(true, 5.0f, DJIGimbal.DJIGimbalRotateDirection.CounterClockwise);
            DJISampleApplication.getProductInstance().getGimbal().rotateGimbalByAngle(DJIGimbal.DJIGimbalRotateAngleMode.RelativeAngle, mPitch, mRoll, mYaw, new DJIBaseComponent.DJICompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                }
            });
            DJISampleApplication.getProductInstance().getGimbal().setGimbalWorkMode(DJIGimbal.DJIGimbalWorkMode.FreeMode, new DJIBaseComponent.DJICompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                }
            });
        }
        else{Utils.setResultToToast(getContext(), "can't move while recording");}

    }


    @Override
    protected void getMRightBtnMethod() {

        if (!recording) {

            DJIGimbal.DJIGimbalAngleRotation mPitch;
            DJIGimbal.DJIGimbalAngleRotation mRoll;
            DJIGimbal.DJIGimbalAngleRotation mYaw;

            mPitch = new DJIGimbal.DJIGimbalAngleRotation(true, 0.0f, DJIGimbal.DJIGimbalRotateDirection.Clockwise);
            mRoll = new DJIGimbal.DJIGimbalAngleRotation(true, 0.0f, DJIGimbal.DJIGimbalRotateDirection.CounterClockwise);
            mYaw = new DJIGimbal.DJIGimbalAngleRotation(true, 5.0f, DJIGimbal.DJIGimbalRotateDirection.Clockwise);
            DJISampleApplication.getProductInstance().getGimbal().rotateGimbalByAngle(DJIGimbal.DJIGimbalRotateAngleMode.RelativeAngle, mPitch, mRoll, mYaw, new DJIBaseComponent.DJICompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                }
            });
            DJISampleApplication.getProductInstance().getGimbal().setGimbalWorkMode(DJIGimbal.DJIGimbalWorkMode.FreeMode, new DJIBaseComponent.DJICompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                }
            });
        }
        else{Utils.setResultToToast(getContext(), "can't move while recording");}
    }


    @Override
    protected void getMiddleBtnMethod() {

    }
}
