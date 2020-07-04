//
//  DeviceUtil.m
//  Apple
//
//  Created by Apple on 16/2/23.
//  Copyright © 2016年 Apple. All rights reserved.
//

#import "DeviceUtil.h"
//#import "sys/utsname.h"
#import "UUIDStringUtil.h"
#import <AdSupport/AdSupport.h>
#import "EngineUtil.h"

static NSString* device_info;

@implementation DeviceUtil
+ (void)init {

    if (nil == device_info) {
        UIDevice* device = [UIDevice new];

        //NSString* name = device.name;
        //NSString* model = device.model;
        //NSString* type = device.localizedModel;
        NSString* systemName = device.systemName;
        NSString* systemVersion = device.systemVersion;
        //name: iPhone Simulator, model: iPhone, type: iPhone, systemName: iPhone OS, systemVersion: 9.2
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
//
////获得设备型号
//+ (NSString *)getCurrentDeviceModel
//{
//
//    struct utsname systemInfo;
//    uname(&systemInfo);
//
//
//    NSString *platform = [NSString stringWithCString:systemInfo.machine encoding:NSASCIIStringEncoding];
//
//
//    if ([platform isEqualToString:@"iPhone1,1"]) return @"iPhone 2G (A1203)";
//    if ([platform isEqualToString:@"iPhone1,2"]) return @"iPhone 3G (A1241/A1324)";
//    if ([platform isEqualToString:@"iPhone2,1"]) return @"iPhone 3GS (A1303/A1325)";
//    if ([platform isEqualToString:@"iPhone3,1"]) return @"iPhone 4 (A1332)";
//    if ([platform isEqualToString:@"iPhone3,2"]) return @"iPhone 4 (A1332)";
//    if ([platform isEqualToString:@"iPhone3,3"]) return @"iPhone 4 (A1349)";
//    if ([platform isEqualToString:@"iPhone4,1"]) return @"iPhone 4S (A1387/A1431)";
//    if ([platform isEqualToString:@"iPhone5,1"]) return @"iPhone 5 (A1428)";
//    if ([platform isEqualToString:@"iPhone5,2"]) return @"iPhone 5 (A1429/A1442)";
//    if ([platform isEqualToString:@"iPhone5,3"]) return @"iPhone 5c (A1456/A1532)";
//    if ([platform isEqualToString:@"iPhone5,4"]) return @"iPhone 5c (A1507/A1516/A1526/A1529)";
//    if ([platform isEqualToString:@"iPhone6,1"]) return @"iPhone 5s (A1453/A1533)";
//    if ([platform isEqualToString:@"iPhone6,2"]) return @"iPhone 5s (A1457/A1518/A1528/A1530)";
//    if ([platform isEqualToString:@"iPhone7,1"]) return @"iPhone 6 Plus (A1522/A1524)";
//    if ([platform isEqualToString:@"iPhone7,2"]) return @"iPhone 6 (A1549/A1586)";
//
//    if ([platform isEqualToString:@"iPod1,1"])   return @"iPod Touch 1G (A1213)";
//    if ([platform isEqualToString:@"iPod2,1"])   return @"iPod Touch 2G (A1288)";
//    if ([platform isEqualToString:@"iPod3,1"])   return @"iPod Touch 3G (A1318)";
//    if ([platform isEqualToString:@"iPod4,1"])   return @"iPod Touch 4G (A1367)";
//    if ([platform isEqualToString:@"iPod5,1"])   return @"iPod Touch 5G (A1421/A1509)";
//
//    if ([platform isEqualToString:@"iPad1,1"])   return @"iPad 1G (A1219/A1337)";
//
//    if ([platform isEqualToString:@"iPad2,1"])   return @"iPad 2 (A1395)";
//    if ([platform isEqualToString:@"iPad2,2"])   return @"iPad 2 (A1396)";
//    if ([platform isEqualToString:@"iPad2,3"])   return @"iPad 2 (A1397)";
//    if ([platform isEqualToString:@"iPad2,4"])   return @"iPad 2 (A1395+New Chip)";
//    if ([platform isEqualToString:@"iPad2,5"])   return @"iPad Mini 1G (A1432)";
//    if ([platform isEqualToString:@"iPad2,6"])   return @"iPad Mini 1G (A1454)";
//    if ([platform isEqualToString:@"iPad2,7"])   return @"iPad Mini 1G (A1455)";
//
//    if ([platform isEqualToString:@"iPad3,1"])   return @"iPad 3 (A1416)";
//    if ([platform isEqualToString:@"iPad3,2"])   return @"iPad 3 (A1403)";
//    if ([platform isEqualToString:@"iPad3,3"])   return @"iPad 3 (A1430)";
//    if ([platform isEqualToString:@"iPad3,4"])   return @"iPad 4 (A1458)";
//    if ([platform isEqualToString:@"iPad3,5"])   return @"iPad 4 (A1459)";
//    if ([platform isEqualToString:@"iPad3,6"])   return @"iPad 4 (A1460)";
//
//    if ([platform isEqualToString:@"iPad4,1"])   return @"iPad Air (A1474)";
//    if ([platform isEqualToString:@"iPad4,2"])   return @"iPad Air (A1475)";
//    if ([platform isEqualToString:@"iPad4,3"])   return @"iPad Air (A1476)";
//    if ([platform isEqualToString:@"iPad4,4"])   return @"iPad Mini 2G (A1489)";
//    if ([platform isEqualToString:@"iPad4,5"])   return @"iPad Mini 2G (A1490)";
//    if ([platform isEqualToString:@"iPad4,6"])   return @"iPad Mini 2G (A1491)";
//
//    if ([platform isEqualToString:@"i386"])      return @"iPhone Simulator";
//    if ([platform isEqualToString:@"x86_64"])    return @"iPhone Simulator";
//    return platform;
//}

@end
