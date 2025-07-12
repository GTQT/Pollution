package keqing.pollution.mixin.extraBotany;


import com.meteor.extrabotany.common.core.handler.PersistentVariableHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

@Mixin(PersistentVariableHandler.class)
public abstract class PersistentVariableHandlerMixin {

    @Redirect(
            method = "save",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/net/URL;openStream()Ljava/io/InputStream;",
                    remap = false
            )
    )
    private static java.io.InputStream redirectOpenStream(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(100); // 0.1秒连接超时
        connection.setReadTimeout(100);    // 0.1秒读取超时
        return connection.getInputStream();
    }

    @Inject(
            method = "save",
            at = @At(
                    value = "INVOKE",
                    target = "java/net/URL.openStream()Ljava/io/InputStream;",
                    shift = At.Shift.BEFORE,
                    remap = false
            ),
            cancellable = true
    )
    private static void beforeOpenStream(CallbackInfo ci) {
        // 在打开流之前清空列表，避免重复添加
        PersistentVariableHandler.contributors.clear();
        PersistentVariableHandler.contributorsuuid.clear();
    }
}