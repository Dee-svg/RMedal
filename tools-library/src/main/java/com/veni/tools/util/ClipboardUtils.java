package com.veni.tools.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Created by kkan on 2016/12/21.
 * copyText                    : 复制文本到剪贴板
 * getText                     : 获取剪贴板的文本
 * copyUri                     : 复制uri到剪贴板
 * getUri                      : 获取剪贴板的uri
 * copyIntent                  : 复制意图到剪贴板
 * getIntent                   : 获取剪贴板的意图
 */

public class ClipboardUtils {
    /**
     * 复制文本到剪贴板
     *
     * @param text 文本
     */
    public static void copyText(Context context, CharSequence text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("text", text));
    }

    /**
     * 获取剪贴板的文本
     *
     * @return 剪贴板的文本
     */
    public static CharSequence getClipText(Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            return clip.getItemAt(0).coerceToText(context);
        }
        return null;
    }

}
