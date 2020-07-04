#import "UDIDStringUtil.h"
@implementation UDIDStringUtil
+ (NSString *)createUUIDString
{
    NSMutableArray *array = [NSMutableArray array];
    for (int i = 0; i < 8; i++) {
        [array addObject:[UDIDStringUtil getString]];
    }
    [array addObject:@"-"];
    for (int i = 0; i < 4; i++) {
        [array addObject:[UDIDStringUtil getString]];
    }
    [array addObject:@"-"];
    for (int i = 0; i < 4; i++) {
        [array addObject:[UDIDStringUtil getString]];
    }
    [array addObject:@"-"];
    for (int i = 0; i < 4; i++) {
        [array addObject:[UDIDStringUtil getString]];
    }
    [array addObject:@"-"];
    for (int i = 0; i < 12; i++) {
        [array addObject:[UDIDStringUtil getString]];
    }
    NSString *result = @"";
    for (NSString *var in array) {
        result = [NSString stringWithFormat:@"%@%@", result, var];
    }
    NSLog(@"%s, %@", __FUNCTION__, result);
    return result;
}
+ (NSString *)getString
{
    int x = arc4random() % 36;
    NSArray *array = @[@"a", @"b", @"c", @"d", @"e", @"f", @"g", @"h", @"i", @"j", @"k", @"l", @"m", @"n", @"o", @"p", @"q", @"r",
                       @"s", @"t", @"u", @"v", @"w", @"x", @"y", @"z", @"0", @"1", @"2", @"3", @"4", @"5", @"6", @"7", @"8", @"9"];
    return array[x];
}
@end
