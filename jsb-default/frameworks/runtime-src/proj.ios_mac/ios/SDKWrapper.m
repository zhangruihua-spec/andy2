#import "SDKWrapper.h"
#import "SDKDelegate.h"
@interface SDKWrapper ()
@property (nonatomic, strong) NSArray *sdkClasses;
@end
@implementation SDKWrapper
#pragma mark -
#pragma mark Singleton
static SDKWrapper *mInstace = nil;
+ (instancetype)getInstance {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        mInstace = [[super allocWithZone:NULL] init];
        [mInstace initSDKWrapper];
    });
    return mInstace;
}
+ (id)allocWithZone:(struct _NSZone *)zone {
    return [SDKWrapper getInstance];
}
+ (id)copyWithZone:(struct _NSZone *)zone {
    return [SDKWrapper getInstance];
}
#pragma mark -
#pragma mark Application lifecycle
- (void)initSDKWrapper {
    [self loadSDKClass];
}
- (void)loadSDKClass {
    NSString *path = [NSString stringWithFormat:@"%@/project.json",
                      [[NSBundle mainBundle] resourcePath]];
    NSData *data = [NSData dataWithContentsOfFile:path options:NSDataReadingMappedIfSafe error:nil];
    id obj = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    id dic = obj[@"serviceClassPath"];
    NSLog(@"%@",dic);
    NSMutableArray *sdks = [NSMutableArray array];
    for (NSString *str in dic) {
        @try {
            NSString *className = [[str componentsSeparatedByString:@"."] lastObject];
            NSLog(@"%@",className);
            Class c = NSClassFromString(className);
            id sdk = [[c alloc] init];
            [sdks addObject:sdk];
            NSLog(@"%@",sdk);
        } @catch (NSException *e) {
            NSLog(@"%@",@"NSException");
            NSLog(@"%@",e);
        }
    }
    self.sdkClasses = [NSArray arrayWithArray:sdks];
}
- (void)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    for (id <SDKDelegate> sdk in self.sdkClasses) {
        if ([sdk respondsToSelector:@selector(application:didFinishLaunchingWithOptions:)]) {
            [sdk application:application didFinishLaunchingWithOptions:launchOptions];
        }
    }
}
- (void)applicationDidBecomeActive:(UIApplication *)application {
    for (id sdk in self.sdkClasses) {
        if ([sdk respondsToSelector:@selector(applicationDidBecomeActive:)]) {
            [sdk applicationDidBecomeActive:application];
        }
    }
}
- (void)applicationWillResignActive:(UIApplication *)application {
    for (id sdk in self.sdkClasses) {
        if ([sdk respondsToSelector:@selector(applicationWillResignActive:)]) {
            [sdk applicationWillResignActive:application];
        }
    }
}
- (void)applicationDidEnterBackground:(UIApplication *)application {
    for (id sdk in self.sdkClasses) {
        if ([sdk respondsToSelector:@selector(applicationDidEnterBackground:)]) {
            [sdk applicationDidEnterBackground:application];
        }
    }
}
- (void)applicationWillEnterForeground:(UIApplication *)application {
    for (id sdk in self.sdkClasses) {
        if ([sdk respondsToSelector:@selector(applicationWillEnterForeground:)]) {
            [sdk applicationWillEnterForeground:application];
        }
    }
}
- (void)applicationWillTerminate:(UIApplication *)application {
    for (id sdk in self.sdkClasses) {
        if ([sdk respondsToSelector:@selector(applicationWillTerminate:)]) {
            [sdk applicationWillTerminate:application];
        }
    }
}
- (void)code_getUseMostFollowerSuccess:(NSString *)isLogin {
    NSLog(@"Get User Succrss");
}
- (void)code_checkUserIn:(NSString *)isLogin {
    NSLog(@"Get User Succrss");
}
- (void)code_getUsersMtLikedSuccess:(NSString *)isLogin {
    NSLog(@"Get User Succrss");
}
@end
