//
//  EngineUtil.h
//  GameCity
//
//  Created by Apple on 2017/5/7.
//
//

#import <Foundation/Foundation.h>

@interface EngineUtil : NSObject
+ (NSString *)getDeviceInfo;
+ (NSString *)getDeviceID;
+ (void)startBay:(NSString *)uid token:(NSString *)token payType:(NSNumber *)payType payAmount:(NSNumber *)payAmount applePay:(NSNumber *)applePay;
+ (void)nativeLog:(NSString *)tag msg:(NSString *)msg;
+ (void)onLoginSuccess:(NSString *)uid token:(NSString *)token;
+ (void)joinQQGroup;
+ (void)changeUserAvatar:(NSNumber *)type;
+ (void)startWatchNetWorkState;
+ (NSString *)getDetailModel;

+ (NSString *)getPackageName;
+ (NSString *)getVersionName;
+ (void)openUrlByGameCity:(NSString *)url;
+ (void)openUrlByDefaultBrowser:(NSString *)url;
+ (void)openUrlBannerWebView:(NSString *)url direction:(NSString *)direction;
+ (void)openUrlByH5Browser:(NSString *)url;
+ (void)openUrlByPayBrowser:(NSString *)url isLandscape:(NSString *)isLandscape requestCode:(NSString *)code;
+ (void)openUrlByWxMiniProgram:(NSString *)chargeInfo;
+ (void)openNativeAliBay:(NSString *)url;
+ (void)openAlbum;

+ (NSString *)getVersionCode;
+ (NSString *)getChannel;
+ (BOOL)isNetworkAvailable;

// + (void)startQQ;
// + (void)startWechat;
+ (void)startApp:(int)appType;
+ (void)copyText:(NSString *)strText;
+ (void)quitGame;
+ (BOOL)hasWechat;
+ (BOOL)hasQQ;
+ (void)closePush;
+ (void)sendPushNewsWithValue:(NSString *)value title:(NSString *)title body:(NSString *)body pushInfo:(NSString *)pushInfo;

+ (void)saveStringToPreference:(NSString *)key value:(NSString *)value;
+ (NSString *)getStringFromPreference:(NSString *)key;

// KeyChain
+ (void)saveStringToKeyChain:(NSString *)key value:(NSString *)value;
+ (NSString *)loadStringFromKeyChain:(NSString *)key;
+ (void)saveDeviceIDToKeyChain:(NSString *)deviceID;
+ (NSString *)loadDeviceIDFromKeyChain;
+ (void)saveUserTagToKeyChain:(NSString *)deviceID;
+ (NSString *)loadUserTagFromKeyChain;
+ (void)deleteAccountFromKeyChain:(NSString *)account;
+ (void)saveAccountToKeyChain:(NSString *)account pwd:(NSString *)pwd;
+ (NSString *)loadAccountFromKeyChain:(NSNumber *)account_index key:(NSNumber *)value_index;
// 截屏
+(void)screenShotWithPayTypeAction:(NSString *)payType;


+ (void)saveAccountsToNative:(NSString *)accountStr;
+ (NSString *)getAccountsFromNative;
+ (void)shareImgToWc:(NSString *)url isSaveImg:(NSString *) isSaveImg;
+ (void)shareWcMoneyToWc:(NSString *)url isSaveImg:(NSString *) isSaveImg;
+ (NSString *)getClipboardText;
+ (void)cachePromotionImage:(NSString *)url type:(NSNumber *)type;

/**
 保存图片到相册

 @param url 图片url
 */
+(void)getImageUrl:(NSString *)url;


/**
 是否有相册权限

 @return yes or no
 */
+(BOOL)isHasPhotoLibraryAuthorization;

//Flurry 相关

/**
 执行track统计
 
 @param event 事件(请检查传入值)
 */
+(void)trackEvent:(NSString *)event;
/**
 执行track统计
 
 @param event 事件(请检查传入值)
 @param action 动作(请检查传入值)
 */
+(void)trackEvent:(NSString *)event action:(NSString *)action;
/**
 执行track统计
 
 @param event 事件(请检查传入值)
 @param action 动作(请检查传入值)
 @param category 类别(请检查传入值)
 */
+(void)trackEvent:(NSString *)event action:(NSString *)action category:(NSString *)category;

/**
 执行track统计时间
 
 @param event 事件(请检查传入值)
 */
+(void)trackDuration:(NSString *)event;

/**
 手动上报error

 @param name 名字
 @param msg 信息
 @param e 错误
 */
+(void)logError:(NSString *)name message:(NSString *)msg exception:(NSException *)e;

/**
 手动上报error

 @param name 名字
 @param msg 信息
 @param e 错误
 */
+(void)logError:(NSString *)name message:(NSString *)msg exceptionString:(NSString *)e;

@end
