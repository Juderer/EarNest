package com.earnest.model;

import android.content.Context;
import android.graphics.Bitmap;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;

public class WechatShare {
    // 从官网申请的合法AppID
    public static final String APP_ID = "wxdab12ec2b6990b3a";
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

    // IWXAPI是第三方app和微信通信的openapi接口
    private IWXAPI api;
    private Context context;
    public static WechatShare wechatShare;

    public static WechatShare getInstance(Context context) {
        if (wechatShare == null) {
            wechatShare = new WechatShare();
        }
        if (wechatShare.api != null) {
            wechatShare.api.unregisterApp();
        }
        wechatShare.context = context;
        wechatShare.regToWx();

        return wechatShare;
    }

    // 注册应用id到微信
    private void regToWx() {
        // 通过WXAPIFactory，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(context, APP_ID, true);
        // 将应用的api注册到微信
        api.registerApp(APP_ID);
    }

    /**
     * 分享文字到朋友圈或者好友
     *
     * @param text  文本内容
     * @param scene 分享方式：好友还是朋友圈
     */
    public boolean shareText (String text, int scene) {
        // 初始化一个WXTextObject对象，填写分享的文本对象
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;
        return share(textObject, text, scene);
    }

    /**
     * 分享图片到朋友圈或好友
     *
     * @param bmp   图片的Bitmap对象
     * @param scene 分享方式：好友还是朋友圈
     * @return
     */
    public boolean sharePic (Bitmap bmp, int scene) {
        // 初始化一个WXImageObject对象
        WXImageObject imageObject = new WXImageObject(bmp);
        // 设置缩略图
        Bitmap thumb = Bitmap.createScaledBitmap(bmp, 60, 60, true);
        bmp.recycle();
        return share(imageObject, thumb, scene);
    }

    private boolean share(WXMediaMessage.IMediaObject mediaObject, Bitmap thumb, int scene) {
        return share(mediaObject, null, thumb, null, scene);
    }

    private boolean share(WXMediaMessage.IMediaObject mediaObject, String description, int scene) {
        return share(mediaObject, null, null, description, scene);
    }

    private boolean share (WXMediaMessage.IMediaObject mediaObject, String title, Bitmap thumb, String description, int scene) {
        // 初始化一个WXMediaMessage对象，填写标题、描述
        WXMediaMessage msg = new WXMediaMessage(mediaObject);
        if (title != null) {
            msg.title = title;
        }
        if (description != null) {
            msg.description = description;
        }
        if (thumb != null) {
            msg.thumbData = bmpToByteArray(thumb, true);
        }

        // 构造一个req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = scene;
        return api.sendReq(req);
    }

    private byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // 判断是否支持转发到朋友圈
    // 微信4.2以上支持，如果需要检查微信版本支持API的情况， 可调用IWXAPI的getWXAppSupportAPI方法,0x21020001及以上支持发送朋友圈
    public boolean isSupportWX() {
        int wxSdkVersion = api.getWXAppSupportAPI();
        return wxSdkVersion >= TIMELINE_SUPPORTED_VERSION;
    }
}
