#import "EngineUtil.h"
#import "sys/utsname.h"
#import "DeviceUtil.h"
@implementation EngineUtil
+ (void)joinQQGroup
{
    NSDictionary *dict = [[NSDictionary alloc] initWithObjectsAndKeys:
                          @"COMMAND_JOIN_QQ_GROUP", @"cmd",
                          nil];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"" object:dict];
}
+ (void)changeUserAvatar:(NSNumber *)type
{
    NSDictionary *dict = [[NSDictionary alloc] initWithObjectsAndKeys:
                          @"COMMAND_CHANGE_AVATAR", @"cmd",
                          type, @"type",
                          nil];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"xxxx" object:dict];
}
+ (BOOL)isNetworkAvailable
{
    return true;
}
+ (NSString *)getDeviceInfo
{
    [DeviceUtil init];
    NSLog(@"this device info is %@",[DeviceUtil deviceInfo]);
    return [DeviceUtil deviceInfo];
}
+ (NSString *)getDeviceID
{
    NSString *deviceID = user_defaults_get_string(@"_global_idfv_device_id");
    return deviceID;
}
+ (void)startBay:(NSString *)uid token:(NSString *)token payType:(NSNumber *)payType payAmount:(NSNumber *)payAmount applePay:(NSNumber *)applePay
{
}
+ (void)nativeLog:(NSString *)tag msg:(NSString *)msg
{
    NSLog(@"%s, TAG: %@ --> Msg: %@", __FUNCTION__, tag, msg);
}
+ (void)onLoginSuccess:(NSString *)uid token:(NSString *)token
{
    user_defaults_set_string(@"_x_key_user_uid_01", uid);
    user_defaults_set_string(@"_x_key_user_token_01", token);
    NSLog(@"%s, uid: %@, token: %@", __FUNCTION__, uid, token);
}
+ (void)startWatchNetWorkState
{
}
+ (void)closePush
{
}
+ (void)sendPushNewsWithValue:(NSString *)value title:(NSString *)title body:(NSString *)body pushInfo:(NSString *)pushInfo
{
}
+ (void)quitGame
{
    exit(0);
}
+ (BOOL)hasApp:(NSString *)pkgName
{
    return NO;
}
+ (NSString *)getPackageName
{
    return [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleIdentifier"];
}
+ (NSString *)getVersionName
{
   return [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
}
+ (void)openUrlByGameCity:(NSString *)url
{
}
+ (void)openUrlByDefaultBrowser:(NSString *)url
{
}
+ (void)openUrlByH5Browser:(NSString *)url
{
}
+ (void)openUrlByPayBrowser:(NSString *)url isLandscape:(NSString *)isLandscape requestCode:(NSString *)code
{
}
+ (void)openUrlByWxMiniProgram:(NSString *)chargeInfo
{
}
+ (void)openUrlBannerWebView:(NSString *)url direction:(NSString *)direction
{
}
+ (void)openNativeAliBay:(NSString *)url {
}
+ (void)openAlbum{
}
+ (void)createQRCodeToSave:(NSString *)url qrcodeUrl:(NSString *)qrcodeUrl{
}
+ (NSString *)getVersionCode
{
     return [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
}
+ (NSString *)getChannel
{
    return @"test0625";
}
+ (BOOL)hasWechat{
    return [[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"weixin://"]];
}
+ (BOOL)hasQQ{
    return [[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"mqq://"]];
}
+ (void)startApp:(int)appType
{
    switch (appType) {
        case 1:             
            break;
        case 2:             
            break;
        default:
            break;
    }
}
+ (void)copyText:(NSString *)strText
{
    UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
    pasteboard.string = [NSString stringWithFormat:@"%@", strText];
}
+ (void)saveStringToPreference:(NSString *)key value:(NSString *)value
{
    user_defaults_set_string(key, value);
}
+ (NSString *)getStringFromPreference:(NSString *)key
{
    return user_defaults_get_string(key);
}
+ (void)saveDeviceIDToKeyChain:(NSString *)deviceID
{
}
+ (NSString *)loadDeviceIDFromKeyChain
{
    return @"";
}
+ (void)saveStringToKeyChain:(NSString *)key value:(NSString *)value
{
}
+ (NSString *)loadStringFromKeyChain:(NSString *)key
{
    return @"";
}
+ (void)saveUserTagToKeyChain:(NSString *)deviceID
{
}
+ (NSString *)loadUserTagFromKeyChain
{
    return @"";
}
+ (void)saveAccountToKeyChain:(NSString *)account pwd:(NSString *)pwd
{
}
+ (void)deleteAccountFromKeyChain:(NSString *)account
{
}
+ (NSString *)loadAccountFromKeyChain:(NSNumber *)account_index key:(NSNumber *)value_index
{
    return @"";
}
+(void)screenShotWithPayTypeAction:(NSString *)payType
{
}
+ (void)saveAccountsToNative:(NSString *)accountStr{
}
+ (NSString *)getAccountsFromNative{
    return nil;
}
+ (void)cachePromotionImage:(NSString *)url type:(NSNumber *)type{
}
+(void)getImageUrl:(NSString *)url {
}
+ (void)image:(UIImage *)image didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo {
}
+ (NSData *)getCachePromotionData:(int) type{
    return nil;
}
+ (void)shareImgToWc:(NSString *)url isSaveImg:(NSString *) isSaveImg{
    [self generateQrImageAndShare:url type:1 qrSize:212 x:468 y:1032 isSaveImg:isSaveImg];
}
+ (void)shareWcMoneyToWc:(NSString *)url isSaveImg:(NSString *) isSaveImg{
    [self generateQrImageAndShare:url type:2 qrSize:230 x:245 y:936 isSaveImg:isSaveImg];
}
+ (void)generateQrImageAndShare:(NSString *)url type:(int)type qrSize:(CGFloat)qrSize x:(CGFloat) x y:(CGFloat)y isSaveImg:(NSString *) isSaveImg{
}
+ (void)shareWcMoneyToWc:(NSString *)url money:(NSString *)money{
}
+(UIImage *) mergeImageByBase:(UIImage *)baseImage qrImage:(UIImage *)qrImage left:(CGFloat) left top:(CGFloat) top qrSize:(CGFloat) qrSize{
    return nil;
}
+ (CGSize)sizeWithFont:(NSString *)text font:(UIFont *)font maxSize:(CGSize)maxSize
{
    NSDictionary *attrs = @{NSFontAttributeName : font};
    return [text boundingRectWithSize:maxSize options:NSStringDrawingUsesLineFragmentOrigin attributes:attrs context:nil].size;
}
+(void) sharePhotoToWc:(UIImage *)shareImage{
}
+(UIImage *) getQrImageBySize:(NSString *)url size:(CGFloat)size{
    return nil;
}
+(UIImage *) getScaledImage:(CIImage *)image size:(CGFloat) size{
    return [UIImage imageWithCGImage:nil];
}
+ (NSString *)getClipboardText{
    UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
    return pasteboard.string;
}
+ (void)showToast:(NSString *)text
{
}
+(void)trackEvent:(NSString *)event {
    if (!event || [event isEqualToString:@""]) {
        return;
    }
}
+(void)trackEvent:(NSString *)event action:(NSString *)action {
    if (!event || [event isEqualToString:@""]) {
        return;
    }
    if (!action || [action isEqualToString:@""]) {
        return;
    }
    NSString *trackEvent = [NSString stringWithFormat:@"%@_%@", action, event];
    if (!trackEvent || [trackEvent isEqualToString:@""]) {
        return;
    }
}
+(void)trackEvent:(NSString *)event action:(NSString *)action category:(NSString *)category {
    if (!event || [event isEqualToString:@""]) {
        return;
    }
    if (!action || [action isEqualToString:@""]) {
        return;
    }
    if (!category || [category isEqualToString:@""]) {
        return;
    }
    NSString *trackEvent = [NSString stringWithFormat:@"%@_%@_%@", category, action, event];
    if (!trackEvent || [trackEvent isEqualToString:@""]) {
        return;
    }
}
+(void)trackDuration:(NSString *)event {
    if (!event || [event isEqualToString:@""]) {
        return;
    }
    NSString *lastString = @"";
    NSArray *event_array = [event componentsSeparatedByString:@"_"];
    if (event_array != nil && event_array.count > 0) {
        if ([event_array lastObject]) {
            lastString = [event_array lastObject];
        }
    }
}
+(void)logError:(NSString *)name message:(NSString *)msg exceptionString:(NSString *)e {
}
+(void)logError:(NSString *)name message:(NSString *)msg exception:(NSException *)e {
    if (!name || [name isEqualToString:@""]) {
        return;
    }
}
+(void) onMainJSStartRunning {
    NSLog(@"GameStart %s", __FUNCTION__);
    [[NSNotificationCenter defaultCenter] postNotificationName:@"_on_main_js_start_running" object:nil];
}
+(void) onStartLoadLaunchScene {
    NSLog(@"GameStart %s", __FUNCTION__);
    [[NSNotificationCenter defaultCenter] postNotificationName:@"_on_main_js_start_load_launche_scene" object:nil];
}
+(void) onFinishLoadLaunchScene {
    NSLog(@"GameStart %s", __FUNCTION__);
    [[NSNotificationCenter defaultCenter] postNotificationName:@"_on_main_js_finish_load_launche_scene" object:nil];
}
+ (NSString*)getDetailModel
{
    struct utsname systemInfo;
    uname(&systemInfo);
    NSString *deviceString = [NSString stringWithCString:systemInfo.machine encoding:NSUTF8StringEncoding];
    if ([deviceString isEqualToString:@"iPhone1,1"])    return @"iPhone 1G";
    if ([deviceString isEqualToString:@"iPhone1,2"])    return @"iPhone 3G";
    if ([deviceString isEqualToString:@"iPhone2,1"])    return @"iPhone 3GS";
    if ([deviceString isEqualToString:@"iPhone3,1"])    return @"iPhone 4";
    if ([deviceString isEqualToString:@"iPhone3,2"])    return @"Verizon iPhone 4";
    if ([deviceString isEqualToString:@"iPhone4,1"])    return @"iPhone 4S";
    if ([deviceString isEqualToString:@"iPhone5,1"])    return @"iPhone 5";
    if ([deviceString isEqualToString:@"iPhone5,2"])    return @"iPhone 5";
    if ([deviceString isEqualToString:@"iPhone5,3"])    return @"iPhone 5C";
    if ([deviceString isEqualToString:@"iPhone5,4"])    return @"iPhone 5C";
    if ([deviceString isEqualToString:@"iPhone6,1"])    return @"iPhone 5S";
    if ([deviceString isEqualToString:@"iPhone6,2"])    return @"iPhone 5S";
    if ([deviceString isEqualToString:@"iPhone7,1"])    return @"iPhone 6 Plus";
    if ([deviceString isEqualToString:@"iPhone7,2"])    return @"iPhone 6";
    if ([deviceString isEqualToString:@"iPhone8,1"])    return @"iPhone 6s";
    if ([deviceString isEqualToString:@"iPhone8,2"])    return @"iPhone 6s Plus";
    if ([deviceString isEqualToString:@"iPhone8,4"])    return @"iPhone SE";
    if ([deviceString isEqualToString:@"iPhone9,1"]
        || [deviceString isEqualToString:@"iPhone9,3"])    return @"iPhone 7";
    if ([deviceString isEqualToString:@"iPhone9,2"]
        || [deviceString isEqualToString:@"iPhone9,4"])    return @"iPhone 7 Plus";
    if ([deviceString isEqualToString:@"iPhone10,1"]
        || [deviceString isEqualToString:@"iPhone10,4"])    return @"iPhone 8";
    if ([deviceString isEqualToString:@"iPhone10,2"]
        || [deviceString isEqualToString:@"iPhone10,5"])    return @"iPhone 8 Plus";
    if ([deviceString isEqualToString:@"iPhone10,3"]
        || [deviceString isEqualToString:@"iPhone10,6"])    return @"iPhone X";
    if ([deviceString isEqualToString:@"iPhone11,8"])    return @"iPhone XR";
    if ([deviceString isEqualToString:@"iPhone11,2"])    return @"iPhone XS";
    if ([deviceString isEqualToString:@"iPhone11,6"])    return @"iPhone XS Max";
    if ([deviceString isEqualToString:@"iPod1,1"])      return @"iPod Touch 1G";
    if ([deviceString isEqualToString:@"iPod2,1"])      return @"iPod Touch 2G";
    if ([deviceString isEqualToString:@"iPod3,1"])      return @"iPod Touch 3G";
    if ([deviceString isEqualToString:@"iPod4,1"])      return @"iPod Touch 4G";
    if ([deviceString isEqualToString:@"iPod5,1"])      return @"iPod Touch 5G";
    if ([deviceString isEqualToString:@"iPod7,1"])      return @"iPod Touch 6G";
    if ([deviceString isEqualToString:@"iPad1,1"])      return @"iPad";
    if ([deviceString isEqualToString:@"iPad2,1"])      return @"iPad 2 (WiFi)";
    if ([deviceString isEqualToString:@"iPad2,2"])      return @"iPad 2 (GSM)";
    if ([deviceString isEqualToString:@"iPad2,3"])      return @"iPad 2 (CDMA)";
    if ([deviceString isEqualToString:@"iPad2,4"])      return @"iPad 2 (32nm)";
    if ([deviceString isEqualToString:@"iPad2,5"])      return @"iPad mini (WiFi)";
    if ([deviceString isEqualToString:@"iPad2,6"])      return @"iPad mini (GSM)";
    if ([deviceString isEqualToString:@"iPad2,7"])      return @"iPad mini (CDMA)";
    if ([deviceString isEqualToString:@"iPad3,1"])      return @"iPad 3(WiFi)";
    if ([deviceString isEqualToString:@"iPad3,2"])      return @"iPad 3(CDMA)";
    if ([deviceString isEqualToString:@"iPad3,3"])      return @"iPad 3(4G)";
    if ([deviceString isEqualToString:@"iPad3,4"])      return @"iPad 4 (WiFi)";
    if ([deviceString isEqualToString:@"iPad3,5"])      return @"iPad 4 (4G)";
    if ([deviceString isEqualToString:@"iPad3,6"])      return @"iPad 4 (CDMA)";
    if ([deviceString isEqualToString:@"iPad4,1"])      return @"iPad Air";
    if ([deviceString isEqualToString:@"iPad4,2"])      return @"iPad Air";
    if ([deviceString isEqualToString:@"iPad4,3"])      return @"iPad Air";
    if ([deviceString isEqualToString:@"iPad4,4"]
        ||[deviceString isEqualToString:@"iPad4,5"]
        ||[deviceString isEqualToString:@"iPad4,6"])      return @"iPad mini 2";
    if ([deviceString isEqualToString:@"iPad4,7"]
        ||[deviceString isEqualToString:@"iPad4,8"]
        ||[deviceString isEqualToString:@"iPad4,9"])      return @"iPad mini 3";
    if ([deviceString isEqualToString:@"iPad5,1"]
        || [deviceString isEqualToString:@"iPad5,2"])      return @"iPad mini 4";
    if ([deviceString isEqualToString:@"iPad5,3"])      return @"iPad Air 2";
    if ([deviceString isEqualToString:@"iPad5,4"])      return @"iPad Air 2";
    if ([deviceString isEqualToString:@"iPad6,3"]
        || [deviceString isEqualToString:@"iPad6,4"])      return @"iPad Pro 9.7-inch";
    if ([deviceString isEqualToString:@"iPad6,7"]
        || [deviceString isEqualToString:@"iPad6,8"])      return @"iPad Pro 12.9-inch";
    if ([deviceString isEqualToString:@"iPad6,11"]
        || [deviceString isEqualToString:@"iPad6,12"])      return @"iPad 5Th";
    if ([deviceString isEqualToString:@"iPad7,1"]
        || [deviceString isEqualToString:@"iPad7,2"])      return @"iPad Pro 12.9-inch 2nd";
    if ([deviceString isEqualToString:@"iPad7,3"]
        || [deviceString isEqualToString:@"iPad7,4"])      return @"iPad Pro 10.5-inch";
    if ([deviceString isEqualToString:@"AirPods1,1"])      return @"AirPods";
    if ([deviceString isEqualToString:@"AppleTV2,1"])      return @"AppleTV 2";
    if ([deviceString isEqualToString:@"AppleTV3,1"]
        ||[deviceString isEqualToString:@"AppleTV3,2"])      return @"AppleTV 3";
    if ([deviceString isEqualToString:@"AppleTV5,3"])      return @"AppleTV 4";
    if ([deviceString isEqualToString:@"AppleTV6,2"])      return @"AppleTV 4K";
    if ([deviceString isEqualToString:@"Watch1,1"]
        ||[deviceString isEqualToString:@"Watch1,2"])      return @"Apple Watch1";
    if ([deviceString isEqualToString:@"Watch2,6"]
        ||[deviceString isEqualToString:@"Watch2,7"])      return @"Apple Watch Series 1";
    if ([deviceString isEqualToString:@"Watch2,3"]
        ||[deviceString isEqualToString:@"Watch2,4"])      return @"Apple Watch Series 2";
    if ([deviceString isEqualToString:@"Watch3,1"]
        ||[deviceString isEqualToString:@"Watch3,2"]
        ||[deviceString isEqualToString:@"Watch3,3"]
        ||[deviceString isEqualToString:@"Watch3,4"])      return @"Apple Watch Series 3";
    if ([deviceString isEqualToString:@"AudioAccessory1,1"])      return @"HomePod";
    if ([deviceString isEqualToString:@"i386"])         return @"Simulator";
    if ([deviceString isEqualToString:@"x86_64"])       return @"Simulator";
    return deviceString;
}
+(BOOL)isHasPhotoLibraryAuthorization {
    return true;
}
@end
