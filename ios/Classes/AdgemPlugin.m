#import "AdgemPlugin.h"
#if __has_include(<adgem_plugin/adgem_plugin-Swift.h>)
#import <adgem_plugin/adgem_plugin-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "adgem_plugin-Swift.h"
#endif

@implementation AdgemPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAdgemPlugin registerWithRegistrar:registrar];
}
@end
