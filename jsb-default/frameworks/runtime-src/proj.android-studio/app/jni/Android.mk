LOCAL_PATH := $(call my-dir)

# --- bugly: 引用 libBugly.so ---
include $(CLEAR_VARS)
LOCAL_MODULE := bugly_native_prebuilt
LOCAL_SRC_FILES := prebuilt/$(TARGET_ARCH_ABI)/libBugly.so
include $(PREBUILT_SHARED_LIBRARY)
# --- bugly: end ---

include $(CLEAR_VARS)

LOCAL_MODULE := cocos2djs_shared

LOCAL_MODULE_FILENAME := libcocos2djs

ifeq ($(USE_ARM_MODE),1)
LOCAL_ARM_MODE := arm
endif

# --- bugly: 增加cpp扩展名mm
LOCAL_CPP_EXTENSION := .mm .cpp .cc
LOCAL_CFLAGS += -x c++

#

LOCAL_SRC_FILES := hellojavascript/main.cpp \
				   ../../../Classes/AppDelegate.cpp \
				   ../../../Classes/bugly/CrashReport.mm \
				   ../../../Classes/jsb_module_register.cpp \

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../../Classes
LOCAL_STATIC_LIBRARIES := cocos2d_js_static
LOCAL_STATIC_LIBRARIES := cocos2dx_static

LOCAL_EXPORT_CFLAGS := -DCOCOS2D_DEBUG=2 -DCOCOS2D_JAVASCRIPT
include $(BUILD_SHARED_LIBRARY)

$(call import-module, cocos)
