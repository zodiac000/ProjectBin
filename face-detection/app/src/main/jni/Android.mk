LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#OPENCV_INSTALL_MODULES:=off
#OPENCV_LIB_TYPE:=SHARED
ifdef OPENCV_ANDROID_SDK
  ifneq ("","$(wildcard D:/opencv-3.3.0-android-sdk/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk)")
    include D:/opencv-3.3.0-android-sdk/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk
  else
    include D:/opencv-3.3.0-android-sdk/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk
  endif
else
  include D:/opencv-3.3.0-android-sdk/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk
endif

LOCAL_SRC_FILES  := DetectionBasedTracker_jni.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS     += -llog -ldl

LOCAL_MODULE     := detection_based_tracker

include $(BUILD_SHARED_LIBRARY)
