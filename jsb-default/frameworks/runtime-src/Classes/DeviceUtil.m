#import "DeviceUtil.h"
#import "UUIDStringUtil.h"
#import <AdSupport/AdSupport.h>
#import "EngineUtil.h"
static NSString* device_info;
@implementation DeviceUtil
+ (void)init {
    if (nil == device_info) {
        UIDevice* device = [UIDevice new];
        NSString* systemName = device.systemName;
        NSString* systemVersion = device.systemVersion;
        NSString* aid = user_defaults_get_string(@"_global_idfv_device_id");
        if (NULL == aid || [aid isKindOfClass:[NSNull class]] || [aid length] <= 0) {
            aid = [EngineUtil loadDeviceIDFromKeyChain];
        }
        if (NULL == aid || [aid isKindOfClass:[NSNull class]] || [aid length] <= 0) {
            if([[ASIdentifierManager sharedManager] isAdvertisingTrackingEnabled])
            {
                aid = [[[ASIdentifierManager sharedManager] advertisingIdentifier] UUIDString];
            } else {
                aid = [[device identifierForVendor] UUIDString];
            }
        }
        if (NULL == aid || [aid isKindOfClass:[NSNull class]] || [aid length] <= 0) {
            aid = [UUIDStringUtil createUUIDString];
        }
        NSString* countryCode = [[NSLocale currentLocale] objectForKey:NSLocaleCountryCode];
        NSString* languageCode = [[NSLocale currentLocale] objectForKey:NSLocaleLanguageCode];
        NSString* versionCode = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleVersion"];
        NSString* versionName = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleShortVersionString"];
        device_info = [NSString stringWithFormat:@"aid=%@&code=%@&lan=%@&svc=%@&svn=%@&cvn=%@&cvc=%d&chn=%@",
                       aid, countryCode, languageCode, systemVersion, systemName, versionName,1, @"0"];
        user_defaults_set_string(@"_global_idfv_device_id", aid);
        user_defaults_set_string(@"_global_idfv_device_info", device_info);
        [EngineUtil saveDeviceIDToKeyChain:aid];
    }
    NSLog(@"device_info: %@", device_info);
}
+ (NSString*)deviceInfo {
    return device_info;
}
@end
