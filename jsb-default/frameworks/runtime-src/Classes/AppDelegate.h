#pragma once
#include "platform/CCApplication.h"
class  AppDelegate : public cocos2d::Application
{
public:
    AppDelegate(int width, int height);
    virtual ~AppDelegate();
    virtual bool applicationDidFinishLaunching() override;
    virtual void applicationDidEnterBackground() override;
    virtual void applicationWillEnterForeground() override;
};
