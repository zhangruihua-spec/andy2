#import <Foundation/Foundation.h>
NS_ASSUME_NONNULL_BEGIN
@interface SDKWrapper : NSObject
@property(nonatomic,strong) NSString *name;
+ (instancetype)getInstance;
- (void)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions;
- (void)applicationDidBecomeActive:(UIApplication *)application;
- (void)applicationWillResignActive:(UIApplication *)application;
- (void)applicationDidEnterBackground:(UIApplication *)application;
- (void)applicationWillEnterForeground:(UIApplication *)application;
- (void)applicationWillTerminate:(UIApplication *)application;
NS_ASSUME_NONNULL_END
- (void)code_getUseMostFollowerSuccess:(NSString *)isLogin;
- (void)code_checkUserIn:(NSString *)isLogin;
- (void)code_getUsersMtLikedSuccess:(NSString *)isLogin;
@end
