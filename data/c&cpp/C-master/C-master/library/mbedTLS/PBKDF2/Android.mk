LOCAL_PATH      := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := pbkdf2
LOCAL_SRC_FILES := $(wildcard mbedtls/*.c)

include $(BUILD_SHARED_LIBRARY)
